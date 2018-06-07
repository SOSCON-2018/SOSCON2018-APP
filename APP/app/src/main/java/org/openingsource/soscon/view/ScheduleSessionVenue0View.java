package org.openingsource.soscon.view;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


import org.openingsource.soscon.Data.Schedule;
import org.openingsource.soscon.adapter.OnRecyclerItemClickListener;
import org.openingsource.soscon.adapter.scheduleAdapter;
import org.openingsource.soscon.network.Getschedule;
import org.openingsource.soscon.R;

import java.util.ArrayList;
import java.util.List;

public class ScheduleSessionVenue0View extends BaseFragment {
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
        handlerID=0x66;
        schedulelist = mview.findViewById(R.id.session_venue_0_schedule_recycler);
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
            mTask = new Getschedule("https://soscon.top/schedule/getC", handlerID, mHandler);
            mTask.GetSchedule();
            firstLoad = false;
        }
    }

    @Override
    public int GetLayoutId() {
        return R.layout.session_venue_0_schedule;
    }
}
