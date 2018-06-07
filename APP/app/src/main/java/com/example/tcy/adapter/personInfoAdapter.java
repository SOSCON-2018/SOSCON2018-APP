package com.example.tcy.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.tcy.app.R;
import com.example.tcy.Data.settingInfo;

import java.util.List;

public class personInfoAdapter extends RecyclerView.Adapter<personInfoAdapter.infoHolder> {
    List<settingInfo> infoList;
    public static enum CARD_TYPE{SETTING,INFO}
    public class infoHolder extends RecyclerView.ViewHolder{
        TextView titleView,contentView;
        infoHolder(View itemView){
            super(itemView);
            titleView=itemView.findViewById(R.id.title);
            contentView=itemView.findViewById(R.id.content);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @NonNull
    @Override
    public infoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        infoHolder temp;
        if(viewType==CARD_TYPE.SETTING.ordinal()) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_info_card, parent, false);
            temp = new infoHolder(v);
        }
        else{
            View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.person_info_card_1,parent,false);
            temp=new infoHolder(v);
        }
        return temp;
    }

    @Override
    public void onBindViewHolder(@NonNull infoHolder holder, int position) {
        if(infoList.get(position).content!=null){
            holder.contentView.setText(infoList.get(position).content);
        }
        else{
            holder.contentView.setText(null);
        }
        holder.titleView.setText(infoList.get(position).title);
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    public personInfoAdapter(List<settingInfo> list){
        infoList=list;
    }

    @Override
    public int getItemViewType(int position) {
         return position>2? CARD_TYPE.SETTING.ordinal():CARD_TYPE.INFO.ordinal();
    }
}

