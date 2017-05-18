package com.damian.aldoc;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;


//Firebase
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;


public class VisitsActivity extends AppCompatActivity {

    private ListView list_view;
    private ArrayAdapter<Visit> adapter;
    private List<Visit> visits = new ArrayList<>();

    private final int ACTION_ADD = 0;
    private final int REQUEST_ADD = 0;
    private final int ACTION_EDIT = 1;
    private final int REQUEST_EDIT = 1;

    // Firebase instance variables
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visits);

        Database.Initialize(true);
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Visit visit = dataSnapshot.getValue(Visit.class);
                visit.setUid(dataSnapshot.getKey());

                visits.add(visit);
                adapter.notifyDataSetChanged();
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                Visit visit = dataSnapshot.getValue(Visit.class);
                String uid = dataSnapshot.getKey();
                visit.setUid(uid);

                for(int v = 0; v < visits.size(); v++)
                {
                    if(visits.get(v).getUid().equals(uid))
                    {
                        visits.set(v, visit);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                String uid = dataSnapshot.getKey();
                for(int v = 0; v < visits.size(); v++)
                {
                    if(visits.get(v).getUid().equals(uid))
                    {
                        visits.remove(v);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(DatabaseError databaseError) {}
        };
        Database.SetLocation("visits").addChildEventListener(mChildEventListener);

        //tworzymy adapter i przypisujemy go do listview zeby wyswietlac wizyty
        adapter = new ArrayAdapter<Visit>(this, android.R.layout.simple_list_item_1, visits);

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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_visit, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Visit visit = visits.get(info.position);

        switch (item.getItemId())
        {
            case R.id.visitMenu_delete:
                AlertDialog.Builder alert_delete = new AlertDialog.Builder(this);
                alert_delete.setTitle("Czy na pewno chcesz usunąć wizytę?");

                alert_delete.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Database.DeleteVisitFromDatabase(visit.getUid());
                    }
                });

                alert_delete.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                Dialog delete_dialog = alert_delete.create();
                delete_dialog.show();
                break;
            case R.id.visitMenu_edit:
                editVisit(visit);
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void editVisit(Visit visit)
    {
        Intent intent = new Intent(this, EditVisitActivity.class);
        intent.putExtra("action", ACTION_EDIT);

        String[] visit_data = {visit.getDoctor(), visit.getLocation(), visit.getDate(), visit.getTime(), visit.getUid()};

        intent.putExtra("visit", visit_data);

        startActivityForResult(intent, REQUEST_EDIT);
    }

    private void addVisit()
    {
        Intent intent = new Intent(this, EditVisitActivity.class);
        intent.putExtra("action", ACTION_ADD);

        startActivityForResult(intent, REQUEST_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == AppCompatActivity.RESULT_OK)
        {
            if (requestCode == REQUEST_ADD)
            {
                String[] result = data.getStringArrayExtra("visit");

                Visit visit = new Visit(result[0], result[1], result[2], result[3]);
                Database.SendObjectVisitToDatabase(visit);
            }
            else if (requestCode == REQUEST_EDIT)
            {
                String[] result = data.getStringArrayExtra("visit");

                Visit visit = new Visit(result[0], result[1], result[2], result[3]);
                Database.UpdateVisitInDatabase(visit, result[4]);
            }
        }
    }

    public void fabAddOnClick(View v)
    {
        addVisit();
    }

    private void visitOnClick(Visit visit)
    {
        Intent intent = new Intent(this, VisitActivity.class);

        String[] visit_data = {visit.getDoctor(), visit.getLocation(), visit.getDate(), visit.getTime(), visit.getUid()};

        intent.putExtra("visit", visit_data);

        startActivity(intent);
    }



}


