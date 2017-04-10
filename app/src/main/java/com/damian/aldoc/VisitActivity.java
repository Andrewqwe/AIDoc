package com.damian.aldoc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class VisitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit);

        String[] visit_data = getIntent().getStringArrayExtra("Visit");

        TextView tv = (TextView)findViewById(R.id.textViewDoctor);
        tv.setText("Doktor: " + visit_data[0]);

        tv = (TextView)findViewById(R.id.textViewLocation);
        tv.setText("Miejsce: " + visit_data[1]);

        tv = (TextView)findViewById(R.id.textViewTime);
        tv.setText("Data: " + visit_data[2]);
    }
}
