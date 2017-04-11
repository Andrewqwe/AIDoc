package com.damian.aldoc;

        import android.content.Intent;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;

public class Diseases1Activity extends AppCompatActivity {

    EditText date;
    EditText mood;
    EditText symptoms;
    EditText medicines;
    EditText reaction;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases1);

        date = (EditText)findViewById(R.id.editText6);
        mood = (EditText)findViewById(R.id.editText3);
        symptoms = (EditText)findViewById(R.id.editText4);
        medicines = (EditText)findViewById(R.id.editText5);
        reaction = (EditText)findViewById(R.id.editText2);
        button = (Button)findViewById(R.id.button);
    }

    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), Diseases2Activity.class);

        String dateString = date.getText().toString();
        String moodString = mood.getText().toString();
        String symptomsString = symptoms.getText().toString();
        String medicinesString = medicines.getText().toString();
        String reactionString = reaction.getText().toString();

        intent.putExtra("date", dateString);
        intent.putExtra("mood", moodString);
        intent.putExtra("symptoms", symptomsString);
        intent.putExtra("medicines", medicinesString);
        intent.putExtra("reaction", reactionString);

        startActivity(intent);
    }
}
