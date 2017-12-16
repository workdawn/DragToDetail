package com.workdawn.dragtodetaillayout.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.workdawn.dragtodetaillayout.DragToDetailLayout;
import com.workdawn.dragtodetaillayout.R;

public class DragListenerActivity extends AppCompatActivity {

    private DragToDetailLayout dragToDetailLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_drag_listener);

        dragToDetailLayout = (DragToDetailLayout) findViewById(R.id.dd_test);

        dragToDetailLayout.setOnEnterDetailLayoutListener(new DragToDetailLayout.EnterDetailLayoutListener() {
            @Override
            public void onEnter(int id) {
                Toast.makeText(DragListenerActivity.this, "进入第 " + id + "页", Toast.LENGTH_LONG).show();
            }
        });
    }
}
