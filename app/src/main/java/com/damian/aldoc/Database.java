package com.damian.aldoc;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;




/**
 * Created by Radosław on 18-04-2017.
 */

public class Database {

    //Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    private void Initalize (){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
    }
    public void SetLocation(String path){
        mDatabaseReference = mFirebaseDatabase.getReference().child(path);
    }
    public void SendObjectToDatabase(String path,Object object){
        mDatabaseReference.getRef().child(path).push().setValue(object);  //wysyła obiekt object do bazy jako dziecko lokacji path
    }
    private void SetPersistence(boolean persistence){
        mFirebaseDatabase.getInstance().setPersistenceEnabled(persistence);  //ustawienie zapisywania tez lokalnie
    }
    public void ReadFromVisits(){
        SetLocation("visits");
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Visit visits = dataSnapshot.getValue(Visit.class);  //czytanie z bazy i tworzenie przycisku
                new VisitsActivity().addVisit(visits);
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };
        mDatabaseReference.addChildEventListener(mChildEventListener);

    }

    Database(){
        boolean persistance = true;
        Initalize();
        SetPersistence(persistance);
    }

    Database(boolean persistance ){
        Initalize();
        SetPersistence(persistance);
    }

}
