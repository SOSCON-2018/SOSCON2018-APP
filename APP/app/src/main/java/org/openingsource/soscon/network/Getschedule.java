package org.openingsource.soscon.network;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.openingsource.soscon.Data.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Getschedule {
    public int SUCGETSCHEDULE;
    public String urlPath="";
    public Handler mHandler;

    public Getschedule(String urlPath,int handlerID,Handler handler){
        this.urlPath=urlPath;
        this.mHandler=handler;
        this.SUCGETSCHEDULE=handlerID;
    }

    public void GetSchedule(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlPath);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setConnectTimeout(10000);
                    conn.connect();
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuffer sb = new StringBuffer();
                        String temp = "";
                        while ((temp = br.readLine()) != null) {
                            sb.append(temp);
                        }
                        JSONArray list = new JSONArray(sb.toString());
                        List<Schedule> tempList = new ArrayList<>();
                        for (int i = 0; i < list.length(); ++i) {
                            JSONObject t = list.getJSONObject(i);
                            String name = null;
                            String introduction = null;
                            if (!t.getString("name").equals("")) {
                                name = t.getString("name").replace("\\n","\n");
                            }
                            if (!t.getString("intro").equals("")) {
                                introduction = null;
                            }
                            Schedule b = new Schedule(t.getString("title"), t.getString("time"),introduction,name);
                            tempList.add(b);
                        }
                        if(tempList!=null){
                            Log.d("schedule", "run: schedule is not null");
                            Message msg=new Message();
                            msg.obj=tempList;
                            msg.what=SUCGETSCHEDULE;
                            mHandler.sendMessage(msg);
                        }
                    }
                }catch(Exception e){
                    Log.d("Geterror", "run: error error error error error");
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
