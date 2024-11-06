package com.example.moneymanager.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moneymanager.R;
import com.example.moneymanager.controller.DetailsController;
import com.example.moneymanager.view.adapter.DetailsAdapter;
import com.example.moneymanager.model.bean.DetailsBean;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView backIv,searchIv;
    EditText searchEt;
    TextView emptyTv;
    ListView searchLv;
    List<DetailsBean> mDatas;
    DetailsAdapter adapter;
    private DetailsController detailsController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        detailsController = new DetailsController(this);
        initView();
        mDatas = new ArrayList<>();
        adapter = new DetailsAdapter(this,mDatas);
        searchLv.setAdapter(adapter);
        searchLv.setEmptyView(emptyTv);
    }

    private void initView() {
        backIv = findViewById(R.id.search_iv_back);
        searchIv = findViewById(R.id.search_iv);
        searchEt = findViewById(R.id.search_et);
        emptyTv = findViewById(R.id.search_tv_empty);
        searchLv = findViewById(R.id.search_lv);
        backIv.setOnClickListener(this);
        searchIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.search_iv_back) {
            finish();
        }if (v.getId()==R.id.search_iv) {
            String msg = searchEt.getText().toString().trim();
            if (TextUtils.isEmpty(msg)) {
                Toast.makeText(this, "输入不能为空！", Toast.LENGTH_SHORT).show();
                return;
            }
            List<DetailsBean> list = detailsController.searchRecords(msg);
            mDatas.clear();
            mDatas.addAll(list);
            adapter.notifyDataSetChanged();
        }

    }
}