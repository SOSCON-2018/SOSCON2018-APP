package org.openingsource.soscon.Data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;

public class PersonInfo {
    public String name;
    public Bitmap QRcode;
    public String realname;
    public String school;
    public PersonInfo(String name,byte[] Byte,String realname,String school){
        this.name=name;
        QRcode= BitmapFactory.decodeByteArray(Byte,0,Byte.length);
        this.realname=realname;
        this.school=school;
    }
}
