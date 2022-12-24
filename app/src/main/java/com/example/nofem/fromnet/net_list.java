package com.example.nofem.fromnet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.nofem.R;
import com.example.nofem.all_nsData;

import java.util.ArrayList;

public class net_list extends AppCompatActivity {
    ArrayList<netobj> netobjgrp = new ArrayList<>();
    all_nsData nsd;
    Button b;
    MenuItem del,home;
    Myadapter ma;

    boolean dele = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_list);

        RecyclerView rv = findViewById(R.id.fornetlist);
        nsd = new all_nsData(this);
        ArrayList<Integer> is = nsd.getnetid();
        int j=-1;
        while(++j<is.size())if(!nsd.getcontents(is.get(j)).equals("du"))netobjgrp.add(new netobj(is.get(j)));

        rv.setLayoutManager(new LinearLayoutManager(this));
        ma = new Myadapter(netobjgrp);
        rv.setAdapter(ma);
    }

    public boolean onCreateOptionsMenu(Menu m){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.net_savecon,m);
        del = m.findItem(R.id.dels);
        home = m.findItem(R.id.homes);
        b = findViewById(R.id.b23);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.homes){
            finish();
        }
        if(item.getItemId()==R.id.dels){
            ma.revlaue(netobjgrp);
            if(dele) {
                del.setTitle(R.string.All_remove);
                home.setVisible(true);
                dele=false;
            }
            else {
                del.setTitle(R.string.All_save);
                home.setVisible(false);
                dele=true;
            }
        }
        return true;
    }

    public void setNetobjgrp(ArrayList<netobj> newog){
        netobjgrp = newog;
    }

    public class Myadapter extends RecyclerView.Adapter<Myadapter.ViewHolder> {

        ArrayList<netobj> netobjgrp;

        Myadapter(ArrayList<netobj> netobjgrp){
            this.netobjgrp = netobjgrp;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext() ;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

            View view = inflater.inflate(R.layout.net_list_item, parent, false) ;

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.onBind(netobjgrp.get(position));
        }

        @Override
        public int getItemCount() {
            return netobjgrp.size() ;
        }

        public void revlaue (ArrayList<netobj> netobjgrp){
            this.netobjgrp=netobjgrp;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView dblist;
            Button b;

            ViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                nsd.delete(netobjgrp.get(getAdapterPosition()).getid());
                                netobjgrp.remove(netobjgrp.get(getAdapterPosition()));
                                setNetobjgrp(netobjgrp);
                                notifyItemRemoved(getAdapterPosition());
                                notifyItemRangeChanged(getAdapterPosition(),getItemCount());
                            }
                        });
                        Intent i = new Intent(net_list.this, net_savecon.class);
                        i.putExtra("id",netobjgrp.get(getAdapterPosition()).getid());
                        startActivity(i);
                    }
                });
                dblist = itemView.findViewById(R.id.nettitle);
                b = itemView.findViewById(R.id.b23);
            }

            public void onBind(netobj obj1) {
                dblist.setText(String.valueOf(obj1.getid()));
                if(dele)b.setVisibility(View.VISIBLE);
                else b.setVisibility(View.INVISIBLE);
            }
        }
    }


    public static class netobj{
        String name="";
        int id;

        netobj(String name,int id){
            this.id = id;
            this.name=name;
        }
        netobj(int id){
            this.id = id;
        }

        public String getname(){
            return name;
        }

        public int getid(){
            return id;
        }
    }
}