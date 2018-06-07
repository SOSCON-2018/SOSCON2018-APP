package org.openingsource.soscon.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.display.DisplayManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.Preference;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.openingsource.soscon.Data.settingInfo;
import org.openingsource.soscon.adapter.OnRecyclerItemClickListener;
import org.openingsource.soscon.adapter.personInfoAdapter;
import org.json.JSONObject;
import org.openingsource.soscon.R;
import org.openingsource.soscon.app.MainActivity;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static android.content.Context.MODE_PRIVATE;

public class PersonInfoView extends BaseFragment {
    enum AppBarLayoutState{EXPANDED, COLLAPSED, INTERNEDIATE};
    AppBarLayoutState state=AppBarLayoutState.EXPANDED;
    String name;
    String ID;
    GetInfo mLoginTask;
    Bitmap qrcode;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    CollapsingToolbarLayout collapsingToolbarLayout;
    RecyclerView infoList;
    AppBarLayout infoBar;
    NestedScrollView listView;
    ImageView qrCodeView;
    List<settingInfo> list;
    personInfoAdapter mAdapter;
    ProgressBar progressBar;
    ImageView qrCodeSimple;
    LoadDialog mDialog;
    Button exitButton;
    BaseDialog sDialog;
    BoringDialog bDialog;
    static int Count=0;
    long lastTime=0;
    @Override
    protected void PreGetData() {
        preferences=getActivity().getSharedPreferences("cookie", MODE_PRIVATE);
        editor=preferences.edit();
        name=preferences.getString("name",null);
        Log.d("personal_name", "PreGetData: "+name);
        ID=preferences.getString("QRcodeID",null);
        Log.d("personal_ID", "PreGetData: "+ID);
        infoList=mview.findViewById(R.id.settingList);
        exitButton=mview.findViewById(R.id.login_exit);
        infoBar=mview.findViewById(R.id.infoBar);
        qrCodeSimple=mview.findViewById(R.id.qrCodeSimple);
        listView=mview.findViewById(R.id.listView);
        qrCodeView=mview.findViewById(R.id.QRcode);
        collapsingToolbarLayout=mview.findViewById(R.id.collapsing_toolbar);
        progressBar=mview.findViewById(R.id.netConnect);
        mLoginTask=new GetInfo();
        mLoginTask.execute();
    }

    @Override
    public int GetLayoutId() {
        return R.layout.person_info;
    }

