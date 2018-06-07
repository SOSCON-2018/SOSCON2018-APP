package com.example.tcy.Data;

import android.graphics.Bitmap;
import android.media.Image;

public class News {
    public String title;
    public String simpleIntroduction;
    public Bitmap image;
    public String url;
    public String content;
    public String author;
    public String pubTime;
    public News(String title,String intro,Bitmap image,String target){
        this.title=title;
        this.simpleIntroduction=intro;
        this.image=image;
        this.url=target;
    }
}
