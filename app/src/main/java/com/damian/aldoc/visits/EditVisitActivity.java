package com.damian.aldoc.visits;

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
import com.damian.aldoc.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;



public class EditVisitActivity extends AppCompatActivity
{
    static TextView date_textView;
    static TextView time_textView;

    /*Time picker fragment
    * Fragment do wybierania godziny wizyty*/

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private EditVisitActivity edit_visit_activity;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            int hour, minute;
            Calendar c = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("hh:mm");

            /*Ustawiamy czas jako ten wczesniej wybrany przez uzytkownika
             *chyba ze jeszcze nic nie wybral, albo format danych jest niepoprawny,
             *wtedy uzywamy aktualnego czasu jako podstawowego wyboru*/
            String time = time_textView.getText().toString();

            try {
                c.setTime(format.parse(time));
            }catch (ParseException e) {}

            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);

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

            int year, month, day;
            Calendar c = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

            /*Ustawiamy date jako te wczesniej wybrana przez uzytkownika
             *chyba ze jeszcze nic nie wybral, albo format danych jest niepoprawny,
             *wtedy uzywamy aktualnej daty jako podstawowego wyboru*/
            String time = date_textView.getText().toString();

            try {
                c.setTime(format.parse(time));
            }catch (ParseException e) {}

            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_visit);

        /*jezeli edytujemy to wpisujemy w pola dotychczasowe dane
        * w przeciwnym wypadku pobieramy obecna dane i godzine i wpisujemy je
        * do pol z data i godzina, pozostale pola zostawiamy puste*/

        int action = getIntent().getIntExtra("action", -1);

        if(action == ACTION_EDIT)
        {
            String[] visit_data = getIntent().getStringArrayExtra("visit");

            EditText et;
            TextView tv;

            et = (EditText)findViewById(R.id.textDoctor);
            et.setText(visit_data[0]);

            et = (EditText)findViewById(R.id.textLocation);
            et.setText(visit_data[1]);

            date_textView = (TextView)findViewById(R.id.textDate);
            date_textView.setText(visit_data[2]);

            time_textView = (TextView)findViewById(R.id.textTime);
            time_textView .setText(visit_data[3]);

        }
        else if (action == ACTION_ADD)
        {
            //pobieramy obecna date i godzine
            Calendar c = Calendar.getInstance();

            String date = new SimpleDateFormat("dd-MM-yyyy").format(c.getTime());
            String time = new SimpleDateFormat("hh:mm").format(c.getTime());

            date_textView = (TextView)findViewById(R.id.textDate);
            date_textView.setText(date);

            time_textView = (TextView)findViewById(R.id.textTime);
            time_textView.setText(time);
        }
    }

    public void applyOnClick(View v)
    {
        //Sprawdzamy jaka akacja miala byc wykonana (edycja/dodanie)
        Intent returnIntent = new Intent();
        int action = getIntent().getIntExtra("action", -1);

        //W przypadku nieznanej akcji, wychodzimy z activity z kodem RESULT_CANCELED
        if(action == -1)
        {
            setResult(AppCompatActivity.RESULT_CANCELED, returnIntent);
            finish();
        }

        //Przygotowujemy dane wizyty do zwrocenia do visits activity
        //Dane, podane przez uzytkownika, odczytujemy z gui
        String[] visit_data = new String[5];

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

        //PRZY EDYCJI
        //uid wizyty odczytujemy z danych przekazanych przez visits activity,
        //nie zmieniamy go tylko zwracamy takie samo
        if(action == ACTION_EDIT)
            visit_data[4] = getIntent().getStringArrayExtra("visit")[4];

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

    /*Zamienia liczby dotyczace daty/godziny na string
    * dodaje 0 z przodu jezeli liczba jest jednocyfrowa*/
    private String timeToString(int val)
    {
        Integer i = Integer.valueOf(val);
        String res;

        if(val < 10)
            res = "0" + i.toString();
        else
            res = i.toString();

        return res;
    }

    /*Dwie funkcje do ustawienia czasu i daty, wolane przez fragmenty
    * TimePickerFragment i DatePickerFragment, w ktorych wybiera sie czas i date*/

    public void setTime(int hour, int minute)
    {
        TextView time_text_view = (TextView)findViewById(R.id.textTime);

        String t = timeToString(hour) + ":" + timeToString(minute);

        time_text_view.setText(t);
    }

    public void setDate(int year, int month, int day)
    {
        TextView date_text_view = (TextView)findViewById(R.id.textDate);

        month += 1;
        String t = timeToString(day) + "-" + timeToString(month) + "-" + timeToString(year);

        date_text_view.setText(t);
    }
}