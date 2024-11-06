package com.example.moneymanager.view.fragment.frag_start;

import com.example.moneymanager.R;


public class StartSecondFragment extends BaseStartFragment {

    @Override
    protected void initView() {
        titleView.setText("智能分析");
        textView.setText("直观的收支分析，让您的财务一目了然");
        imageView.setImageResource(R.drawable.start_2);
    }
}