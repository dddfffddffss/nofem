package com.example.nofem.fromnet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.nofem.R;
import com.example.nofem.all_net;
import com.example.nofem.all_nData;

import java.io.IOException;
import java.util.ArrayList;

public class net_main extends AppCompatActivity {

    String s = "";
    all_net all_net;
    public Button bm;
    public Button sd;
    public Button list;
    public TextView tv;
    public EditText et;
    public ArrayList<String> as;
    public ArrayList<String> rs;
    public all_nData nd;
    public AlertDialog.Builder pd;
    public AlertDialog ad;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_main);
        all_net = new all_net();

        bm = findViewById(R.id.bm);
        sd = findViewById(R.id.send);
        list = findViewById(R.id.list);
        tv = findViewById(R.id.tv);
        et = findViewById(R.id.editText);
        as = new ArrayList<>();
        rs = new ArrayList<>();
        nd = new all_nData(this);
        final Intent ls = new Intent(this, net_list.class);
        pd = new AlertDialog.Builder(this);
        ad = pd.create();
        ad.setMessage(getResources().getString(R.string.Net_Main_connecting));
        ad.setCancelable(false);

        bm.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                s = et.getText().toString();
                if(s.length()>10||s.length()==0){
                    Toast.makeText(getApplicationContext(),R.string.All_stringerror,Toast.LENGTH_SHORT).show();
                    return;
                }
                as.add(s.trim());
                et.setText("");
                tv.append(s+"\n");
            }
        });

        sd.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                new network().start();
                ad.show();
            }
        });

        list.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                startActivity(ls);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
    }

    class network extends Thread{
        Message ms;
        Bundle b;
        myhandler mh = new myhandler(net_main.this);
        public void run(){
            try {
                ArrayList<String> asi;
                ms = mh.obtainMessage();
                b = new Bundle();
                asi = all_net.gettocloud(as);
                b.putStringArrayList("asi",asi);
                ms.setData(b);
                mh.sendMessage(ms);
            } catch (IOException e) {
                b.putString("error",e.getMessage());
                ms.setData(b);
                mh.sendMessage(ms);
            }
        }
    }

    class myhandler extends Handler{
        Context c;
        ArrayList<String> irs;

        myhandler(Context c){
            this.c = c;
        }
        @Override
        public void handleMessage(Message m){
            super.handleMessage(m);

            Bundle b = m.getData();
            ad.dismiss();
            if(b.getString("error")!=null){
                Toast.makeText(getApplicationContext(),R.string.Net_Main_notconnected,Toast.LENGTH_SHORT).show();
                return;
            }
            irs=b.getStringArrayList("asi");
            if(irs.size()<2){
                Toast.makeText(getApplicationContext(),R.string.Net_Main_noresult,Toast.LENGTH_SHORT).show();
                return;
            }
            as.clear();
            tv.setText("");
            final Intent i = new Intent(net_main.this, net_con.class);
            i.putExtra("tag",irs);
            startActivityForResult(i,123);
        }
    }
}