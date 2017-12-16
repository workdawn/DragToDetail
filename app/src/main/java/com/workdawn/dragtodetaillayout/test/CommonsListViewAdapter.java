package com.workdawn.dragtodetaillayout.test;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * 通用的listView适配器
 * Created by Administrator on 2017/3/11.
 */
public abstract class CommonsListViewAdapter<E> extends BaseAdapter {

    private List<E> mData;
    private LayoutInflater mInflater;
    private int layoutId;

    public CommonsListViewAdapter(int layoutId, Context context) {
        mInflater = LayoutInflater.from(context);
        this.layoutId = layoutId;
    }

    public CommonsListViewAdapter(int layoutId, Context context, List<E> data) {
        mInflater = LayoutInflater.from(context);
        this.layoutId = layoutId;
        this.mData = data;
    }

    public void setTopData(List<E> mData){
        if(mData != null && mData.size() > 0){
            this.mData = mData;
            notifyDataSetChanged();
        }
    }

    public void setLastData(List<E> mData){
        if(mData != null){
            this.mData.addAll(mData);
            notifyDataSetChanged();
        }
    }

    public void clearData(){
        if(mData != null){
            mData.clear();
            notifyDataSetChanged();
        }
    }

    public List<E> getData(){
        return mData;
    }

    @Override
    public int getCount() {
        return mData == null ? 10 : mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommonsViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(layoutId, parent, false);
            holder = new CommonsViewHolder(convertView);
            processViews(holder);
            convertView.setTag(holder);
        }else{
            holder = (CommonsViewHolder) convertView.getTag();
        }
        if(validateData(mData, position)){
            inflateData(holder, position, mData);
            setTag(mData.get(position), holder);
        }
        return convertView;
    }

    /**
     * 处理控件如：适配大小，给控件添加点击事件等等
     * @param holder holder
     */
    public abstract void processViews(CommonsViewHolder holder);

    /**
     * 填充数据
     * @param holder holder
     * @param position item微整
     * @param data 数据集合
     */
    public abstract void inflateData(CommonsViewHolder holder, int position, List<E> data);

    /**
     * 数据校验
     * @param data 数据
     * @param position item位置
     */
    public abstract boolean validateData(List<E> data, int position);

    /**
     * 设置Tag
     * @param data 数据
     * @param holder holder
     */
    public abstract void setTag(E data, CommonsViewHolder holder);
}
