package com.example.tcy.Data;

public class Schedule {
    public String person_name,hold_time,introduction,title;
    public Schedule(String title,String time,String intro){
        this.title=title;
        hold_time=time;
        introduction=intro;
        person_name=null;
    }
    public Schedule(String title,String time,String intro,String name){
        this.title=title;
        person_name=name;
        hold_time=time;
        introduction=intro;
    }
}
