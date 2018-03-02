package com.mxt.anitrend.adapter.pager.index;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.mxt.anitrend.R;
import com.mxt.anitrend.base.custom.pager.BaseStatePageAdapter;
import com.mxt.anitrend.util.ApplicationPref;
import com.mxt.anitrend.util.KeyUtils;
import com.mxt.anitrend.view.fragment.search.CharacterSearchFragment;
import com.mxt.anitrend.view.fragment.search.SeriesSearchFragment;
import com.mxt.anitrend.view.fragment.search.StaffSearchFragment;
import com.mxt.anitrend.view.fragment.search.StudioSearchFragment;
import com.mxt.anitrend.view.fragment.search.UserSearchFragment;

/**
 * Created by max on 2017/12/19.
 */

public class SearchPageAdapter extends BaseStatePageAdapter {

    public SearchPageAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager, context);
        setPagerTitles(new ApplicationPref(context).isAuthenticated()? R.array.search_titles_auth : R.array.search_titles);
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return SeriesSearchFragment.newInstance(getParams(), KeyUtils.ANIME);
            case 1:
                return SeriesSearchFragment.newInstance(getParams(), KeyUtils.MANGA);
            case 2:
                return StudioSearchFragment.newInstance(getParams());
            case 3:
                return StaffSearchFragment.newInstance(getParams());
            case 4:
                return CharacterSearchFragment.newInstance(getParams());
            case 5:
                return UserSearchFragment.newInstance(getParams());
        }
        return null;
    }
}
