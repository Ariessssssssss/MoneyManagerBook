package com.example.moneymanager.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AIAnalysisServiceUtil {
    private static final String TAG = "AIAnalysisService";
    private static final String API_KEY = "AIzaSyCRiEV-9OUOcbbql-Gsg8hqBBPaOihFsME";
    private static AIAnalysisServiceUtil instance;
    private final GenerativeModel model;
    private final GenerativeModelFutures modelFutures;
    private final Context context;

    private AIAnalysisServiceUtil(Context context) {
        // 保存上下文
        this.context = context.getApplicationContext();
        GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
        configBuilder.temperature = 0.8f;
        configBuilder.maxOutputTokens = 500;
        GenerationConfig generationConfig = configBuilder.build();
        // 初始化Gemini模型
        model = new GenerativeModel(
                "gemini-1.5-flash",  // 使用最新的模型版本
                API_KEY,
                generationConfig);
        // 初始化供Future使用的模型实例
        modelFutures = GenerativeModelFutures.from(model);
    }

    public static synchronized AIAnalysisServiceUtil getInstance(Context context) {
        if (instance == null) {
            instance = new AIAnalysisServiceUtil(context);
        }
        return instance;
    }

    /**
     * 生成日常账单分析报告（近7天）
     */
    public void generateDailyAnalysisReport(Map<String, Float> expenseMap, Map<String, Float> incomeMap,
                                            float totalExpense, float totalIncome,
                                            AIAnalysisCallback callback) {
        generateAnalysisReport("日常", expenseMap, incomeMap, totalExpense, totalIncome, null, null, callback);
    }

    /**
     * 生成月度账单分析报告
     */
    public void generateMonthlyAnalysisReport(Map<String, Float> expenseMap, Map<String, Float> incomeMap,
                                              float totalExpense, float totalIncome,
                                              AIAnalysisCallback callback) {
        generateAnalysisReport("月度", expenseMap, incomeMap, totalExpense, totalIncome, null, null, callback);
    }

    /**
     * 生成年度账单分析报告
     */
    public void generateYearlyAnalysisReport(Map<String, Float> expenseMap, Map<String, Float> incomeMap,
                                             float totalExpense, float totalIncome,
                                             List<Float> monthlyExpenses, List<Float> monthlyIncomes,
                                             AIAnalysisCallback callback) {
        generateAnalysisReport("年度", expenseMap, incomeMap, totalExpense, totalIncome,
                monthlyExpenses, monthlyIncomes, callback);
    }

    private void generateAnalysisReport(String timeRange, Map<String, Float> expenseMap,
                                        Map<String, Float> incomeMap, float totalExpense,
                                        float totalIncome, List<Float> monthlyExpenses,
                                        List<Float> monthlyIncomes, AIAnalysisCallback callback) {
        // 检查网络连接
        if (!isNetworkAvailable()) {
            callback.onError("网络连接不可用，请检查您的网络设置");
            return;
        }

        // 检查数据是否为空
        if ((expenseMap == null || expenseMap.isEmpty()) && (incomeMap == null || incomeMap.isEmpty())) {
            callback.onError("没有足够的数据生成分析报告");
            return;
        }

        // 构建提示词
        StringBuilder prompt = new StringBuilder();
        prompt.append("请作为财务分析师，对以下").append(timeRange).append("账单数据进行分析并生成简短财务报告：\n\n");

        // 添加总收支数据 - 修复了Locale问题
        Locale locale = Locale.getDefault();
        prompt.append("总收入: ¥").append(String.format(locale, "%.2f", totalIncome)).append("\n");
        prompt.append("总支出: ¥").append(String.format(locale, "%.2f", totalExpense)).append("\n");
        prompt.append("收支差额: ¥").append(String.format(locale, "%.2f", totalIncome - totalExpense)).append("\n\n");

        // 添加支出分类数据 - 修复了Locale问题
        prompt.append("支出分类明细:\n");
        for (Map.Entry<String, Float> entry : expenseMap.entrySet()) {
            prompt.append("- ").append(entry.getKey()).append(": ¥")
                    .append(String.format(locale, "%.2f", entry.getValue()));
            if (totalExpense > 0) {
                prompt.append(" (").append(String.format(locale, "%.1f", entry.getValue() / totalExpense * 100)).append("%)");
            }
            prompt.append("\n");
        }
        prompt.append("\n");

        // 添加收入分类数据 - 修复了Locale问题
        prompt.append("收入分类明细:\n");
        for (Map.Entry<String, Float> entry : incomeMap.entrySet()) {
            prompt.append("- ").append(entry.getKey()).append(": ¥")
                    .append(String.format(locale, "%.2f", entry.getValue()));
            if (totalIncome > 0) {
                prompt.append(" (").append(String.format(locale, "%.1f", entry.getValue() / totalIncome * 100)).append("%)");
            }
            prompt.append("\n");
        }

        // 如果是年度报表，添加月度趋势数据 - 修复了Locale问题
        if (monthlyExpenses != null && monthlyIncomes != null && !monthlyExpenses.isEmpty() && !monthlyIncomes.isEmpty()) {
            prompt.append("\n月度趋势数据:\n");
            for (int i = 0; i < Math.min(monthlyExpenses.size(), monthlyIncomes.size()); i++) {
                prompt.append("第").append(i + 1).append("月 - 收入: ¥").append(String.format(locale, "%.2f", monthlyIncomes.get(i)))
                        .append(", 支出: ¥").append(String.format(locale, "%.2f", monthlyExpenses.get(i))).append("\n");
            }
        }

        // 添加分析要求
        prompt.append("\n请提供以下信息：\n");
        prompt.append("1. 总体财务状况概述（1-2句）\n");
        prompt.append("2. 支出模式分析（1-2句）\n");
        prompt.append("3. 收入来源分析（1-2句）\n");
        prompt.append("4. 财务健康建议（1-2条具体可行的建议）\n");
        prompt.append("\n请保持分析简洁、专业，总字数在300字以内。不要出现提示词中的步骤编号，直接给出流畅的分析文本。");

        // 创建请求内容
        final String promptText = prompt.toString();
        Log.d(TAG, "生成分析报告的提示词: " + promptText);

        try {
            // 创建Content实例 - 修正方法调用问题
            Content.Builder contentBuilder = new Content.Builder();
            contentBuilder.addText(promptText);
            Content content = contentBuilder.build();

            // 使用正确的GenerativeModelFutures API
            ListenableFuture<GenerateContentResponse> future = modelFutures.generateContent(content);

            // 添加回调
            Futures.addCallback(future, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    if (result == null || result.getText() == null || result.getText().isEmpty()) {
                        Log.e(TAG, "API返回为空");
                        callback.onError("生成分析报告失败：API返回为空");
                        return;
                    }

                    String responseText = result.getText();
                    Log.d(TAG, "AI响应: " + responseText);

                    if (responseText.isEmpty()) {
                        callback.onError("分析报告生成失败，未收到有效响应");
                    } else {
                        callback.onAnalysisReady(responseText);
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    Log.e(TAG, "AI分析生成失败", t);
                    callback.onError("生成分析报告时出错: " + t.getMessage());
                }
            }, MoreExecutors.directExecutor());
        } catch (Exception e) {
            Log.e(TAG, "创建AI请求时出错", e);
            callback.onError("创建AI请求时出错: " + e.getMessage());
        }
    }

    // 检查网络可用性
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // 回调接口
    public interface AIAnalysisCallback {
        void onAnalysisReady(String analysisText);
        void onError(String errorMessage);
    }
}