package com.example.moneymanager.view.fragment.frag_start;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneymanager.R;

public class BaseStartFragment extends Fragment{
    TextView titleView,textView;
    ImageView imageView;
    Button startBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        titleView = view.findViewById(R.id.start_frag_title);
        textView = view.findViewById(R.id.start_frag_tv);
        imageView = view.findViewById(R.id.start_frag_iv);
        initView();
        return view;
    }

    protected void initView() {

    }

}