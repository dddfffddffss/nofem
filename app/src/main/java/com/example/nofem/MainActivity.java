package com.example.nofem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nofem.fromnet.net_main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

//문서마다 하이퍼링크 및 연결 구조도 만들기, 주석 기능
//서버 메모를 하이퍼링크 - 인터넷화
//저장 라이브러리도 메인 액티비티화

public class MainActivity extends AppCompatActivity {
    all_Data d;
    main_adapter ma;
    static int iffipointer;
    static RecyclerView rv;
    static boolean[] bs;
    static int[] is;
    static boolean chg=false;
    static boolean move=false;
    Resources r;
    util u = new util();

    static int idpointer,ap;
    static int moveid=0;
    static HashMap<Integer,String> currentidtoname;
    static HashMap<Integer,Boolean> currentidtoisfi;
    static HashMap<Integer,Integer> currentidtoorder;
    static ArrayList<String> currentname = new ArrayList<>();
    static ArrayList<Integer> currentid = new ArrayList<>();
    static ArrayList<main_adapter_obj> objgrp = new ArrayList<>();

    final static int FILE_SELECT_CODE = 559;
    static Uri dburi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        u.verifyStoragePermissions(this);

        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        d = new all_Data(0,this);
        r=getResources();
        main_sync sy = new main_sync(this);
        sy.execute();

        ma = new main_adapter(objgrp,this);
        ma.setocl(new main_adapter.onclicklistener() {
            @Override
            public void ontimeclick(View v,int s,boolean isfi,int adapterpointer) {
                if(!move&&chg){
                    ap=adapterpointer;
                    return;
                }
                if(move&&s==moveid){
                    Toast.makeText(getApplicationContext(),R.string.Main_move_error,Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!isfi)changedir(s);
                else tofile(s);
            }
        });

        changedir(0);
        rv.setAdapter(ma);
    }

    public void onBackPressed() {
        if(idpointer!=0)changedir(d.getppid());
        else super.onBackPressed();
    }

    public void changedir(int newid){
        idpointer = newid;
        d.setpid(idpointer);
        currentidtoname = d.currentidtoname();
        currentidtoisfi = d.currentidtoisfi();
        currentidtoorder = d.currentidtoorder();
        currentname.clear();
        currentid.clear();
        objgrp.clear();
        for (int i : currentidtoname.keySet()){
            objgrp.add(new main_adapter_obj(i,currentidtoname.get(i),currentidtoisfi.get(i),currentidtoorder.get(i)));
            currentname.add(currentidtoname.get(i));
            currentid.add(i);
        }
        ma.revlaue(objgrp);
        getSupportActionBar().setTitle(d.getpdir());
    }

    public void tofile(int id){
        Intent i = new Intent(getApplicationContext(), main_con.class);
        i.putExtra("id",id);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(chg) inflater.inflate(R.menu.main_sort,menu);
        else inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final EditText et = new EditText(MainActivity.this);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        //주 메뉴 '추가'//
        if(item.getItemId()==R.id.add) {
            alert.setTitle(R.string.Main_add);
            alert.setView(et);
            iffipointer=0;
            alert.setSingleChoiceItems(new String[]{r.getString(R.string.All_file),r.getString(R.string.All_folder)}, 0,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            iffipointer = which;
                        }
            });

