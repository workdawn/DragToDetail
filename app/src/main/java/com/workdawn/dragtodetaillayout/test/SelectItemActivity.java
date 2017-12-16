package com.workdawn.dragtodetaillayout.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.workdawn.dragtodetaillayout.DragToDetailLayout;
import com.workdawn.dragtodetaillayout.R;

public class SelectItemActivity extends AppCompatActivity {
    private DragToDetailLayout dragToDetailLayout;
    private EditText et_num;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_item);

        dragToDetailLayout= (DragToDetailLayout) findViewById(R.id.dd_test);
        et_num = (EditText) findViewById(R.id.et_num);
    }

    public void jumpTo(View view){
        String numStr = et_num.getText() == null ? "0" : et_num.getText().toString();
        int num ;
        try{
            num = Integer.parseInt(numStr);
        }catch (Exception e){
            e.printStackTrace();
            num = 0;
        }
        dragToDetailLayout.setSelectionItem(num);
    }
}
