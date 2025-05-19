package com.example.moneymanager.frag_start;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.moneymanager.R;


public class StartSecondFragment extends BaseStartFragment {

    @Override
    public void initView(View view) {
        textView.setText(R.string.start2_tv);
        imageView.setImageResource(R.drawable.start_2);
    }
}