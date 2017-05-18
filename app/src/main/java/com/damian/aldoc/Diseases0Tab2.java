package com.damian.aldoc;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by dawid on 2017-04-04.
 */

public class Diseases0Tab2 extends Fragment {

    //Zmienne pomocnicze do inicjowania pól z XML'a
    private ListView list;
    private EditText search;
    //Adapter, lista i listener do posługiwania się chorobami
    private ArrayAdapter<Disease> adapter;
    private List<Disease> diseasesList = new ArrayList<>();
    private ChildEventListener dChildEventListener;
    //Zmienne pomocnicze
    private String disease;
    private static String dString;

    //Klasa używana do generowania okna dialogowego umożliwiającego usunięcie choroby
    public static class MyDialogFragment1 extends DialogFragment {
        static MyDialogFragment1 newInstance() {
            MyDialogFragment1 dialog = new MyDialogFragment1();
            return dialog;
        }

        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setMessage("Co zrobić?");
            dialogBuilder.setPositiveButton("Cofnij", new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });
            dialogBuilder.setNegativeButton("Usuń", new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {
                    MyDialogFragment2 dialog2 = MyDialogFragment2.newInstance();
                    dialog2.show(getFragmentManager(), "Dialog");
                }
            });
            return dialogBuilder.create();
        }
    }

    //Klasa używana do generowania okna dialogowego wymagającego potwierdzenia chęci usunięcie choroby
    public static class MyDialogFragment2 extends DialogFragment {
        static MyDialogFragment2 newInstance() {
            MyDialogFragment2 dialog = new MyDialogFragment2();
            return dialog;
        }
        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setMessage("Czy na pewno chcesz usunąć chorobę?");
            dialogBuilder.setPositiveButton("Anuluj", new Dialog.OnClickListener() {
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

    //Klasa używana do generowania okna dialogowego informującego o potrzebie podania nazwy choroby
    public static class MyDialogFragment3 extends DialogFragment {
        static MyDialogFragment3 newInstance() {
            MyDialogFragment3 dialog = new MyDialogFragment3();
            return dialog;
        }
        @Override
        public Dialog onCreateDialog(final Bundle savedInstanceState) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setMessage("Należy podać nazwę choroby!");
            dialogBuilder.setPositiveButton("Rozumiem", new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            });
            return dialogBuilder.create();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_diseases0, container, false);
        //Jeśli klikniemy przycisk dodamy chorobę do bazy
        Button button = (Button) view.findViewById(R.id.buttonAdd);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                search = (EditText) getActivity().findViewById(R.id.editTextDisease);
                disease = search.getText().toString();
                //Jeśli editText był pusty informujemy o tym i nic nie wysyłamy do bazy
                if (disease.length() == 0){
                    MyDialogFragment3 dialog3 = MyDialogFragment3.newInstance();
                    dialog3.show(getFragmentManager(), "Dialog");
                }
                else {
                    Disease d = new Disease(disease);
                    Database.SendObjectToDatabase("diseases", d);
                    //Czyścimy editText
                    search.setText("");
                }
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
        //Inicjujemy bazę danych i tworzymy listenera dodającego/usuwającego choroby z listy
        Database.Initialize(true);
        dChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Disease disease = dataSnapshot.getValue(Disease.class);
                disease.setDid(dataSnapshot.getKey());
                diseasesList.add(disease);
                adapter.notifyDataSetChanged();
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                String did = dataSnapshot.getKey();
                for(int i = 0; i < diseasesList.size(); i++)
                {
                    if(diseasesList.get(i).getDid().equals(did))
                    {
                        diseasesList.remove(i);
                        adapter.notifyDataSetChanged();
                       break;
                    }
                }
            }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };
        //Przechodzimy do chorób w bazie i ustawiamy utworzonego wcześniej listenera
        Database.SetLocation("diseases").addChildEventListener(dChildEventListener);
        //Tworzymy adapter i przypisujemy go do listview żeby wyswietlac choroby
        adapter = new ArrayAdapter<Disease>(getActivity(), android.R.layout.simple_list_item_1, diseasesList);
        list = (ListView)getActivity().findViewById(R.id.listDiseases);
        list.setAdapter(adapter);

        //Ustawiamy listenera wykrywającego naciśnięcie jednego z elementów listy i wywołującego odpowiednią metodę
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                diseaseOnClick(diseasesList.get(pos));
            }
        });
    }

    //Funkcja wyświetlająca okno dialogowe
    private void diseaseOnClick(Disease d){
        dString = d.getDid();
        MyDialogFragment1 dialog1 = MyDialogFragment1.newInstance();
        dialog1.show(getFragmentManager(), "Dialog");
    }
}

