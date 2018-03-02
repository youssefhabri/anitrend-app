package com.mxt.anitrend.adapter.recycler.group;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import com.bumptech.glide.Glide;
import com.mxt.anitrend.R;
import com.mxt.anitrend.adapter.recycler.shared.GroupTitleViewHolder;
import com.mxt.anitrend.base.custom.recycler.RecyclerViewAdapter;
import com.mxt.anitrend.base.custom.recycler.RecyclerViewHolder;
import com.mxt.anitrend.databinding.AdapterEntityGroupBinding;
import com.mxt.anitrend.databinding.AdapterSeriesCharacterBinding;
import com.mxt.anitrend.model.entity.anilist.Favourite;
import com.mxt.anitrend.model.entity.base.SeriesBase;
import com.mxt.anitrend.model.entity.group.EntityGroup;
import com.mxt.anitrend.util.CompatUtil;
import com.mxt.anitrend.util.KeyUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by max on 2018/01/27.
 * Character Staff Series Roles
 */

public class GroupRoleCharacterAdapter extends RecyclerViewAdapter<EntityGroup> {

    private List<SeriesBase> favouriteSeries;

    public GroupRoleCharacterAdapter(List<EntityGroup> data, Context context) {
        super(data, context);
        Favourite favourite = presenter.getFavourites();
        if(favourite != null) {
            favouriteSeries = new ArrayList<>();
            if(favourite.getAnime() != null)
                favouriteSeries.addAll(favourite.getAnime());
            if(favourite.getManga() != null)
                favouriteSeries.addAll(favourite.getManga());
        }
    }

    @Override
    public RecyclerViewHolder<EntityGroup> onCreateViewHolder(ViewGroup parent, @KeyUtils.RecyclerViewType int viewType) {
        if (viewType == KeyUtils.RECYCLER_TYPE_HEADER)
            return new GroupTitleViewHolder(AdapterEntityGroupBinding.inflate(CompatUtil.getLayoutInflater(parent.getContext()), parent, false));
        return new SeriesCharacterViewHolder(AdapterSeriesCharacterBinding.inflate(CompatUtil.getLayoutInflater(parent.getContext()), parent, false));
    }

    @Override
    public @KeyUtils.RecyclerViewType
    int getItemViewType(int position) {
        return data.get(position).getContentType();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    protected class SeriesCharacterViewHolder extends RecyclerViewHolder<EntityGroup> {

        private AdapterSeriesCharacterBinding binding;

        /**
         * Default constructor which includes binding with butter knife
         *
         * @param binding
         */
        SeriesCharacterViewHolder(AdapterSeriesCharacterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        /**
         * Load image, text, buttons, etc. in this method from the given parameter
         * <br/>
         *
         * @param entityGroup Is the model at the current adapter position
         */
        @Override
        public void onBindViewHolder(EntityGroup entityGroup) {
            SeriesBase model = (SeriesBase) entityGroup;
            binding.setModel(model);
            binding.seriesTitle.setTitle(model);
            binding.customRatingWidget.setFavourState(favouriteSeries != null && favouriteSeries.contains(model));
            binding.executePendingBindings();
        }

        /**
         * If any image views are used within the view holder, clear any pending async img requests
         * by using Glide.clear(ImageView) or Glide.with(context).clear(view) if using Glide v4.0
         * <br/>
         *
         * @see Glide
         */
        @Override
        public void onViewRecycled() {
            Glide.with(getContext()).clear(binding.seriesImage);
            binding.unbind();
        }

        /**
         * Handle any onclick events from our views
         * <br/>
         *
         * @param v the view that has been clicked
         * @see View.OnClickListener
         */
        @Override @OnClick(R.id.container)
        public void onClick(View v) {
            int index;
            if((index = getAdapterPosition()) > -1)
                clickListener.onItemClick(v, data.get(index));
        }

        @Override @OnLongClick(R.id.container)
        public boolean onLongClick(View view) {
            int index;
            if((index = getAdapterPosition()) > -1)
                clickListener.onItemLongClick(view, data.get(index));
            return true;
        }
    }
}