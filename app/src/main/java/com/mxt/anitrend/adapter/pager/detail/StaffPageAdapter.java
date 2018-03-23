package com.mxt.anitrend.adapter.pager.detail;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.mxt.anitrend.R;
import com.mxt.anitrend.base.custom.pager.BaseStatePageAdapter;
import com.mxt.anitrend.util.KeyUtils;
import com.mxt.anitrend.view.fragment.detail.StaffOverviewFragment;
import com.mxt.anitrend.view.fragment.group.MediaRolesFragment;
import com.mxt.anitrend.view.fragment.group.SeriesStaffRoleFragment;

/**
 * Created by max on 2017/12/01.
 */

public class StaffPageAdapter extends BaseStatePageAdapter {

    public StaffPageAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager, context);
        setPagerTitles(R.array.staff_page_titles);
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
                return StaffOverviewFragment.newInstance(getParams());
            case 1:
                return MediaRolesFragment.newInstance(getParams(), KeyUtils.ANIME, KeyUtils.STAFF_MEDIA_REQ);
            case 2:
                return MediaRolesFragment.newInstance(getParams(), KeyUtils.MANGA, KeyUtils.STAFF_MEDIA_REQ);
            case 3:
                return SeriesStaffRoleFragment.newInstance(getParams());
        }
        return null;
    }
}