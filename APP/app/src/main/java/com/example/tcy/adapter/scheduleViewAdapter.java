package com.example.tcy.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.example.tcy.view.*;
import java.util.ArrayList;
import java.util.List;

public class scheduleViewAdapter extends FragmentPagerAdapter {
    List<Fragment> fragmentList=new ArrayList<>();
    List<String> nameList=new ArrayList<>();
    public scheduleViewAdapter(FragmentManager fm){
        super(fm);
        fragmentList.add(new ScheduleDayoneView());
        fragmentList.add(new ScheduleMainVenueView());
        fragmentList.add(new ScheduleSessionVenue0View());
        fragmentList.add(new ScheduleSessionVenue1View());
        nameList.add("6.9");
        nameList.add("6.10主会场");
        nameList.add("6.10一号分会场");
        nameList.add("6.10二号分会场");
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return nameList.get(position);
    }
}
