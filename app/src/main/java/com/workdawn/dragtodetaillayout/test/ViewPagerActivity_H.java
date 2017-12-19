package com.workdawn.dragtodetaillayout.test;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.workdawn.dragtodetaillayout.R;

import java.util.ArrayList;

public class ViewPagerActivity_H extends AppCompatActivity{
    private ViewPager viewPager;
    /**
     * Fragment集合
     */
    ArrayList<Fragment> fragmentList = new ArrayList<>();
    /**
     * Fragment标题集合
     */
    ArrayList<String> fragmentTitles = new ArrayList<>();

    ViewPagerFragmentAdapter viewPagerFragmentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_pager_h);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        fragmentTitles.add("TAB1");
        fragmentTitles.add("TAB2");
        fragmentTitles.add("TAB3");
        fragmentList.add(DragFragment.newInstance());
        fragmentList.add(DragFragment1.newInstance());
        fragmentList.add(DragFragment2.newInstance());
        viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), fragmentList, fragmentTitles);
        viewPager.setAdapter(viewPagerFragmentAdapter);
        viewPager.setOffscreenPageLimit(3);
    }
}
