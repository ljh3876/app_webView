package com.example.jinhwan.myapplication;

/**
 * Created by Jinhwan on 2017-05-04.
 */

public class URL {
    String name;
    String url;
    public URL(){}
    public URL(String name, String url){
        this.name = name;
        this.url = url;
    }
    public void setName(String name){
        this.name = name;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public String getName(){
        return name;
    }
    public String getUrl(){
        return url;
    }
}
