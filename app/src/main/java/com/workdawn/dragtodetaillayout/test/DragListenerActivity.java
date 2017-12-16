package com.workdawn.dragtodetaillayout.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.workdawn.dragtodetaillayout.DragToDetailLayout;
import com.workdawn.dragtodetaillayout.R;

public class DragListenerActivity extends AppCompatActivity {

    private DragToDetailLayout dragToDetailLayout;
    private TextView tv_scr_distance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drag_listener);

        dragToDetailLayout = (DragToDetailLayout) findViewById(R.id.dd_test);
        tv_scr_distance = (TextView) findViewById(R.id.tv_scr_distance);

        dragToDetailLayout.setOnEnterDetailLayoutListener(new DragToDetailLayout.EnterDetailLayoutListener() {
            @Override
            public void onEnter(int id) {
                Toast.makeText(DragListenerActivity.this, "进入第 " + id + "页", Toast.LENGTH_LONG).show();
            }
        });

        dragToDetailLayout.setOnDragScrollListener(new DragToDetailLayout.DragScrollListener() {
            @Override
            public void onScrollChanged(View v, float distanceY, float distanceX) {
                tv_scr_distance.setText("垂直方向移动距离 = " + distanceY);
            }
        });
    }
}
