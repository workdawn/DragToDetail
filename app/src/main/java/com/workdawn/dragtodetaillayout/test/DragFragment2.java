package com.workdawn.dragtodetaillayout.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.workdawn.dragtodetaillayout.R;

import java.util.List;

public class DragFragment2 extends Fragment{

    public static DragFragment2 newInstance(){
        return new DragFragment2();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment1, container, false);
        ListView lv = (ListView) view.findViewById(R.id.lv);
        lv.setAdapter(new CommonsListViewAdapter<String>(R.layout.item_listview, getActivity()) {
            @Override
            public void processViews(CommonsViewHolder holder) {

            }

            @Override
            public void inflateData(CommonsViewHolder holder, int position, List<String> data) {

            }

            @Override
            public boolean validateData(List<String> data, int position) {
                return false;
            }

            @Override
            public void setTag(String data, CommonsViewHolder holder) {

            }
        });
        return view;
    }
}
