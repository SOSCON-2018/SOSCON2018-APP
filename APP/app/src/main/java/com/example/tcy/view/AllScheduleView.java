package com.example.tcy.view;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.example.tcy.adapter.*;
import com.example.tcy.app.R;
import com.example.tcy.app.MainActivity;
public class AllScheduleView extends BaseFragment {
    static public AllScheduleView mSchedule;
    public FragmentPagerAdapter fragmentPagerAdapter;
    public ViewPager viewPager;
    public TabLayout tabLayout;
    long lastTime;
    @Override
    public int GetLayoutId() {
        return R.layout.schedule_view;
    }

    @Override
    protected void PreGetData() {

    }

    @Override
    protected void InitView() {
        mSchedule=this;
        tabLayout=mview.findViewById(R.id.schedule_tab);
        viewPager=mview.findViewById(R.id.schedule_view_pager);
        fragmentPagerAdapter=new scheduleViewAdapter(getChildFragmentManager());
        viewPager.setAdapter(fragmentPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                long curTime=System.currentTimeMillis();
                if(curTime-lastTime<1000)
                    MainActivity.boringCount++;
                lastTime=curTime;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
