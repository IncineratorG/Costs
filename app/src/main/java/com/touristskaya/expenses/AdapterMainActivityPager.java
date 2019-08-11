package com.touristskaya.expenses;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class AdapterMainActivityPager extends FragmentStatePagerAdapter {
    private int numberOfTabs;
    private List<Fragment> fragmentsList;

    public AdapterMainActivityPager(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.numberOfTabs = numberOfTabs;

        fragmentsList = new ArrayList<>(3);
        fragmentsList.add(new FragmentCurrentMonthScreen());
        fragmentsList.add(new FragmentLastEnteredValuesScreen());
        fragmentsList.add(new FragmentStatisticMainScreen());
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentsList.get(position);
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }

    // ================= !!Переделать!! ===================
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    // ====================================================

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }
}
