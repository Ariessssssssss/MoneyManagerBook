package com.example.moneymanager.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class StartPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragmentList = new ArrayList<>();

    public StartPagerAdapter(@NonNull FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }
    @Override
    public int getCount() {
        return fragmentList.size();
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }


}
