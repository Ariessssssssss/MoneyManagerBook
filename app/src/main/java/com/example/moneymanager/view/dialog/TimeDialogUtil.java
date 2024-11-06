package com.example.moneymanager.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.moneymanager.R;
import com.example.moneymanager.controller.DetailsController;
import com.example.moneymanager.view.adapter.TimeAdapter;
import com.example.moneymanager.model.dao.DBManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimeDialogUtil extends Dialog implements View.OnClickListener {
    ImageView backIv;
    GridView gv;
    LinearLayout hsvLayout;
    List<TextView>hsvViewList;
    List<Integer>yearList;
    int selectPos = -1;//表示正在被点击的年份的位置
    int selectMonth = -1;
    private TimeAdapter adapter;
    private DetailsController detailsController;
    public  interface OnRefreshListener{
        public void onRefresh(int selPos,int year, int month);
    }
    OnRefreshListener onRefreshListener;

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }

    public TimeDialogUtil(@NonNull Context context,int selectPos, int selectMonth) {
        super(context);
        this.selectPos = selectPos;
        this.selectMonth = selectMonth;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_time);
        detailsController = new DetailsController(getContext());
        backIv = findViewById(R.id.dialog_time_iv);
        gv = findViewById(R.id.dialog_time_gv);
        hsvLayout = findViewById(R.id.dialog_time_layout);
        backIv.setOnClickListener(this);

        addViewLayout();
        initGridView();
        setGVClickListener();


    }

    private void setGVClickListener() {
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.selPos = position;
                adapter.notifyDataSetInvalidated();
                int month = position + 1;
                int year = adapter.year;
                onRefreshListener.onRefresh(selectPos,year,month);
                cancel();
            }
        });
    }

    private void initGridView() {
        int selYear = yearList.get(selectPos);
        adapter = new TimeAdapter(getContext(), selYear);
        if (selectMonth == -1) {
            int month = Calendar.getInstance().get(Calendar.MONTH);
            adapter.selPos = month;
        }else {
            adapter.selPos = selectMonth - 1;
        }
        gv.setAdapter(adapter);

    }


    private void addViewLayout() {
        hsvViewList = new ArrayList<>();
        yearList = DetailsController.getYearDetailsList();
        if (yearList.size()==0) {
            int year = Calendar.getInstance().get(Calendar.YEAR);
            yearList.add(year);
        }

        for (int i = 0; i < yearList.size(); i++){
            int year = yearList.get(i);
            View view = getLayoutInflater().inflate(R.layout.item_dialogtime_hsv, null);
            hsvLayout.addView(view);
            TextView hsvTv = view.findViewById(R.id.item_dialogtime_hsv_tv);
            hsvTv.setText(year+"");
            hsvViewList.add(hsvTv);
        }
        if (selectPos == -1) {
            selectPos = hsvViewList.size()-1;
        }
        changeTvbg(selectPos);
        setHSVClickListener();
    }

    private void setHSVClickListener() {
        for (int i = 0; i < hsvViewList.size(); i++) {
            TextView view = hsvViewList.get(i);
            int pos = i ;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeTvbg((pos));
                    selectPos = pos;
                    int year = yearList.get(selectPos);
                    adapter.setYear(year);
                }
            });
        }
    }

    private void changeTvbg(int selectPos) {
        for (int i = 0; i < hsvViewList.size() ; i++) {
            TextView tv = hsvViewList.get(i);
            tv.setBackgroundResource(R.drawable.remark_cancelbtn_bg);
            tv.setTextColor(Color.BLACK);
        }
        TextView selectView = hsvViewList.get(selectPos);
        selectView.setBackgroundResource(R.drawable.main_recordbtn_bg);
        selectView.setTextColor(Color.WHITE);
    }
    public void setDialogSize(){
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        Display defaultDisplay = window.getWindowManager().getDefaultDisplay();
        wlp.width = (int) (defaultDisplay.getWidth());
        wlp.gravity = Gravity.TOP;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setAttributes(wlp);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.dialog_time_iv) {
            cancel();
        }
    }
}