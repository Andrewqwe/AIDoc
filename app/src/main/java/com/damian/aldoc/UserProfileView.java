package com.damian.aldoc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

//TODO: Zaladowanie i ustawianie zdjecia uzytkownika
public class UserProfileView extends AppCompatActivity {

    private ListView lista;
    private ArrayList<HashMap<String,String>> rekordy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        rekordy = new ArrayList<HashMap<String, String>>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lista = (ListView) findViewById(R.id.user_data_list);
        lista.setSelector(android.R.color.transparent); //pozbywa sie animacji po kliknieciu w element listy

        //===== TODO: Przemyslec zmiane tego ustawienia przy przebudowie wygladu aplikacji
        lista.setOverScrollMode(View.OVER_SCROLL_NEVER);//wylaczenie efektu ktory pozwala
        // na przeciagniecie listy dalej niz sa w niej dane, pojawia sie na koncu wkurzajacy niebieski cien
        //ktorego nie wymyslilem jak wylaczyc

        // TODO: Zapytac tutaj baze o imie i nazwisko danego uzytkownika
        this.setUserName("Jan Kowalski"); // ustawianie imienia i nazwiska

        String[] arr = getResources().getStringArray(R.array.user_data_key_array);
        // Linijka wyzej odwoluje sie do slownika zdefiniowanego w res->values->strings.xml
        for (String str : arr) {
            // kazdy rekord w slowniku ma konstrukcje klucz//tlumaczenie
            // korzystajac z danego separatora trzeba podzielic te dwa parametry
            String temp[] = str.split("//");
            // temp[0] to klucz o jaki zostanie zapytana baza
            // temp[1] to tekst jaki odpowiada temu kluczowi w slowniku
            String value = "Tu powinna byc wartosc z bazy";
            //TODO: tutaj powinnismy zapytac baze o parametr temp[0] ktory jest kolejnymi kluczami ze slownika

            addValue(temp[1], value); //dodanie tlumaczenie i jego wartosci do listy
        }

        lista.requestLayout();
        //utworzenie obiektu adaptera napisanego na potrzeby tego ekranu
        UserProfileListAdapter adapter = new UserProfileListAdapter(rekordy, this);
        lista.setAdapter(adapter);
    }

    public void setUserName(String userName)
    {
        TextView userNameView = (TextView) findViewById(R.id.profile_full_name);
        userNameView.setText(userName);
    }

    public void addValue(String name, String value)
    {
        HashMap<String,String> temp= new HashMap<String,String>();
        temp.put("name",name);
        temp.put("value",value);
        rekordy.add(temp);
    }
}
