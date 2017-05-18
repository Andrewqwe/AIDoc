package com.damian.aldoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dawid on 2017-04-04.
 */

public class Diseases0Tab1 extends Fragment {

    private ListView list ;
    private ArrayAdapter<Note> adapter ;
    private List<Note> notes = new ArrayList<>();
    private ChildEventListener mChildEventListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.tab1_diseases0, container, false);

        Button button = (Button) view.findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity().getApplicationContext(), Diseases1Activity.class);
                intent.putExtra("action", 0);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Database.Initialize(true);
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Note note = dataSnapshot.getValue(Note.class);
                note.setUid(dataSnapshot.getKey());

                notes.add(note);
                adapter.notifyDataSetChanged();
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                Note note = dataSnapshot.getValue(Note.class);
                note.setUid(dataSnapshot.getKey());

                for(int i = 0; i < notes.size(); i++)
                {
                    if(notes.get(i).getUid().equals(dataSnapshot.getKey()))
                    {
                        notes.set(i, note);
                        adapter.notifyDataSetChanged();
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
                        notes.remove(i);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };
        Database.SetLocation("notes").addChildEventListener(mChildEventListener);

        //tworzymy adapter i przypisujemy go do listview zeby wyswietlac wizyty
        adapter = new ArrayAdapter<Note>(getActivity(), android.R.layout.simple_list_item_1, notes);

        list = (ListView)getActivity().findViewById(R.id.list2);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
            noteOnClick(notes.get(pos));
            }
        });
    }

    private void noteOnClick(Note note)
    {
        Intent intent = new Intent(getActivity().getApplicationContext(), Diseases2Activity.class);

        String[] note_table = {note.getUid(), note.getDate(), note.getTime(), note.getMood(), note.getSymptoms(), note.getMedicines(), note.getReaction()};

        intent.putExtra("note", note_table);

        startActivity(intent);
    }
}
