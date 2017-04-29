package com.damian.aldoc;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CalendarActivity extends AppCompatActivity {
    CalendarView calendarView;
    TextView dayTxt;

    public final int REQUEST_ADD = 0;

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

        Visit visit = new Visit();
        visit.setDoctor("Kowalski");
        visit.setLocation("Wrocław");

        addVisit(visit);
    }

    public class VisitView extends Button {

        public VisitView(Context context, Visit visit) {
            this(context);
            this.setVisit(visit);
        }

        public VisitView(Context context) {
            super(context);
            this.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        }

        public void setVisit(Visit v)
        {
            this.m_visit = v;
            this.setText(m_visit.getLocation() + "\n" + m_visit.getDoctor());
        }

        public Visit getVisit()
        {
            return this.m_visit;
        }

        private Visit m_visit;
    }

    public void visitViewOnClick(View v)
    {
        VisitView vv = (VisitView)v;
        Visit visit = vv.getVisit();

        String[] visit_data = {visit.getDoctor(), visit.getLocation(), visit.getDate(), visit.getTime()};

        Intent intent = new Intent(this, VisitActivity.class);
        intent.putExtra("visit", visit_data);

        startActivityForResult(intent, REQUEST_ADD);
    }

    private void addVisit(Visit visit)
    {
        VisitView vv = new VisitView(this, visit);
        vv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visitViewOnClick(v);
            }
        });

        vv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });

        LinearLayout rl = (LinearLayout)findViewById(R.id.visits_linear_layout);
        rl.addView(vv);
    }
}
