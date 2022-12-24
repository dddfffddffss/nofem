package com.example.nofem;

public class main_adapter_obj implements Comparable<main_adapter_obj>{
    boolean isfi;
    String name;
    int id,order;

    main_adapter_obj(int id, String name, boolean isfi, int order){
        this.id=id;
        this.name=name;
        this.isfi=isfi;
        this.order=order;
    }

    public int compareTo(main_adapter_obj obj1){
        return obj1.getOrder()<getOrder()?1:-1;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setId(int id){
        this.id=id;
    }
    public void setOrder(int order){
        this.order=order;
    }

    public String getName(){
                return name;
            }
    public boolean getIsfi(){
        return isfi;
    }
    public int getId(){
        return id;
    }
    public int getOrder(){
        return order;
    }
}
