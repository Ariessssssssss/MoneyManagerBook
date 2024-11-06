package com.example.moneymanager.controller;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.moneymanager.model.bean.DetailsBean;
import com.example.moneymanager.model.bean.TypeBean;
import com.example.moneymanager.model.service.DetailsService;
import com.example.moneymanager.model.service.TypeService;
import com.example.moneymanager.view.interfaces.RecordView;
import net.sqlcipher.Cursor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 记录控制器
 * 负责处理记账记录相关的业务逻辑，作为View和Model的桥梁
 */
public class DetailsController {
    private static DetailsService detailsService;
    private TypeService typeService;
    private RecordView recordView;
    private Context context;

    /**
     * 用于需要视图交互的场景
     * @param recordView 记录视图接口
     */
    public DetailsController(RecordView recordView) {
        this.recordView = recordView;
        this.context = (Context) recordView;
        this.detailsService = new DetailsService(context);
        this.typeService = new TypeService(context);
    }

    /**
     * 用于不需要视图交互的场景
     * @param context 上下文
     */
    public DetailsController(Context context) {
        this.context = context.getApplicationContext();
        this.detailsService = new DetailsService(context);
        this.typeService = new TypeService(context);
    }

    /**
     * 获取记录类型列表
     * @param kind 类型（0=支出，1=收入）
     */
    public void loadTypes(int kind) {
        List<TypeBean> types = typeService.getTypeList(kind);
        if (recordView != null) {
            recordView.showTypes(types);
        }
    }

    /**
     * 获取记录类型列表
     * @param kind 类型（0=支出，1=收入）
     * @return 类型列表
     */
    public List<TypeBean> getTypeList(int kind) {
        return typeService.getTypeList(kind);
    }

    /**
     * 保存记录
     * @param detailsBean 记录数据
     */
    public void saveRecord(DetailsBean detailsBean) {
        if (detailsBean.getMoney() <= 0) {
            if (recordView != null) {
                recordView.showMessage("金额必须大于0");
            }
            return;
        }

        boolean result;
        if (detailsBean.getId() > 0) {
            // 更新
            result = detailsService.updateRecord(detailsBean.getId(), detailsBean);
            if (recordView != null) {
                if (result) {
                    recordView.showMessage("修改成功");
                    recordView.finishRecord();
                } else {
                    recordView.showMessage("修改失败");
                }
            }
        } else {
            // 新增
            result = detailsService.addRecord(detailsBean);
            if (recordView != null) {
                if (result) {
                    recordView.finishRecord();
                } else {
                    recordView.showMessage("保存失败");
                }
            }
        }
    }

    /**
     * 添加一条记录
     * @param bean 记录数据
     * @return 是否添加成功
     */
    public boolean addRecord(DetailsBean bean) {
        return detailsService.addRecord(bean);
    }

    /**
     * 更新一条记录
     * @param id 记录ID
     * @param bean 记录数据
     * @return 是否更新成功
     */
    public boolean updateRecord(int id, DetailsBean bean) {
        return detailsService.updateRecord(id, bean);
    }

    /**
     * 删除一条记录
     * @param id 记录ID
     * @return 是否删除成功
     */
    public boolean deleteRecord(int id) {
        return detailsService.deleteRecord(id);
    }

    /**
     * 获取指定日期的记录列表
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @return 记录列表
     */
    public List<DetailsBean> getDailyDetailsList(int year, int month, int day) {
        return detailsService.getDailyDetailsList(year, month, day);
    }

    /**
     * 获取指定月份的记录列表
     * @param year 年份
     * @param month 月份
     * @return 记录列表
     */
    public List<DetailsBean> getMonthlyDetailsList(int year, int month) {
        return detailsService.getMonthlyDetailsList(year, month);
    }
    /**
     * 获取指定年份的记录列表
     *
     * @return 记录列表
     */
    public static List<Integer> getYearDetailsList() {
        return detailsService.getYearDetailsList();
    }
    /**
     * 根据关键字搜索记录
     * @param keyword 关键字
     * @return 记录列表
     */
    public List<DetailsBean> searchRecords(String keyword) {
        return detailsService.searchRecords(keyword);
    }

