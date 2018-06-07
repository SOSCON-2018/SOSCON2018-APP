package org.openingsource.soscon.view;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.nfc.cardemulation.HostApduService;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import org.openingsource.soscon.Data.News;
import org.openingsource.soscon.adapter.OnRecyclerItemClickListener;
import org.openingsource.soscon.adapter.newsAdapter;
import org.openingsource.soscon.network.GetNews;
import org.openingsource.soscon.app.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openingsource.soscon.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsView extends BaseFragment{
    public static List<News> news;
    private RecyclerView newslist;
    private newsAdapter mAdapter;
    private StaggeredGridLayoutManager manager;
    private ScheduleCardDialog mDialog;
    private SwipeRefreshLayout mRefresh;
    private GetNews mNewsTask;
    private LoadDialog mLoader;
    private static Handler mHandler;
    private static boolean firstLoad=true;
    protected void InitView(){
        if(firstLoad) {
            Refresh();
            firstLoad=false;
        }
    }
    @Override
    public int GetLayoutId() {
        return R.layout.news_push;
    }

    @Override
    protected void PreGetData() {
        if(firstLoad) {
            news = new ArrayList<>();
        }
        newslist=mview.findViewById(R.id.newslist);
        manager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        newslist.setLayoutManager(manager);
        mAdapter=new newsAdapter(news);
        newslist.setAdapter(mAdapter);
        newslist.setNestedScrollingEnabled(false);
        newslist.addOnItemTouchListener(new OnRecyclerItemClickListener(newslist){
            @Override
            public void onItemClick(int position) {
                mDialog=new ScheduleCardDialog(mcontext,news.get(position).image,news.get(position).title,news.get(position).content);
                mDialog.show();
            }
        });
        mRefresh=mview.findViewById(R.id.refresh);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refresh();
            }
        });
        mRefresh.setColorSchemeColors(0x3F51B5);
        mHandler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                news=(List<News>)msg.obj;
                mAdapter=new newsAdapter(news);
                newslist.setAdapter(mAdapter);
                if(mRefresh.isRefreshing()){
                    mRefresh.setRefreshing(false);
                }
            }
        };
    }

    private void Refresh(){
        if(!mRefresh.isRefreshing()) {
            mRefresh.setRefreshing(true);
        }
        mNewsTask=new GetNews("https://openingsource.org/special/soscon18/feed/",mHandler);
        mNewsTask.GetXml();
    }
    public void Add(News m){
        news.add(m);
        mAdapter.notifyItemInserted(news.size());
    }

    public class SetImg extends AsyncTask<String,Void,Bitmap>{
        private int pos;
        private News mNews;
        public SetImg(News n,int i){
            this.mNews=n;
            this.pos=i;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL bitUrl = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection) bitUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(10000);
                conn.connect();
                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                    InputStream in=new BufferedInputStream(conn.getInputStream());
                    Bitmap img=BitmapFactory.decodeStream(in);
                    return img;
                }
            }catch(Exception e){
                e.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if(bitmap!=null){
                mNews.image=bitmap;
                newslist.getAdapter().notifyItemChanged(pos);
            }else{
                Toast.makeText(mcontext,mNews.title+":图片加载错误",Toast.LENGTH_SHORT).show();
                /////设置加载失败的图片


                //////
            }
        }
    }
}
