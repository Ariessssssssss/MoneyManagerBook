package com.example.moneymanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.moneymanager.adapter.ChartVPAdapter;
import com.example.moneymanager.adapter.StartPagerAdapter;
import com.example.moneymanager.frag_start.StartFirstFragment;
import com.example.moneymanager.frag_start.StartSecondFragment;
import com.example.moneymanager.frag_start.StartThirdFragment;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity implements View.OnClickListener{
    TextView skipTv;
    ViewPager startVp;
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
            ImageView imageView = new ImageView(this);
            if (i == 0) {
                imageView.setLayoutParams(new ViewGroup.LayoutParams(30, 10));
                imageView.setImageResource(R.drawable.roll);
            }else {
                imageView.setLayoutParams(new ViewGroup.LayoutParams(14, 14));
                imageView.setImageResource(R.drawable.roll_short);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 15;
            params.rightMargin = 15;
            viewGroup.addView(imageView,params);
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
                imageView.setImageResource(R.drawable.roll);
            }else {
                imageView.setImageResource(R.drawable.roll_short);
            }
        }
    }

    private void initView() {
        skipTv = findViewById(R.id.start_skip);
        startVp = findViewById(R.id.start_vp);
        viewGroup = findViewById(R.id.start_viewGroup);
        skipTv.setOnClickListener(this);
        startVp.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_skip) {
            Intent intent ;
            intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

}