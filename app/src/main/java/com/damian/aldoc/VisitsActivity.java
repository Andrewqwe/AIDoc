package com.damian.aldoc;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.Toast;


//Firebase
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VisitsActivity extends AppCompatActivity {

    public final int ACTION_ADD = 0;
    public final int REQUEST_ADD = 0;
    public final int ACTION_EDIT = 1;

    // Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
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

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                Visit visits = dataSnapshot.getValue(Visit.class);  //Tutaj wywala null pointer exception - Radosław  -- Czytanie z basy
                                addVisit(visits);
                            }

                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
           public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };
                mMessagesDatabaseReference.addChildEventListener(mChildEventListener);


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

    private void editVisit()
    {
        Intent intent = new Intent(this, EditVisitActivity.class);
        intent.putExtra("Action", ACTION_EDIT);

        startActivity(intent);
    }

    private void addVisit()
    {
        Intent intent = new Intent(this, EditVisitActivity.class);
        intent.putExtra("Action", ACTION_ADD);

        startActivityForResult(intent, REQUEST_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ADD) {
            if(resultCode == AppCompatActivity.RESULT_OK){
                String[] result = data.getStringArrayExtra("Visit");

                Visit visit = new Visit(result[0],result[1]);
              //  visit.setDoctor(result[0]);  // Jak utworzylem konstruktor to już raczej nie musze tego nizej dawac..
             //   visit.setPlace(result[1]);

                addVisit(visit);
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

        String[] visit_data = {visit.getDoctor(), visit.getPlace(), "time"};

        Intent intent = new Intent(this, VisitActivity.class);
        intent.putExtra("Visit", visit_data);

        startActivityForResult(intent, REQUEST_ADD);
    }



}


