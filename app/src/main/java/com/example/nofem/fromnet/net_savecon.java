package com.example.nofem.fromnet;

import android.os.Bundle;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.nofem.R;
import com.example.nofem.all_nsData;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class net_savecon extends AppCompatActivity {
    all_nsData nsv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_savecon);

        nsv = new all_nsData(this);
        String s="";

        TextView tv = findViewById(R.id.netscon);
        try {
            s = URLDecoder.decode(nsv.getcontents(getIntent().getIntExtra("id",0)),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        tv.setText(s);
    }
    public void onBackPressed() {
        finish();
    }
}
