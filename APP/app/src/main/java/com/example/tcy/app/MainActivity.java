package com.example.tcy.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.style.UpdateLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tcy.Data.News;
import com.example.tcy.widget.*;
import com.example.tcy.view.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    static public MainActivity mainActivity;
    private TextView mTextMessage;
    public NoSlideVpager homePager;
    private NewsView newsView;
    private AllScheduleView scheduleView;
    private PersonInfoView personInfoView;
    long lastTime=0;
    public static int boringCount;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    homePager.setCurrentItem(0,true);
                    return true;
                case R.id.navigation_dashboard:
                    homePager.setCurrentItem(1,true);
                    return true;
                case R.id.navigation_notifications:
                    homePager.setCurrentItem(2,true);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity=this;
        setContentView(R.layout.activity_main);
        homePager=(NoSlideVpager)findViewById(R.id.news_vp);
        mTextMessage = (TextView) findViewById(R.id.message);
        InitHome();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        boringCount=getSharedPreferences("cookie",MODE_PRIVATE).getInt("boringCount",0);
        lastTime=System.currentTimeMillis();
    }
    private void InitHome(){
        final List<Fragment> fragments=new ArrayList<>();
        newsView=new NewsView();
        scheduleView=new AllScheduleView();
        personInfoView=new PersonInfoView();
        fragments.add(newsView);
        fragments.add(scheduleView);
        fragments.add(personInfoView);
        FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        homePager.setAdapter(mAdapter);
        homePager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                long curTime=System.currentTimeMillis();
                if(curTime-lastTime<1500)
                    boringCount++;
                lastTime=curTime;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        SharedPreferences.Editor editor=getSharedPreferences("cookie",MODE_PRIVATE).edit();
        editor.putInt("boringCount",boringCount);
        editor.commit();
        super.onDestroy();
    }



    public static int GetRandomColor(){
        int[] colors=mainActivity.getResources().getIntArray(R.array.randomColors);
        return colors[new Random().nextInt(colors.length)];
    }
}
