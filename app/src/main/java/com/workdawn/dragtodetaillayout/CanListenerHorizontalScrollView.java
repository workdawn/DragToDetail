package com.workdawn.dragtodetaillayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * 可以在任何版本监听滚动事件的HorizontalScrollView
 * Created by Administrator on 2017/12/16.
 */
public class CanListenerHorizontalScrollView extends HorizontalScrollView {

    private OnScrollChangeListener mOnScrollChangeListener;

    public CanListenerHorizontalScrollView(Context context) {
        super(context);
    }

    public CanListenerHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CanListenerHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(mOnScrollChangeListener != null){
            mOnScrollChangeListener.onScrollChanged(this, l, t);
        }
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener){
        this.mOnScrollChangeListener = onScrollChangeListener;
    }

    public interface OnScrollChangeListener{
        void onScrollChanged(View v, int x, int y);
    }
}
