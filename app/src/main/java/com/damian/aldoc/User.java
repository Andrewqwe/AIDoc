package com.damian.aldoc;

/**
 * Created by Radosław on 24-04-2017.
 */

public class User {
    public String name;
    public String email;
    public String pesel;
    public String phonenumber;

    public User() {}
    public User(String name,String email, String pesel, String phonenumber) {
        this.email = email;
        this.name = name;
        this.pesel = pesel;
        this.phonenumber = phonenumber;
    }
    public User(String name,String email) {
        this.email = email;
        this.name = name;
    }


}
