package com.damian.aldoc;

/**
 * Created by dawid on 2017-05-13.
 */

public class Disease {
    String name;
    String did;

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

    public void setDid(String d) { did = d; }
    public String getDid() {
        return did;
    }

    public void setName(String n) { name = n; }
    public String getName() {
        return name;
    }
}
