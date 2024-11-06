package com.example.moneymanager.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneymanager.R;
import com.example.moneymanager.model.bean.ChartItemBean;
import com.example.moneymanager.utils.FloatUtil;

import java.util.List;

public class ChartItemAdapter extends BaseAdapter {
    Context context;
    List<ChartItemBean>mDatas;
    LayoutInflater inflater;

    public ChartItemAdapter(Context context, List<ChartItemBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
        inflater = LayoutInflater.from(context);
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
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_chartfrag_lv,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        ChartItemBean chartItemBean = mDatas.get(position);
        holder.chartIv.setImageResource(chartItemBean.getsImageId());
        holder.typeTv.setText(chartItemBean.getType());
        float percent =  chartItemBean.getPercentage();
        String percentage = FloatUtil.ratioToPercent(percent);
        holder.percentTv.setText(percentage);
        holder.totalTv.setText("¥ "+chartItemBean.getTotalMoney());
        return convertView;
    }



    class ViewHolder{
        ImageView chartIv;
        TextView typeTv,percentTv,totalTv;
        public ViewHolder(View view){
            chartIv = view.findViewById(R.id.item_chartfrag_iv);
            typeTv = view.findViewById(R.id.item_chartfrag_tv_type);
            percentTv = view.findViewById(R.id.item_chartfrag_tv_percent);
            totalTv = view.findViewById(R.id.item_chartfrag_tv_total);
        }
    }
}
