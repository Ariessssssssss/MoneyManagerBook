package com.example.moneymanager.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;

import com.example.moneymanager.R;

public class RemarkDialogUtil extends Dialog implements View.OnClickListener {
    EditText editText;
    Button cancelBtn,ensureBtn;
    public interface OnEnsureListener{
        public void onEnsure();
    }
    private OnEnsureListener onEnsureListener;
    public void setOnEnsureListener(OnEnsureListener onEnsureListener) {
        this.onEnsureListener = onEnsureListener;
    }

    public RemarkDialogUtil(@NonNull Context context) {
        super(context);
    }

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_remark);
        editText = findViewById(R.id.dialog_remark_et);
        cancelBtn = findViewById(R.id.dialog_remark_btn_cancel);
        ensureBtn = findViewById(R.id.dialog_remark_btn_ensure);
        cancelBtn.setOnClickListener(this);
        ensureBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_remark_btn_cancel) {
            cancel();
        }
        else {
            if (onEnsureListener != null) {
                onEnsureListener.onEnsure();
            }
        }

    }

    public String getEditText(){
        return editText.getText().toString().trim();
    }

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
