package com.example.moneymanager.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.moneymanager.R;
import com.example.moneymanager.controller.BudgetController;
import com.example.moneymanager.controller.DetailsController;
import com.example.moneymanager.controller.SettingController;
import com.example.moneymanager.view.adapter.DetailsAdapter;
import com.example.moneymanager.model.bean.DetailsBean;
import com.example.moneymanager.view.dialog.BudgetDialogUtil;
import com.example.moneymanager.view.dialog.MoreDialogUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton imageButton;
    ListView todayLv;
    List<DetailsBean> mDatas;
    ImageView imageView;
    DetailsAdapter adapter;
    int year,month,day;
    Button topButton;
    View headerView;
    TextView topOutTv,topInTv,topBudgetTv,topSurplusTv,topChartTv,topDayInTv,topDayOutTv;
    ImageView topShowIv;
    SharedPreferences preferences;
    // 添加控制器
    private DetailsController detailsController;
    private BudgetController budgetController;
    private SettingController settingController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化控制器
        detailsController = new DetailsController(this);
        budgetController = new BudgetController(this);
        settingController = new SettingController(this);

        initTime();
        initView();
        addLVHeaderView();
        mDatas = new ArrayList<>();
        adapter = new DetailsAdapter(this,mDatas);
        todayLv.setAdapter(adapter);
        preferences = getSharedPreferences("money", Context.MODE_PRIVATE);

    }

    //获取今日时间
    private void initTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH)+1;
        day = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void initView() {
        imageView = findViewById(R.id.main_iv_search);
        imageButton = findViewById(R.id.main_btn_more);
        todayLv = findViewById(R.id.main_lv);
        imageButton.setOnClickListener(this);
        imageView.setOnClickListener(this);
        setLVLongClickListener();
        setLVClickListener();
    }

    //点击修改记录
    private void setLVClickListener() {
        todayLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = position - 1;
                DetailsBean clickbean = mDatas.get(pos);
                showUpadteItemDialog(clickbean);
            }
        });
    }

    private void showUpadteItemDialog(DetailsBean clickbean) {
        int id = clickbean.getId();
        String typename = clickbean.getTypename();
        int sImageId = clickbean.getsImageId();
        String record = clickbean.getRecord();
        float money = clickbean.getMoney();
        String time = clickbean.getTime();
        int kind = clickbean.getKind();
        int year = clickbean.getYear();
        int month = clickbean.getMonth();
        int day = clickbean.getDay();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("您确定要修改这条记录吗？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showUpdateLayout(id,typename,sImageId,record,money,time,year,month,day,kind);
                    }
                });
        builder.create().show();
    }

    private void showUpdateLayout(int id, String typename, int sImageId, String record, float money, String time, int year, int month, int day, int kind) {
        Intent intent;
        intent = new Intent(MainActivity.this, RecordActivity.class);
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

    //长按删除记录
    private void setLVLongClickListener() {
        todayLv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return false;
                }
                int pos = position - 1;
                DetailsBean clickbean = mDatas.get(pos);
                showDeleteItemDialog(clickbean);
                return false;
            }
        });
    }

    private void showDeleteItemDialog(DetailsBean clickbean) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提示信息").setMessage("您确定要删除这条记录吗？")
                .setNegativeButton("取消",null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int click_Id = clickbean.getId();
                        // 使用RecordController删除记录
                        boolean success = detailsController.deleteRecord(click_Id);
                        if (success) {
                            mDatas.remove(clickbean);
                            adapter.notifyDataSetChanged();
                            setTopTvShow();
                        }
                    }
                });
        builder.create().show();
    }

    private void addLVHeaderView() {
        headerView = getLayoutInflater().inflate(R.layout.item_mainlv_top, null);
        todayLv.addHeaderView(headerView);
        topInTv = headerView.findViewById(R.id.item_mainlv_top_in);
        topOutTv = headerView.findViewById(R.id.item_mainlv_top_out);
        topBudgetTv = headerView.findViewById(R.id.item_mainlv_top_budget);
        topSurplusTv = headerView.findViewById(R.id.item_mainlv_top_budget_surplus);
        topChartTv = headerView.findViewById(R.id.item_mainlv_top_chart);
        topDayInTv = headerView.findViewById(R.id.item_mainlv_top_day_in);
        topDayOutTv = headerView.findViewById(R.id.item_mainlv_top_day_out);
        topShowIv = headerView.findViewById(R.id.item_mainlv_top_iv_open);
        topButton = headerView.findViewById(R.id.mainlv_btn_record);
        topInTv.setOnClickListener(this);
        topOutTv.setOnClickListener(this);
        topBudgetTv.setOnClickListener(this);
        topChartTv.setOnClickListener(this);
        topDayInTv.setOnClickListener(this);
        topDayOutTv.setOnClickListener(this);
        topShowIv.setOnClickListener(this);
        topButton.setOnClickListener(this);
    }

    //当activity获取焦点时，会调用的方法
    @Override
    protected void onResume() {
        super.onResume();
        loadDBData();
        setTopTvShow();
    }

    //设置头布局数据更新
    private void setTopTvShow() {
        // 获取月度收入支出
        float incomeOneMonth = detailsController.getMonthlySum(year, month, 1);
        float payoutOneMonth = detailsController.getMonthlySum(year, month, 0);

        // 获取日收入支出
        float incomeOneDay = detailsController.getDailySum(year, month, day, 1);
        float payoutOneDay = detailsController.getDailySum(year, month, day, 0);

        // 获取预算信息
        float budget = budgetController.getBudget();
        float remainingBudget = budgetController.getRemainingBudget(year, month);

        // 更新UI
        topInTv.setText("¥ " + incomeOneMonth);
        topOutTv.setText("¥ " + payoutOneMonth);
        topDayInTv.setText("收 ¥ " + incomeOneDay);
        topDayOutTv.setText("支 ¥ " + payoutOneDay);
        topBudgetTv.setText("¥ " + budget);
        topSurplusTv.setText("¥ " + remainingBudget);
    }

    //加载数据库更新
    private void loadDBData() {
        // 使用RecordController获取数据
        List<DetailsBean> list = detailsController.getDailyDetailsList(year, month, day);
        mDatas.clear();
        mDatas.addAll(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.main_iv_search){//搜索
            Intent intent;
            intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.main_btn_more) {//更多
            showMoreDialog();
        }
        if (v.getId() == R.id.item_mainlv_top_budget) {//添加预算dialog
            showBudgetDialog();
        }
        if (v.getId() == R.id.item_mainlv_top_chart) {
            Intent intent;
            intent = new Intent(MainActivity.this, DetailsActivity.class);
            startActivity(intent);
        }
        if (v.getId() == R.id.item_mainlv_top_iv_open) {//金额隐藏或显示
            toggleShow();
        }
        if (v.getId() == R.id.mainlv_btn_record) {//跳转记账页面
            Intent intent;
            intent = new Intent(MainActivity.this, RecordActivity.class);
            startActivity(intent);
        }
    }

    private void showMoreDialog() {
        MoreDialogUtil moreDialogUtil = new MoreDialogUtil(this);
        moreDialogUtil.show();
        moreDialogUtil.setDialogSize();
    }

    private void showBudgetDialog() {
        BudgetDialogUtil dialogUtil = new BudgetDialogUtil(this);
        dialogUtil.show();
        dialogUtil.setDialogSize();
        dialogUtil.setOnEnsureListener(new BudgetDialogUtil.OnEnsureListener() {
            @Override
            public void onEnsure(float budget) {
                // 使用BudgetController设置预算
                budgetController.setBudget(budget);

                // 更新UI显示
                float remainingBudget = budgetController.getRemainingBudget(year, month);
                topBudgetTv.setText("¥ " + budget);
                topSurplusTv.setText("¥ " + remainingBudget);
            }
        });
    }

    boolean isShow = true;
    private void toggleShow() {
        // 使用SettingController切换显示状态
        isShow = !isShow;
        if (isShow) { // 显示 ===> 隐藏
            PasswordTransformationMethod passwordMethod = PasswordTransformationMethod.getInstance();
            topInTv.setTransformationMethod(passwordMethod);
            topOutTv.setTransformationMethod(passwordMethod);
            topBudgetTv.setTransformationMethod(passwordMethod);
            topSurplusTv.setTransformationMethod(passwordMethod);
            topShowIv.setImageResource(R.drawable.eye_close);
        } else {
            HideReturnsTransformationMethod hideMethod = HideReturnsTransformationMethod.getInstance();
            topInTv.setTransformationMethod(hideMethod);
            topOutTv.setTransformationMethod(hideMethod);
            topBudgetTv.setTransformationMethod(hideMethod);
            topSurplusTv.setTransformationMethod(hideMethod);
            topShowIv.setImageResource(R.drawable.eye_open);
        }
        settingController.setShowAmount(isShow);
    }

}