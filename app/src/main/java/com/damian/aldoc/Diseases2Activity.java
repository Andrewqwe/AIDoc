package com.damian.aldoc;

        import android.app.Dialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.TextView;

public class Diseases2Activity extends AppCompatActivity {

    //Zmienna pomocnicza do inicjowania pól z XML'a
    private TextView var;
    //Tabela pomocnicza
    private String[] note_table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases2);

        //Pobieramy dane odebrane z poprzedniej aktywności i wypełniamy nimi pola obecnej
        note_table = getIntent().getStringArrayExtra("note");

        var = (TextView)findViewById(R.id.textViewDateV);
        var.setText(note_table[1]);

        var = (TextView)findViewById(R.id.textViewMoodV);
        var.setText(note_table[2]);

        var = (TextView)findViewById(R.id.textViewSymptomsV);
        var.setText(note_table[3]);

        var = (TextView)findViewById(R.id.textViewMedicinesV);
        var.setText(note_table[4]);

        var = (TextView)findViewById(R.id.textViewReactionV);
        var.setText(note_table[5]);

        var = (TextView)findViewById(R.id.textViewDiseaseV);
        var.setText(note_table[6]);
    }

    //Po kliknięciu przycisku "Edytuj notatkę" pobieramy dane o przeglądanej notatce i przechodzimy do nowej aktywności
    public void editOnClick(View v) {
        Intent intent = new Intent(getApplicationContext(), Diseases1Activity.class);
        //Aby zakomunikować, że edytujemy istniejącą notatkę(a nie dodajemy nową) wysyłamy 1
        intent.putExtra("action", 1);
        intent.putExtra("note", note_table);

        startActivity(intent);
    }

    //Po kliknięciu przycisku "Usuń notatkę" wyświetlamy odpowiednie okno dialogowe
    public void removeOnClick(View v){
        showDialog(0);
    }

    //Funkcja tworzy okno dialogowe sprawdzające czy użytkownik chce usunąć notatkę. Jeśli tak - usuwa ją i opuszcza aktywność
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Czy na pewno chcesz usunąć notatkę?");
        dialogBuilder.setPositiveButton("Anuluj", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        dialogBuilder.setNegativeButton("Usuń", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = new Intent(getApplicationContext(), Diseases0Activity.class);
                Database.DeleteNoteFromDatabase(note_table[0]);
                startActivity(intent);
            }
        });
        return dialogBuilder.create();
    }
}