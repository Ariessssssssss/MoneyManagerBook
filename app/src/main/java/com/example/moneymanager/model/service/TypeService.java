package com.example.moneymanager.model.service;

import android.content.Context;

import com.example.moneymanager.model.bean.TypeBean;
import com.example.moneymanager.model.dao.DBManager;
import com.example.moneymanager.model.dao.TypeDAO;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.List;

/**
 * 类型服务类
 * 提供类型相关的业务逻辑处理
 */
public class TypeService {
    private TypeDAO typeDAO;

    public TypeService(Context context) {
        SQLiteDatabase db = DBManager.getDatabase();
        this.typeDAO = new TypeDAO(db);
    }

    /**
     * 获取类型列表
     * @param kind 类型（0=支出，1=收入）
     * @return 类型列表
     */
    public List<TypeBean> getTypeList(int kind) {
        return typeDAO.getTypeList(kind);
    }

    /**
     * 添加类型
     * @param bean 类型数据
     * @return 是否添加成功
     */
    public boolean addType(TypeBean bean) {
        return typeDAO.addType(bean);
    }

    /**
     * 更新类型
     * @param bean 类型数据
     * @return 是否更新成功
     */
    public boolean updateType(TypeBean bean) {
        return typeDAO.updateType(bean);
    }

    /**
     * 删除类型
     * @param id 类型ID
     * @return 是否删除成功
     */
    public boolean deleteType(int id) {
        return typeDAO.deleteType(id);
    }
}