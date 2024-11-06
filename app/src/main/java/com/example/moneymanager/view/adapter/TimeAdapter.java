package com.example.moneymanager.view.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.moneymanager.R;

import java.util.ArrayList;
import java.util.List;

public class TimeAdapter extends BaseAdapter {
    Context context;
    List<String> mDatas;
    public int year;
    public int selPos = -1;

    public TimeAdapter(Context context, int year) {
        this.context = context;
        mDatas = new ArrayList<>();
        this.year = year;
        loadDatas(year);
    }

    private void loadDatas(int year) {
        for (int i = 1; i < 13 ; i++) {
            String data = year +"/"+i;
            mDatas.add(data);
        }
    }

    public void setYear(int year) {
        this.year = year;
        mDatas.clear();
        loadDatas(year);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_dialogtime_gv,parent,false);
        TextView tv = convertView.findViewById(R.id.item_dialogtime_gv_tv);
        tv.setText(mDatas.get(position));
        tv.setBackgroundResource(R.color.grey_DDDDDD);
        tv.setTextColor(Color.BLACK);
        if (position == selPos) {
            tv.setBackgroundResource(R.color.green_0C7C0C);
            tv.setTextColor(Color.WHITE);
        }
        return convertView;
    }
}
