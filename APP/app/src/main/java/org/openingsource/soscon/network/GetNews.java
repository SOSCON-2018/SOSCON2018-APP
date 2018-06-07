package org.openingsource.soscon.network;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.openingsource.soscon.Data.*;

import org.xmlpull.v1.XmlPullParser;

public class GetNews {
    public static int SUCGETXML=0x99;
    static String urlPath="";
    static Handler mHandler;
    public GetNews(String url,Handler handler){
        urlPath=url;
        mHandler=handler;
    }
    public static void GetXml(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlPath);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(10000);
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        InputStream in = conn.getInputStream();
                        List<News> temp = ParseXml(in);
                        if (temp != null) {
                            Message msg = new Message();
                            msg.obj = temp;
                            msg.what = SUCGETXML;
                            mHandler.sendMessage(msg);
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static List<News> ParseXml(InputStream in){
        List<News> list=new ArrayList<>();
        try{
            XmlPullParser xp= Xml.newPullParser();
            News news=null;
            xp.setInput(in,"UTF-8");
            int eventCode=xp.getEventType();
            while(eventCode!=XmlPullParser.END_DOCUMENT){
                switch(eventCode){
                    case XmlPullParser.START_TAG:
                        if (xp.getName().equals("item")) {
                            news = new News(null, "", null, null);
                        } else if (xp.getName().equals("title")) {
                            if(news!=null) {
                                news.title = xp.nextText();
                            }
                        }else if(xp.getName().equals("encoded")){
                            news.content=xp.nextText();
                        }else if(xp.getName().equals("pubDate")){
                            if(news!=null){
                                news.pubTime=xp.nextText().replace("+0000","");
                            }
                        }else if(xp.getName().equals("creator")){
                            if(news!=null){
                                news.author=xp.nextText();
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if(xp.getName().equals("item")) {
                            list.add(news);
                            news=null;
                        }
                        break;

                }
                eventCode=xp.next();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
}
