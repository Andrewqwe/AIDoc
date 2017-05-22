package com.damian.aldoc.calendar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.damian.aldoc.Database;
import com.damian.aldoc.R;
import com.damian.aldoc.visits.Visit;
import com.damian.aldoc.visits.VisitActivity;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressLint("SimpleDateFormat")
public class CalendarActivity extends AppCompatActivity {
    private CaldroidFragment caldroidFragment;
    private CaldroidFragment dialogCaldroidFragment;
    private ListView list_view;
    private ArrayAdapter<Visit> adapter;
    private List<Visit> visits = new ArrayList<>();
    private HashMap<String, ArrayList<Visit>> visitsHashMap = new HashMap<>();
    private Date dataTmp;
    private Date dateToday;

    // Firebase instance variables
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        caldroidFragment = new CaldroidFragment();

        // Ustawianie właściwości

        // Jeżeli aktywność tworzona jest po rotacji
        if (savedInstanceState != null) {
            caldroidFragment.restoreStatesFromKey(savedInstanceState,
                    "CALDROID_SAVED_STATE");
        }
        else {
            Bundle args = new Bundle();
            Calendar cal = Calendar.getInstance();
            args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
            args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
            args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
            args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
            args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.MONDAY);
            args.putInt(CaldroidFragment.THEME_RESOURCE, com.caldroid.R.style.CaldroidDefaultDark);
            caldroidFragment.setArguments(args);
        }

        // Przypinanie do aktywności
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar1, caldroidFragment);
        t.commit();

        Calendar cal = Calendar.getInstance();
        dataTmp = cal.getTime();
        dateToday = cal.getTime();
        caldroidFragment.setBackgroundDrawableForDate(ResourcesCompat.getDrawable(getResources(), R.drawable.today, null), dataTmp);
        caldroidFragment.refreshView();

        Database.Initialize(true);
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Visit visit = dataSnapshot.getValue(Visit.class);
                visit.setUid(dataSnapshot.getKey());

                Date data = getDateFromVisit(visit);
                caldroidFragment.setBackgroundDrawableForDate(ResourcesCompat.getDrawable(getResources(), R.drawable.event, null), data);
                caldroidFragment.refreshView();

                addVisitToVisitHashMap(data, visit);

                Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();
                checkVisitForDate(date);
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                Visit visit = dataSnapshot.getValue(Visit.class);
                String uid = dataSnapshot.getKey();
                visit.setUid(uid);

                for (Visit visit1 : visits) {
                    if (visit1.getUid().equals(uid)) {
                        Date data = getDateFromVisit(visit1);
                        caldroidFragment.clearBackgroundDrawableForDate(data);

                        removeVisitFormHashMap(data, visit);
                        data = getDateFromVisit(visit);

                        caldroidFragment.setBackgroundDrawableForDate(ResourcesCompat.getDrawable(getResources(), R.drawable.event, null), data);
                        caldroidFragment.refreshView();

                        addVisitToVisitHashMap(data, visit);

                        Calendar cal = Calendar.getInstance();
                        Date date = cal.getTime();
                        checkVisitForDate(date);
                        break;
                    }
                }
            }
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                String uid = dataSnapshot.getKey();
                for (Visit visit : visits) {
                    if (visit.getUid().equals(uid)) {
                        Date data = getDateFromVisit(visit);
                        caldroidFragment.clearBackgroundDrawableForDate(data);
                        caldroidFragment.refreshView();

                        removeVisitFormHashMap(data, visit);

                        Calendar cal = Calendar.getInstance();
                        Date date = cal.getTime();
                        checkVisitForDate(date);
                        break;
                    }
                }
            }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };
        Database.SetLocation(Database.getVisitsPath()).addChildEventListener(mChildEventListener);

        final CaldroidListener listener = new CaldroidListener() {
            @Override
            public void onSelectDate(Date date, View view) {
                caldroidFragment.clearBackgroundDrawableForDate(dataTmp);
                caldroidFragment.setBackgroundDrawableForDate(ResourcesCompat.getDrawable(getResources(), R.drawable.select, null), date);
                if (getDateAsString(dataTmp).equals(getDateAsString(dateToday))) {
                    caldroidFragment.setBackgroundDrawableForDate(ResourcesCompat.getDrawable(getResources(), R.drawable.today, null), dataTmp);
                }
                if (visitsHashMap.containsKey(getDateAsString(dataTmp))) {
                    caldroidFragment.setBackgroundDrawableForDate(ResourcesCompat.getDrawable(getResources(), R.drawable.event, null), dataTmp);
                }

                caldroidFragment.refreshView();
                dataTmp = date;
                visits.clear();
                if (visitsHashMap.containsKey(getDateAsString(date))) {
                    caldroidFragment.setBackgroundDrawableForDate(ResourcesCompat.getDrawable(getResources(), R.drawable.select_event, null), date);
                    caldroidFragment.refreshView();
                    visits.addAll(visitsHashMap.get(getDateAsString(date)));
                }
                adapter.notifyDataSetChanged();
            }
        };

        caldroidFragment.setCaldroidListener(listener);

        //tworzymy adapter i przypisujemy go do listview zeby wyswietlac wizyty
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, visits);

        list_view = (ListView)findViewById(R.id.visits_listView);
        list_view.setAdapter(adapter);
        registerForContextMenu(list_view);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                visitOnClick(visits.get(position));
            }
        });
    }

    /**
     * Zapisywanie stanu kalendarza
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);

        if (caldroidFragment != null) {
            caldroidFragment.saveStatesToKey(outState, "CALDROID_SAVED_STATE");
        }

        if (dialogCaldroidFragment != null) {
            dialogCaldroidFragment.saveStatesToKey(outState,
                    "DIALOG_CALDROID_SAVED_STATE");
        }
    }

    private Date getDateFromVisit(Visit visit) {
        String date[] = visit.getDate().split("-");
        Calendar cal = Calendar.getInstance();

        cal.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]));
        return cal.getTime();
    }

    private String getDateAsString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }

    private void visitOnClick(Visit visit)
    {
        Intent intent = new Intent(this, VisitActivity.class);
        intent.putExtra("visit", visit.getUid());
        startActivity(intent);
    }

    private void checkVisitForDate(Date date) {
        visits.clear();
        if (visitsHashMap.containsKey(getDateAsString(date))) {
            visits.addAll(visitsHashMap.get(getDateAsString(date)));
        }
        adapter.notifyDataSetChanged();
    }

    private void addVisitToVisitHashMap(Date date, Visit visit) {
        if (visitsHashMap.containsKey(getDateAsString(date))) {
            ArrayList<Visit> visitArrayListHelp = visitsHashMap.get(getDateAsString(date));
            visitArrayListHelp.add(visit);
        }
        else {
            ArrayList<Visit> visitArrayListHelp = new ArrayList<>();
            visitArrayListHelp.add(visit);
            visitsHashMap.put(getDateAsString(date), visitArrayListHelp);
        }
    }

    private void removeVisitFormHashMap(Date date, Visit visit) {
        if (visitsHashMap.containsKey(getDateAsString(date))) {
            ArrayList<Visit> visitArrayListHelp = visitsHashMap.get(getDateAsString(date));
            visitArrayListHelp.remove(visit);
            if (visitArrayListHelp.isEmpty()) {
                visitsHashMap.remove(getDateAsString(date));
            }
        }
    }
}
