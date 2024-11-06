package com.example.moneymanager.frag_start;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.moneymanager.MainActivity;
import com.example.moneymanager.R;
import com.example.moneymanager.StartActivity;

public class StartThirdFragment extends BaseStartFragment {
    @Override
    public void initView(View view) {
        textView.setText(R.string.start3_tv);
        imageView.setImageResource(R.drawable.start_3);
        startBtn.setVisibility(View.VISIBLE);
    }
}