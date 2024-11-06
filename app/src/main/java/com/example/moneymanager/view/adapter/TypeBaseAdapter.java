package com.example.moneymanager.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneymanager.R;
import com.example.moneymanager.model.bean.TypeBean;

import java.util.List;

public class TypeBaseAdapter extends BaseAdapter {
    Context context;
    List<TypeBean> mDatas;
    public int selectPos = 0;//选择位置

    public TypeBaseAdapter(Context context, List<TypeBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
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

    public void setSelectedIndex(int index) {
        this.selectPos = index;
        notifyDataSetChanged(); // 更新视图
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_recordfrag_gv,parent,false);
        ImageView iv = convertView.findViewById(R.id.item_recordfrag_iv);
        TextView tv = convertView.findViewById(R.id.item_recordfrag_tv);
        TypeBean typeBean = mDatas.get(position);
        tv.setText(typeBean.getTypename());
        if (selectPos == position) {
            iv.setImageResource(typeBean.getsImageId());
        }else {
            iv.setImageResource(typeBean.getImageId());
        }
        return convertView;
    }
}
