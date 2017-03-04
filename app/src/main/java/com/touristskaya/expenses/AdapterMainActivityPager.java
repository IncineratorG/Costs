package com.touristskaya.expenses;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;


public class AdapterMainActivityPager extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public AdapterMainActivityPager(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FragmentCurrentMonthScreen tab1 = new FragmentCurrentMonthScreen();
                return tab1;
            case 1:
                FragmentLastEnteredValuesScreen tab2 = new FragmentLastEnteredValuesScreen();
                return tab2;
            case 2:
                FragmentStatisticMainScreen tab3 = new FragmentStatisticMainScreen();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
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
