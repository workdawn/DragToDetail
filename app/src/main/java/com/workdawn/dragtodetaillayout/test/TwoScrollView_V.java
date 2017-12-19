package com.workdawn.dragtodetaillayout.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.workdawn.dragtodetaillayout.DragToDetailLayout;
import com.workdawn.dragtodetaillayout.R;

public class TwoScrollView_V extends AppCompatActivity {
    private DragToDetailLayout dragToDetailLayout;
    private Button btn_drag_tip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_two_scr_v);

        dragToDetailLayout = (DragToDetailLayout) findViewById(R.id.dtd);
        btn_drag_tip = (Button) findViewById(R.id.btn_drag_tip);

        dragToDetailLayout.setOnDragListener(new DragToDetailLayout.OnDragListener() {
            @Override
            public void onDrag(View dragView, float distanceY, float distanceX) {
                btn_drag_tip.setText("上拉查看更多详情，上拉距离 = " + distanceY);
            }

            @Override
            public void onDragComplete(View dragView) {
                btn_drag_tip.setText("松手查看更多详情");
            }
        });
    }
}
