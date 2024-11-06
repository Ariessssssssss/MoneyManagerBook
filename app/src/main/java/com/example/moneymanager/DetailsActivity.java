package com.example.moneymanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneymanager.adapter.ChartVPAdapter;
import com.example.moneymanager.database.DBManager;
import com.example.moneymanager.frag_chart.IncomeBarChartFragment;
import com.example.moneymanager.frag_chart.PayoutBarChartFragment;
import com.example.moneymanager.utils.TimeDialogUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView backIv, calenderIv;
    Button incomeBtn,payoutBtn;
    TextView dateTv,incomeTv,payoutTv;
    ViewPager chartVp;
    int dialogSelPos = -1, dialogSelMonth = -1;
    int year;
    int month;
    List<Fragment> chartFragList;
    private ChartVPAdapter chartVPAdapter;
    private IncomeBarChartFragment incomeChartFragment;
    private PayoutBarChartFragment payoutChartFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initView();
        initTime();
        initDatas(year,month);
        initFrag();
        selVPSelectListener();

    }

    private void selVPSelectListener() {
        chartVp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                setButtonStyle(position);
            }
        });
    }

    private void initFrag() {
        chartFragList = new ArrayList<>();
        incomeChartFragment = new IncomeBarChartFragment();
        payoutChartFragment = new PayoutBarChartFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("year",year);
        bundle.putInt("month",month);

        payoutChartFragment.setArguments(bundle);
        incomeChartFragment.setArguments(bundle);

        chartFragList.add(payoutChartFragment);
        chartFragList.add(incomeChartFragment);

        chartVPAdapter = new ChartVPAdapter(getSupportFragmentManager(), chartFragList);
        chartVp.setAdapter(chartVPAdapter);
    }

    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
    }

    private void initDatas(int year,int month) {
        float inSumMoneyOneMonth = DBManager.getSumMoneyOneMonth(year, month, 1);
        float outSumMoneyOneMonth = DBManager.getSumMoneyOneMonth(year, month, 0);
        int inCountItemOneMonth = DBManager.getCountItemOneMonth(year, month, 1);
        int outCountItemOneMonth = DBManager.getCountItemOneMonth(year, month, 0);
        dateTv.setText(year+"年"+month+"月账单");
        payoutTv.setText("共"+outCountItemOneMonth+"笔支出，¥ "+outSumMoneyOneMonth);
        incomeTv.setText("共"+inCountItemOneMonth+"笔收入，¥ "+inSumMoneyOneMonth);


    }

    private void initView() {
        backIv = findViewById(R.id.details_iv_back);
        calenderIv = findViewById(R.id.details_iv_calender);
        incomeBtn = findViewById(R.id.details_btn_in);
        payoutBtn = findViewById(R.id.details_btn_out);
        dateTv = findViewById(R.id.details_tv_date);
        incomeTv = findViewById(R.id.details_tv_income);
        payoutTv = findViewById(R.id.details_tv_payout);
        chartVp = findViewById(R.id.details_vp);
        backIv.setOnClickListener(this);
        calenderIv.setOnClickListener(this);
        incomeBtn.setOnClickListener(this);
        payoutBtn.setOnClickListener(this);
        dateTv.setOnClickListener(this);
        incomeTv.setOnClickListener(this);
        payoutTv.setOnClickListener(this);
        chartVp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.details_iv_back) {
            finish();
        }if (v.getId()==R.id.details_btn_in) {
            setButtonStyle(1);
            chartVp.setCurrentItem(1);
        }if (v.getId()==R.id.details_btn_out) {
            setButtonStyle(0);
            chartVp.setCurrentItem(0);
        }if (v.getId()==R.id.details_iv_calender) {
            showCalenderDialog();
        }
    }

    private void showCalenderDialog() {
        TimeDialogUtil timeDialogUtil = new TimeDialogUtil(this,dialogSelPos,dialogSelMonth);
        timeDialogUtil.show();
        timeDialogUtil.setDialogSize();
        timeDialogUtil.setOnRefreshListener(new TimeDialogUtil.OnRefreshListener() {
            @Override
            public void onRefresh(int selPos, int year, int month) {
                DetailsActivity.this.dialogSelPos = selPos;
                DetailsActivity.this.dialogSelMonth = month;
                initDatas(year, month);
                incomeChartFragment.setDate(year, month);
                payoutChartFragment.setDate(year, month);
            }
        });
    }

    private void setButtonStyle(int kind){
        if (kind == 0) {
            payoutBtn.setBackgroundResource(R.drawable.main_recordbtn_bg);
            payoutBtn.setTextColor(Color.WHITE);
            incomeBtn.setBackgroundResource(R.drawable.remark_cancelbtn_bg);
            incomeBtn.setTextColor(Color.BLACK);
        }else if (kind == 1){
            payoutBtn.setBackgroundResource(R.drawable.remark_cancelbtn_bg);
            payoutBtn.setTextColor(Color.BLACK);
            incomeBtn.setBackgroundResource(R.drawable.main_recordbtn_bg);
            incomeBtn.setTextColor(Color.WHITE);
        }
    }
}