package com.example.moneymanager.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.moneymanager.R;

public class BudgetDialogUtil extends Dialog implements View.OnClickListener {
    ImageView cancelIv;
    EditText budgetEt;
    Button ensureBtn;

    public interface OnEnsureListener{
        public void onEnsure(float budget);
    }
    private OnEnsureListener onEnsureListener;

    public void setOnEnsureListener(OnEnsureListener onEnsureListener) {
        this.onEnsureListener = onEnsureListener;
    }

    public BudgetDialogUtil(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_budget);
        cancelIv = findViewById(R.id.dialog_budget_iv);
        budgetEt = findViewById(R.id.dialog_budget_et);
        ensureBtn = findViewById(R.id.dialog_budget_btn);
        cancelIv.setOnClickListener(this);
        ensureBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_budget_iv) {
            cancel();
        }
        if (v.getId() == R.id.dialog_budget_btn) {
            String data = budgetEt.getText().toString();
            if (TextUtils.isEmpty(data)) {
                Toast.makeText(getContext(), "输入数据不能为空！", Toast.LENGTH_SHORT).show();
                return;
            }
            float budget = Float.parseFloat(data);
            if (budget <= 0) {
                Toast.makeText(getContext(), "预算必须大于0！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (onEnsureListener != null) {
                onEnsureListener.onEnsure(budget);
            }
            cancel();
        }

    }

    //设置Dialog的尺寸与屏幕尺寸一致
    public void setDialogSize(){
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        Display defaultDisplay = window.getWindowManager().getDefaultDisplay();
        wlp.width = (int) (defaultDisplay.getWidth());
        wlp.gravity = Gravity.BOTTOM;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setAttributes(wlp);
        handler.sendEmptyMessageDelayed(1,150);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == 1) {
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null) {
                    inputMethodManager.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }

        }
    };
}
