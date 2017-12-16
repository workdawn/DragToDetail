package com.workdawn.dragtodetaillayout.test;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.workdawn.dragtodetaillayout.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    public void twoScrV(View view){
        startActivity(new Intent(this, TwoScrollView_V.class));
    }

    public void threeScrV(View view){
        startActivity(new Intent(this, ThreeScroll_V.class));
    }

    public void twoScrH(View view){
        startActivity(new Intent(this, TwoScroll_H.class));
    }

    public void threeScrH(View view){
        startActivity(new Intent(this, ThreeScroll_H.class));
    }

    public void setSelectionItem(View view){
        startActivity(new Intent(this, SelectItemActivity.class));
    }

    public void listenerTest(View view){
        startActivity(new Intent(this, DragListenerActivity.class));
    }

    public void withViewPager(View view){
        startActivity(new Intent(this, ScrollViewAndViewPager.class));
    }
}
