package com.damian.aldoc;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;



/**
 * Created by Radosław on 18-04-2017.
 */

/**
 *
 * Klasa odpowiedzialna za obsługę bazy danych (w trakcie tworzenia)
 *
 */
public class Database {

    //Firebase instance variables
    static private FirebaseDatabase mDatabase;
    static private DatabaseReference mDatabaseReference;
    static private ChildEventListener mChildEventListener;

    public static void Initalize(boolean persistence) {
        if (mDatabase == null){
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(persistence);
        }
        return;
    }

    static public DatabaseReference SetLocation(String path) {
        mDatabaseReference = mDatabase.getReference().child(path);
        return mDatabaseReference;
    }

    //zawsze jest powód do użycia noża

    /**
     * Metoda publiczna do wysyłania dancyh do naszej bazy.
     *
     * @param path  - ścieżka do lokacji nadrzędnej do której chcemy wysłać dane
     * @param object - wysyłany obiekt (obecnie wysyłamy tylko obiekty!!!)
     */
    static public void SendObjectToDatabase(String path, Object object) {
        SetLocation(path);
        mDatabaseReference.push().setValue(object);  //wysyła obiekt object do bazy jako dziecko lokacji path
    }

    static public void ReadFromVisits() {
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Visit visits = dataSnapshot.getValue(Visit.class);  //czytanie z bazy i tworzenie przycisku
                VisitsActivity helper = new VisitsActivity();
                helper.addVisit(visits);
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        };
        SetLocation("visits").addChildEventListener(mChildEventListener);
    }
}