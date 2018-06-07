package org.openingsource.soscon.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import org.openingsource.soscon.R;
import org.openingsource.soscon.app.MainActivity;

public class ScheduleCardDialog extends Dialog{
    String name;
    String content;
    Bitmap person_image;
    ScheduleCardDialog mDialog;
    WebView intro;
    ImageView image;
    ViewTask mTask;
    long createTime;
    enum AppBarLayoutState{EXPANDED,COLLAPSED,INTERNEDIATE};
    AppBarLayoutState state;
    final String linkCSS= "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "+
            "<style type=\"text/css\"> " +
            "img{"+
            "max-width:100% !important; width:auto; height:auto;"+
            "}"+
            "@font-face{"+
            "font-family:mFont;src:url('/font/notosans_medium.ttf');"+
            "}"+
            "@font-face{"+
            "font-family:mFont-italic;src:url('/font/notosans_lightitalic.ttf');"+
            "}"+
            "a{" +
            "text-decoration:none; font-family:mFont-italic;color:#05c3de;"+
            "}" +
            "</style>";
    public ScheduleCardDialog(Context context, Bitmap img, String name, String content){
        super(context, R.style.Dialog);
        this.name=name;
        this.content=content;
        this.person_image=img;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_content);
        createTime=System.currentTimeMillis();
        mDialog=this;
        final CollapsingToolbarLayout collapsingToolbarLayout=findViewById(R.id.schedule_collapsing_toolbar);
        FloatingActionButton fab=findViewById(R.id.schedule_exit);
        AppBarLayout infoBar=findViewById(R.id.schedule_infoBar);
        intro=findViewById(R.id.schedule_content);
        image=findViewById(R.id.schedule_image);
        if(person_image!=null) {
            image.setImageBitmap(person_image);
        }
        final View block=findViewById(R.id.Block);
        collapsingToolbarLayout.setTitle(name);
        infoBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if(verticalOffset==0){
                    if(state!=AppBarLayoutState.EXPANDED){
                        state=AppBarLayoutState.EXPANDED;
                    }
                }else if(Math.abs(verticalOffset)>=appBarLayout.getTotalScrollRange()-10){
                    if(state!= AppBarLayoutState.COLLAPSED){
                        block.setVisibility(View.GONE);
                        state= AppBarLayoutState.COLLAPSED;
                    }
                }else{
                    if(state!= AppBarLayoutState.INTERNEDIATE){
                        if(state== AppBarLayoutState.COLLAPSED){
                                block.setVisibility(View.VISIBLE);
                        }
                        state= AppBarLayoutState.INTERNEDIATE;
                    }
                }
            }
        });
        mTask=new ViewTask();
        mTask.execute();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        this.setCanceledOnTouchOutside(true);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                long curTime=System.currentTimeMillis();
                if(curTime-createTime<1500)
                    MainActivity.boringCount++;
            }
        });
    }

    public class ViewTask extends AsyncTask<Void,Void,String>{
        @Override
        protected String doInBackground(Void... voids) {
            String mContent="<html><header>" + linkCSS + "</header>" + "<body style:'height:auto;max-width: 100%; width:auto;font-family:mFont;'>"+content + "</body></html>";
            return mContent;
        }

        @Override
        protected void onPostExecute(String s) {
            intro.getSettings().setJavaScriptEnabled(true);
            intro.getSettings().setTextZoom(80);
            intro.loadData(s,"text/html","UTF-8");
        }
    }

}
