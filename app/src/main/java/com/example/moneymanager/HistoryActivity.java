package com.example.moneymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.moneymanager.adapter.DetailsAdapter;
import com.example.moneymanager.database.DBManager;
import com.example.moneymanager.database.DetailsBean;
import com.example.moneymanager.utils.ChangeDialogUtil;
import com.example.moneymanager.utils.TimeDialogUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView backIv, calenderIv;
    TextView timeTv;
    ListView historyLv;
    List<DetailsBean> mDatas;
    DetailsAdapter adapter;
    int year, month;
    int dialogSelPos = -1;
    int dialogSelMonth = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initTime();
        initView();
        mDatas = new ArrayList<>();
        adapter = new DetailsAdapter(this, mDatas);
        historyLv.setAdapter(adapter);
        timeTv.setText(year + "年" + month + "月");
        loadData(year,month);
        setLVLongClickListener();
        setLVClickListener();
    }

    private void setLVClickListener() {
        historyLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DetailsBean detailsBean = mDatas.get(position);
                UpdateItem(detailsBean);
                loadData(year,month);
            }
        });
    }

    private void UpdateItem(DetailsBean detailsBean) {
        int id = detailsBean.getId();
        String typename = detailsBean.getTypename();
        int sImageId = detailsBean.getsImageId();
        String record = detailsBean.getRecord();
        float money = detailsBean.getMoney();
        String time = detailsBean.getTime();
        int kind = detailsBean.getKind();
        int year = detailsBean.getYear();
        int month = detailsBean.getMonth();
        int day = detailsBean.getDay();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("您确定要修改这条记录吗？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showUpdateLayout(id,typename,sImageId,record,money,time,year,month,day,kind);
                        finish();
                    }
                });
        builder.create().show();
    }

    private void showUpdateLayout(int id,String typename, int sImageId, String record, float money, String time,int year, int month, int day, int kind) {
        Intent intent;
        intent = new Intent(HistoryActivity.this, RecordActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("key", 1);
        intent.putExtra("typename", typename);
        intent.putExtra("sImageId", sImageId);
        intent.putExtra("record", record);
        intent.putExtra("money", money);
        intent.putExtra("time", time);
        intent.putExtra("year", year);
        intent.putExtra("month", month);
        intent.putExtra("day", day);
        intent.putExtra("kind", kind);
        startActivity(intent);

    }

    private void setLVLongClickListener() {
        historyLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DetailsBean detailsBean = mDatas.get(position);
                deleteItem(detailsBean);
                return false;
            }
        });
    }

    private void deleteItem(DetailsBean detailsBean) {
        int id = detailsBean.getId();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("您确定要删除这条记录吗？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBManager.deleteItemFromDetailstbById(id);
                        mDatas.remove(detailsBean);
                        adapter.notifyDataSetChanged();
                    }
                });
        builder.create().show();
    }

    private void loadData(int year, int month) {
        List<DetailsBean> list = DBManager.getDetailsListOneMonthFromDetailstb(year, month);
        mDatas.clear();
        mDatas.addAll(list);
        adapter.notifyDataSetChanged();
    }

    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
    }

    private void initView() {
        backIv = findViewById(R.id.history_iv_back);
        calenderIv = findViewById(R.id.history_iv_calender);
        timeTv = findViewById(R.id.history_tv_time);
        historyLv = findViewById(R.id.history_lv);
        backIv.setOnClickListener(this);
        calenderIv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.history_iv_back) {
            finish();
        }
        if (v.getId() == R.id.history_iv_calender) {
            TimeDialogUtil timeDialogUtil = new TimeDialogUtil(this,dialogSelPos,dialogSelMonth);
            timeDialogUtil.show();
            timeDialogUtil.setDialogSize();
            timeDialogUtil.setOnRefreshListener(new TimeDialogUtil.OnRefreshListener() {
                @Override
                public void onRefresh(int selPos, int year, int month) {
                    timeTv.setText(year+"年"+month+"月");
                    loadData(year,month);
                    dialogSelPos = selPos;
                    dialogSelMonth = month;
                }
            });

        }
    }

}