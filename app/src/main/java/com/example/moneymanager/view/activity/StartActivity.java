package com.example.moneymanager.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.moneymanager.R;
import com.example.moneymanager.view.adapter.StartPagerAdapter;
import com.example.moneymanager.view.fragment.frag_start.StartFirstFragment;
import com.example.moneymanager.view.fragment.frag_start.StartSecondFragment;
import com.example.moneymanager.view.fragment.frag_start.StartThirdFragment;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{
    ViewPager startVp;
    Button startBtn;
    LinearLayout viewGroup;
    List<Fragment> fragmentList = new ArrayList<>();
    int selPos = -1;
    private StartPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initView();
        StartFirstFragment firstFragment = new StartFirstFragment();
        StartSecondFragment secondFragment = new StartSecondFragment();
        StartThirdFragment thirdFragment = new StartThirdFragment();
        fragmentList.add(firstFragment);
        fragmentList.add(secondFragment);
        fragmentList.add(thirdFragment);
        for (int i = 0; i < fragmentList.size(); i++) {
            ImageView dot = new ImageView(this);
            int size = (int) getResources().getDimension(R.dimen.dot_size);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            params.setMargins(8, 0, 8, 0);
            dot.setLayoutParams(params);
            dot.setImageResource(i == 0 ? R.drawable.dot_selected : R.drawable.dot_normal);
            viewGroup.addView(dot);
        }
        pagerAdapter = new StartPagerAdapter(getSupportFragmentManager(),fragmentList);
        startVp.setOffscreenPageLimit(fragmentList.size() - 1);
        startVp.setAdapter(pagerAdapter);
        startVp.addOnPageChangeListener(onPageChangerListener);
    }


    private ViewPager.OnPageChangeListener onPageChangerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setImageBackground(position);
            // 在最后一页显示"开始使用"按钮
            if (position == fragmentList.size() - 1) {
                startBtn.setVisibility(View.VISIBLE);
            } else {
                startBtn.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setImageBackground(int selectIItem) {
        for (int i = 0; i < fragmentList.size(); i++) {
            ImageView imageView = (ImageView) viewGroup.getChildAt(i);
            imageView.setBackground(null);
            if (i == selectIItem) {
                imageView.setImageResource(R.drawable.dot_selected);
            }else {
                imageView.setImageResource(R.drawable.dot_normal);
            }
        }
    }

    private void initView() {
        startVp = findViewById(R.id.start_vp);
        startBtn = findViewById(R.id.start_btn);
        viewGroup = findViewById(R.id.dots_container);
        startBtn.setOnClickListener(this);
        startVp.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_btn) {
            Intent intent ;
            intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

}