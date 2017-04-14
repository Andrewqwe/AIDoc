package com.damian.aldoc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;



public class EditVisitActivity extends AppCompatActivity {

    public final int ACTION_ADD = 0;
    public final int ACTION_EDIT = 1;

    //Firebase declaration
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mVisitsDatabaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_visit);

        //Firebase Initialization
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mVisitsDatabaseReference = mFirebaseDatabase.getReference().child("visits");

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void applyOnClick(View v)
    {
        Intent returnIntent = new Intent();
        int action = getIntent().getIntExtra("Action", -1);

        if(action == -1)
        {
            setResult(AppCompatActivity.RESULT_CANCELED, returnIntent);
            finish();
        }

        String[] visit_data = new String[3];

        EditText et = (EditText)findViewById(R.id.textDoctor);
        visit_data[0] = et.getText().toString();

        et = (EditText)findViewById(R.id.textLocation);
        visit_data[1] = et.getText().toString();

        et = (EditText)findViewById(R.id.textTime);
        visit_data[2] = et.getText().toString();

        Visit Visit = new Visit(visit_data[0],visit_data[2]); //visit_data[0]- doktor visit_data[2] - miejsce
        mVisitsDatabaseReference.push().setValue(Visit);         //To dodałem Radosław - wysyłanie do bazy

        returnIntent.putExtra("Visit", visit_data);

        setResult(AppCompatActivity.RESULT_OK,returnIntent);

        finish();
    }
}