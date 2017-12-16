package com.workdawn.dragtodetaillayout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * 可以获取到当前pager显示页面的fragment适配器
 * Created by Administrator on 2017/12/12.
 */
public abstract class DragFragmentPagerAdapter extends FragmentPagerAdapter {
    private View currentView;

    public DragFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (object instanceof View) {
            currentView = (View) object;
        } else if (object instanceof Fragment) {
            Fragment fragment = (Fragment) object;
            currentView = fragment.getView();
        } else if (object instanceof android.app.Fragment) {
            android.app.Fragment fragment = (android.app.Fragment) object;
            currentView = fragment.getView();
        }
        super.setPrimaryItem(container, position, object);
    }

    public View getCurrentView() {
        return currentView;
    }
}
