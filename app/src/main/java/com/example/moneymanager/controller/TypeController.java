package com.example.moneymanager.controller;

import android.content.Context;

import com.example.moneymanager.model.bean.TypeBean;
import com.example.moneymanager.model.service.TypeService;
import com.example.moneymanager.view.interfaces.TypeView;

import java.util.List;

/**
 * 类型控制器
 * 负责处理类型相关的业务逻辑，作为View和Model的桥梁
 */
public class TypeController {
    private TypeService typeService;
    private TypeView typeView;

    /**
     * 用于需要视图交互的场景
     * @param typeView 类型视图接口
     */
    public TypeController(TypeView typeView) {
        this.typeView = typeView;
        Context context = (Context) typeView;
        this.typeService = new TypeService(context);
    }

    /**
     * 用于不需要视图交互的场景
     * @param context 上下文
     */
    public TypeController(Context context) {
        this.typeService = new TypeService(context);
    }

    /**
     * 加载类型列表
     * @param kind 类型（0=支出，1=收入）
     */
    public void loadTypes(int kind) {
        List<TypeBean> types = typeService.getTypeList(kind);
        if (typeView != null) {
            typeView.showTypes(types);
        }
    }

    /**
     * 添加类型
     * @param bean 类型数据
     */
    public void addType(TypeBean bean) {
        boolean result = typeService.addType(bean);
        if (typeView != null) {
            if (result) {
                typeView.showMessage("添加类型成功");
                typeView.refreshTypeList(bean.getKind());
            } else {
                typeView.showMessage("添加类型失败");
            }
        }
    }

    /**
     * 更新类型
     * @param bean 类型数据
     */
    public void updateType(TypeBean bean) {
        boolean result = typeService.updateType(bean);
        if (typeView != null) {
            if (result) {
                typeView.showMessage("更新类型成功");
                typeView.refreshTypeList(bean.getKind());
            } else {
                typeView.showMessage("更新类型失败");
            }
        }
    }

    /**
     * 删除类型
     * @param id 类型ID
     * @param kind 类型（0=支出，1=收入）
     */
    public void deleteType(int id, int kind) {
        boolean result = typeService.deleteType(id);
        if (typeView != null) {
            if (result) {
                typeView.showMessage("删除类型成功");
                typeView.refreshTypeList(kind);
            } else {
                typeView.showMessage("删除类型失败");
            }
        }
    }
}