package com.example.moneymanager.view.interfaces;

import com.example.moneymanager.model.bean.TypeBean;
import java.util.List;

public interface RecordView extends BaseView {
    void showTypes(List<TypeBean> types);
    void finishRecord();
    void selectType(TypeBean typeBean);
}