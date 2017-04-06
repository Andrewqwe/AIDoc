package com.damian.aldoc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {
    CalendarView calendarView;
    TextView dayTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarView = (CalendarView) findViewById(R.id.calendarView);
        dayTxt = (TextView) findViewById(R.id.dateTxt);

        initCalendar();
    }

    private void initCalendar() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String selectedDate = sdf.format(new Date(calendarView.getDate()));

        dayTxt.setText(selectedDate);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                String selectedDate = sdf.format(new Date(calendarView.getDate()));

                dayTxt.setText(selectedDate);
            }
        });
    }
}
