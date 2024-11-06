package com.example.moneymanager.view.interfaces;

import com.example.moneymanager.model.bean.TypeBean;
import java.util.List;

/**
 * 类型视图接口
 * 负责类型管理的视图交互
 */
public interface TypeView extends BaseView {
    /**
     * 显示类型列表
     * @param types 类型列表
     */
    void showTypes(List<TypeBean> types);

    /**
     * 刷新类型列表
     * @param kind 类型（0=支出，1=收入）
     */
    void refreshTypeList(int kind);
}