package com.example.nofem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class all_nData extends AppCompatActivity {

    SQLiteDatabase sdb;
    Context c;

    public all_nData(Context c){
        this.c = c;
        sdb = c.openOrCreateDatabase("mainnofem",MODE_PRIVATE,null);
        sdb.execSQL("create table IF NOT EXISTS netfi (id int,name text,contents text)");
        Cursor cs = sdb.rawQuery("select id from netfi where id = -1",null);
        if(cs.getCount()==0){
            long now = System.currentTimeMillis();
            Date mDate = new Date(now);
            SimpleDateFormat simpleDate = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
            String getTime = simpleDate.format(mDate);
            sdb.execSQL(" insert into netfi(name,id) values ('"+getTime+"',-1)");
        }
        cs.close();
    }

    public void add(int id,String name,String contents){
        sdb.execSQL("insert into netfi(id,name,contents) values ("+id+",'"+name+"','"+contents+"')");
    }

    public ArrayList<Integer> getnetid() {
        ArrayList<Integer> as = new ArrayList<>();
        Cursor c = sdb.rawQuery("select id from netfi",null);
        if(c.getCount()==0){
            c.close();
            return as;
        }
        while(c.moveToNext())as.add(c.getInt(0));
        c.close();
        return as;
    }

    public String getname(int id) {
        Cursor c = sdb.rawQuery("select name from netfi where id = "+id,null);
        if(c.getCount()==0)return "";
        c.moveToFirst();
        String rs = c.getString(0);
        c.close();
        return rs;
    }

    public String getcontents(int id) {
        Cursor c = sdb.rawQuery("select contents from netfi where id = "+id,null);
        if(c.getCount()==0)return "";
        c.moveToFirst();
        String rs = c.getString(0);
        c.close();
        return rs;
    }

    public String getdeviceid() {
        Cursor c = sdb.rawQuery("select name from netfi where id = -1",null);
        c.moveToFirst();
        String rs = c.getString(0);
        c.close();
        return rs;
    }

    public void delete(int id) {
        sdb.execSQL("delete from netfi where id = "+id);
    }
}