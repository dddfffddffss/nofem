package com.example.nofem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class all_Data extends AppCompatActivity {

    SQLiteDatabase sdb;
    int pid,idpointer;
    Context c;

    public all_Data(int pid, Context c){
        this.pid = pid;
        this.c = c;
        sdb = c.openOrCreateDatabase("mainnofem",MODE_PRIVATE,null);
        sdb.execSQL("create table IF NOT EXISTS fi (" +
                "pid int," +
                "id int," +
                "name text," +
                "isfi boolean," +
                "contents text," +
                "sortid int," +
                "inidcounter int," +
                "indexcontents text)");

        Cursor cs = sdb.rawQuery("select id from fi where name = 'root' and id = 0",null);
        if(cs.getCount()==0)sdb.execSQL(" insert into fi(name,pid,id) values ('root',17,0)");

        cs = sdb.rawQuery("select id from fi where name = 'idpointer' and pid = -1",null);
        if(cs.getCount()==0){
            sdb.execSQL(" insert into fi(name,pid,id,inidcounter) values ('idpointer',-1,1,0)");
            idpointer=1;
            return;
        }
        cs.moveToFirst();
        idpointer =  cs.getInt(0);
        cs.close();
    }

    public void add(String name) {
        sdb.execSQL("insert into fi(pid,id,name,isfi,sortid) values ('"+pid+"',"+idpointer+",'"+name+"',0,"+idpointer+")");
        moveidpointerback();
    }

    public void add(String name,String contents) {
        sdb.execSQL("insert into fi(pid,id,name,isfi,contents,sortid,inidcounter,indexcontents) " +
                "values ('"+pid+"',"+idpointer+",'"+name+"',1,'"+contents+"',"+idpointer+",0,' ')");
        moveidpointerback();
    }

    public void moveidpointerback() {
        idpointer++;
        sdb.execSQL("update fi set id = "+idpointer+" where pid = "+(-1)+" and name = 'idpointer'");
    }

    public int getidpointer() {
        return idpointer;
    }

    public ArrayList<Integer> getallfileid() {
        ArrayList<Integer> as = new ArrayList<>();
        Cursor c = sdb.rawQuery("select id from fi where id > 0 and isfi = 1",null);
        while (c.moveToNext()) as.add(c.getInt(0));
        c.close();
        return as;
    }

    public HashMap<Integer,Boolean> currentidtoisfi() {
        HashMap<Integer,Boolean> as = new HashMap<>();
        Cursor c = sdb.rawQuery("select id,isfi from fi where pid = '"+pid+"'",null);
        while (c.moveToNext()) as.put(c.getInt(0), c.getInt(1) > 0);
        c.close();
        return as;
    }

    public HashMap<Integer,String> currentidtoname() {
        HashMap<Integer,String> as = new HashMap<>();
        Cursor c = sdb.rawQuery("select id,name from fi where pid = '"+pid+"'",null);
        while (c.moveToNext()) as.put(c.getInt(0), c.getString(1));
        c.close();
        return as;
    }

    public HashMap<Integer,Integer> currentidtoorder() {
        HashMap<Integer,Integer> as = new HashMap<>();
        Cursor c = sdb.rawQuery("select id,sortid from fi where pid = '"+pid+"'",null);
        while (c.moveToNext()) as.put(c.getInt(0), c.getInt(1));
        c.close();
        return as;
    }

    public void setpid(int pid) {
        this.pid = pid;
    }

    public int getppid() {
        Cursor c = sdb.rawQuery("select pid from fi where id = "+pid,null);
        c.moveToFirst();
        int rs = c.getInt(0);
        c.close();
        return rs;
    }

    public String getpdir() {
        Cursor c = sdb.rawQuery("select name from fi where id = "+pid,null);
        c.moveToFirst();
        String rs = c.getString(0);
        c.close();
        return rs;
    }

    public void setorder(int id,int order) {
        sdb.execSQL("update fi set sortid = "+order+" where id = "+id);
    }

    public String getnamebyid(int id) {
        Cursor c = sdb.rawQuery("select name from fi where id = "+id,null);
        c.moveToFirst();
        String rs = c.getString(0);
        c.close();
        return rs;
    }

    public int getinidcounterbyid(int id) {
        Cursor c = sdb.rawQuery("select inidcounter from fi where id = "+id,null);
        c.moveToFirst();
        int rs = c.getInt(0);
        c.close();
        return rs;
    }

    public void setinidcounterbyid(int id,int inid) {
        sdb.execSQL("update fi set inidcounter = "+inid+" where id = "+id);
    }

    public int getpidbyid(int id) {
        Cursor c = sdb.rawQuery("select pid from fi where id = "+id,null);
        c.moveToFirst();
        int rs = c.getInt(0);
        c.close();
        return rs;
    }

    public boolean isalblename(String name) {
        Cursor c = sdb.rawQuery("select name,pid from fi where name = '"+name+"' and pid = "+pid,null);
        boolean rs = c.getCount()==0;
        c.close();
        return !rs;
    }

    public String getcontents(int id) {
        Cursor c = sdb.rawQuery("select contents from fi where id = "+id,null);
        if(c.getCount()==0)return "";
        c.moveToFirst();
        String rs = c.getString(0);
        c.close();
        return rs;
    }

    public void setindexcontents(int id,String indexcontents) {
        sdb.execSQL("update fi set indexcontents = '"+indexcontents+"' where id = "+id);
    }

    public String getindexcontents(int id) {
        Cursor c = sdb.rawQuery("select indexcontents from fi where id = "+id,null);
        c.moveToFirst();
        String rs = c.getString(0);
        c.close();
        return rs;
    }

    public void setcontents(int id,String contents) {
        sdb.execSQL("update fi set contents = '"+contents+"' where id = "+id);
    }

    public void setpidbyid(int pid,int id) {
        sdb.execSQL("update fi set pid = '"+pid+"' where id = "+id);
    }

    public void setnamebyid(String name,int id) {
        sdb.execSQL("update fi set name = '"+name+"' where id = "+id);
    }

    public void setsize(int size) {
        sdb.execSQL("update fi set pid = "+size+" where id=0 and name = 'root'");
    }

    public int getsize() {
        Cursor c = sdb.rawQuery("select pid from fi where id=0 and name = 'root'",null);
        c.moveToFirst();
        int rs = c.getInt(0);
        c.close();
        return rs;
    }

    public int getnetcount() {
        Cursor c = sdb.rawQuery("select inidcounter from fi where pid=-1 and name = 'idpointer'",null);
        c.moveToFirst();
        int rs = c.getInt(0);
        c.close();
        sdb.execSQL("update fi set inidcounter = "+(rs+1)+" where pid=-1 and name = 'idpointer'");
        return rs;
    }

    public void delete(int id) {
        sdb.execSQL("delete from fi where pid = "+id);
        sdb.execSQL("delete from fi where id = "+id);
    }
}