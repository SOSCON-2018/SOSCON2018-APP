package com.example.tcy.view;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.tcy.Data.Schedule;
import com.example.tcy.adapter.OnRecyclerItemClickListener;
import com.example.tcy.adapter.scheduleAdapter;
import com.example.tcy.app.R;
import com.example.tcy.app.MainActivity;
import com.example.tcy.network.Getschedule;

import java.util.ArrayList;
import java.util.List;

public class ScheduleDayoneView extends BaseFragment {
    private static List<Schedule> s_list;
    private RecyclerView schedulelist;
    private scheduleAdapter sAdapter;
    private Getschedule mTask;
    private RecyclerView.LayoutManager manager;
    private static Handler mHandler;
    private static int handlerID;
    private NewsCardDialog mDialog;
    private static boolean firstLoad=true;
    @Override
    protected void PreGetData() {
        handlerID=0x88;
        schedulelist = mview.findViewById(R.id.first_day_schedule_recycler);
        if(firstLoad) {
            s_list = new ArrayList<>();
        }
        sAdapter=new scheduleAdapter(s_list);
        manager = new LinearLayoutManager(mcontext);
        schedulelist.setLayoutManager(manager);
        schedulelist.setAdapter(sAdapter);
        schedulelist.setNestedScrollingEnabled(false);
        schedulelist.addOnItemTouchListener(new OnRecyclerItemClickListener(schedulelist) {
            @Override
            public void onItemClick(int position) {
                if (s_list.get(position).person_name != null) {
                    //显示框
                    mDialog=new NewsCardDialog(mcontext,s_list.get(position).title,s_list.get(position).person_name);
                    mDialog.show();
                }
//                mDialog=new ScheduleCardDialog(mcontext,null,s_list.get(position).person_name,"great");
//                mDialog.show();
            }
        });
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                    s_list=(List<Schedule>)msg.obj;
                    sAdapter=new scheduleAdapter(s_list);
                    schedulelist.setAdapter(sAdapter);

            }
        };
    }

    @Override
    protected void InitView() {
        if(firstLoad) {
            mTask = new Getschedule("https://soscon.top/schedule/getA", handlerID, mHandler);
            mTask.GetSchedule();
            firstLoad=false;
        }
    }

    @Override
    public int GetLayoutId() {
        return R.layout.first_day_shedule;
    }


}
