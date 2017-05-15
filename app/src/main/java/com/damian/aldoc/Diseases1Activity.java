package com.damian.aldoc;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Diseases1Activity extends AppCompatActivity {

    public static Diseases1Activity activity;
    private Button button;
    private TextView tv;
    private EditText et;
    private Spinner sp;
    private int action;
    private String uid;
    private final int EDIT = 1;
    private ArrayAdapter<Disease> adapter;
    private List<Disease> diseases = new ArrayList<>();
    private ChildEventListener dChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases1);

        Disease disease = new Disease("Inne");
        diseases.add(disease);
        dChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Disease disease = dataSnapshot.getValue(Disease.class);
                disease.setUid(dataSnapshot.getKey());
                diseases.add(disease);
                adapter.notifyDataSetChanged();
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                Disease disease = dataSnapshot.getValue(Disease.class);
                disease.setUid(dataSnapshot.getKey());

                for(int i = 0; i < diseases.size(); i++)
                {
                    if(diseases.get(i).getUid().equals(dataSnapshot.getKey()))
                    {
                        diseases.set(i, disease);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                String uid = dataSnapshot.getKey();
                for(int i = 0; i < diseases.size(); i++)
                {
                    if(diseases.get(i).getUid().equals(uid))
                    {
                        diseases.remove(i);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };

        Database.SetLocation("diseases").addChildEventListener(dChildEventListener);
        //tworzymy adapter i przypisujemy go do listview zeby wyswietlac wizyty
        adapter = new ArrayAdapter<Disease>(this, android.R.layout.simple_spinner_item, diseases);
        sp = (Spinner) findViewById(R.id.spinnerDisease);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        action = getIntent().getIntExtra("action", -1);

        Calendar c = Calendar.getInstance();

        String date = new SimpleDateFormat("dd-MM-yyyy").format(c.getTime());
        String time = new SimpleDateFormat("hh:mm").format(c.getTime());

        tv = (TextView)findViewById(R.id.textViewDateTime);
        tv.setText(date+" "+time);

        if(action == EDIT) {
            String[] note_table = getIntent().getStringArrayExtra("note");

            uid = note_table[0];

            et = (EditText) findViewById(R.id.editTextMood);
            et.setText(note_table[2]);

            et = (EditText) findViewById(R.id.editTextSymptoms);
            et.setText(note_table[3]);

            et = (EditText) findViewById(R.id.editTextMedicines);
            et.setText(note_table[4]);

            et = (EditText) findViewById(R.id.editTextReaction);
            et.setText(note_table[5]);

            sp.setSelection(findSpinner(note_table[6]));
        }
        activity = this;
    }

    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), Diseases0Activity.class);
        String[] note_table = new String[7];
        if(action == -1)
        {
            setResult(AppCompatActivity.RESULT_CANCELED, intent);
            finish();
        }

        note_table[0] = uid;

        tv = (TextView)findViewById(R.id.textViewDateTime);
        note_table[1] = tv.getText().toString();

        et = (EditText)findViewById(R.id.editTextMood);
        note_table[2] = et.getText().toString();

        et = (EditText)findViewById(R.id.editTextSymptoms);
        note_table[3] = et.getText().toString();

        et = (EditText)findViewById(R.id.editTextMedicines);
        note_table[4] = et.getText().toString();

        et = (EditText)findViewById(R.id.editTextReaction);
        note_table[5] = et.getText().toString();

        sp = (Spinner) findViewById(R.id.spinnerDisease);
        note_table[6] = sp.getSelectedItem().toString();

        Note note = new Note(note_table[1], note_table[2], note_table[3], note_table[4], note_table[5], note_table[6]);

        if(action == 0) Database.SendObjectNotesToDatabase(note);
        else if(action == 1) Database.UpdateNoteInDatabase(note, note_table[0]);

        startActivity(intent);
    }

    private int findSpinner(String s){
        /*int index = -1;
        for (Disease d : diseases)
        {
            if (d.getName() == s)
            {
                index = diseases.indexOf(d);
            }
        }
        if(index != -1) return index;
        else*/ return 0;
    }

}