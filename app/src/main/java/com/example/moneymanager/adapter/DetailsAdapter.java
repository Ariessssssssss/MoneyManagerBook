package com.example.moneymanager.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneymanager.R;
import com.example.moneymanager.database.DetailsBean;

import java.util.Calendar;
import java.util.List;

public class DetailsAdapter extends BaseAdapter {
    Context context;
    List<DetailsBean> mDatas;
    LayoutInflater inflater;
    int year,month,day;

    public DetailsAdapter(Context context, List<DetailsBean> mDatas) {
        this.context = context;
        this.mDatas = mDatas;
        inflater = LayoutInflater.from(context);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
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
        if (convertView == null){
            convertView = inflater.inflate(R.layout.item_mainlv,parent,false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        DetailsBean bean = mDatas.get(position);
        holder.typeIv.setImageResource(bean.getsImageId());
        holder.typeTv.setText(bean.getTypename());
        holder.remarkTv.setText(bean.getRecord());
        holder.moneyTv.setText("¥ "+bean.getMoney());
        if (bean.getYear() == year && bean.getMonth() == month && bean.getDay() == day) {
            String time = bean.getTime().split(" ")[1];
            holder.timeTv.setText("今天"+time);
        }else {
            holder.timeTv.setText(bean.getTime());
        }

        return convertView;
    }

    class ViewHolder{
        ImageView typeIv;
        TextView typeTv,remarkTv,moneyTv,timeTv;
        public ViewHolder(View view){
            typeIv = view.findViewById(R.id.item_mainlv_iv);
            typeTv = view.findViewById(R.id.item_mainlv_tv_title);
            remarkTv = view.findViewById(R.id.item_mainlv_tv_remark);
            moneyTv = view.findViewById(R.id.item_mainlv_tv_money);
            timeTv = view.findViewById(R.id.item_mainlv_tv_time);

        }
    }
}
