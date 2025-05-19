package com.example.moneymanager.frag_start;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneymanager.LoginActivity;
import com.example.moneymanager.MainActivity;
import com.example.moneymanager.R;

public class BaseStartFragment extends Fragment implements View.OnClickListener{
    TextView textView;
    ImageView imageView;
    Button startBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        textView = view.findViewById(R.id.start_frag_tv);
        imageView = view.findViewById(R.id.start_frag_iv);
        startBtn = view.findViewById(R.id.start_frag_btn);
        startBtn.setOnClickListener(this);
        initView(view);
        return view;
    }

    public void initView(View view) {
        textView.setText(R.string.start1_tv);
        imageView.setImageResource(R.drawable.start_1);
        startBtn.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_frag_btn) {
            Intent intent ;
            intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }
    }
}