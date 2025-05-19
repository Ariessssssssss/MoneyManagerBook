package com.example.moneymanager.frag_record;

import android.annotation.SuppressLint;
import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.moneymanager.R;
import com.example.moneymanager.adapter.TypeBaseAdapter;
import com.example.moneymanager.database.DBManager;
import com.example.moneymanager.database.DetailsBean;
import com.example.moneymanager.database.TypeBean;
import com.example.moneymanager.utils.KeyBoardUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PayoutFragment extends BaseRecordFragment {

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (bundle != null) {
            detailsBean = new DetailsBean();
            detailsBean.setTypename(bundle.getString("typename"));
            detailsBean.setsImageId(bundle.getInt("sImageId"));
        }
        else {
            detailsBean = new DetailsBean();
            detailsBean.setTypename("餐饮");
            detailsBean.setsImageId(R.mipmap.food_red);
        }
    }

    @Override
    public void saveDetailsToDB() {
        detailsBean.setKind(0);
        if (bundle != null) {
            detailsBean.setRecord(noteTv.getText().toString());
            if (timeTv.getText().toString().equals(bundle.getString("time"))){
                detailsBean.setTime(timeTv.getText().toString());
                detailsBean.setYear(bundle.getInt("year"));
                detailsBean.setMonth(bundle.getInt("month"));
                detailsBean.setDay(bundle.getInt("day"));
            }
            DBManager.updateItemToDeatilstb(bundle.getInt("id"),detailsBean);
            Toast.makeText(getActivity(),"修改成功！",Toast.LENGTH_SHORT).show();
        }else {
            DBManager.insertItemToDetailstb(detailsBean);
        }

    }
    @Override
    public void initData() {
        super.initData();
        if (bundle.getInt("kind") == 0) {
            typeIv.setImageResource(bundle.getInt("sImageId"));
            typeTv.setText(bundle.getString("typename"));
            noteTv.setText(bundle.getString("record"));
            timeTv.setText(bundle.getString("time"));
            moneyEdit.setText((bundle.getFloat("money"))+"");
        }

    }

    @Override
    public void loadDataToGv() {
        super.loadDataToGv();
        List<TypeBean> outList = DBManager.getTypeList(0);
        typeList.addAll(outList);
        adapter.notifyDataSetChanged();
        if (bundle != null) {
            detailsBean = new DetailsBean();
            detailsBean.setTypename(bundle.getString("typename"));
            detailsBean.setsImageId(bundle.getInt("sImageId"));
            if (bundle.getInt("kind") == 0){
                if (bundle.getString("typename") != null) {
                    switch (Objects.requireNonNull(bundle.getString("typename"))) {
                        case "餐饮":
                            adapter.setSelectedIndex(0);
                            break;
                        case "交通":
                            adapter.setSelectedIndex(1);
                            break;
                        case "娱乐":
                            adapter.setSelectedIndex(2);
                            break;
                        case "服饰":
                            adapter.setSelectedIndex(3);
                            break;
                        case "日用品":
                            adapter.setSelectedIndex(4);
                            break;
                        case "住房":
                            adapter.setSelectedIndex(5);
                            break;
                        case "医疗":
                            adapter.setSelectedIndex(6);
                            break;
                        case "教育":
                            adapter.setSelectedIndex(7);
                            break;
                        case "投资":
                            adapter.setSelectedIndex(8);
                            break;
                        case "其他":
                            adapter.setSelectedIndex(9);
                            break;
                    }
                }
            }
        }else {
            detailsBean = new DetailsBean();
            detailsBean.setTypename("餐饮");
            detailsBean.setsImageId(R.mipmap.food_red);
        }

    }
}