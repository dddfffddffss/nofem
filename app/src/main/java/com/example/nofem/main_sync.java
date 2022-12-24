package com.example.nofem;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;

public class main_sync extends AsyncTask<String,String,String> {

    Context c;
    all_nData nd;
    all_net all_net;

    public main_sync(Context c){
        this.c = c;
        nd = new all_nData(c);
        all_net = new all_net();
    }

    @Override
    protected String doInBackground(String... params) {
        ArrayList<Integer> ai = nd.getnetid();
        try {
            all_net.deletetocloudbydeviceid(nd.getdeviceid());
            for (Integer i : ai)
                if (i > 0) all_net.sendtocloud(nd.getdeviceid(), i, nd.getname(i), nd.getcontents(i));
        } catch (IOException ignored) {}
        nd=null;
        all_net =null;
        c=null;
        return null;
    }
}