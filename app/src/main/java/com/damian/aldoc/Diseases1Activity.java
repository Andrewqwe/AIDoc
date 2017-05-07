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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Diseases1Activity extends AppCompatActivity {

    public static Diseases1Activity activity;
    private Button button;
    private EditText var;
    private int action;
    private String uid;
    private final int ADD = 0;
    private final int EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases1);

        action = getIntent().getIntExtra("action", -1);
        if (action == ADD)
        {
            Calendar c = Calendar.getInstance();

            String date = new SimpleDateFormat("dd-MM-yyyy").format(c.getTime());
            String time = new SimpleDateFormat("hh:mm").format(c.getTime());

            button = (Button)findViewById(R.id.button2);
            button.setText(date);

            button = (Button)findViewById(R.id.button3);
            button.setText(time);
        }
        else if(action == EDIT) {
            String[] note_table = getIntent().getStringArrayExtra("note");

            uid = note_table[0];

            button = (Button) findViewById(R.id.button2);
            button.setText(note_table[1]);

            button = (Button) findViewById(R.id.button3);
            button.setText(note_table[2]);

            var = (EditText) findViewById(R.id.editText3);
            var.setText(note_table[3]);

            var = (EditText) findViewById(R.id.editText4);
            var.setText(note_table[4]);

            var = (EditText) findViewById(R.id.editText5);
            var.setText(note_table[5]);

            var = (EditText) findViewById(R.id.editText2);
            var.setText(note_table[6]);
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

        button = (Button)findViewById(R.id.button2);
        note_table[1] = button.getText().toString();

        button = (Button)findViewById(R.id.button3);
        note_table[2] = button.getText().toString();

        var = (EditText)findViewById(R.id.editText3);
        note_table[3] = var.getText().toString();

        var = (EditText)findViewById(R.id.editText4);
        note_table[4] = var.getText().toString();

        var = (EditText)findViewById(R.id.editText5);
        note_table[5] = var.getText().toString();

        var = (EditText)findViewById(R.id.editText2);
        note_table[6] = var.getText().toString();

        Note note = new Note(note_table[1], note_table[2], note_table[3], note_table[4], note_table[5], note_table[6]);

        if(action == 0) Database.SendObjectNotesToDatabase(note);
        else if(action == 1) Database.UpdateNoteInDatabase(note, note_table[0]);

        startActivity(intent);
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private Diseases1Activity diseases1_activity;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void setActivity(Diseases1Activity activity)
        {
            diseases1_activity = activity;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            diseases1_activity.setTime(hourOfDay, minute);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private Diseases1Activity diseases1_activity;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void setActivity(Diseases1Activity activity)
        {
            diseases1_activity = activity;
        }

        public void onDateSet(DatePicker view, int year, int month, int day)
        {
            diseases1_activity.setDate(year, month, day);
        }
    }

    private String toString(int val)
    {
        Integer i = Integer.valueOf(val);

        if(val < 10)
            return "0" + i.toString();
        else
            return i.toString();
    }

    public void setTime(int hour, int minute)
    {
        button = (Button)findViewById(R.id.button3);
        Integer h = Integer.valueOf(hour);
        Integer m = Integer.valueOf(minute);

        String t = toString(h) + ":" + toString(m);

        button.setText(t);
    }

    public void setDate(int year, int month, int day)
    {
        button = (Button)findViewById(R.id.button2);
        Integer y = Integer.valueOf(year);
        Integer m = Integer.valueOf(month + 1);
        Integer d = Integer.valueOf(day);

        String t = toString(y) + "-" + toString(m) + "-" + toString(d);

        button.setText(t);
    }

    /*Dwie funkcje do ustawienia czasu i daty, wolane przez fragmenty
    * TimePickerFragment i DatePickerFragment, w ktorych wybiera sie czas i date*/
    public void setTimeOnClick(View v)
    {
        DialogFragment time_pick_fragment = new TimePickerFragment();
        ((TimePickerFragment)time_pick_fragment).setActivity(this);
        time_pick_fragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void setDateOnClick(View v)
    {
        DialogFragment date_pick_fragment = new DatePickerFragment();
        ((DatePickerFragment)date_pick_fragment).setActivity(this);
        date_pick_fragment.show(getSupportFragmentManager(), "timePicker");
    }

}