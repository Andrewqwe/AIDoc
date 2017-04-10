package com.damian.aldoc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class DiseasesActivity extends AppCompatActivity {

    private ListView list;
    private ArrayAdapter<String> adapter;
    EditText disease;
    String diseaseString;

    private void initListView() {
        String notes[] = {diseaseString};

        ArrayList<String> note = new ArrayList<String>();
        note.addAll(Arrays.asList(notes));

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, note);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                Intent intent = new Intent(getApplicationContext(), Diseases0Activity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases);

        list = (ListView) findViewById(R.id.list1);
        diseaseString = "Brak";
        initListView();
    }

    public void onClick(View v) {
        disease = (EditText)findViewById(R.id.editText);
        diseaseString = disease.getText().toString();
        initListView();
    }

}