            alert.setPositiveButton(R.string.All_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                    String s = et.getText().toString();
                    if (s.contains("'")||s.length()>10){
                        Toast.makeText(getApplicationContext(),R.string.All_stringerror,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (d.isalblename(s)){
                        Toast.makeText(getApplicationContext(),R.string.Main_name_existerror,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (iffipointer == 1) d.add(s);
                    else d.add(s, "");
                    d.setorder(d.getidpointer()-1,objgrp.size());
                    changedir(idpointer);
                }
            });

            alert.setNegativeButton(R.string.All_cancle, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int which) {
                    d.dismiss();
                }
            });
            alert.show();
        }

        //주 메뉴 '삭제'//
        if(item.getItemId()==R.id.del) {
            alert.setTitle(R.string.Main_delete);
            int size=objgrp.size();
            String[] ss = new String[size];
            is = new int[size];
            bs = new boolean[size];
            int i=-1;
            for(main_adapter_obj obj1:objgrp){
                is[++i]=obj1.getId();
                ss[i]=obj1.getName();
            }
            alert.setMultiChoiceItems(ss, bs, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which,boolean ischecked) {
                    bs[which]=ischecked;
                }
            });

            alert.setPositiveButton(R.string.All_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                    for(int i=0;i<currentname.size();i++) {
                        if(!bs[i])continue;
                        d.delete(is[i]);
                    }
                    changedir(idpointer);
                }
            });

            alert.setNegativeButton(R.string.All_cancle, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int which) {
                    d.dismiss();
                }
            });
            alert.show();
        }

        //주 메뉴 '정렬'//
        if(item.getItemId()==R.id.sort) {
            if(currentname.size()==0){
                Toast.makeText(getApplicationContext(),R.string.All_cancle,Toast.LENGTH_SHORT).show();
                return true;
            }
            chg=true;
            invalidateOptionsMenu();
            ma.setissort(true);
            ma.revlaue(objgrp);
        }

        //주 메뉴 '이동'//
        if (item.getItemId() == R.id.move) {
            move = true;
            chg = true;
            invalidateOptionsMenu();
            alert.setTitle(R.string.Main_move);
            int size=objgrp.size();
            String[] ss = new String[size];
            is = new int[size];
            bs = new boolean[size];
            int i=-1;
            for(main_adapter_obj obj1:objgrp){
                is[++i]=obj1.getId();
                ss[i]=obj1.getName();
            }
            alert.setSingleChoiceItems(ss, -1, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    moveid=objgrp.get(which).getId();
                }
            });
            alert.setPositiveButton(R.string.All_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                }
            });

            alert.setNegativeButton(R.string.All_cancle, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int which) {
                    d.dismiss();
                    moveid=-1;
                }
            });
            alert.show();
        }

        int j=0;
        //주 메뉴 '정렬 후 저장'//
        if (item.getItemId() == R.id.sortsave) {
            chg = false;
            invalidateOptionsMenu();
            objgrp = ma.getobjgrp();
            for (main_adapter_obj obj1 : objgrp) d.setorder(obj1.getId(), j++);
            ma.setissort(false);
            ma.revlaue(objgrp);
            if(move){
                d.setpidbyid(idpointer,moveid);
                move = false;
                changedir(idpointer);
                ma.revlaue(objgrp);
            }
        }

        //주 메뉴 '이름변경'//
        if (item.getItemId() == R.id.name) {
            final EditText net = new EditText(this);
            alert.setTitle(R.string.Main_rename);
            alert.setView(net);
            String[] ss = new String[objgrp.size()];
            int i=-1;
            for(main_adapter_obj obj1:objgrp){
                ss[++i]=obj1.getName();
            }
            alert.setSingleChoiceItems(ss, -1, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    moveid=objgrp.get(which).getId();
                }
            });
            alert.setPositiveButton(R.string.All_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int which) {
                    String name = net.getText().toString();
                    if(name.contains("'")||name.length()>10){
                        Toast.makeText(getApplicationContext(),R.string.All_stringerror, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    d.setnamebyid(name,moveid);
                    changedir(idpointer);
                    ma.revlaue(objgrp);
                }
            });
            alert.show();
        }

        //주 메뉴 'nofem'//
        if (item.getItemId() == R.id.dif) {
            Intent i = new Intent(this, net_main.class);
            startActivity(i);
        }

        //주 메뉴 '백업'//
        if (item.getItemId() == R.id.bu) {
            try {
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat simpleDate1 = new SimpleDateFormat("yyMMddhhmmss");
                String getTime1 = simpleDate1.format(mDate);

                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();

                File backupDBdir = new File(sd, "/nofem");
                if(!backupDBdir.exists())backupDBdir.mkdirs();

                File currentDB = new File(data, "/data/com.example.nofem/databases/mainnofem");
                File backupDB = new File(sd, "/nofem/"+getTime1+".db");

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());

                src.close();
                dst.close();
                Log.v("로그", "123");
                Toast.makeText(getApplicationContext(), R.string.All_sucess, Toast.LENGTH_SHORT).show();
            }
            catch (Exception e) {
                Toast.makeText(getApplicationContext(), R.string.All_fail, Toast.LENGTH_SHORT).show();
                Log.v("로그",e.getMessage());
            }
        }

        //주 메뉴 '복원'//
        if (item.getItemId() == R.id.rst) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);        //안드로이드 4.4버전 (킷캣) 확인해볼 것  / 안된다 함

            try {
                startActivityForResult(Intent.createChooser(intent, "Select a File to Read"),FILE_SELECT_CODE);
            } catch (android.content.ActivityNotFoundException ex) {
                // Potentially direct the user to the Market with a Dialog
            }
        }

        //주 메뉴 '블루투스'//
        if (item.getItemId() == R.id.blue) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            File backupDB = null;
            try {
                long now = System.currentTimeMillis();
                Date mDate = new Date(now);
                SimpleDateFormat simpleDate1 = new SimpleDateFormat("yyMMddhhmmss");
                String getTime1 = simpleDate1.format(mDate);

                File sd = Environment.getExternalStorageDirectory();
                File data = Environment.getDataDirectory();

                File backupDBdir = new File(sd, "/nofem");
                if(!backupDBdir.exists())backupDBdir.mkdirs();

                File currentDB = new File(data, "/data/com.example.nofem/databases/mainnofem");
                backupDB = new File(sd, "/nofem/"+getTime1+".db");

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());

                src.close();
                dst.close();
            }
            catch (Exception e) {
                Log.v("로그",e.getMessage());
            }

            intent.putExtra(Intent.EXTRA_STREAM, backupDB);
            intent.setType("*/*");

            startActivity(Intent.createChooser(intent,"this is a test"));
        }

        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        switch (requestCode){
            case FILE_SELECT_CODE:
                try {
                    if (resultCode == RESULT_OK) dburi = i.getData();


                    File data = Environment.getDataDirectory();

                    File currentDB = new File(data, "/data/com.example.nofem/databases/mainnofem");
                    File backupDB = new File(u.getRealPathFromURI(this, dburi));

                    FileChannel src = new FileOutputStream(currentDB).getChannel();
                    FileChannel dst = new FileInputStream(backupDB).getChannel();
                    src.transferFrom(dst, 0, dst.size());

                    src.close();
                    dst.close();
                    Toast.makeText(getApplicationContext(), R.string.All_sucess, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.v("복원", Log.getStackTraceString(e));
                    Toast.makeText(getApplicationContext(), R.string.All_fail, Toast.LENGTH_SHORT).show();
                }
                idpointer=0;
                onBackPressed();
        }
        super.onActivityResult(requestCode, resultCode, i);
    }
}
