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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;



public class EditVisitActivity extends AppCompatActivity
{

    /*Time picker fragment
    * Fragment do wybierania godziny wizyty*/

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private EditVisitActivity edit_visit_activity;

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

        public void setActivity(EditVisitActivity activity)
        {
            edit_visit_activity = activity;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute)
        {
            edit_visit_activity.setTime(hourOfDay, minute);
        }
    }

    /*Date picker fragment
    * Fragment do wybierania daty wizyty*/
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        private EditVisitActivity edit_visit_activity;

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

        public void setActivity(EditVisitActivity activity)
        {
            edit_visit_activity = activity;
        }

        public void onDateSet(DatePicker view, int year, int month, int day)
        {
            edit_visit_activity.setDate(year, month, day);
        }
    }

    public final int ACTION_ADD = 0;
    public final int ACTION_EDIT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_visit);
    }

    public void applyOnClick(View v)
    {
        Intent returnIntent = new Intent();
        int action = getIntent().getIntExtra("action", -1);

        if(action == -1)
        {
            setResult(AppCompatActivity.RESULT_CANCELED, returnIntent);
            finish();
        }

        String[] visit_data = new String[4];

        EditText et;
        TextView tv;

        et = (EditText)findViewById(R.id.textDoctor);
        visit_data[0] = et.getText().toString();

        et = (EditText)findViewById(R.id.textLocation);
        visit_data[1] = et.getText().toString();

        tv = (TextView)findViewById(R.id.textDate);
        visit_data[2] = tv.getText().toString();

        tv = (TextView)findViewById(R.id.textTime);
        visit_data[3] = tv.getText().toString();

        returnIntent.putExtra("visit", visit_data);
        setResult(AppCompatActivity.RESULT_OK,returnIntent);

        finish();
    }

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

    /*Dwie funkcje do ustawienia czasu i daty, wolane przez fragmenty
    * TimePickerFragment i DatePickerFragment, w ktorych wybiera sie czas i date*/

    public void setTime(int hour, int minute)
    {
        TextView time_text_view = (TextView)findViewById(R.id.textTime);

        Integer h = Integer.valueOf(hour);
        Integer m = Integer.valueOf(minute);
        String t = h.toString() + ":" + m.toString();

        time_text_view.setText(t);
    }

    public void setDate(int year, int month, int day)
    {
        TextView date_text_view = (TextView)findViewById(R.id.textDate);

        Integer y = Integer.valueOf(year);
        Integer m = Integer.valueOf(month + 1);
        Integer d = Integer.valueOf(day);
        String t = d.toString() + "-" + m.toString() + "-" + y.toString();

        date_text_view.setText(t);
    }
}