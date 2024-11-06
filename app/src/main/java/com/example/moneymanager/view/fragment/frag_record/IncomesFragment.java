package com.example.moneymanager.view.fragment.frag_record;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.moneymanager.R;
import com.example.moneymanager.model.dao.DBManager;
import com.example.moneymanager.model.bean.DetailsBean;
import com.example.moneymanager.model.bean.TypeBean;

import java.util.List;
import java.util.Objects;

public class IncomesFragment extends BaseRecordFragment {

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (bundle != null) {
            detailsBean = new DetailsBean();
            detailsBean.setTypename(bundle.getString("typename"));
            detailsBean.setsImageId(bundle.getInt("sImageId"));
        }
        else {
            detailsBean = new DetailsBean();
            detailsBean.setTypename("工资");
            detailsBean.setsImageId(R.mipmap.wages_blue);
        }
    }

    @Override
    public void saveDetailsToDB() {
        detailsBean.setKind(1);
        if (bundle != null) {
            detailsBean.setRecord(noteTv.getText().toString());
            if (timeTv.getText().toString().equals(bundle.getString("time"))){
                detailsBean.setTime(timeTv.getText().toString());
                detailsBean.setYear(bundle.getInt("year"));
                detailsBean.setMonth(bundle.getInt("month"));
                detailsBean.setDay(bundle.getInt("day"));
            }
            detailsController.updateRecord(bundle.getInt("id"),detailsBean);
            Toast.makeText(getActivity(),"修改成功！",Toast.LENGTH_SHORT).show();
        }else {
            detailsController.addRecord(detailsBean);
        }
    }

    @Override
    public void initData() {
        super.initData();
        if (bundle.getInt("kind") == 1) {
            typeIv.setImageResource(bundle.getInt("sImageId"));
            typeTv.setText(bundle.getString("typename"));
            noteTv.setText(bundle.getString("record"));
            timeTv.setText(bundle.getString("time"));
            moneyEdit.setText(((int) bundle.getFloat("money"))+"");
        }

    }

    public void loadDataToGv() {
        super.loadDataToGv();
        List<TypeBean> inList = detailsController.getTypeList(1);
        typeList.addAll(inList);
        adapter.notifyDataSetChanged();
        if (bundle != null) {
            detailsBean = new DetailsBean();
            detailsBean.setTypename(bundle.getString("typename"));
            detailsBean.setsImageId(bundle.getInt("sImageId"));
            if (bundle.getInt("kind") == 1) {
                if (bundle.getString("typename") != null) {
                    switch (Objects.requireNonNull(bundle.getString("typename"))) {
                        case "工资":
                            adapter.setSelectedIndex(0);
                            break;
                        case "奖金":
                            adapter.setSelectedIndex(1);
                            break;
                        case "兼职":
                            adapter.setSelectedIndex(2);
                            break;
                        case "理财":
                            adapter.setSelectedIndex(3);
                            break;
                        case "其他":
                            adapter.setSelectedIndex(4);
                            break;
                    }
                }
            }
        }
        else {
            detailsBean = new DetailsBean();
            detailsBean.setTypename("工资");
            detailsBean.setsImageId(R.mipmap.wages_blue);
        }
    }
}