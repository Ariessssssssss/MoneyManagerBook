package com.example.moneymanager.view.dialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.moneymanager.R;
import com.example.moneymanager.view.adapter.RecordPagerAdapter;
import com.example.moneymanager.view.fragment.frag_record.IncomesFragment;
import com.example.moneymanager.view.fragment.frag_record.PayoutFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ChangeDialogUtil extends DialogFragment implements View.OnClickListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ImageView imageView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //设置布局相关
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE); //不显示弹框title

        //设置默认布局位置
        Window window = this.getDialog().getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0); //去掉dialog默认的padding
        window.setBackgroundDrawable(new ColorDrawable());

        View view = inflater.inflate(R.layout.dialog_change,container,false);
        tabLayout = view.findViewById(R.id.dialog_change_tabs);
        viewPager = view.findViewById(R.id.dialog_change_vp);
        imageView = view.findViewById(R.id.dialog_change_iv_back);
        return view;
    }

    private void initPager() {
        //初始化ViewPager页面的集合
        List<Fragment> fragmentList = new ArrayList<>();
        PayoutFragment outFrag = new PayoutFragment();//支出
        IncomesFragment inFrag = new IncomesFragment();//收入
        fragmentList.add(outFrag);
        fragmentList.add(inFrag);

        //创建适配器
        RecordPagerAdapter pagerAdapter = new RecordPagerAdapter( getChildFragmentManager(),fragmentList);

        //设置适配器
        viewPager.setAdapter(pagerAdapter);
        //将TabLayout和ViewPager进行关联
        tabLayout.setupWithViewPager(viewPager);
    }

    public void setDialogSize(){
        Window window = this.getDialog().getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        Display defaultDisplay = window.getWindowManager().getDefaultDisplay();
        wlp.width = (int) (defaultDisplay.getWidth());
        wlp.gravity = Gravity.TOP;
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setAttributes(wlp);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.dialog_change_iv_back) {

        }
    }

    public void show() {

    }
}
