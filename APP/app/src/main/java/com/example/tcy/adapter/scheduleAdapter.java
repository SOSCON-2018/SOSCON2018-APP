package com.example.tcy.adapter;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.example.tcy.app.R;
import com.example.tcy.Data.Schedule;
import com.example.tcy.view.ScheduleCardDialog;

import java.util.List;

public class scheduleAdapter extends RecyclerView.Adapter<scheduleAdapter.ScheduleHolder> {
    List<Schedule> list;
    int count=0;
    public static class ScheduleHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        public TextView nameView,posView,timeView,introView;
        ScheduleHolder(View itemView){
            super(itemView);
            cardView=itemView.findViewById(R.id.schedulecard);
            nameView=itemView.findViewById(R.id.schedule_title);
            posView=itemView.findViewById(R.id.schedule_person_name);
            timeView=itemView.findViewById(R.id.schedule_time);
        }
    }

    public scheduleAdapter.ScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_card,parent,false);
        scheduleAdapter.ScheduleHolder n=new scheduleAdapter.ScheduleHolder(v);
        return n;
    }

    public int getItemCount(){
        return list.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleHolder holder, int position) {
        switch(count%5){
            case 0:holder.cardView.setBackgroundColor(0xff51dde8);
            break;
            case 1:holder.cardView.setBackgroundColor(0xff5971ff);
            break;
            case 2:holder.cardView.setBackgroundColor(0xff66ccff);
            break;
            case 3:holder.cardView.setBackgroundColor(0xff59ffdd);
            break;
            case 4:holder.cardView.setBackgroundColor(0xff518ee8);
            break;
        }
        count++;
        holder.timeView.setText(list.get(position).hold_time);
        if(list.get(position).person_name!=null) {
            holder.posView.setText(list.get(position).person_name);
        }
        holder.nameView.setText(list.get(position).title);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public scheduleAdapter(List<Schedule> temp){
        list=temp;
    }
}
