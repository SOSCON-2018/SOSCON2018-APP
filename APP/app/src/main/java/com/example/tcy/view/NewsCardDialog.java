package com.example.tcy.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.tcy.app.*;
import com.example.tcy.Data.*;
import com.example.tcy.app.MainActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsCardDialog extends Dialog {
    private Context mContext;
    private Bitmap img;
    private String mName;
    private String introduction;
    private String content;
    public String mTitle;
    private ImageView imageView;
    private TextView nameView,introView,contentView;
    private Dialog mDialog;
    private GetSchedule mTask;
    private ImageButton fab;
    long createTime;
    public NewsCardDialog(Context context,String title,String name){
        super(context,R.style.Dialog);
        this.mContext=context;
        this.mTitle=title;
        this.mName=name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_content);
        createTime=System.currentTimeMillis();
        mDialog=this;
        fab=findViewById(R.id.schedule_exit);
        imageView=findViewById(R.id.photo);
        introView=findViewById(R.id.introduction);
        contentView=findViewById(R.id.content_introduction);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mTask=new GetSchedule();
        mTask.execute("https://soscon.top/schedule/detail");
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                long curTime=System.currentTimeMillis();
                if(curTime-createTime<1500)
                    MainActivity.boringCount++;
            }
        });
    }


    public class GetImg extends AsyncTask<String,Void,Bitmap>{
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                InputStream inputStream = null;
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream=conn.getInputStream();
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len = 0;
                        while ((len = inputStream.read(buffer)) != -1) {
                            outStream.write(buffer, 0, len);
                        }
                        outStream.close();
                        inputStream.close();
                        byte[] bytes = outStream.toByteArray();
                        if (bytes != null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            return bitmap;
                        }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap==null){
                Toast.makeText(mContext,"图片加载失败",Toast.LENGTH_SHORT).show();
            }else{
                img=bitmap;
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    public class GetSchedule extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            try{
                URL url=new URL(strings[0]);
                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                JSONObject jobj=new JSONObject();
                jobj.put("title",mTitle);
                DataOutputStream dp=new DataOutputStream(conn.getOutputStream());
                dp.write(jobj.toString().getBytes("UTF-8"));
                dp.flush();
                dp.close();
                Log.d("schedule content", "doInBackground: "+conn.getResponseCode());
                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb=new StringBuilder();
                    String temp="";
                    while((temp=br.readLine())!=null){
                        sb.append(temp);
                    }
                    return sb.toString();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                if(s!=null) {
                    Log.d("schedule content", "onPostExecute: "+s);
                    JSONObject obj = new JSONObject(s);
                    introduction = obj.getString("personInfo");
                    content = obj.getString("intro");
                    introduction=introduction.replace("\\n","\n");
                    content=content.replace("\\n","\n");
                    if (obj.getString("img") != null) {
                        GetImg getImg = new GetImg();
                        getImg.execute("https://soscon.top"+obj.getString("img"));
                    }
                    introView.setText(introduction);
                    contentView.setText(content);
                }
            }catch(Exception e){
                e.printStackTrace();
                Toast.makeText(mContext,"加载错误",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
