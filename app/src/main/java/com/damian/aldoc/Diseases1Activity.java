package com.damian.aldoc;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Diseases1Activity extends AppCompatActivity {

    public static Diseases1Activity activity;
    //Zmienne pomocnicze do inicjowania pól z XML'a
    private TextView tv;
    private EditText et;
    private Spinner sp;
    private MultiAutoCompleteTextView mactv;
    //Zmienne pomocnicze do wykrywania czy tworzymy nową notatkę czy edytujemy już istniejącą
    private int action;
    private final int EDIT = 1;
    //Adaptery, lista i listener do posługiwania się chorobami i lekami
    private ArrayAdapter<Disease> adapter1;
    private ArrayAdapter<String> adapter2;
    private ArrayAdapter<String> adapter3;
    private List<Disease> diseasesList = new ArrayList<>();
    private ChildEventListener dChildEventListener;
    //Zmienne pomocnicze
    private String nid;
    private String dcategory, mString;
    private int mSpinn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases1);

        //Ustawiamy obecną datę i godzinę
        Calendar c = Calendar.getInstance();
        String date = new SimpleDateFormat("dd-MM-yyyy").format(c.getTime());
        String time = new SimpleDateFormat("hh:mm").format(c.getTime());

        tv = (TextView)findViewById(R.id.textViewDateTime);
        tv.setText(date+" "+time);

        //Pobieramy zmienną wskazującą czy dodajemy czy edytujemy notatkę
        action = getIntent().getIntExtra("action", -1);
        //Inicjumemy zmienną pomocniczą nieznaczącą wartością
        dcategory = "Brak";
        mSpinn = 0;
        //Jeśli edytujemy pobieramy dane z poprzedniej aktywności i inicjujemy nimi pola obecnej aktywności
        if(action == EDIT) {
            String[] note_table = getIntent().getStringArrayExtra("note");

            nid = note_table[0];

            mString = note_table[2];
            mSpinn = 1;

            et = (EditText) findViewById(R.id.editTextSymptoms);
            et.setText(note_table[3]);

            mactv = (MultiAutoCompleteTextView) findViewById(R.id.mactvMedicines);
            mactv.setText(note_table[4]);

            et = (EditText) findViewById(R.id.editTextReaction);
            et.setText(note_table[5]);

            //Dodajemy na początek listy chorobę, której dotyczy notatka aby ją wyświetlać na starcie
            dcategory = note_table[6];
            Disease disease = new Disease(dcategory);
            diseasesList.add(disease);
        }
        else {
            //Jeśli dodajemy inicjujemy pierwsze element listy wartością braku wyboru
            Disease disease = new Disease("--wybierz--");
            diseasesList.add(disease);
            mSpinn = 0;
        }
        /*Jeśli dodana na wstępie choroba jest różna od "Inne", dodajemy takiego stringa do listy,
          ponieważ nie przechowujemy tego go w bazie*/
        if(!dcategory.equals("Inne")) {
            Disease disease = new Disease("Inne");
            diseasesList.add(disease);
        }

        //Inicjujemy bazę danych i tworzymy listenera dodającego choroby do listy
        Database.Initialize(true);
        dChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Disease disease = dataSnapshot.getValue(Disease.class);
                disease.setDid(dataSnapshot.getKey());
                //Jeśli różna od dodanej na wstępie choroby
                if(!dcategory.equals(disease.name))diseasesList.add(disease);
                adapter1.notifyDataSetChanged();
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };
        //Przechodzimy do chorób w bazie i ustawiamy utworzonego wcześniej listenera
        Database.SetLocation(Database.getDiseasesPath()).addChildEventListener(dChildEventListener);
        //Wypełniamy adapter i przypisujemy go do spinnera żeby wyswietlac choroby
        adapter1 = new ArrayAdapter<Disease>(this, android.R.layout.simple_spinner_item, diseasesList);
        sp = (Spinner) findViewById(R.id.spinnerDisease);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter1);

        //Tworzymy tablicę stringów i przypisujemy jej listę leków z pliku drugs.xml
        String[] medicinesList = getResources().getStringArray(R.array.drugs_array);
        //Wypełniamy adapter i przypisujemy go do pola do wpisywania przyjętych leków aby domyślał się o jakie leki nam chodzi
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, medicinesList);
        mactv = (MultiAutoCompleteTextView) findViewById(R.id.mactvMedicines);
        mactv.setAdapter(adapter2);
        //Separujemy leki przecinkiem i spacją
        mactv.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        //Tworzymy tablicę stringów, wypełniamy nią adapter i przypisujemy go do spinnera żeby podpowiadać samopoczucie
        String[] moodList = new String[]{"--wybierz--", "Bardzo dobre", "Dobre", "Neutralne", "Złe"};
        //Jeśli edytujemy usuwamy z tablicy samopoczucie z danej notatki i wsrawiamy je na przód zastępując "--wybierz--"
        if(mSpinn == 1) {
            int i = 0;
            while (!mString.equals(moodList[i])) i++;
            moodList[0] = mString;
            while (i < 4) {
                moodList[i] = moodList[i + 1];
                i++;
            }
            String[] mList = new String[]{moodList[0], moodList[1], moodList[2], moodList[3]};
            adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mList);
        }
        else adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, moodList);
        sp = (Spinner) findViewById(R.id.spinnerMood);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter3);
        activity = this;
    }

    //Funkcja dodająca/modyfikująca notatkę po kliknięciu na przycisk "Dodaj"
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), Diseases0Activity.class);
        //Tworzymy tablicę, do której pobierzemy wartości z pól aktywności
        String[] note_table = new String[7];

        //Jeśli dobrze się nie przesłała zmienna wyłączam aktywność
        if(action == -1)
        {
            setResult(AppCompatActivity.RESULT_CANCELED, intent);
            finish();
        }
        note_table[0] = nid;

        tv = (TextView)findViewById(R.id.textViewDateTime);
        note_table[1] = tv.getText().toString();

        sp = (Spinner) findViewById(R.id.spinnerMood);
        note_table[2] = sp.getSelectedItem().toString();

        et = (EditText)findViewById(R.id.editTextSymptoms);
        note_table[3] = et.getText().toString();

        mactv = (MultiAutoCompleteTextView)findViewById(R.id.mactvMedicines);
        note_table[4] = mactv.getText().toString();

        et = (EditText)findViewById(R.id.editTextReaction);
        note_table[5] = et.getText().toString();

        sp = (Spinner) findViewById(R.id.spinnerDisease);
        note_table[6] = sp.getSelectedItem().toString();

        //Jeśli choroba jakiej dotyczy notatka nie została wybrana wyświetlamy odpowiednie okno dialogowe i nic nie wykonujemy
        if(note_table[6].equals("--wybierz--") || note_table[2].equals("--wybierz--") || note_table[2].equals("")){
            showDialog(0);
        }
        //W przeciwnym razie tworzymy uwtworzoną notatkę i wysyłamy ją do bazy
        else {
            Note note = new Note(note_table[1], note_table[2], note_table[3], note_table[4], note_table[5], note_table[6], -System.currentTimeMillis()/1000);

            if (action == 0) Database.SendObjectNotesToDatabase(note);
            else if (action == 1) Database.UpdateNoteInDatabase(note, note_table[0]);

            startActivity(intent);
        }
    }

    //Okno dialogowe informujące i potrzebie wybrania choroby jakiej dotyczy notatka
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        //dialogBuilder.setTitle("Usuwanie notatki");
        dialogBuilder.setMessage("Zarówno samopoczucie jak i choroba jakiej dotyczy notatka muszą być wybrane!");
        //dialogBuilder.setCancelable(false);
        dialogBuilder.setNegativeButton("Rozumiem", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        return dialogBuilder.create();
    }

}