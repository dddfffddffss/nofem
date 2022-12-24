package com.example.nofem;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class main_con extends AppCompatActivity {
    EditText et;
    ScrollView scv;
    InputMethodManager imm;
    MenuItem cloud,schitem,save,addindex;

    all_Data d;
    all_nData nd;
    Resources r;

    int id,size,sel,indexturn,indexcounter=-1;
    int tem=0;
    int clickins=0;
    int clickdly=0;
    int savecount=1;
    boolean sch=false;
    String encodes,decodes,name,rawindex;
    SpannableString contents;
    Drawable indimg;
    ArrayList<Integer> allid;
    HashMap<Integer,Integer> inidtosel = new HashMap<>();
    HashMap<Integer,Integer> inidtoid = new HashMap<>();
    HashMap<Integer,String> inidtoexplain = new HashMap<>();
    HashMap<Integer,Integer> teminidtosel = new HashMap<>();
    HashMap<Integer,Integer> teminidtoid = new HashMap<>();
    HashMap<Integer,String> teminidtoexplain = new HashMap<>();
    ArrayList<MyClickableSpan> m;

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_con);
        imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        et = findViewById(R.id.et);
        scv = findViewById(R.id.scv);
        r = getResources();
        indimg = r.getDrawable(R.drawable.index);
        indimg.setBounds(0,0,30,30);

        id = getIntent().getIntExtra("id",0);
        indexturn = getIntent().getIntExtra("indexturn",0)+1;
        if(indexturn==5)Toast.makeText(getApplicationContext(),R.string.Main_Con_indexturnerror,Toast.LENGTH_LONG).show();

        d = new all_Data(0,this);
        nd = new all_nData(this);
        name = d.getnamebyid(id);
        encodes = d.getcontents(id);
        size = d.getsize();
        indexcounter = d.getinidcounterbyid(id);
        rawindex = d.getindexcontents(id);
        allid = d.getallfileid();
        while(rawindex.contains("/!/")){
            String s2 = rawindex.substring(0,rawindex.indexOf("/!/"));
            int inid = Integer.parseInt(s2.substring(0,s2.indexOf("/")));
            s2 = s2.substring(s2.indexOf("/")+1);
            inidtoid.put(inid,Integer.parseInt(s2.substring(0,s2.indexOf("/"))));
            s2 = s2.substring(s2.indexOf("/")+1);
            inidtoexplain.put(inid,s2.substring(0,s2.indexOf("/")));
            s2 = s2.substring(s2.indexOf("/")+1);
            inidtosel.put(inid,Integer.parseInt(s2));
            rawindex = rawindex.substring(rawindex.indexOf("/!/")+3);
        }
        for(Integer inid:inidtoid.keySet()){
            if(!allid.contains(inidtoid.get(inid))){
                inidtoid.remove(inid);
                inidtoexplain.remove(inid);
                inidtosel.remove(inid);
            }
        }

        m = new ArrayList<>();

        getSupportActionBar().setTitle(name);

        try {
            decodes = URLDecoder.decode(encodes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        contents=new SpannableString(decodes);
        tem=0;

        for(final Integer inid : inidtosel.keySet()){
            final Integer temsel = inidtosel.get(inid);
            contents.setSpan(new IndexSpan(0.5f,indimg,inid),temsel, temsel+3, 0);
            contents.setSpan(new MyClickableSpan(inid),temsel,temsel+3,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if(temsel+2>contents.length()-1)contents=new SpannableString(new SpannableStringBuilder(contents).append(" "));
        }
        et.setMovementMethod(LinkMovementMethod.getInstance());

        et.setText(contents);
        et.setTextSize(size);

        et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                if(savecount % 2 == 1) {
                    int k = getOffset(et, e);
                    if(e.getAction()==MotionEvent.ACTION_UP) {
                        clickins++;
                        hd.sendMessageDelayed(new Message(), 200);
                        ifclickindex(et,k);
                    }
                    if(e.getAction()==MotionEvent.ACTION_UP&&clickins-clickdly==1) {
                        try {Thread.sleep(200);}
                        catch (Exception ignored) {}
                    }
                    if(e.getAction()==MotionEvent.ACTION_UP&&clickins-clickdly>1){
                        onOptionsItemSelected(save);
                        et.setSelection(k);
                    }
                    if(clickins>15||clickins<clickdly){
                        clickins=0;
                        clickdly=0;
                    }
                    return true;
                }
                else {
                    return false;
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler hd = new Handler(){
        public void handleMessage(Message m){
            clickdly++;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_con,menu);
        cloud = menu.findItem(R.id.cloud);
        schitem = menu.findItem(R.id.sch);
        save = menu.findItem(R.id.sv);
        addindex = menu.findItem(R.id.addindex);
        if(nd.getname(id).length()!=0)cloud.setTitle(R.string.All_down);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.sv) {
            int tem1;
            if (savecount % 2 == 0) {
                contents = new SpannableString(et.getText());
                decodes = contents.toString();
                try {
                    encodes = URLEncoder.encode(decodes,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                d.setcontents(id,encodes);
                IndexSpan[] iss = contents.getSpans(0,contents.length(),IndexSpan.class);
                for(IndexSpan iss1:iss){
                    if(contents.getSpanEnd(iss1)==contents.getSpanStart(iss1))continue;
                    tem1=iss1.getid();
                    teminidtosel.put(tem1,contents.getSpanStart(iss1));
                    teminidtoid.put(tem1,inidtoid.get(tem1));
                    teminidtoexplain.put(tem1,inidtoexplain.get(tem1));
                }
                Log.v("로그",teminidtoid.size()+"");
                inidtosel.clear();
                inidtoid.clear();
                inidtoexplain.clear();
                inidtoid.putAll(teminidtoid);
                inidtoexplain.putAll(teminidtoexplain);
                inidtosel.putAll(teminidtosel);
                teminidtosel.clear();
                teminidtoid.clear();
                teminidtoexplain.clear();
                StringBuilder temsb = new StringBuilder();
                Log.v("로그",inidtoid.size()+" /!/ ");
                for(Integer inid:inidtoid.keySet()){
                    temsb.append(inid).append("/")
                            .append(inidtoid.get(inid)).append("/")
                            .append(inidtoexplain.get(inid)).append("/")
                            .append(inidtosel.get(inid)).append("/!/");
                    Log.v("로그",inid+" /?/");
                }
                d.setindexcontents(id,temsb.toString());


                imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                save.setTitle(R.string.All_edit);
                schitem.setVisible(true);
                cloud.setVisible(true);
                addindex.setVisible(false);
                et.setCursorVisible(false);

                clickins=0;
                clickdly=0;
                try {Thread.sleep(100);}
                catch (Exception ignored) {}
                Toast.makeText(getApplicationContext(),String.format(r.getString(R.string.Main_Con_saveafter), name), Toast.LENGTH_SHORT).show();
            }
            if (savecount % 2 == 1) {
                et.setSelection(et.length());

                et.requestFocus();
                imm.showSoftInput(et, 0);

                save.setTitle(R.string.All_save);
                schitem.setVisible(false);
                cloud.setVisible(false);
                addindex.setVisible(true);
                et.setCursorVisible(true);
            }
            savecount++;
        }

        if(item.getItemId()==R.id.sch) {
            if(sch){
                BackgroundColorSpan[] bs = contents.getSpans(0,contents.length()-1,BackgroundColorSpan.class);
                for(BackgroundColorSpan bs1:bs)contents.removeSpan(bs1);
                et.setText(contents);
                sch=false;
                schitem.setTitle(R.string.All_search);
                save.setVisible(true);
                cloud.setVisible(true);
            }
            else {
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                final EditText etsch = new EditText(this);
                alert.setTitle(R.string.All_search);
                alert.setView(etsch);

                alert.setPositiveButton(R.string.All_search, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String schs;
                        sch = true;
                        schs = etsch.getText().toString();
                        int p = -schs.length();
                        if (schs.length() == 0) onBackPressed();
                        while (true) {
                            p = decodes.indexOf(schs, p + schs.length());
                            if (p == -1) break;
                            contents.setSpan(new BackgroundColorSpan(Color.RED), p, p + schs.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                        et.setText(contents);
                        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                        save.setVisible(false);
                        cloud.setVisible(false);
                        schitem.setTitle(R.string.All_cancle);
                    }
                });
                alert.show();
            }
        }

        if(item.getItemId()==R.id.cloud) {
            if(nd.getname(id).length()!=0){
                cloud.setTitle(R.string.All_up);
                nd.delete(id);
            }
            else {
                if(decodes.length()<2)return false;
                cloud.setTitle(R.string.All_down);
                nd.add(id,name,encodes);
            }
            Toast.makeText(getApplicationContext(),R.string.All_guldown, Toast.LENGTH_SHORT).show();
        }

        if(item.getItemId()==R.id.size) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.All_size);
            String[]  ss = new String[]{
                    r.getString(R.string.All_sizesmall),
                    r.getString(R.string.All_sizesmaller),
                    r.getString(R.string.All_sizemiddle),
                    r.getString(R.string.All_sizebigger),
                    r.getString(R.string.All_sizebig)};
            alert.setSingleChoiceItems(ss, (size-14)/2,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            size = 14+2*which;
                        }
                    });
            alert.setPositiveButton(R.string.All_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    d.setsize(size);
                    et.setTextSize(size);
                }
            });
            alert.show();
        }

        if(item.getItemId()==R.id.addindex) {
            int o=0;
            final int s = et.getSelectionStart();
            final SpannableStringBuilder ssbtem = new SpannableStringBuilder(new SpannableString(et.getText()));
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);

            View inf = getLayoutInflater().inflate(R.layout.main_con_addindex,null);
            final EditText indexedittext = inf.findViewById(R.id.indexedit);
            final Spinner spinner = inf.findViewById(R.id.spinner);
            String[] ss = new String[allid.size()-1];
            if(ss.length==0){
                Toast.makeText(getApplicationContext(),R.string.All_brank,Toast.LENGTH_SHORT).show();
                return false;
            }
            for(Integer q:allid){
                if(q==id)continue;
                ss[o++] = d.getnamebyid(d.getpidbyid(q))+"/"+d.getnamebyid(q);
            }
            ArrayAdapter<String> sAdapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,ss);
            spinner.setAdapter(sAdapter);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    sel=position;
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    sel=-1;
                }
            });
            alert.setView(inf);
            alert.setPositiveButton(R.string.All_ok, new DialogInterface.OnClickListener() {
                int p;
                private void seto(int p){
                    this.p=p;
                }
                @RequiresApi(api = Build.VERSION_CODES.Q)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    indexcounter++;
                    seto(indexcounter);
                    if(sel==-1)return;
                    String encodedindex="";
                    ssbtem.insert(s,"[*]");
                    inidtosel.put(indexcounter,s);
                    inidtoid.put(indexcounter,allid.get(sel));
                    try {
                        encodedindex = URLEncoder.encode(indexedittext.getText().toString(),"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    inidtoexplain.put(indexcounter,encodedindex);
                    if(et.getSelectionEnd()>et.length()-2)ssbtem.append(" ");
                    contents = new SpannableString(ssbtem);
                    contents.setSpan(new MyClickableSpan(indexcounter),s,s+3,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    contents.setSpan(new IndexSpan(0.5f,indimg,indexcounter),s, s+3, 0);
                    et.setText(contents);
                    Log.v("로그","후위 인덱스 카운터 "+indexcounter);
                    d.setinidcounterbyid(id,indexcounter);
                }
            });
            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed() {
        if(savecount %2==0){
            et.setText(contents);
            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
            save.setTitle(R.string.All_edit);
            schitem.setVisible(true);
            cloud.setVisible(true);
            addindex.setVisible(false);
            et.setCursorVisible(false);

            clickins=0;
            clickdly=0;
            savecount++;
        }
        else if(sch) onOptionsItemSelected(schitem);
        else finish();
    }

    //출처: https://fimtrus.tistory.com/entry/TextView에서-터치된-영역의-Offset-가져오기 [Lv.Max 를 꿈꾸는 개발자 블로그]
    public int getOffset(EditText text, MotionEvent event) {
        int positionX = (int) event.getX();
        int positionY = (int) event.getY();

        positionX -= text.getTotalPaddingLeft();
        positionY -= text.getTotalPaddingTop();

        Layout layout = text.getLayout();

        int line = layout.getLineForVertical(positionY);

        return layout.getOffsetForHorizontal(line, positionX);
    }

    public static class IndexSpan extends ImageSpan {

        float proportion;
        Drawable d;
        int id;

        @RequiresApi(api = Build.VERSION_CODES.Q)
        public IndexSpan(float proportion, Drawable d, int id) {
            super(d);
            this.d=d;
            this.id = id;
            this.proportion=proportion;
        }

        public int getid(){
            return id;
        }
    }

    public class MyClickableSpan extends ClickableSpan{
        int inid;
        MyClickableSpan(int inid) {
            super();
            this.inid = inid;
        }
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(@NonNull View widget) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(main_con.this);
            EditText temtv = new EditText(main_con.this);
            String decodedindex="";
            try {
                decodedindex = URLDecoder.decode(inidtoexplain.get(inid),"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            SpannableString ins = new SpannableString(decodedindex);

            final String finalDecodedindex = decodedindex;
            alert.setPositiveButton(R.string.All_edit, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final EditText temet = new EditText(main_con.this);
                    temet.setText(finalDecodedindex, TextView.BufferType.EDITABLE);
                    temet.setBackground(null);
                    alert.setView(temet);
                    alert.setPositiveButton(R.string.All_save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String temets = temet.getText().toString();
                            try {
                                temets = URLEncoder.encode(temets,"UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            inidtoexplain.remove(inid);
                            inidtoexplain.put(inid,temets);
                            Toast.makeText(getApplicationContext(),R.string.Main_Con_indextemsave,Toast.LENGTH_LONG).show();
                        }
                    });
                    alert.show();
                    if(savecount%2==1){
                        et.setSelection(et.length());

                        et.requestFocus();
                        imm.showSoftInput(et, 0);

                        save.setTitle(R.string.All_save);
                        schitem.setVisible(false);
                        cloud.setVisible(false);
                        addindex.setVisible(true);
                        et.setCursorVisible(true);
                        savecount++;
                    }
                }
            });

            if(indexturn<3){
                ins.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        Intent newi = new Intent(getApplicationContext(),main_con.class);
                        newi.putExtra("id",inidtoid.get(inid));
                        newi.putExtra("indexturn", indexturn);
                        startActivity(newi);
                    }
                },0,decodedindex.length(),0);
            }
            temtv.setText(ins, TextView.BufferType.SPANNABLE);
            temtv.setShowSoftInputOnFocus(false);
            temtv.setMovementMethod(LinkMovementMethod.getInstance());
            temtv.setCursorVisible(false);
            temtv.setBackground(null);
            alert.setView(temtv);
            alert.show();
        }
    }

    private void ifclickindex(EditText et,int k){
        k+=1;
        int ktem = k-5;
        while(ktem<--k) {
            if (inidtosel.containsValue(k)) {
                ClickableSpan cs = contents.getSpans(k, k + 3, ClickableSpan.class)[0];
                cs.onClick(et);
                clickins=0;
                clickdly=0;
                break;
            }
        }
    }
}
