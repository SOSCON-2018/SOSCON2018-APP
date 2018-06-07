package org.openingsource.soscon.adapter;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import org.openingsource.soscon.adapter.*;
public abstract class OnRecyclerItemClickListener implements RecyclerView.OnItemTouchListener{
    GestureDetectorCompat gestureDetectorCompat;
    RecyclerView recyclerView;
    public OnRecyclerItemClickListener(final RecyclerView recyclerView){
        this.recyclerView=recyclerView;
        gestureDetectorCompat=new GestureDetectorCompat(recyclerView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                View childView=recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(childView!=null){
                    int position=recyclerView.getChildAdapterPosition(childView);
                    onItemClick(position);
                }
                return true;
            }
        });
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetectorCompat.onTouchEvent(e);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetectorCompat.onTouchEvent(e);
        return false;
    }

    public abstract void onItemClick(int position);
}
