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
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;


/**
 * Created by Radosław on 18-04-2017.
 * Update by Radosław on 24-04-2017.
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

    public static void Initialize(boolean persistence) {
        if (mDatabase == null){
            mDatabase = FirebaseDatabase.getInstance();
            mDatabase.setPersistenceEnabled(persistence);
        }
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
        Initialize(true);
        SetLocation(path);
        mDatabaseReference.push().setValue(object);  //wysyła obiekt object do bazy jako dziecko lokacji path
    }

    /**
     * Metoda publiczna wysyłająca obiekt do lokacji visits w Bazie danych
     * @param object wysyłany obiekt klasy Visit
     */
    static public void SendObjectVisitToDatabase(Object object) {
        Initialize(true);
        SetLocation("visits");
        mDatabaseReference.push().setValue(object);
    }

    /**
     * Metoda publiczna wysyłająca obiekt do lokacji notes w Bazie danych
     * @param object wysyłany obiekt klasy Note - której jeszcze nie ma ????
     */
    static public void SendObjectNotesToDatabase(Object object) {
        Initialize(true);
        SetLocation("notes");
        mDatabaseReference.push().setValue(object);
    }

    /**
     * Metoda prywatna pobierająca z bazy danych dane o zalogowanym użytkowniku
     * @return Zwraca tabele stringów gdzie kolejno jest nazwa użytkownika,e-mail,UID lub pustą tabelę gdy użytkownik nie jest zalogowany
     */
    static private String[]  GetUserInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
            String[] details = {name,email,uid};
            return details;
        }
            return null;
    }

    /**
     * Metoda publiczna pobierająca z bazy UID użytkownika
     * @return String UID lub null gdy użytkownik nie jest zalogowany
     */
    static public String GetUserUID() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            return uid;
        }
        return null;
    }

    /**
     * Metoda publiczna tworząca profil użytkownika bazujący na UID. Funkcja pobiera imie(nazwe uzytkownika) i adres e-mail z danych podanych przy rejestracji(logowaniu)
     * Funkcja nadpisuje profil użytkownika tz nie wypełnienie wszystkich danych będzie skutkować wyczyszczeniem niewypełnionych pól.
     * Funkcja tworzy obiekt klasy User w trakcie wykonania (dobrze żeby tak pozostało)
     * @param pesel - nr pesel użytkownika
     * @param phonenumber - nr telefonu użytkownika
     */
    static public void SendObjectProfileInfoToDatabase(String pesel,String phonenumber) {
        Initialize(true);
        SetLocation("users");
        String[] details = GetUserInfo();
        User user = new User(details[0],details[1],pesel,phonenumber);
        mDatabaseReference.child(GetUserUID()).setValue(user);
       // mDatabaseReference.child(GetUserUID()).child("pesel").setValue(pesel); - podmienianie tylko gałęzi pesel (można warunek if zrobić i sprawdzać czy pesel != null i dopiero wtedy podmieniać) to już zależy od metody zrobienia panelu do wprowadzania danych
    }

    /**
     * Obecnie Read From Visits nie dziala
     */
    static public void ReadFromVisits() {
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Visit visits = dataSnapshot.getValue(Visit.class);  //czytanie z bazy i tworzenie przycisku
                VisitsActivity helper = new VisitsActivity();
                //helper.addVisit(visits);
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

//TODO  Metoda odczytująca z bazy danych. Metody wysyłające dane do większej ilości funkcjionalności. Podpięcie tworzenia grup i dodawania członków rodziny do nich.
//TODO  Zejście z metod obsługiwanych przez podanie ścieżki aby je wywołać.