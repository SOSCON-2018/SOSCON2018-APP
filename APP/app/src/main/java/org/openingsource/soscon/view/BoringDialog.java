package org.openingsource.soscon.view;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



import org.json.JSONObject;
import org.openingsource.soscon.R;
import org.openingsource.soscon.app.MainActivity;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BoringDialog extends Dialog {

    TextView boringView;
    Button boringButton,exitButton,upButton;
    Dialog mDialog;
    long lastTime;
    Context mContext;
    SharedPreferences pr;
    UpScore mTask;
    public BoringDialog(Context context){
        super(context, R.style.Dialog);
        mContext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boring_dialog);
        mDialog=this;
        boringView=findViewById(R.id.boring_count);
        boringButton=findViewById(R.id.boring_button);
        exitButton=findViewById(R.id.close_button);
        upButton=findViewById(R.id.up_button);
        boringView.setText((new Integer(MainActivity.boringCount)).toString());
        boringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.boringCount++;
                boringView.setText((new Integer(MainActivity.boringCount)).toString());
            }
        });
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long curTime=System.currentTimeMillis();
                if(curTime-lastTime>5000){
                    if(mTask==null) {
                        mTask = new UpScore();
                        mTask.execute();
                    }
                    lastTime=curTime;
                }else{
                    Toast.makeText(mContext,"提交过于频繁！",Toast.LENGTH_SHORT).show();
                    MainActivity.boringCount++;
                }
            }
        });
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        pr=mContext.getSharedPreferences("cookie",Context.MODE_PRIVATE);
    }

    public class UpScore extends AsyncTask<Void,Void,String>{
        final String urlPath="https://soscon.top/account/boring";
        String name="";
        @Override
        protected String doInBackground(Void... voids) {
            if((name=pr.getString("nickName",null))!=null) {
                try {
                    URL url = new URL(urlPath);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setReadTimeout(5000);
                    conn.setConnectTimeout(5000);
                    JSONObject obj = new JSONObject();
                    obj.put("name", name);
                    obj.put("score",MainActivity.boringCount);
                    DataOutputStream out=new DataOutputStream(conn.getOutputStream());
                    out.write(obj.toString().getBytes("UTF-8"));
                    out.flush();
                    Log.d("connect", "doInBackground: "+conn.getResponseCode());
                    if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                        StringBuilder sb=new StringBuilder();
                        String temp="";
                        BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while((temp=br.readLine())!=null){
                            sb.append(temp);
                        }
                        return sb.toString();
                    }
                }catch(Exception e){
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mTask=null;
            if(s==null){
                Toast.makeText(mContext,"提交失败了QAQ",Toast.LENGTH_SHORT).show();
            }else if(s.equals("success")){
                Toast.makeText(mContext,"提交成功",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
