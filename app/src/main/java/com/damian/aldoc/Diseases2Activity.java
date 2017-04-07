package com.damian.aldoc;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.widget.TextView;

public class Diseases2Activity extends AppCompatActivity {

    TextView date;
    TextView mood;
    TextView symptoms;
    TextView medicines;
    TextView reaction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases2);

        date = (TextView)findViewById(R.id.textView11);
        mood = (TextView)findViewById(R.id.textView12);
        symptoms = (TextView)findViewById(R.id.textView13);
        medicines = (TextView)findViewById(R.id.textView14);
        reaction = (TextView)findViewById(R.id.textView15);

        Bundle bundle = getIntent().getExtras();
        String dateString = bundle.getString("date");
        String moodString = bundle.getString("mood");
        String symptomsString = bundle.getString("symptoms");
        String medicinesString = bundle.getString("medicines");
        String reactionString = bundle.getString("reaction");
        date.setText(dateString);
        mood.setText(moodString);
        symptoms.setText(symptomsString);
        medicines.setText(medicinesString);
        reaction.setText(reactionString);
    }
}