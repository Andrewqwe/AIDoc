package com.damian.aldoc;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by dawid on 2017-04-04.
 */

public class Diseases0Tab2 extends Fragment {

    private ListView lv;
    private List<Disease> dlist = new ArrayList<>();
    private ArrayAdapter<Disease> adapter;
    private ChildEventListener mChildEventListener;
    private EditText et;
    private String diseaseString;
    private static String dString;

    public static class MyDialogFragment extends DialogFragment {
        static MyDialogFragment newInstance() {
            MyDialogFragment dialog = new MyDialogFragment();
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            //dialogBuilder.setTitle("Usuwanie notatki");
            dialogBuilder.setMessage("Co zrobić?");
            //dialogBuilder.setCancelable(false);
            dialogBuilder.setPositiveButton("Cofnij", new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });
            dialogBuilder.setNegativeButton("Usuń", new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    Database.DeleteDiseaseFromDatabase(dString);
                }
            });
            return dialogBuilder.create();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_diseases0, container, false);

        Button button = (Button) view.findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                et = (EditText) getActivity().findViewById(R.id.editText);
                diseaseString = et.getText().toString();
                Disease d = new Disease(diseaseString);
                Database.SendObjectToDatabase("diseases", d);
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
                Disease disease = dataSnapshot.getValue(Disease.class);
                disease.setUid(dataSnapshot.getKey());
                dlist.add(disease);
                adapter.notifyDataSetChanged();
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                Disease disease = dataSnapshot.getValue(Disease.class);
                disease.setUid(dataSnapshot.getKey());

                for(int i = 0; i < dlist.size(); i++)
                {
                    if(dlist.get(i).getUid().equals(dataSnapshot.getKey()))
                    {
                        dlist.set(i, disease);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                String uid = dataSnapshot.getKey();
                for(int i = 0; i < dlist.size(); i++)
                {
                    if(dlist.get(i).getUid().equals(uid))
                    {
                        dlist.remove(i);
                        adapter.notifyDataSetChanged();
                       break;
                    }
                }
            }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };
        Database.SetLocation("diseases").addChildEventListener(mChildEventListener);
        //tworzymy adapter i przypisujemy go do listview zeby wyswietlac wizyty
        adapter = new ArrayAdapter<Disease>(getActivity(), android.R.layout.simple_list_item_1, dlist);
        lv = (ListView)getActivity().findViewById(R.id.list1);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                diseaseOnClick(dlist.get(pos));
            }
        });
    }

    private void diseaseOnClick(Disease d){
        dString = d.getUid();
        MyDialogFragment dialog = MyDialogFragment.newInstance();
        dialog.show(getFragmentManager(), "Dialog");
    }
}

