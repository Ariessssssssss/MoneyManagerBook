package com.example.moneymanager.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.moneymanager.R;

import java.util.Calendar;

public class SelectCalenderUtil extends Dialog implements View.OnClickListener {
    DatePicker datePicker;
    TimePicker timePicker;
    Button cancelBtn,ensureBtn;

    private OnEnsureListener onEnsureListener;

    public interface OnEnsureListener{
        public void onEnsure(String time,int year,int month,int day);
    }

    public void setOnEnsureListener(OnEnsureListener onEnsureListener) {
        this.onEnsureListener = onEnsureListener;

    }


    public SelectCalenderUtil(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_calender);
        datePicker = findViewById(R.id.dialog_calender_dp);
        timePicker = findViewById(R.id.dialog_calender_tp);
        cancelBtn = findViewById(R.id.dialog_calender_btn_cancel);
        ensureBtn = findViewById(R.id.dialog_calender_btn_ensure);
        cancelBtn.setOnClickListener(this);
        ensureBtn.setOnClickListener(this);
        timePicker.setIs24HourView(true);
        hideDatePicker();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() ==R.id.dialog_calender_btn_cancel) {
            cancel();
        }
        if (v.getId() ==R.id.dialog_calender_btn_ensure) {
            Calendar maxCalendar = Calendar.getInstance();
            Calendar calendar = Calendar.getInstance();
            maxCalendar.set(maxCalendar.get(Calendar.YEAR),maxCalendar.get(Calendar.MONTH)+1,maxCalendar.get(Calendar.DAY_OF_MONTH)
                    ,maxCalendar.get(Calendar.HOUR_OF_DAY),maxCalendar.get(Calendar.MINUTE));
            int year = datePicker.getYear();
            int month = datePicker.getMonth() + 1;
            int day = datePicker.getDayOfMonth();
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            String timeFormat = year + "年" + formatWithLeadingZero(month) + "月" +
                    formatWithLeadingZero(day) + "日" +" "+
                    formatWithLeadingZero(hour) + ":" + formatWithLeadingZero(minute);
            calendar.set(year,month,day,hour,minute);
            long maxtime = maxCalendar.getTimeInMillis();
            long time = calendar.getTimeInMillis();
            if (time > maxtime) {
                Toast.makeText(getContext(), "日期超出有效范围", Toast.LENGTH_SHORT).show();
                return;

            }

            if (onEnsureListener != null) {
                onEnsureListener.onEnsure(timeFormat, year, month, day);
            }
            cancel();
        }
    }


    private String formatWithLeadingZero(int value) {
        String valueStr = String.valueOf(value);
        if (value < 10){
            valueStr = "0" + value;
        }

        return valueStr;
    }

    private  void hideDatePicker(){
        ViewGroup rootView = (ViewGroup) datePicker.getChildAt(0);
        if (rootView == null) {
            return;
        }
        View headerView = rootView.getChildAt(0);
        if (headerView == null) {
            return;
        }

        //date_picker_header(6.0+) day_picker_selector_layout(5.0)
        int headerId = getContext().getResources().getIdentifier("date_picker_header", "id", "android");
        if (headerId == headerView.getId()) {
            headerView.setVisibility(View.GONE);
            ViewGroup.LayoutParams layoutParamsRoot = rootView.getLayoutParams();
            layoutParamsRoot.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            rootView.setLayoutParams(layoutParamsRoot);

            ViewGroup animator = (ViewGroup) rootView.getChildAt(1);
            ViewGroup.LayoutParams layoutParamsAnimator = animator.getLayoutParams();
            layoutParamsAnimator.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            animator.setLayoutParams(layoutParamsAnimator);

            ViewGroup child = (ViewGroup) animator.getChildAt(0);
            ViewGroup.LayoutParams layoutParamsChild = child.getLayoutParams();
            layoutParamsChild.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            child.setLayoutParams(layoutParamsChild);
            return;
        }


    }


    public void setOnEnsureListener() {
    }
}
