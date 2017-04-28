package com.damian.aldoc;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Button;


//Firebase
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class VisitsActivity extends AppCompatActivity {

    public final int ACTION_ADD = 0;
    public final int REQUEST_ADD = 0;
    public final int ACTION_EDIT = 1;

    // Firebase instance variables
    private ChildEventListener mChildEventListener;

    public class VisitView extends Button{

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

            String visit_info = new SimpleDateFormat("dd-MM-yyyy hh:mm").format(v.getTime().getTime()) + m_visit.getPlace() + "\n" + m_visit.getDoctor();

            this.setText(m_visit.getPlace() + "\n" + m_visit.getDoctor());
        }

        public Visit getVisit()
        {
            return this.m_visit;
        }

        private Visit m_visit;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visits);

        Database.Initialize(true);
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Visit visits = dataSnapshot.getValue(Visit.class);  //czytanie z bazy i tworzenie przycisku
                                addVisitView(visits);
                            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };
        Database.SetLocation("visits").addChildEventListener(mChildEventListener);
  }

    public void addVisitView(Visit visit)
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

    private void editVisit()
    {
        Intent intent = new Intent(this, EditVisitActivity.class);
        intent.putExtra("action", ACTION_EDIT);

        startActivity(intent);
    }

    private void addVisit()
    {
        Intent intent = new Intent(this, EditVisitActivity.class);
        intent.putExtra("action", ACTION_ADD);

        startActivityForResult(intent, REQUEST_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ADD) {
            if(resultCode == AppCompatActivity.RESULT_OK){
                String[] result = data.getStringArrayExtra("visit");

                String[] date = result[2].split("-");
                String[] time = result[3].split(":");

                Calendar c = Calendar.getInstance();
                c.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]), Integer.parseInt(date[0]), Integer.parseInt(time[1]), Integer.parseInt(time[0]));

                Visit visit = new Visit(result[0],result[1], c);
                Database.SendObjectToDatabase("visits", visit);
                //addVisitView(visit);
            }
        }
    }

    public void fabAddOnClick(View v)
    {
        addVisit();
    }

    public void visitViewOnClick(View v)
    {
        VisitView vv = (VisitView)v;
        Visit visit = vv.getVisit();

        Calendar c = visit.getTime();
        String date = new SimpleDateFormat("dd-MM-yyyy").format(c.getTime());
        String time = new SimpleDateFormat("hh:mm").format(c.getTime());

        String[] visit_data = {visit.getDoctor(), visit.getPlace(), date, time};

        Intent intent = new Intent(this, VisitActivity.class);
        intent.putExtra("visit", visit_data);

        startActivityForResult(intent, REQUEST_ADD);
    }



}


