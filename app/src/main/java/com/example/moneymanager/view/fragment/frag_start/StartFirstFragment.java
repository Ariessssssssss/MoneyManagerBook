package com.example.moneymanager.view.fragment.frag_start;

import com.example.moneymanager.R;



public class StartFirstFragment extends BaseStartFragment {
    @Override
    protected void initView() {
        titleView.setText("轻松记账");
        textView.setText("简单快捷的记账方式，帮您轻松管理财务");
        imageView.setImageResource(R.drawable.start_1);
    }
}