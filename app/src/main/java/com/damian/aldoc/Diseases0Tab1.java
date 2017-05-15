package com.damian.aldoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private ListView list ;
    private EditText search;
    private ArrayAdapter<Note> adapter1;
    private ArrayAdapter<Disease> adapter2;
    private List<Note> notes = new ArrayList<>();
    private List<Disease> diseasesList = new ArrayList<>();
    private List<Note> notesList = new ArrayList<>();
    private ChildEventListener nChildEventListener, dChildEventListener;
    private String disease0;

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        notesList.clear();
        if (charText.length() == 0 && disease0 == "Wszystkie") {
            notesList.addAll(notes);
        }
        else if (charText.length() == 0){
            for (Note n : notes) {
                if(n.getDisease().equals(disease0)){
                    notesList.add(n);
                }
            }
        }
        else
        {
            for (Note n : notes)
            {
                if ((n.getDate().toLowerCase(Locale.getDefault()).contains(charText) || n.getSymptoms().toLowerCase(Locale.getDefault()).contains(charText)) && (disease0 == "Wszystkie" || n.getDisease().equals(disease0)))
                {
                    notesList.add(n);
                }
            }
        }
        adapter1.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.tab1_diseases0, container, false);
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity().getApplicationContext(), Diseases1Activity.class);
                intent.putExtra("action", 0);
                startActivity(intent);
            }
        });

        search = (EditText) view.findViewById(R.id.editTextSerarch);
        Button button = (Button) view.findViewById(R.id.buttonFilter);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinnerDiseases);
                disease0 = spinner.getSelectedItem().toString();

                String text = search.getText().toString().toLowerCase(Locale.getDefault());
                filter(text);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        disease0 = "Wszystkie";
        Database.Initialize(true);
        nChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Note note = dataSnapshot.getValue(Note.class);
                note.setUid(dataSnapshot.getKey());
                notesList.add(note);
                notes.add(note);
                adapter1.notifyDataSetChanged();
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                Note note = dataSnapshot.getValue(Note.class);
                note.setUid(dataSnapshot.getKey());

                for(int i = 0; i < notes.size(); i++)
                {
                    if(notes.get(i).getUid().equals(dataSnapshot.getKey()))
                    {
                        notesList.set(i, note);
                        notes.set(i, note);
                        adapter1.notifyDataSetChanged();
                        break;
                    }
                }
            }
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                String uid = dataSnapshot.getKey();
                for(int i = 0; i < notes.size(); i++)
                {
                    if(notes.get(i).getUid().equals(uid))
                    {
                        notesList.remove(i);
                        notes.remove(i);
                        adapter1.notifyDataSetChanged();
                        break;
                    }
                }
            }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };
        Disease disease1 = new Disease("Wszystkie");
        diseasesList.add(disease1);
        Disease disease2 = new Disease("Inne");
        diseasesList.add(disease2);
        dChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Disease disease = dataSnapshot.getValue(Disease.class);
                disease.setUid(dataSnapshot.getKey());
                diseasesList.add(disease);
                adapter2.notifyDataSetChanged();
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                Disease disease = dataSnapshot.getValue(Disease.class);
                disease.setUid(dataSnapshot.getKey());

                for(int i = 0; i < diseasesList.size(); i++)
                {
                    if(diseasesList.get(i).getUid().equals(dataSnapshot.getKey()))
                    {
                        diseasesList.set(i, disease);
                        adapter2.notifyDataSetChanged();
                        break;
                    }
                }
            }
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };
        Database.SetLocation("notes").addChildEventListener(nChildEventListener);
        //tworzymy adapter i przypisujemy go do listview zeby wyswietlac wizyty
        adapter1 = new ArrayAdapter<Note>(getActivity(), android.R.layout.simple_list_item_1, notesList);
        list = (ListView)getActivity().findViewById(R.id.listNotes);
        list.setAdapter(adapter1);
        Database.SetLocation("diseases").addChildEventListener(dChildEventListener);
        //tworzymy adapter i przypisujemy go do listview zeby wyswietlac wizyty
        adapter2 = new ArrayAdapter<Disease>(getActivity(), android.R.layout.simple_spinner_item, diseasesList);
        Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinnerDiseases);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter2);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
            noteOnClick(notesList.get(pos));
            }
        });
    }

    private void noteOnClick(Note note)
    {
        Intent intent = new Intent(getActivity().getApplicationContext(), Diseases2Activity.class);

        String[] note_table = {note.getUid(), note.getDate(), note.getMood(), note.getSymptoms(), note.getMedicines(), note.getReaction(), note.getDisease()};

        intent.putExtra("note", note_table);

        startActivity(intent);
    }
}
