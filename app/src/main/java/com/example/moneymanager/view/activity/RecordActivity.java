package com.example.moneymanager.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.moneymanager.R;
import com.example.moneymanager.controller.DetailsController;
import com.example.moneymanager.view.adapter.RecordPagerAdapter;
import com.example.moneymanager.view.fragment.frag_record.IncomesFragment;
import com.example.moneymanager.view.fragment.frag_record.PayoutFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;


public class RecordActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private  ImageView imageView;
    private Intent intent;
    private int key,id,sImageId,year,month,day,kind;
    private float money;
    private String typename,record,time;
    // 添加控制器
    private DetailsController detailsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        // 初始化控制器
        detailsController = new DetailsController(this);

        tabLayout = findViewById(R.id.record_tabs);
        viewPager = findViewById(R.id.record_vp);
        imageView = findViewById(R.id.record_iv_back);
        initData();
        initPager();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initData() {
        intent= getIntent();
        key = intent.getIntExtra("key",0);
        id = intent.getIntExtra("id",0);
        typename = intent.getStringExtra("typename");
        sImageId = intent.getIntExtra("sImageId",0);
        record = intent.getStringExtra("record");
        money = intent.getFloatExtra("money", 0);
        time = intent.getStringExtra("time");
        kind = intent.getIntExtra("kind",3);
        year = intent.getIntExtra("year",0);
        month = intent.getIntExtra("month",0);
        day = intent.getIntExtra("day",0);
    }

    private void initPager() {
        //初始化ViewPager页面的集合
        List<Fragment>fragmentList = new ArrayList<>();
        PayoutFragment outFrag = new PayoutFragment();//支出
        IncomesFragment inFrag = new IncomesFragment();//收入
        if (key == 1) {
            Bundle bundle = new Bundle();
            bundle.putInt("key",key);
            bundle.putInt("id",id);
            bundle.putString("typename",typename);
            bundle.putInt("sImageId",sImageId);
            bundle.putString("record",record);
            bundle.putString("time",time);
            bundle.putFloat("money",money);
            bundle.putInt("kind",kind);
            bundle.putInt("year",year);
            bundle.putInt("month",month);
            bundle.putInt("day",day);
            inFrag.setArguments(bundle);
            outFrag.setArguments(bundle);
        }
        fragmentList.add(outFrag);
        fragmentList.add(inFrag);

        //创建适配器
        RecordPagerAdapter pagerAdapter = new RecordPagerAdapter(getSupportFragmentManager(), fragmentList);

        //设置适配器
        viewPager.setAdapter(pagerAdapter);
        if (key == 1) {
            if (kind == 1){
                viewPager.setCurrentItem(1);
            } else if (kind == 0) {
                viewPager.setCurrentItem(0);
            }
        }
        //将TabLayout和ViewPager进行关联
        tabLayout.setupWithViewPager(viewPager);
    }

}