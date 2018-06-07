package com.example.tcy.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.example.tcy.view.*;
import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private UserLoginTask mAuthTask = null;
    private BoolLogin mLoginTask=null;
    private boolean emailError=false;
    private boolean passwordError=false;
    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private AppCompatActivity loginActivity;
    private LoadDialog mDialog;
    Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Check();
        Init();
        vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        // Set up the login form.'
        loginActivity = this;
        mEmailView = (EditText)findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mEmailView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mPasswordView.requestFocus();
                    return true;
                }
                return false;
            }
        });
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        mEmailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(emailError){
                    mEmailView.setBackground(getResources().getDrawable(R.drawable.shape_round));
                    emailError=false;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mPasswordView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(passwordError){
                    mPasswordView.setBackground(getResources().getDrawable(R.drawable.shape_round));
                    passwordError=false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            setError(getString(R.string.error_invalid_password),mPasswordView);
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            setError(getString(R.string.error_field_required),mEmailView);
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            setError(getString(R.string.error_invalid_email),mEmailView);
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserLoginTask(email, password);
            String[] user=new String[]{
                    mEmailView.getText().toString(),
                    mPasswordView.getText().toString()
            };
            mAuthTask.execute(user);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 0;
    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<String, Boolean, String> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO: attempt authentication against a network service.
            preferences=getSharedPreferences("cookie",MODE_PRIVATE);
            editor=preferences.edit();
            final String urlPath="https://soscon.top/account/applogin";
            String result=null;
            try {
                publishProgress(false);
                URL url=new URL(urlPath);
                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type","application/json");
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(10000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.connect();
                JSONObject obj=new JSONObject();
                obj.put("email", params[0]);
                obj.put("password",params[1]);
                DataOutputStream printout = new DataOutputStream(conn.getOutputStream ());
                printout.write(obj.toString().getBytes("UTF-8"));
                printout.flush ();
                printout.close();
                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                    List cookie=conn.getHeaderFields().get("Set-Cookie");
                   if(cookie!=null){
                       String sessionID=(String)cookie.get(0);
                       editor.putString("sessionID",sessionID);
                       editor.putBoolean("HaveLogin",true);
                       editor.commit();
                   }
                    InputStream is=conn.getInputStream();
                    BufferedReader br=new BufferedReader(new InputStreamReader(is));
                    StringBuffer sb=new StringBuffer();
                    String temp="";
                    while((temp=br.readLine())!=null){
                        sb.append(temp);
                    }
                    result=sb.toString();
                    br.close();
                }
            }catch (SocketTimeoutException e){
                publishProgress(true);
                e.printStackTrace();
                Log.d("connect", "doInBackground: connect false");
                result="timeout";
                return result;
            }catch (IOException e){
                publishProgress(true);
                e.printStackTrace();
                result="timeout";
                return result;
            }catch(JSONException e){
                publishProgress(true);
                e.printStackTrace();
                result="timeout";
                return result;
            }
            publishProgress(true);
            return result;
            // TODO: register the new account here.
        }

        @Override
        protected void onProgressUpdate(Boolean... values) {
            if(values[0]){
                if(mDialog!=null){
                    mDialog.dismiss();
                    mDialog=null;
                }
            }else{
                mDialog=new LoadDialog(loginActivity);
                mDialog.show();
            }
        }

        @Override
        protected void onPostExecute(final String success) {
            Log.d("message", "onPostExecute: "+success);
            mAuthTask = null;
            if(success.equals("timeout")){
                Toast.makeText(loginActivity,"连接错误",Toast.LENGTH_SHORT).show();
            }else {
                try {
                    if (success == null) {
                        setError("用户名或密码错误", mPasswordView);
                    } else {
                        JSONObject jobj = new JSONObject(success);
                        String result = jobj.getString("result");
                        Log.d("message", "onPostExecute: " + result);
                        if (result.equals("登录失败")) {
                            setError("用户名或密码错误", mPasswordView);
                        } else {
                            boolean active = jobj.getBoolean("check");
                            if (!active) {
                                setError("该账号未激活", mPasswordView);
                            } else {
                                mLoginTask = new BoolLogin();
                                mLoginTask.execute();
                                Intent intent = new Intent();
                                intent.setClass(loginActivity, MainActivity.class);
                                startActivity(intent);
                                loginActivity.finish();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void setError(CharSequence error, EditText text){
        text.requestFocus();
        if(text==mEmailView){
            emailError=true;
        }
        else if(text==mPasswordView){
            passwordError=true;
            mPasswordView.clearComposingText();
        }
        text.setBackground(getResources().getDrawable(R.drawable.text_warn));
        Animation shake=AnimationUtils.loadAnimation(loginActivity,R.anim.shake);
        text.startAnimation(shake);
        vibrator.vibrate(500);
        Toast.makeText(loginActivity,error,Toast.LENGTH_SHORT).show();
    }
//判断首次登陆
    private boolean IsFirstLogin(){
        preferences=getSharedPreferences("cookie",MODE_PRIVATE);
        if(preferences.getBoolean("HaveLogin",false)){
            return false;
        }
        return true;
    }
    //加载界面
    private void Init(){
        if(IsFirstLogin()){
        }
        else{
            Jump();
        }
    }

    private void Jump(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void Check(){
        mLoginTask=new BoolLogin();
        mLoginTask.execute();
    }
    //登陆判断
    public class BoolLogin extends AsyncTask<Void,Void,String>{
        final String urlPath="https://soscon.top/account/check";
        @Override
        protected String doInBackground(Void...voids) {
            preferences=getSharedPreferences("cookie",MODE_PRIVATE);
            String sessionID=preferences.getString("sessionID",null);
            try {
                URL url = new URL(urlPath);
                HttpURLConnection conn =(HttpURLConnection)url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Cookie",sessionID);
                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                    BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String temp="";
                    StringBuffer sb=new StringBuffer();
                    while((temp=br.readLine())!=null){
                        sb.append(temp);
                    }
                    br.close();
                    return sb.toString();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mLoginTask=null;
            preferences=getSharedPreferences("cookie",MODE_PRIVATE);
            editor=preferences.edit();
            try{
                if(s==null){
                    editor.putBoolean("HaveLogin",false);
                    editor.commit();
                }else {
                    JSONObject info = new JSONObject(s);
                    editor.putString("nickName",info.getString("name"));
                    if (!info.getBoolean("check")) {
                        editor.putBoolean("HaveLogin", false);
                        editor.commit();
                    } else {
                        Log.d("information", "onPostExecute: " + s);
                        if (info.getBoolean("haveTicket")) {
                            String ID = info.getString("ID");
                            editor.putString("QRcodeID", ID);
                            Log.d("logincheck", "onPostExecute: " + ID);
                        }
                        editor.commit();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                mLoginTask=null;
            }
        }

        @Override
        protected void onCancelled() {
            mLoginTask=null;
        }
    }
}

