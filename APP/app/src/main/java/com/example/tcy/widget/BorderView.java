package com.example.tcy.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.tcy.app.R;

public class BorderView extends View {
    boolean borders = false;
    boolean borderLeft = false;
    boolean borderTop = false;
    boolean borderRight = false;
    boolean borderBottom = false;
    int borderColor;
    float borderWidth,titleSize,contentSize;
    String title;
    String content;
    String style="line";
    public BorderView(Context context){
        super(context);
    }

    public BorderView(Context context, AttributeSet attr){
        super(context,attr);
        TypedArray typedArray=context.obtainStyledAttributes(attr, R.styleable.BorderView);
        borderBottom=typedArray.getBoolean(R.styleable.BorderView_borderView_bottom,false);
        borderLeft=typedArray.getBoolean(R.styleable.BorderView_borderView_left,false);
        borderRight=typedArray.getBoolean(R.styleable.BorderView_borderView_right,false);
        borderTop=typedArray.getBoolean(R.styleable.BorderView_borderView_top,false);
        borderColor=typedArray.getColor(R.styleable.BorderView_borderView_color, Color.GRAY);
        borderWidth=typedArray.getDimension(R.styleable.BorderView_borderView_width, 30);
        title=typedArray.getString(R.styleable.BorderView_borderView_title);
        content=typedArray.getString(R.styleable.BorderView_borderView_content);
        titleSize=typedArray.getDimension(R.styleable.BorderView_borderView_titleSize,30);
        contentSize=typedArray.getDimension(R.styleable.BorderView_borderView_contentSize,30);
        style=typedArray.getString(R.styleable.BorderView_borderView_style);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint paint=new Paint();
        if(borders){
            DrawBottom(canvas,paint,borderColor,borderWidth,style);
            DrawLeft(canvas,paint,borderColor,borderWidth,style);
            DrawRight(canvas,paint,borderColor,borderWidth,style);
            DrawTop(canvas,paint,borderColor,borderWidth,style);
        }
        else{
            if(borderTop){
                DrawTop(canvas,paint,borderColor,borderWidth,style);
            }
            if(borderBottom){
                DrawBottom(canvas,paint,borderColor,borderWidth,style);
            }
            if(borderRight){
                DrawRight(canvas,paint,borderColor,borderWidth,style);
            }
            if(borderLeft){
                DrawLeft(canvas,paint,borderColor,borderWidth,style);
            }
        }
        DrawText(canvas,paint,contentSize,titleSize,content,title);
        paint.reset();
    }

    protected void DrawTop(Canvas canvas,Paint paint,int color,float width,String style ){
            paint.setColor(color);
            paint.setAntiAlias(true);
            if(style.equals("shadow")) {
                for (float a = 0, b = 0; a < width; a += 0.5, ++b) {
                    paint.setStrokeWidth(0.5f);
                    paint.setAlpha(40 - (int) ((40 / (width / 0.5)) * b));
                    canvas.drawLine(0, width - a, this.getWidth() - 1, width - a, paint);
                }
            }
            else if(style.equals("center_line")){
                paint.setStrokeWidth(width);
                paint.setAlpha(40);
                canvas.drawLine(this.getWidth()/7, 0, this.getWidth()*6/7- 1, 0, paint);
            }
            paint.reset();
        }


    protected void DrawBottom(Canvas canvas,Paint paint,int color,float width,String style ){
            paint.setColor(color);
            paint.setAntiAlias(true);
            if(style.equals("shadow")) {
                for (float a = 0, b = 0; a < width; a += 0.5, ++b) {
                    paint.setStrokeWidth(0.5f);
                    paint.setAlpha(40 - (int) ((40 / (width / 0.5)) * b));
                    canvas.drawLine(0, this.getHeight() - 1 - width + a, this.getWidth() - 1, this.getHeight() - 1 - width + a, paint);
                }
            }
            else if(style.equals("center_line")){
                paint.setStrokeWidth(width);
                paint.setAlpha(40);
                canvas.drawLine(this.getWidth()/7, this.getHeight()-1, this.getWidth()*6/7- 1, this.getHeight()-1, paint);
            }
            paint.reset();
        }


    protected void DrawRight(Canvas canvas,Paint paint,int color,float width,String style){
            paint.setColor(color);
            paint.setAntiAlias(true);
            if(style.equals("shadow")) {
                for (float a = 0, b = 0; a < width; a += 0.5, ++b) {
                    paint.setStrokeWidth(0.5f);
                    paint.setAlpha(40 - (int) ((40 / (width / 0.5)) * b));
                    canvas.drawLine(this.getWidth() - 1 - width + a, 0, this.getWidth() - 1 - width + a, this.getHeight() - 1, paint);
                }
            }
            else if(style.equals("center_line")){
                paint.setStrokeWidth(width);
                paint.setAlpha(40);
                canvas.drawLine(this.getWidth()-1, this.getHeight()/7, this.getWidth()- 1, this.getHeight()*6/7-1, paint);
            }
            paint.reset();
        }


    protected void DrawLeft(Canvas canvas,Paint paint,int color,float width,String style){
            paint.setColor(color);
            paint.setAntiAlias(true);
            if(style.equals("shadow")) {
                for (float a = 0, b = 0; a < width; a += 0.5, ++b) {
                    paint.setStrokeWidth(0.5f);
                    paint.setAlpha(40 - (int) ((40 / (width / 0.5)) * b));
                    canvas.drawLine(width - a, 0, width - a, this.getHeight() - 1, paint);
                }
            }
            else if(style.equals("center_line")){
                paint.setStrokeWidth(width);
                paint.setAlpha(40);
                canvas.drawLine(0, this.getHeight()/7, 0, this.getHeight()*6/7-1, paint);
            }
            paint.reset();
        }

        protected void DrawText(Canvas canvas,Paint paint,float contentSize,float titleSize,String content,String title){
        paint.setStrokeWidth(5);
        paint.setColor(Color.BLACK);
        paint.setAlpha(100);
        paint.setTextSize(titleSize);
        Rect bounds = new Rect();
        paint.getTextBounds(title, 0, title.length(), bounds);
        Log.d("alphaxxxxxxxxx", "DrawText: "+paint.getAlpha());
        canvas.drawText(title,15,getHeight()/2+bounds.height()/2,paint);
        paint.setAlpha(60);
        paint.setTextSize(contentSize);
        paint.getTextBounds(content,0,content.length(),bounds);
        Log.d("alphaxxxxxxxxx", "DrawText: "+paint.getAlpha());
        canvas.drawText(content,getWidth()-bounds.width()-15,getHeight()/2+bounds.height()/2,paint);
        paint.reset();
        }
}

