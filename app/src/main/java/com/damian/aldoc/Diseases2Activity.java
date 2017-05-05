package com.damian.aldoc;

        import android.app.Dialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.TextView;

        import org.w3c.dom.Text;

public class Diseases2Activity extends AppCompatActivity {

    private TextView var;
    private String[] note_table;
    static final private int ALERT_DIALOG_BUTTONS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases2);

        note_table = getIntent().getStringArrayExtra("note");

        var = (TextView)findViewById(R.id.textView11);
        var.setText(note_table[1]+" "+note_table[2]);

        var = (TextView)findViewById(R.id.textView12);
        var.setText(note_table[3]);

        var = (TextView)findViewById(R.id.textView13);
        var.setText(note_table[4]);

        var = (TextView)findViewById(R.id.textView14);
        var.setText(note_table[5]);

        var = (TextView)findViewById(R.id.textView15);
        var.setText(note_table[6]);
    }

    public void editOnClick(View v) {
        Intent intent = new Intent(getApplicationContext(), Diseases1Activity.class);

        intent.putExtra("action", 1);
        intent.putExtra("note", note_table);

        startActivity(intent);
    }

    public void removeOnClick(View v){
        showDialog(0);
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        //dialogBuilder.setTitle("Usuwanie notatki");
        dialogBuilder.setMessage("Czy chcesz usunąć notatkę?");
        //dialogBuilder.setCancelable(false);
        dialogBuilder.setPositiveButton("Tak", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent intent = new Intent(getApplicationContext(), Diseases0Activity.class);
                Database.DeleteNoteFromDatabase(note_table[0]);
                startActivity(intent);
            }
        });
        dialogBuilder.setNegativeButton("Nie", new Dialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        return dialogBuilder.create();
    }
}