    @Override
    protected void InitView() {
        infoBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ///////////////////////////////////////
                if(verticalOffset==0){
                    if(state!=AppBarLayoutState.EXPANDED){
                        state=AppBarLayoutState.EXPANDED;
                    }
                }else if(Math.abs(verticalOffset)>=appBarLayout.getTotalScrollRange()){
                    if(state!=AppBarLayoutState.COLLAPSED){
                        qrCodeSimple.setVisibility(View.VISIBLE);
                        state=AppBarLayoutState.COLLAPSED;
                    }
                }else{
                    if(state!=AppBarLayoutState.INTERNEDIATE){
                        if(state==AppBarLayoutState.COLLAPSED){
                            qrCodeSimple.setVisibility(View.GONE);
                        }
                        state=AppBarLayoutState.INTERNEDIATE;
                    }
                }
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExitLogin(v);
            }
        });
        qrCodeSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoBar.setExpanded(true);
                long curTime=System.currentTimeMillis();
                if(curTime-lastTime<2000){
                    MainActivity.boringCount++;
                }
                lastTime=curTime;
            }
        });
    }

    public void ExitLogin(View view){
        Count=0;
        MainActivity.boringCount=0;
        preferences=getActivity().getSharedPreferences("cookie",MODE_PRIVATE);
        editor=preferences.edit();
        editor.putString("sessionID",null);
        editor.putBoolean("HaveLogin",false);
        editor.putString("name",null);
        editor.putString("QRcodeID",null);
        editor.putString("QRcode",null);
        editor.putInt("boringCount",0);
        editor.putBoolean("boringActive",false);
        editor.putString("nickName",null);
        editor.commit();
        final Intent intent = getActivity().getPackageManager()
                .getLaunchIntentForPackage(getActivity().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
        getActivity().finish();
    }


    public class GetInfo extends AsyncTask<Void,Boolean,String> {
        final String urlPath = "https://soscon.top/account/information";

        @Override
        protected String doInBackground(Void... voids) {
            preferences = getActivity().getSharedPreferences("cookie", MODE_PRIVATE);
            String sessionID = preferences.getString("sessionID", null);
            Log.d("sessionIDcheck", "doInBackground: "+sessionID);
            try {
                String QRcodeByte;
                if((preferences.getString("name",null))!=null) {
                    QRcodeByte=preferences.getString("QRcode","");
                    byte[] byteArray = Base64.decode(QRcodeByte, Base64.DEFAULT);
                    qrcode=BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
                    return "suc";
                }else {
                    publishProgress(false);
                    qrcode = GetQRCode(ID);
                    URL url = new URL(urlPath);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(10000);
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Cookie", sessionID);
                    Log.d("netresponde", "doInBackground: " + conn.getResponseCode());
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        String temp = "";
                        StringBuffer sb = new StringBuffer();
                        while ((temp = br.readLine()) != null) {
                            sb.append(temp);
                        }
                        br.close();
                        publishProgress(true);
                        return sb.toString();
                    }
                }
            } catch (SocketTimeoutException e) {
                publishProgress(true);
                return "timeout";
            } catch (IOException e) {
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
            publishProgress(true);
            return "error";
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            if(values[0]){
                if(mDialog!=null){
                    mDialog.dismiss();
                    mDialog=null;
                }
            }
            else{
                mDialog=new LoadDialog(mcontext);
                mDialog.show();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("checkresult", "onPostExecute: "+s);
            mLoginTask = null;
            if(s.equals("timeout")){
                Toast.makeText(getContext(),"连接超时",Toast.LENGTH_LONG).show();
                mLoginTask.cancel(true);
            }else if(s.equals("suc")){
                list=new ArrayList<>();
                list.add(new settingInfo("姓名",preferences.getString("name","")));
                list.add(new settingInfo("学校",preferences.getString("school","")));
                list.add(new settingInfo("邮箱",preferences.getString("email","")));
                mAdapter=new personInfoAdapter(list);
                qrCodeView.setImageBitmap(qrcode);
                collapsingToolbarLayout.setTitle(name);
                infoList.setAdapter(mAdapter);
                infoList.setLayoutManager(new LinearLayoutManager(mcontext));
                infoList.addItemDecoration(new DividerItemDecoration(mcontext,DividerItemDecoration.VERTICAL));
                if(!preferences.getBoolean("boringActive",false)){
                    infoList.addOnItemTouchListener(new OnRecyclerItemClickListener(infoList){
                        @Override
                        public void onItemClick(int position) {
                            switch(Count){
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4: {
                                    sDialog = new BaseDialog(mcontext, "如若信息有误请联系管理员");
                                    sDialog.show();
                                    break;
                                }
                                case 5: {
                                    sDialog=new BaseDialog(mcontext,"你很无聊吗？请不要再试了");
                                    sDialog.show();
                                    break;
                                }
                                case 6:{
                                    sDialog=new BaseDialog(mcontext,"彩蛋？不存在的，请你别再按了");
                                    sDialog.show();
                                    break;
                                }
                                case 7:{
                                    sDialog=new BaseDialog(mcontext,"你在干嘛啊啊啊啊！说了没有就是没有嘛！");
                                    sDialog.show();
                                    break;
                                }
                                case 8:{
                                    sDialog=new BaseDialog(mcontext,"好吧，你赢了，你将开启一项全新功能：无聊的大比拼！");
                                    sDialog.show();
                                    list.add(new settingInfo("无聊大比拼！",null));
                                    editor.putBoolean("boringActive",true);
                                    editor.commit();
                                    mAdapter=new personInfoAdapter(list);
                                    infoList.setAdapter(mAdapter);
                                    break;
                                }
                            }
                            MainActivity.boringCount++;
                            Count++;
                            if(position==3){
                                bDialog=new BoringDialog(mcontext);
                                bDialog.show();
                            }
                        }
                    });
                }else{
                    list.add(new settingInfo("无聊大比拼",null));
                    mAdapter = new personInfoAdapter(list);
                    infoList.setAdapter(mAdapter);
                    infoList.addOnItemTouchListener(new OnRecyclerItemClickListener(infoList){
                        @Override
                        public void onItemClick(int position) {
                            if(position==3){
                                bDialog=new BoringDialog(mcontext);
                                bDialog.show();
                            }
                        }
                    });
                }
            }else if(s.equals("error")){
                qrCodeView.setImageResource(R.drawable.info_no_ticket);
                Toast.makeText(getContext(),"好像没领票哟",Toast.LENGTH_LONG).show();
            }else {
                try {
                    JSONObject obj = new JSONObject(s);
                    list = new ArrayList<>();
                    list.add(new settingInfo("姓名", obj.getString("realname")));
                    list.add(new settingInfo("学校", obj.getString("school")));
                    list.add(new settingInfo("邮箱", obj.getString("email")));
                    editor.putString("name", obj.getString("realname"));
                    editor.putString("school", obj.getString("school"));
                    editor.putString("email", obj.getString("email"));
                    editor.commit();
                    mAdapter = new personInfoAdapter(list);
                    qrCodeView.setImageBitmap(qrcode);
                    collapsingToolbarLayout.setTitle(name);
                    infoList.setAdapter(mAdapter);
                    infoList.setLayoutManager(new LinearLayoutManager(mcontext));
                    infoList.addItemDecoration(new DividerItemDecoration(mcontext, DividerItemDecoration.VERTICAL));
                    if(!preferences.getBoolean("boringActive",false)){
                        infoList.addOnItemTouchListener(new OnRecyclerItemClickListener(infoList){
                            @Override
                            public void onItemClick(int position) {
                                switch(Count){
                                    case 0:
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4: {
                                        MainActivity.boringCount++;
                                        sDialog = new BaseDialog(mcontext, "如若信息有误请联系管理员");
                                        sDialog.show();
                                        break;
                                    }
                                    case 5: {
                                        MainActivity.boringCount++;
                                        sDialog=new BaseDialog(mcontext,"你很无聊吗？请不要再试了");
                                        sDialog.show();
                                        break;
                                    }
                                    case 6:{
                                        MainActivity.boringCount++;
                                        sDialog=new BaseDialog(mcontext,"彩蛋？不存在的，请你别再按了");
                                        sDialog.show();
                                        break;
                                    }
                                    case 7:{
                                        MainActivity.boringCount++;
                                        sDialog=new BaseDialog(mcontext,"你在干嘛啊啊啊啊！说了没有就是没有嘛！");
                                        sDialog.show();
                                        break;
                                    }
                                    case 8:{
                                        MainActivity.boringCount++;
                                        sDialog=new BaseDialog(mcontext,"好吧，你赢了，你将开启一项全新功能：无聊的大比拼！");
                                        sDialog.show();
                                        list.add(new settingInfo("无聊大比拼！",null));
                                        infoList.getAdapter().notifyDataSetChanged();
                                        editor.putBoolean("boringActive",true);
                                        editor.commit();
                                        break;
                                    }
                                }
                                Count++;
                                if(position==3){
                                    bDialog=new BoringDialog(mcontext);
                                    bDialog.show();
                                }
                            }
                        });
                    }else{
                        list.add(new settingInfo("无聊大比拼",null));
                        mAdapter = new personInfoAdapter(list);
                        infoList.setAdapter(mAdapter);
                        infoList.addOnItemTouchListener(new OnRecyclerItemClickListener(infoList){
                            @Override
                            public void onItemClick(int position) {
                                if(position==3){
                                    bDialog=new BoringDialog(mcontext);
                                    bDialog.show();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mLoginTask = null;
                }
            }
        }

        public Bitmap GetQRCode(String id)throws Exception{
            final String path="https://soscon.top/account/img/"+id;
            URL url=new URL(path);
            HttpURLConnection conn=(HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            InputStream inputStream=null;
            if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                inputStream=conn.getInputStream();
            }else{
                inputStream=null;
            }
            if(inputStream==null){
                throw new Exception("stream is null");
            }else{
                try{
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while( (len=inputStream.read(buffer)) != -1){
                        outStream.write(buffer, 0, len);
                    }
                    outStream.close();
                    inputStream.close();
                    byte[] bytes=outStream.toByteArray();
                    if(bytes!=null){
                        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        String imgString = new String(Base64.encodeToString(bytes, Base64.DEFAULT));
                        editor.putString("QRcode",imgString);
                        editor.commit();
                        return bitmap;
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            inputStream.close();
            return null;
        }
    }
}
