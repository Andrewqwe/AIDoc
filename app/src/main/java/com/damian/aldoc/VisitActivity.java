package com.damian.aldoc;

import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class VisitActivity extends AppCompatActivity {

    private List<String> prescriptions = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);

        String[] visit_data = getIntent().getStringArrayExtra("visit");

        TextView tv = (TextView)findViewById(R.id.textViewDoctor);
        tv.setText("Doctor: " + visit_data[0]);

        tv = (TextView)findViewById(R.id.textViewLocation);
        tv.setText("Location: " + visit_data[1]);

        tv = (TextView)findViewById(R.id.textViewDate);
        tv.setText("Date: " + visit_data[2]);

        tv = (TextView)findViewById(R.id.textViewTime);
        tv.setText("Time: " + visit_data[3]);
    }

    public void addOnClick(View v)
    {
        final ListView list = (ListView)findViewById(R.id.listViewPrescriptions);

        View view = (LayoutInflater.from(this)).inflate(R.layout.prescription_name, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setView(view);

        final EditText et_prescription = (EditText)view.findViewById(R.id.textPrescriptionName);

        alertBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                //TODO: dodac do listy recept nowa recepte o nazwie odczytanej z et_prescription
                //list.add
            }
        });

        Dialog dialog = alertBuilder.create();
        dialog.show();
    }
}
