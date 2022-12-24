package com.example.nofem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class all_nsData extends AppCompatActivity {

    SQLiteDatabase sdb;
    Context c;
    int idpointer;

    public all_nsData(Context c){
        this.c = c;
        sdb = c.openOrCreateDatabase("mainnofem",MODE_PRIVATE,null);
        sdb.execSQL("create table IF NOT EXISTS netsfi (id int,name text,contents text)");
        Cursor cs = sdb.rawQuery("select id from netsfi where contents = 'du'",null);
        if(cs.getCount()==0){
            sdb.execSQL(" insert into netsfi(id,contents) values (1,'du')");
            idpointer = 1;
        }
        else {
            cs.moveToNext();
            idpointer = cs.getInt(0);
        }
        cs.close();
    }

    public void add(String contents){
        sdb.execSQL("update netsfi set id = "+(idpointer+1)+" where contents = 'du'");
        sdb.execSQL("insert into netsfi(id,contents) values ("+idpointer+",'"+contents+"')");
    }

    public boolean isaddable(String s){
        Cursor cs = sdb.rawQuery("select id from netsfi",null);
        boolean sw1 = cs.getCount()<12;
        cs = sdb.rawQuery("select id from netsfi where contents = '"+s+"'",null);
        boolean sw2 = cs.getCount()==0;
        cs.close();
        return sw1&sw2;
    }

    public ArrayList<Integer> getnetid() {
        ArrayList<Integer> as = new ArrayList<>();
        Cursor c = sdb.rawQuery("select id from netsfi",null);
        if(c.getCount()==0){
            c.close();
            return as;
        }
        while(c.moveToNext())as.add(c.getInt(0));
        c.close();
        return as;
    }

    public String getcontents(int id) {
        Cursor c = sdb.rawQuery("select contents from netsfi where id = "+id,null);
        if(c.getCount()==0)return "";
        c.moveToFirst();
        String rs = c.getString(0);
        c.close();
        return rs;
    }

    public void delete(int id) {
        sdb.execSQL("delete from netsfi where id = "+id);
    }
}