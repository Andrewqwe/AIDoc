package com.damian.aldoc.visits;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.damian.aldoc.*;
import com.damian.aldoc.R;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class VisitActivity extends AppCompatActivity {

    private ListView list_view;
    private ArrayAdapter<Prescription> adapter;
    private List<Prescription> prescriptions = new ArrayList<>();

    private Visit m_visit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.damian.aldoc.R.layout.activity_visit);

        final String visit_uid = getIntent().getStringExtra("visit");

        /*czytamy wizyte z bazy (uid wizyty przekazane z poprzedniego activity)*/
        Database.SetLocation(Database.getVisitsPath() + "/" + visit_uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot)
            {
                m_visit = (Visit)snapshot.getValue(Visit.class);
                m_visit.setUid(visit_uid);

                /*Wpisujemy do pol tekstowych dane wizyty*/
                TextView tv = (TextView)findViewById(R.id.textViewDoctor);
                tv.setText("Lekarz: " + m_visit.getDoctor());

                tv = (TextView)findViewById(R.id.textViewLocation);
                tv.setText("Miejsce wizyty: " + m_visit.getLocation());

                tv = (TextView)findViewById(R.id.textViewDate);
                tv.setText("Data: " + m_visit.getDate());

                tv = (TextView)findViewById(R.id.textViewTime);
                tv.setText("Godzina: " + m_visit.getTime());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        //tworzymy listenera, ktory dodaje do listview wszystkie recepty
        //dotyczace danej wizyty
        Database.Initialize(true);
        DatabaseReference ref = Database.SetLocation(Database.getPrescriptionsPath());
        Query q = ref.orderByChild("visitUid").equalTo(visit_uid);
        q.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Prescription p = dataSnapshot.getValue(Prescription.class);
                p.setUid(dataSnapshot.getKey());

                prescriptions.add(p);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s)
            {
                Prescription prescription = dataSnapshot.getValue(Prescription.class);
                String uid = dataSnapshot.getKey();
                prescription.setUid(uid);

                for(int p = 0; p < prescriptions.size(); p++)
                {
                    if(prescriptions.get(p).getUid().equals(uid))
                    {
                        prescriptions.set(p, prescription);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                String uid = dataSnapshot.getKey();
                for(int p = 0; p < prescriptions.size(); p++)
                {
                    if(prescriptions.get(p).getUid().equals(uid))
                    {
                        prescriptions.remove(p);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //tworzymy adapter i przypisujemy go do listview zeby wyswietlac recepty
        adapter = new ArrayAdapter<Prescription>(this, android.R.layout.simple_list_item_1, prescriptions);

        list_view = (ListView)findViewById(R.id.listViewPrescriptions);
        list_view.setAdapter(adapter);
        registerForContextMenu(list_view);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                onPrescriptionClick(prescriptions.get(position));
            }
        });
    }

    private void onPrescriptionClick(Prescription p)
    {
        Intent intent = new Intent(this, PrescriptionActivity.class);
        String[] prescription_data = {p.getName(), p.getUid(), p.getPhoto()};
        intent.putExtra("prescription", prescription_data);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_prescription, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final Prescription p = prescriptions.get(info.position);

        switch (item.getItemId())
        {
            case R.id.prescriptionMenu_delete:
                AlertDialog.Builder alert_delete = new AlertDialog.Builder(this);
                alert_delete.setTitle("Czy na pewno chcesz usunąć receptę?");

                alert_delete.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Database.DeletePrescriptionFromDatabase(p.getUid());
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
            case R.id.prescriptionMenu_changeName:
                View view = (LayoutInflater.from(this)).inflate(R.layout.prescription_name, null);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setView(view);
                alertBuilder.setTitle("Nazwa recepty");

                final EditText et_prescription = (EditText)view.findViewById(R.id.textPrescriptionName);
                et_prescription.setText(p.getName());

                alertBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        p.setName(et_prescription.getText().toString());
                        Database.UpdatePrescriptionInDatabase(p, p.getUid());
                    }
                });

                alertBuilder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                Dialog dialog = alertBuilder.create();
                dialog.show();
                break;
        }

        return super.onContextItemSelected(item);
    }

    public void addOnClick(View v)
    {
        View view = (LayoutInflater.from(this)).inflate(R.layout.prescription_name, null);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setView(view);
        alertBuilder.setTitle("Nazwa recepty");

        final EditText et_prescription = (EditText)view.findViewById(R.id.textPrescriptionName);

        alertBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Prescription p = new Prescription(m_visit.getUid(), et_prescription.getText().toString(), null);
                Database.SendObjectPrescriptionToDatabase(p);
            }
        });

        alertBuilder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        Dialog dialog = alertBuilder.create();
        dialog.show();
    }
}
