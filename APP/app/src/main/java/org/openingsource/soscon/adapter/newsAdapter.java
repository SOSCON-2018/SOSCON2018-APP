package org.openingsource.soscon.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.openingsource.soscon.R;
import org.openingsource.soscon.Data.*;
import org.openingsource.soscon.app.MainActivity;
import java.util.List;

public class newsAdapter extends RecyclerView.Adapter<newsAdapter.NewsViewHolder> {
    List<News> newsList;
    public static class NewsViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView title,author,pTime;
        View divider;
        NewsViewHolder(View itemView){
            super(itemView);
            divider=itemView.findViewById(R.id.divider);
            cardView=(CardView)itemView.findViewById(R.id.card);
            title=(TextView)itemView.findViewById(R.id.news_title);
            author=itemView.findViewById(R.id.author);
            pTime=itemView.findViewById(R.id.publish_time);
        }
    }

    public newsAdapter(List<News>news){
        newsList=news;
    }

    public int getItemCount(){
        return newsList.size();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.true_news_card,parent,false);
        NewsViewHolder n=new NewsViewHolder(v);
        return n;
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        int color=MainActivity.GetRandomColor();
        holder.cardView.setBackgroundColor(color);
        if(color==0xffffffff){
            holder.divider.setBackgroundColor(0xff000000);
            holder.title.setTextColor(0xff000000);
            holder.author.setTextColor(0x8a000000);
            holder.pTime.setTextColor(0x8a000000);
        }else{
            holder.divider.setBackgroundColor(0xffffffff);
            holder.title.setTextColor(0xffffffff);
            holder.author.setTextColor(0x8affffff);
            holder.pTime.setTextColor(0x8affffff);
        }
        holder.title.setText(newsList.get(position).title);
        holder.pTime.setText(newsList.get(position).pubTime);
        holder.author.setText(newsList.get(position).author);
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
