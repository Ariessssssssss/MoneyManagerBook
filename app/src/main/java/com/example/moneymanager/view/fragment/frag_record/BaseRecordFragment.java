package com.example.moneymanager.view.fragment.frag_record;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.moneymanager.controller.DetailsController;
import com.example.moneymanager.view.activity.MainActivity;
import com.example.moneymanager.R;
import com.example.moneymanager.view.adapter.TypeBaseAdapter;
import com.example.moneymanager.model.bean.DetailsBean;
import com.example.moneymanager.model.bean.TypeBean;
import com.example.moneymanager.utils.KeyBoardUtil;
import com.example.moneymanager.view.dialog.RemarkDialogUtil;
import com.example.moneymanager.view.dialog.SelectCalenderUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class BaseRecordFragment extends Fragment implements View.OnClickListener{

    KeyboardView keyboardView;
    RelativeLayout bottomLayout;
    //显示自定义键盘
    private KeyBoardUtil boardUtil;
    EditText moneyEdit;
    ImageView typeIv;
    TextView typeTv,noteTv,timeTv;//noteTv 备注
    GridView typeGv;
    Context context;
    List<TypeBean> typeList;
    TypeBaseAdapter adapter;
    DetailsBean detailsBean;
    Bundle bundle;
    DetailsController detailsController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailsController = new DetailsController(getActivity());
        detailsBean = new DetailsBean();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payout, container, false);
        bundle = getArguments();
        initView(view);
        setGVListener();
        loadDataToGv();
        // 在这里设置初始图标和文字
        if (detailsBean.getTypename() != null) {
            typeTv.setText(detailsBean.getTypename());
            typeIv.setImageResource(detailsBean.getsImageId());
        }
        setInitTime();
        if (bundle != null) {
            initData();
        }
        return view;
    }

    public void initData() {
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initView(View view) {
        keyboardView = view.findViewById(R.id.frag_record_keyboard);
        bottomLayout = view.findViewById(R.id.frag_record_rl_bottom);
        moneyEdit = view.findViewById(R.id.frag_record_et_money);
        typeIv = view.findViewById(R.id.frag_record_iv);
        typeTv = view.findViewById(R.id.frag_record_tv_type);
        noteTv = view.findViewById(R.id.frag_record_tv_note);
        timeTv = view.findViewById(R.id.frag_record_tv_time);
        typeGv = view.findViewById(R.id.frag_record_gv);
        context = getActivity();
        noteTv.setOnClickListener(this);
        timeTv.setOnClickListener(this);
        // 延迟初始化键盘，确保视图已完全加载
        view.post(() -> {
            boardUtil = new KeyBoardUtil(context, keyboardView, moneyEdit, 1);
            boardUtil.setOnClickDone(() -> {
                String moneyStr = moneyEdit.getText().toString();
                if (moneyStr.isEmpty()) {
                    getActivity().finish();
                    return;
                }
                Float money = Float.parseFloat(moneyStr);
                if (money <= 0) {
                    // 处理金额为0或负数的情况
                } else {
                    detailsBean.setMoney(money);
                    if (bundle != null) {
                        saveDetailsToDB();
                        getActivity().finish();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        saveDetailsToDB();
                        getActivity().finish();
                    }
                }
            });
            // 显示键盘
            boardUtil.showKeyboard();
        });

    }

    public void loadDataToGv() {
        typeList = new ArrayList<>();
        adapter = new TypeBaseAdapter(getContext(), typeList);
        typeGv.setAdapter(adapter);
    }

    private void setGVListener() {
        typeGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.selectPos = position;
                adapter.notifyDataSetChanged();
                TypeBean typeBean = typeList.get(position);
                String typename = typeBean.getTypename();
                typeTv.setText(typename);
                detailsBean.setTypename(typename);
                int sImageId = typeBean.getsImageId();
                typeIv.setImageResource(sImageId);
                detailsBean.setsImageId(sImageId);
            }
        });
    }
    /*获取当前时间*/
    private void setInitTime() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = simpleDateFormat.format(date);
        timeTv.setText(time);
        detailsBean.setTime(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH)+1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        detailsBean.setYear(year);
        detailsBean.setMonth(month);
        detailsBean.setDay(day);
    }

    public void saveDetailsToDB() {

    }

    @Override
    public void onClick(View v) {
        // 使用KeyBoardUtil的hideKeyboard方法
        if (v.getId() ==R.id.frag_record_tv_note) {
            //boardUtil.hideKeyboard();
            // 延迟显示备注对话框，确保键盘动画完成
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showRemarkDialog();
                }
            }, 200);
        }
        if (v.getId() ==R.id.frag_record_tv_time) {
            showCalenderDialog();
        }
    }

    private void showCalenderDialog() {
        SelectCalenderUtil calenderUtil = new SelectCalenderUtil(getContext());
        calenderUtil.show();
        calenderUtil.setOnEnsureListener(new SelectCalenderUtil.OnEnsureListener() {
            @Override
            public void onEnsure(String time,int year, int month, int day) {
                timeTv.setText(time);
                detailsBean.setTime(time);
                detailsBean.setYear(year);
                detailsBean.setMonth(month);
                detailsBean.setDay(day);
            }
        });

    }

    private void showRemarkDialog() {
        RemarkDialogUtil dialogUtil = new RemarkDialogUtil(getContext());
        dialogUtil.show();
        dialogUtil.setDialogSize();

        dialogUtil.setOnEnsureListener(new RemarkDialogUtil.OnEnsureListener() {
            @Override
            public void onEnsure() {
                String msg = dialogUtil.getEditText();
                if (!TextUtils.isEmpty(msg)) {
                    noteTv.setText(msg);
                    detailsBean.setRecord(msg);
                }
                dialogUtil.cancel();
            }
        });
    }
}