    /**
     * 获取指定日期的总金额
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @param kind 类型（0=支出，1=收入）
     * @return 总金额
     */
    public float getDailySum(int year, int month, int day, int kind) {
        return detailsService.getDailySum(year, month, day, kind);
    }

    /**
     * 获取指定月份的总金额
     * @param year 年份
     * @param month 月份
     * @param kind 类型（0=支出，1=收入）
     * @return 总金额
     */
    public float getMonthlySum(int year, int month, int kind) {
        return detailsService.getMonthlySum(year, month, kind);
    }

    /**
     * 获取指定年份的总金额
     * @param year 年份
     * @param kind 类型（0=支出，1=收入）
     * @return 总金额
     */
    public float getYearlySum(int year, int kind) {
        return detailsService.getYearlySum(year, kind);
    }
    public int getMonthlyCountItem(int year,int month, int kind){
        return detailsService.getMonthlyCountItem(year,month,kind);
    }
    /**
     * 获取所有明细记录的游标
     * @return 数据库游标
     */
    public Cursor getAllDetailsCursor() {
        return detailsService.getAllDetailsCursor();
    }
    /**
     * 清空所有记录
     * @return 是否成功
     */
    public boolean clearAllRecords() {
        return detailsService.clearAllRecords();
    }

    /**
     * 创建一条新记录
     * @param typename 类型名称
     * @param sImageId 类型图标
     * @param record 备注
     * @param money 金额
     * @param kind 收支类型（0=支出，1=收入）
     * @return 创建的记录
     */
    public DetailsBean createRecord(String typename, int sImageId, String record, float money, int kind) {
        // 获取当前日期
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        String time = sdf.format(date);

        // 获取年月日
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int year = calendar.get(java.util.Calendar.YEAR);
        int month = calendar.get(java.util.Calendar.MONTH) + 1;
        int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);

        // 创建记录对象
        DetailsBean bean = new DetailsBean(0, typename, sImageId, record, money, time, year, month, day, kind);

        // 保存到数据库
        if (addRecord(bean)) {
            return bean;
        }
        return null;
    }

    /**
     * 获取首页显示数据
     * @param year 年份
     * @param month 月份
     * @param day 日期
     * @param prefs SharedPreferences实例，用于获取预算值
     * @return 包含各种统计数据的Map
     */
    public Map<String, Object> getMainPageData(int year, int month, int day, SharedPreferences prefs) {
        Map<String, Object> result = new HashMap<>();

        // 获取日期记录
        List<DetailsBean> dailyRecords = getDailyDetailsList(year, month, day);

        // 获取统计数据
        float incomeOneMonth = getMonthlySum(year, month, 1);
        float expenseOneMonth = getMonthlySum(year, month, 0);
        float incomeOneDay = getDailySum(year, month, day, 1);
        float expenseOneDay = getDailySum(year, month, day, 0);

        // 获取预算相关数据
        float budget = prefs.getFloat("budget", 0);
        float remainingBudget = budget - expenseOneMonth;
        if (remainingBudget < 0) remainingBudget = 0;

        // 设置结果
        result.put("dailyRecords", dailyRecords);
        result.put("incomeOneMonth", incomeOneMonth);
        result.put("expenseOneMonth", expenseOneMonth);
        result.put("incomeOneDay", incomeOneDay);
        result.put("expenseOneDay", expenseOneDay);
        result.put("budget", budget);
        result.put("remainingBudget", remainingBudget);

        return result;
    }

    /**
     * 分析消费模式
     * @param year 年份
     * @param month 月份
     * @return 消费分析结果
     */
    public DetailsService.ConsumptionAnalysis analyzeMonthlyConsumption(int year, int month) {
        return detailsService.analyzeMonthlyConsumption(year, month);
    }

    /**
     * 更新预算
     * @param budget 新预算值
     * @param prefs SharedPreferences实例
     * @return 是否成功
     */
    public boolean updateBudget(float budget, SharedPreferences prefs) {
        try {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat("budget", budget);
            return editor.commit();
        } catch (Exception e) {
            return false;
        }
    }
}