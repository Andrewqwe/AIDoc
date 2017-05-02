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

import java.util.Calendar;

public class Diseases1Activity extends AppCompatActivity {

    Button days;
    Button time;
    EditText mood;
    EditText symptoms;
    EditText medicines;
    EditText reaction;
    Button button;

    public static Diseases1Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diseases1);

        days = (Button)findViewById(R.id.button2);
        time = (Button)findViewById(R.id.button3);
        mood = (EditText)findViewById(R.id.editText3);
        symptoms = (EditText)findViewById(R.id.editText4);
        medicines = (EditText)findViewById(R.id.editText5);
        reaction = (EditText)findViewById(R.id.editText2);
        button = (Button)findViewById(R.id.button);

        activity = this;
    }

    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), Diseases2Activity.class);

        String daysString = days.getText().toString();
        String timeString = time.getText().toString();
        String dateString = daysString+" "+timeString;
        String moodString = mood.getText().toString();
        String symptomsString = symptoms.getText().toString();
        String medicinesString = medicines.getText().toString();
        String reactionString = reaction.getText().toString();

        Note note = new Note(dateString, moodString, symptomsString, medicinesString, reactionString);
        Database.SendObjectNotesToDatabase(note);

        intent.putExtra("uid", note.getUid());
        intent.putExtra("date", dateString);
        intent.putExtra("mood", moodString);
        intent.putExtra("symptoms", symptomsString);
        intent.putExtra("medicines", medicinesString);
        intent.putExtra("reaction", reactionString);

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

    public void setTime(int hour, int minute)
    {

        Integer h = Integer.valueOf(hour);
        Integer m = Integer.valueOf(minute);
        String t = h.toString() + ":" + m.toString();

        time.setText(t);
    }

    public void setDate(int year, int month, int day)
    {

        Integer y = Integer.valueOf(year);
        Integer m = Integer.valueOf(month + 1);
        Integer d = Integer.valueOf(day);
        String t = d.toString() + "-" + m.toString() + "-" + y.toString();

        days.setText(t);
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