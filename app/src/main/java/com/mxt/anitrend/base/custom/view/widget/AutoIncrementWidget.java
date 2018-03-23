package com.mxt.anitrend.base.custom.view.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mxt.anitrend.R;
import com.mxt.anitrend.base.custom.consumer.BaseConsumer;
import com.mxt.anitrend.base.interfaces.event.RetroCallback;
import com.mxt.anitrend.base.interfaces.view.CustomView;
import com.mxt.anitrend.databinding.WidgetAutoIncrementerBinding;
import com.mxt.anitrend.model.entity.anilist.MediaList;
import com.mxt.anitrend.model.entity.container.request.QueryContainer;
import com.mxt.anitrend.presenter.widget.WidgetPresenter;
import com.mxt.anitrend.util.CompatUtil;
import com.mxt.anitrend.util.ErrorUtil;
import com.mxt.anitrend.util.GraphUtil;
import com.mxt.anitrend.util.KeyUtils;
import com.mxt.anitrend.util.NotifyUtil;
import com.mxt.anitrend.util.MediaUtil;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by max on 2018/02/22.
 * auto increment widget for changing series progress with just a tap
 */

public class AutoIncrementWidget extends LinearLayout implements CustomView, View.OnClickListener, RetroCallback<MediaList> {

    private @KeyUtils.RequestType int requestType = KeyUtils.MUT_SAVE_MEDIA_LIST;
    private WidgetPresenter<MediaList> presenter;
    private WidgetAutoIncrementerBinding binding;
    private MediaList model;

    private String currentUser;

    public AutoIncrementWidget(Context context) {
        super(context);
        onInit();
    }

    public AutoIncrementWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        onInit();
    }

    public AutoIncrementWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onInit();
    }

    public AutoIncrementWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        onInit();
    }

    @Override
    public void onInit() {
        presenter = new WidgetPresenter<>(getContext());
        binding = WidgetAutoIncrementerBinding.inflate(CompatUtil.getLayoutInflater(getContext()), this, true);
        binding.setOnClickEvent(this);
    }

    @Override
    public void onClick(View view) {
        if(presenter.isCurrentUser(currentUser) && MediaUtil.isAllowedStatus(model)) {
            if (!MediaUtil.isIncrementLimitReached(model)) {
                switch (view.getId()) {
                    case R.id.widget_flipper:
                        if (binding.widgetFlipper.getDisplayedChild() == WidgetPresenter.CONTENT_STATE) {
                            binding.widgetFlipper.showNext();
                            updateModelState();
                        } else
                            NotifyUtil.makeText(getContext(), R.string.busy_please_wait, Toast.LENGTH_SHORT).show();
                        break;
                }
            } else
                NotifyUtil.makeText(getContext(), MediaUtil.isAnimeType(model.getMedia()) ?
                                R.string.text_unable_to_increment_episodes : R.string.text_unable_to_increment_chapters,
                        R.drawable.ic_warning_white_18dp, Toast.LENGTH_SHORT).show();
        }
    }

    public void setModel(MediaList model, String currentUser) {
        this.model = model; this.currentUser = currentUser;
        binding.seriesProgressIncrement.setSeriesModel(model, presenter.isCurrentUser(currentUser));
    }

    @Override
    public void onViewRecycled() {
        resetFlipperState();
        if(presenter != null)
            presenter.onDestroy();
        model = null;
    }

    private void resetFlipperState() {
        if(binding.widgetFlipper.getDisplayedChild() == WidgetPresenter.LOADING_STATE)
            binding.widgetFlipper.setDisplayedChild(WidgetPresenter.CONTENT_STATE);
    }

    @Override
    public void onResponse(@NonNull Call<MediaList> call, @NonNull Response<MediaList> response) {
        try {
            MediaList mediaList;
            if(response.isSuccessful() && (mediaList = response.body()) != null) {
                boolean isModelCategoryChanged = !mediaList.getStatus().equals(model.getStatus());

                model = mediaList;

                binding.seriesProgressIncrement.setSeriesModel(model, presenter.isCurrentUser(currentUser));
                presenter.getDatabase().getBoxStore(MediaList.class).put(model); resetFlipperState();
                if(isModelCategoryChanged) {
                    NotifyUtil.makeText(getContext(), R.string.text_changes_saved, R.drawable.ic_check_circle_white_24dp, Toast.LENGTH_SHORT).show();
                    presenter.notifyAllListeners(new BaseConsumer<>(requestType, model), false);
                }
            } else {
                resetFlipperState();
                Log.e(this.toString(), ErrorUtil.getError(response));
                NotifyUtil.makeText(getContext(), R.string.text_error_request, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(@NonNull Call<MediaList> call, @NonNull Throwable throwable) {
        try {
            Log.e(toString(), throwable.getLocalizedMessage());
            throwable.printStackTrace();
            resetFlipperState();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateModelState() {
        model.setProgress(model.getProgress() + 1);
        if(MediaUtil.isIncrementLimitReached(model))
            model.setStatus(KeyUtils.COMPLETED);
        presenter.setParams(getParam());
        presenter.requestData(requestType, getContext(), this);
    }

    private Bundle getParam() {
        QueryContainer queryContainer = GraphUtil.getDefaultQuery(false);
        queryContainer.setVariable(KeyUtils.arg_mediaId, model.getMediaId());
        queryContainer.setVariable(KeyUtils.arg_list_status, model.getStatus());
        queryContainer.setVariable(KeyUtils.arg_list_score_raw, model.getScore());
        queryContainer.setVariable(KeyUtils.arg_list_notes, model.getNotes());
        queryContainer.setVariable(KeyUtils.arg_list_private, model.isHidden());
        queryContainer.setVariable(KeyUtils.arg_list_priority, model.getPriority());
        queryContainer.setVariable(KeyUtils.arg_list_hiddenFromStatusLists, model.isHiddenFromStatusLists());

        queryContainer.setVariable(KeyUtils.arg_list_advanced_score, model.getAdvancedScores());
        queryContainer.setVariable(KeyUtils.arg_list_custom_list, model.getCustomLists());

        queryContainer.setVariable(KeyUtils.arg_list_repeat, model.getRepeat());
        queryContainer.setVariable(KeyUtils.arg_list_progress, model.getProgress());
        queryContainer.setVariable(KeyUtils.arg_list_progressVolumes, model.getProgressVolumes());

        Bundle bundle = new Bundle();
        bundle.putParcelable(KeyUtils.arg_graph_params, queryContainer);
        return bundle;
    }
}
