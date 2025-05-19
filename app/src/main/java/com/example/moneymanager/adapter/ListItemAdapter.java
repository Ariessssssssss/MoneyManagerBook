package com.example.moneymanager.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymanager.R;

import java.util.List;

import javax.security.auth.callback.Callback;

public class ListItemAdapter extends BaseAdapter implements View.OnClickListener {
    private List<String> mList;
    private static final String TAG = "ListItemAdapter";
    private LayoutInflater mInflater;
    private Callback mCallback;

    public interface Callback{
        public void click(View v);
    }

    public ListItemAdapter(List<String> mList, Context context, Callback callback) {
        this.mList = mList;
        mInflater = LayoutInflater.from(context);
        mCallback = callback;
    }

    @Override
    public int getCount() {
        Log.i(TAG, "getCount");
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        Log.i(TAG, "getItem");
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        Log.i(TAG, "getItemId");
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i(TAG, "getView");
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_mainlv_top, null);
            holder = new ViewHolder();
            /*holder.textView = (TextView) convertView.findViewById(R.id.textView1);*/
            holder.button = (Button) convertView.findViewById(R.id.mainlv_btn_record);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        /*holder.textView.setText(mList.get(position));*/
        holder.button.setOnClickListener(this);
        holder.button.setTag(position);

        return convertView;
    }

    public class ViewHolder {
        /*public TextView textView;*/
        public Button button;

    }

    @Override
    public void onClick(View v) {

    }
}
