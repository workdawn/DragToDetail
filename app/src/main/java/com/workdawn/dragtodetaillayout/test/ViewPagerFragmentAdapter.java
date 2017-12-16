package com.workdawn.dragtodetaillayout.test;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.workdawn.dragtodetaillayout.DragFragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerFragmentAdapter extends DragFragmentPagerAdapter {

    /**Fragment集合*/
    ArrayList<Fragment> fragmentList;
    /**Fragment标题集合*/
    ArrayList<String> fragmentTitles;

    public ViewPagerFragmentAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList,  ArrayList<String> fragmentTitles) {
        super(fm);
        this.fragmentList = fragmentList;
        this.fragmentTitles = fragmentTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitles.get(position % fragmentTitles.size());
    }
}
