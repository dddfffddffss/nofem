package com.example.nofem;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class all_net extends AppCompatActivity {
    URL url;
    HttpURLConnection huc;

    public void request(String Method,String q) throws IOException {
        huc.setReadTimeout(5000);
        huc.setConnectTimeout(5000);
        huc.setRequestMethod(Method);
        huc.setDoInput(true);
        huc.setDoOutput(true);
        huc.connect();

        OutputStream os = huc.getOutputStream();
        os.write(q.getBytes("UTF-8"));
        os.flush();
        os.close();
    }

    public ArrayList<String> respond() throws IOException {
        ArrayList<String> as = new ArrayList<>();
        String s;
        BufferedReader isr = new BufferedReader(new InputStreamReader(huc.getInputStream(),"UTF-8"));
        while((s=isr.readLine())!=null)as.add(s);
        huc.disconnect();
        return as;
    }

    public void sendtocloud(String deviceid,int id,String name,String contents) throws IOException {
        url = new URL("http","218.39.82.155",1424,"/php/send.php");
        huc = (HttpURLConnection) url.openConnection();
        request("POST", "deviceid=" + deviceid + "&id=" + id + "&name=" + name + "&contents=" + contents);
        respond();
    }

    public void deletetocloudbydeviceid(String deviceid) throws IOException {
        url = new URL("http","218.39.82.155",1424,"/php/delete.php");
        huc = (HttpURLConnection) url.openConnection();
        String s = "deviceid="+deviceid;
        request("POST",s);
        respond();
    }

    public ArrayList<String> gettocloud(ArrayList<String> tag) throws IOException {
        url = new URL("http","218.39.82.155",1424,"/php/get.php");
        huc = (HttpURLConnection) url.openConnection();
        StringBuilder s = new StringBuilder("tag=");
        for(String s1:tag) s.append(s1).append(" thisisablank ");
        request("POST", s.toString());
        return respond();
    }
}