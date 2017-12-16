package com.workdawn.dragtodetaillayout.test;

import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 通用的listView缓存
 * Created by Administrator on 2017/3/11.
 */
public class CommonsViewHolder {

    private SparseArray<View> mViews;
    private View mConvertView;

    public CommonsViewHolder(View convertView) {
        mConvertView = convertView;
        mViews = new SparseArray<>();
    }

    public void setText(int resId, String txt){
        TextView t = getView(resId);
        t.setText(txt);
    }

    public void setText(int resId, int textStringId){
        TextView t = getView(resId);
        t.setText(textStringId);
    }

    public <T extends View> T getView(int resId){
        View view = mViews.get(resId);
        if(view == null){
            view = mConvertView.findViewById(resId);
            mViews.put(resId, view);
        }
        return (T) view;
    }
}
