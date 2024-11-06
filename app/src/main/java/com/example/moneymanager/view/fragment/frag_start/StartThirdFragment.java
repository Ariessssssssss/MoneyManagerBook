package com.example.moneymanager.view.fragment.frag_start;

import com.example.moneymanager.R;

public class StartThirdFragment extends BaseStartFragment {
    @Override
    protected void initView() {
        titleView.setText("目标规划");
        textView.setText("设定储蓄目标，实现财务自由");
        imageView.setImageResource(R.drawable.start_3);
    }
}