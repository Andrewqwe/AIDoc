package com.damian.aldoc;

/**
 * Created by dawid on 2017-05-13.
 */

public class Disease {
    String name;
    String uid;

    Disease(){
    }

    Disease(String n){
        name = n;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public void setName(String n) { name = n; }
    public String getName() {
        return name;
    }

    public void setUid(String u) { uid = u; }
    public String getUid() {
        return uid;
    }
}
