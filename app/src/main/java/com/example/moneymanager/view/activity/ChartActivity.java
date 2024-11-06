package com.example.moneymanager.view.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.moneymanager.R;
import com.example.moneymanager.view.fragment.frag_chart.DailyChartFragment;
import com.example.moneymanager.view.fragment.frag_chart.MonthlyChartFragment;
import com.example.moneymanager.view.fragment.frag_chart.YearlyChartFragment;

public class ChartActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView backIv;
    private TextView dayTv, monthTv, yearTv;
    private Fragment currentFragment;
    private DailyChartFragment dailyChartFragment;
    private MonthlyChartFragment monthlyChartFragment;
    private YearlyChartFragment yearlyChartFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        initView();
        initFragment();
    }

    private void initView() {
        // 顶部返回按钮
        backIv = findViewById(R.id.chart_back);
        backIv.setOnClickListener(this);
        
        // 选项卡
        dayTv = findViewById(R.id.chart_tv_day);
        monthTv = findViewById(R.id.chart_tv_month);
        yearTv = findViewById(R.id.chart_tv_year);
        dayTv.setOnClickListener(this);
        monthTv.setOnClickListener(this);
        yearTv.setOnClickListener(this);
    }

    private void initFragment() {
        dailyChartFragment = new DailyChartFragment();
        monthlyChartFragment = new MonthlyChartFragment();
        yearlyChartFragment = new YearlyChartFragment();

        // 默认显示日常统计Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.chart_fragment_container, dailyChartFragment);
        transaction.commit();
        currentFragment = dailyChartFragment;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.chart_back) {
            finish();
        } else if (v.getId() == R.id.chart_tv_day) {
            setTab(0);
        } else if (v.getId() == R.id.chart_tv_month) {
            setTab(1);
        } else if (v.getId() == R.id.chart_tv_year) {
            setTab(2);
        }
    }

    private void setTab(int index) {
        // 重置所有选项卡样式
        dayTv.setBackgroundColor(Color.TRANSPARENT);
        monthTv.setBackgroundColor(Color.TRANSPARENT);
        yearTv.setBackgroundColor(Color.TRANSPARENT);
        dayTv.setTextColor(ContextCompat.getColor(this, R.color.black));
        monthTv.setTextColor(ContextCompat.getColor(this, R.color.black));
        yearTv.setTextColor(ContextCompat.getColor(this, R.color.black));

        // 设置选中的选项卡样式
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (index) {
            case 0:
                dayTv.setBackgroundColor(ContextCompat.getColor(this, R.color.green_006400));
                dayTv.setTextColor(ContextCompat.getColor(this, R.color.white));
                if (currentFragment != dailyChartFragment) {
                    if (dailyChartFragment.isAdded()) {
                        transaction.hide(currentFragment).show(dailyChartFragment);
                    } else {
                        transaction.hide(currentFragment).add(R.id.chart_fragment_container, dailyChartFragment);
                    }
                    currentFragment = dailyChartFragment;
                }
                break;
            case 1:
                monthTv.setBackgroundColor(ContextCompat.getColor(this, R.color.green_006400));
                monthTv.setTextColor(ContextCompat.getColor(this, R.color.white));
                if (currentFragment != monthlyChartFragment) {
                    if (monthlyChartFragment.isAdded()) {
                        transaction.hide(currentFragment).show(monthlyChartFragment);
                    } else {
                        transaction.hide(currentFragment).add(R.id.chart_fragment_container, monthlyChartFragment);
                    }
                    currentFragment = monthlyChartFragment;
                }
                break;
            case 2:
                yearTv.setBackgroundColor(ContextCompat.getColor(this, R.color.green_006400));
                yearTv.setTextColor(ContextCompat.getColor(this, R.color.white));
                if (currentFragment != yearlyChartFragment) {
                    if (yearlyChartFragment.isAdded()) {
                        transaction.hide(currentFragment).show(yearlyChartFragment);
                    } else {
                        transaction.hide(currentFragment).add(R.id.chart_fragment_container, yearlyChartFragment);
                    }
                    currentFragment = yearlyChartFragment;
                }
                break;
        }

        transaction.commit();
    }

}