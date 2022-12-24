package com.example.nofem.fromnet;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nofem.R;
import com.example.nofem.all_Data;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

public class net_con extends AppCompatActivity {
    int t;
    public Button back,fo,delete,fin;
    public TextView netcont,turn,num;
    all_Data d;
    ArrayList<String> rs = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rs=getIntent().getStringArrayListExtra("tag");

        setContentView(R.layout.net_con);

        fo = findViewById(R.id.fo);
        back = findViewById(R.id.back);
        delete = findViewById(R.id.delete);
        fin = findViewById(R.id.fin);
        netcont = findViewById(R.id.netcont);
        turn = findViewById(R.id.turn);
        num = findViewById(R.id.num);
        d = new all_Data(0,this);

        for(int i=rs.size()-1;i>-1;i--){
            if(rs.get(i).length()<2)rs.remove(i);
            try {
                rs.set(i,URLDecoder.decode(rs.get(i),"UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        t=-1;
        num.setText(rs.size()+"");

        fo.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                ++t;
                if(t==rs.size())t=0;
                turn.setText((t+1)+"");
                netcont.setText(rs.get(t));
            }
        });

        back.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                --t;
                if(t==-1)t=rs.size()-1;
                turn.setText((t+1)+"");
                netcont.setText(rs.get(t));
            }
        });

        delete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                String s="";
                try {
                    s = URLEncoder.encode(rs.get(t),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String name;
                do{
                     name = "net"+d.getnetcount();
                }while(d.isalblename(name));
                d.add(name,s);
                Toast.makeText(getApplicationContext(),R.string.All_saveafter,Toast.LENGTH_SHORT).show();
            }
        });

        fin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent i = new Intent();
                setResult(RESULT_OK,i);
                finish();
            }
        });
        fo.performClick();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}