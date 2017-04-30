package com.damian.aldoc;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import static com.twitter.sdk.android.core.TwitterCore.TAG;


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
     * Metoda publiczna wysyłająca obiekt do lokacji prescriptions w Bazie danych
     * @param object wysyłany obiekt klasy Prescription
     */
    static public void SendObjectPrescriptionToDatabase(Object object) {
        Initialize(true);
        SetLocation("prescriptions");
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
    static public String[]  GetUserInfo(){
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

    static public Uri  GetUserImage(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Uri photoUrl = user.getPhotoUrl();
            return photoUrl;
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
    static public void SendUserInfoToDatabase(String pesel,String phonenumber) {
        Initialize(true);
        SetLocation("users");
        String[] details = GetUserInfo();
        User user = new User(details[0],details[1],pesel,phonenumber);
        mDatabaseReference.child(GetUserUID()).setValue(user);
       // mDatabaseReference.child(GetUserUID()).child("pesel").setValue(pesel); - podmienianie tylko gałęzi pesel (można warunek if zrobić i sprawdzać czy pesel != null i dopiero wtedy podmieniać) to już zależy od metody zrobienia panelu do wprowadzania danych
    }

    /**
     * Wysyła i uaktoalnia nr pesel użytkownika w bazie danych
     * @param pesel nr pesel użytkownika
     */

    static public void SendUserPeselToDatabase(String pesel){
        Initialize(true);
        SetLocation("users");
        if(pesel != null)
        mDatabaseReference.child(GetUserUID()).child("Pesel").setValue(pesel);
    }

    /**
     * Wysyła i uaktualnia numer telefonu komórkowego użytkownika w bazie danych. Można wywołać podczas wprowadzania numer i późniejszej jego zmiany.
     * @param phone nr telefonu użytkownika
     */
    static public void SendUserPhoneNumberToDatabase(String phone){
        Initialize(true);
        SetLocation("users");
        if(phone != null)
        mDatabaseReference.child(GetUserUID()).child("phone").setValue(phone);
    }
    /**
     * Obecnie Read From Visits nie dziala
     */

    static private void ReadFromVisits() {
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
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());

            }
        };
        SetLocation("visits").addChildEventListener(mChildEventListener);
    }

    static private void ReadProfileDetails(){

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User user = dataSnapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Profile failed, log a message
                Log.w(TAG, "loadProfile:onCancelled", databaseError.toException());
            }
        };
        SetLocation("profile").addListenerForSingleValueEvent(postListener);
    }
    static private void pesel(String pesel){
        ReadProfileDetails();
    };

    static public void Dsasa(String uid){
        Initialize(true);
        Query visitQuery = SetLocation("visits").orderByChild("uid").equalTo(uid);
        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        //Query visitQuery = ref.child("firebase-test").orderByChild("title").equalTo("Apple");

        visitQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled", databaseError.toException());
            }
        });
    }

    /**
     * Metoda do kasowania całego obiektu o danym kluczu.
     * @param uid klucz do danego miejsca w wizytach np. -KivIPsb0iuUBuOns6Bv
     */
    static public void DeleteVisitFromDatabase(String uid){
        Initialize(true);
        DatabaseReference ref = SetLocation("visits");
        ref.child(uid).removeValue();
    }

    /**
     *Metoda która wyszukuje w bazie wizyty które na konkretnej pozycji - parametrName mają dokładną wartość - value
     * Metoda nic nie zwraca dlatego w OnChildAdded należy dodać wywołanie własnej funkcji która będzie coś robiła z tymi obiektami
     * DataSnapshot zawsze tworzy liste nawet 1 elementową (komputer jest głupi i nie wie czy szukana przez nas dana jest tylko w 1 miejscu)
     * @param parametrName nazwa zmiennej którą chcemy zmienić np location
     * @param value wartość na jaką chcemy podmienić np Breslav
     */
    static public void GetVisitByValueFromDatabase(String parametrName, String value){
        Initialize(true);
        DatabaseReference ref = SetLocation("visits");
        Query aa= ref.orderByChild(parametrName).equalTo(value);
        aa.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println(dataSnapshot.getKey());  //tutaj podmienic na funkcjie która ma działać z otrzymanym kluczem.
                //wyżej dataSnapshot.getKey() otrzymuje uid znalezionych obiektów. ten print zostanie wywołany tyle razy ile obiektów będzie zgadzało się z naszym wyszukiwaniem!!!!!!!
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    /**
     *Metoda do podmiany wartości w sprecyzowanym miejscu w bazie. Tą metodą można też dodać nową zmienną jeżeli podamy parametrName który nie znajduje się w bazie.
     * @param uid klucz do danej lokacji w bazie (ten długi np.-KivIPsb0iuUBuOns6Bv)
     * @param parametrName nazwa zmiennej którą chcemy zmienić np location
     * @param value wartość na jaką chcemy podmienić np Breslav
     */
    static public void ModifyValueInDatabase(String uid,String parametrName, String value){

        Initialize(true);
        DatabaseReference ref = SetLocation("visits");

        Map<String, Object> nickname = new HashMap<String, Object>();
        nickname.put(parametrName, value);
        ref.child(uid).updateChildren(nickname);
    }
}
// TODO  Metoda odczytująca z bazy danych. Metody wysyłające dane do większej ilości funkcjionalności. Podpięcie tworzenia grup i dodawania członków rodziny do nich.
// TODO  Zejście z metod obsługiwanych przez podanie ścieżki aby je wywołać.
// TODO obsługa wysyłania i odczytywania zdjęć z bazy danych