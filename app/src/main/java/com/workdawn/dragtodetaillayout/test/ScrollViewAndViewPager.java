package com.workdawn.dragtodetaillayout.test;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.workdawn.dragtodetaillayout.DragToDetailLayout;
import com.workdawn.dragtodetaillayout.R;

import java.util.ArrayList;

public class ScrollViewAndViewPager extends AppCompatActivity {

    private DragToDetailLayout dragToDetailLayout;
    private TabLayout tabLayout;
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

    boolean loaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scr_viewpager);

        dragToDetailLayout = (DragToDetailLayout) findViewById(R.id.dd_test);

        dragToDetailLayout.setOnEnterDetailLayoutListener(new DragToDetailLayout.OnEnterDetailLayoutListener() {
            @Override
            public void onEnter(int id) {
                if(id == 2){
                    if(!loaded){
                        //数据放在进入页面的时候加载，可以提高性能，当然完全可以把这端加载数据代码提出去
                        tabLayout = (TabLayout) findViewById(R.id.tablayout);
                        viewPager = (ViewPager) findViewById(R.id.viewpager);
                        fragmentTitles.add("TAB1");
                        fragmentTitles.add("TAB2");
                        fragmentTitles.add("TAB3");

                        tabLayout.addTab(tabLayout.newTab().setText(fragmentTitles.get(0)));
                        tabLayout.addTab(tabLayout.newTab().setText(fragmentTitles.get(1)));
                        tabLayout.addTab(tabLayout.newTab().setText(fragmentTitles.get(2)));

                        fragmentList.add(DragFragment.newInstance());
                        fragmentList.add(DragFragment1.newInstance());
                        fragmentList.add(DragFragment2.newInstance());

                        viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), fragmentList, fragmentTitles);
                        viewPager.setAdapter(viewPagerFragmentAdapter);
                        tabLayout.setupWithViewPager(viewPager);
                        loaded = true;
                    }
                }
            }
        });

    }
}
