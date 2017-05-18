package com.damian.aldoc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by dawid on 2017-04-04.
 */

public class Diseases0Tab1 extends Fragment {

    //Zmienne pomocnicze do inicjowania pól z XML'a
    private ListView list;
    private EditText search;
    //Adaptery, listy i listenery do posługiwania się chorobami i notatkami
    private ArrayAdapter<Note> adapter1;
    private ArrayAdapter<Disease> adapter2;
    private List<Note> notesList1 = new ArrayList<>();
    private List<Note> notesList2 = new ArrayList<>();
    private List<Disease> diseasesList = new ArrayList<>();
    private ChildEventListener nChildEventListener, dChildEventListener;
    //Zmienne pomocnicze
    private String disease;
    private String text = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.tab1_diseases0, container, false);
        //Jeśli klikniemy na floating acttion button przechodzimy do aktywności dodawania notatki
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity().getApplicationContext(), Diseases1Activity.class);
                //Aby zakomunikować, że dodajemy nową notatkę(a nie edytujemy już istniejącą) wysyłamy 0
                intent.putExtra("action", 0);
                startActivity(intent);
            }
        });

        //Jeśli klikniemy przycisk filtruj odczytamy z editText string i wyświetlimy notatki zawierające dany ciąg znaków
        search = (EditText) view.findViewById(R.id.editTextSerarch);
        Button button = (Button) view.findViewById(R.id.buttonFilter);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Dodatkowo wyświetlimy tylko te, które odnoszą się do choroby zaznaczonej obecnie w spinnerze
                Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinnerDiseases);
                disease = spinner.getSelectedItem().toString();

                text = search.getText().toString().toLowerCase(Locale.getDefault());
                filter(text);
                //Czyścimy editText
                search.setText("");

                //Chowamy klawiaturę po kliknięciu przycisku
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*Po włączeniu aktywności zaznaczoną wartością na spinerze będzie "Wszystkie"
          Inicjujemy więc zmienną wykorzystywaną przy filtrowaniu notatek przypisując jej takiego stringa.*/
        disease = "Wszystkie";
        //Inicjujemy bazę danych i tworzymy listenera dodającego notatki do list
        Database.Initialize(true);
        nChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Note note = dataSnapshot.getValue(Note.class);
                note.setNid(dataSnapshot.getKey());
                notesList2.add(note);
                notesList1.add(note);
                adapter1.notifyDataSetChanged();
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };
        /*Przechodzimy do notatek w bazie, segregujemy je po odpowiednio zmodyfikowanych timestampach aby pojawiały się
        na liście od najnowszych do najstarszych i ustawiamy utworzonego wcześniej listenera*/
        Database.SetLocation("notes").orderByChild("timestamp").addChildEventListener(nChildEventListener);
        //Tworzymy adapter i przypisujemy go do listview żeby wyswietlac notatki
        adapter1 = new ArrayAdapter<Note>(getActivity(), android.R.layout.simple_list_item_1, notesList2);
        list = (ListView)getActivity().findViewById(R.id.listNotes);
        list.setAdapter(adapter1);

        //Ustawiamy listenera wykrywającego naciśnięcie jednego z elementów listy i wywołującego odpowiednią metodę
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                noteOnClick(notesList2.get(pos));
            }
        });


        /*Dodajemy ręcznie na pierwsze 2 pozycje listy z chorobami wartości potrzebne przy filtrowaniu,
          a nie znajdujące się w bazie danych*/
        Disease disease1 = new Disease("Wszystkie");
        diseasesList.add(disease1);
        Disease disease2 = new Disease("Inne");
        diseasesList.add(disease2);
        //Tworzymy listenera dodającego choroby do odpowiedniej listy
        dChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Disease disease = dataSnapshot.getValue(Disease.class);
                disease.setDid(dataSnapshot.getKey());
                diseasesList.add(disease);
                adapter2.notifyDataSetChanged();
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };
        //Przechodzimy do chorób w bazie i ustawiamy utworzonego wcześniej listenera
        Database.SetLocation("diseases").addChildEventListener(dChildEventListener);
        //Tworzymy adapter i przypisujemy go do spinnera żeby wyswietlac choroby
        adapter2 = new ArrayAdapter<Disease>(getActivity(), android.R.layout.simple_spinner_item, diseasesList);
        Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinnerDiseases);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter2);

        //Ustawiamy listenera wykrywającego wybranie jednego z elementów spinnera
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                //Zamieniamy item na stringa i filtrujemy po nim notatki
                disease = parent.getSelectedItem().toString();
                filter("");
            }
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    //Funkcja pobierajaca informacje o naciśniętej notatce i przechodząca do aktywności wyświetlającej je
    private void noteOnClick(Note note)
    {
        Intent intent = new Intent(getActivity().getApplicationContext(), Diseases2Activity.class);

        String[] note_table = {note.getNid(), note.getDate(), note.getMood(), note.getSymptoms(), note.getMedicines(), note.getReaction(), note.getDisease()};

        intent.putExtra("note", note_table);

        startActivity(intent);
    }

    //Funkcja filtrująca notatki
    public void filter(String charText) {
        //Konwertujemy text, po którym filtrujemy do małych liter
        charText = charText.toLowerCase(Locale.getDefault());
        //Czyścimy listę pomocniczą
        notesList2.clear();
        //Wyświetlamy wszystkie
        if (charText.length() == 0 && disease == "Wszystkie") {
            notesList2.addAll(notesList1);
        }
        //Wyświetlamy notatki dotyczące wybranej choroby
        else if (charText.length() == 0){
            for (Note n : notesList1) {
                if(n.getDisease().equals(disease)){
                    notesList2.add(n);
                }
            }
        }
        //Wyświetlamy notatki zawierające wpisany tekst i dotyczące wybranej choroby
        else
        {
            for (Note n : notesList1)
            {
                if ((n.getDate().toLowerCase(Locale.getDefault()).contains(charText) ||
                        n.getSymptoms().toLowerCase(Locale.getDefault()).contains(charText))
                        && (disease == "Wszystkie" || n.getDisease().equals(disease)))
                {
                    notesList2.add(n);
                }
            }
        }
        adapter1.notifyDataSetChanged();
    }
}
