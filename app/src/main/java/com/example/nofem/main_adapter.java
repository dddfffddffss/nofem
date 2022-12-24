package com.example.nofem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;

public class main_adapter extends RecyclerView.Adapter<main_adapter.ViewHolder> {

    ArrayList<main_adapter_obj> objgrp;
    main_adapter.ViewHolder vh;
    boolean issort;
    Context c;

    public interface onclicklistener{
        void ontimeclick(View v,int s,boolean isfi,int adapterposition);
    }

    private onclicklistener ocl= null;
    public void setocl(onclicklistener ocl){
        this.ocl=ocl;
    }

    main_adapter(ArrayList<main_adapter_obj> objgrp, Context c){
        this.objgrp = objgrp;
        this.c=c;
        Collections.sort(objgrp);
    }

    @Override
    public main_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.main_item, parent, false);
        vh = new main_adapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(main_adapter.ViewHolder holder, int position) {
        holder.onBind(objgrp.get(position));
    }

    @Override
    public int getItemCount() {
        return objgrp.size() ;
    }

    public void setorder(int updown,int sortp) {
        if(sortp-updown>objgrp.size()-1||sortp-updown<0)return;
        objgrp.get(sortp).setOrder(sortp-updown);
        objgrp.get(sortp-updown).setOrder(sortp);
    }

    public void setissort(boolean issort){
        this.issort = issort;
    }

    public void revlaue (ArrayList<main_adapter_obj> objgrp){
        this.objgrp=objgrp;
        Collections.sort(objgrp);
        notifyDataSetChanged();
    }

    public ArrayList<main_adapter_obj> getobjgrp(){
        return objgrp;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dblist;
        ImageView isf;
        ImageView up;
        ImageView down;
        int k;

        ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int Clickedid = objgrp.get(getAdapterPosition()).getId();
                    boolean isfi = objgrp.get(getAdapterPosition()).getIsfi();
                    if(ocl!=null)ocl.ontimeclick(v,Clickedid,isfi,getAdapterPosition());
                }
            });
            dblist = itemView.findViewById(R.id.title);
            isf = itemView.findViewById(R.id.img);
            up = itemView.findViewById(R.id.up);
            down = itemView.findViewById(R.id.down);
            up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    k=getAdapterPosition();
                    setorder(1,k);
                    revlaue(getobjgrp());
                }
            });
            down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    k=getAdapterPosition();
                    setorder(-1, getAdapterPosition());
                    revlaue(getobjgrp());
                }
            });
        }

        public void onBind(main_adapter_obj obj1) {
            dblist.setText(obj1.getName());
            if(obj1.getIsfi())isf.setImageResource(R.drawable.fi);
            else isf.setImageResource(R.drawable.fo);
            if(issort){
                up.setVisibility(View.VISIBLE);
                down.setVisibility(View.VISIBLE);
            }
            else {
                up.setVisibility(View.INVISIBLE);
                down.setVisibility(View.INVISIBLE);
            }
        }
    }
}
