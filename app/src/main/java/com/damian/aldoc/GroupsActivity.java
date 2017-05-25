package com.damian.aldoc;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.*;
import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GroupsActivity extends AppCompatActivity {

    private HashMap<String, View> m_group_views = new HashMap<>();
    private HashMap<String, View> m_user_views = new HashMap<>();
    private HashMap<String, ChildEventListener> m_group_listeners = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        /*Na poczatek ustawiamy listener dla listy grup danego usera
        * zeby wiedziec do jakich grup nalezy i wiedziec kiedy sie to zmienia*/

        DatabaseReference ref = Database.SetLocation(Database.getUsersDirName());
        ref.child(Database.GetUserUID()).child("groups").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                /*czytamy uid grupy i dodajemy listenera do danej grupy
                * ktory bedzie czytal jej czlonkow*/
                final String gid = dataSnapshot.getValue(String.class);

                final DatabaseReference group_ref = Database.SetLocation(Database.getGroupsDirName()).child(gid);

                /*listener do danej grupy*/
                ChildEventListener listener = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        /*klucz jest jednoczesnie uid uzytkownika w bazie*/
                        final String user_uid = dataSnapshot.getKey();

                        /*znajdujemy view danej grupy w hashmapie*/
                        final View group_view = m_group_views.get(gid);
                        final LayoutInflater inflater = LayoutInflater.from(group_view.getContext());

                        /*tworzymy view rzędu przedstawiającego jednego z użytkowników w grupie*/
                        final View user_row_view = inflater.inflate(R.layout.user_group_row, null);

                        /*znajdujemy w powyższym view textview wyswietlajace nazwe uzytkownika*/
                        final TextView tv_username = (TextView)user_row_view.findViewById(R.id.userGroupRow_userName);

                        /*uzywamy uid użytkownika żeby przeczytać jego imie z bazy
                        * i ustawic je jako tekst w textview dotyczacym danego uzytkownika*/
                        DatabaseReference username_ref = Database.SetLocation(Database.getUsersDirName()).child(user_uid).child("name");
                        username_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                String username = dataSnapshot.getValue(String.class);
                                tv_username.setText(username);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });

                        /*znajdujemy view z obrazkiem uzytkownika*/
                        final ImageView iv_userimage = (ImageView)user_row_view.findViewById(R.id.userGroupRow_userImage);

                        /*pobieramy z bazy zdjęcie użytkownika*/
                        Uri user_image_uri = Database.GetUserImage();

                        /*jeżeli zdjęcie istnieje to tworzymy z niego bitmape i wyświetlamy*/
                        if(user_image_uri != null)
                        {
                            try {
                                InputStream pictureStream = getContentResolver().openInputStream(user_image_uri);
                                Bitmap bitmap = BitmapFactory.decodeStream(pictureStream);
                                iv_userimage.setImageBitmap(bitmap);
                            } catch (FileNotFoundException e) {}
                        }

                        /*dodajemy onClickListenera do view z rzedem danej grupy, tak zeby mozna bylo
                        * klikac na danego usera i sie na niego przelaczyc itp.*/
                        user_row_view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Database.setCurrentUid(user_uid);
                            }
                        });

                        //TODO:Dodać onLongClickiListenera który wyświetli menu z opcjami (np. usuń użytkownika z grupy, zmień uprawnienia itp)

                        /*dodajemy view z rzedem danego uzytkownika do view grupy*/
                        LinearLayout group_layout = (LinearLayout)group_view.findViewById(R.id.userGroup_usersLayout);
                        group_layout.addView(user_row_view);

                        /*dodajemy view z rzedem do hashmapy z kluczem uid uzytkownika
                        * zeby potem moc usunac dane view kiedy uzytkownik zostanie usuniety
                        * albo zeby zmodyfikowac view kiedy uzytkownik zostanie zmodyfikowany*/
                        m_user_views.put(user_uid, user_row_view);
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s)
                    {
                        //TODO: tutaj kod jezeli uprawnienia jakiegos uzytkownika sie zmienia
                        Long permission_level = dataSnapshot.getValue(Long.class);
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot)
                    {
                        /*klucz jest jednoczesnie uid uzytkownika w bazie*/
                        final String user_uid = dataSnapshot.getKey();

                        /*usuwamy view z rzedem danego uzytkownika z view grupy*/
                        final View group_view = m_group_views.get(gid);
                        LinearLayout group_layout = (LinearLayout)group_view.findViewById(R.id.userGroup_usersLayout);
                        group_layout.removeView(m_user_views.get(user_uid));

                        /*usuwamy view z rzedem danego uzytkownika z hashmapy*/
                        m_user_views.remove(user_uid);
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                };

                group_ref.child("users").addChildEventListener(listener);

                /*dodajemy listenera do hashmapy z kluczem uid grupy
                * aby pozniej móc usunąć konkretnego listenera od konkretnej grupy
                * np. kiedy dana grupa zostanie usunieta*/
                m_group_listeners.put(gid, listener);

                /*dla każdej grupy dodajemy View zawierające listę członków danej grupy*/
                LinearLayout groupsLayout = (LinearLayout)findViewById(R.id.activityGroups_layout);

                /*tworzymy view*/
                LayoutInflater inflater = LayoutInflater.from(GroupsActivity.this);
                LinearLayout group_view = (LinearLayout)inflater.inflate(R.layout.user_group, null);

                /*znajdujemy textview ktore ma wyswietlac nazwe grupy*/
                final TextView tv_groupName = (TextView)group_view.findViewById(R.id.userGroup_groupName);

                /*pobieramy z bazy nazwe grupy i ustawiamy ja jako tekst w textview*/
                group_ref.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        tv_groupName.setText(dataSnapshot.getValue(String.class));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                /*dodajemy view wyświetlające członków grupy do layoutu*/
                groupsLayout.addView(group_view);

                /*dodajemy stworzone view do hashmapy z kluczem uid grupy
                * żeby potem móc usunąć konkretne view kiedy zostanie usunięta
                * konkretna grupa*/
                m_group_views.put(gid, group_view);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot)
            {
                /*kiedy grupa zostanie usunieta z bazy pobieramy jej uid*/
                String gid = dataSnapshot.getValue(String.class);
                DatabaseReference group_ref = Database.SetLocation(Database.getGroupsDirName()).child(gid);

                /*usuwamy listenera przypisanego do danej grupy (znajdujemy go w hashmapie po kluczu uid grupy)*/
                group_ref.removeEventListener(m_group_listeners.get(gid));

                /*usuwamy z layoutu view odpowiadające danej grupie (view znajdujemy w hashmapie po kluczu uid grupy)*/
                LinearLayout groupsLayout = (LinearLayout)findViewById(R.id.activityGroups_layout);
                groupsLayout.removeView(m_group_views.get(gid));

                /*usuwamy view i listenera z hashmap*/
                m_group_views.remove(gid);
                m_group_listeners.remove(gid);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void createGroupOnClick(View view)
    {
        AlertDialog.Builder createGroupDialogBuilder = new AlertDialog.Builder(this);

        View createGroupView = LayoutInflater.from(this).inflate(R.layout.create_group_layout, null);
        final TextView tv_groupName = (TextView)createGroupView.findViewById(R.id.createGroup_groupName);

        createGroupDialogBuilder.setTitle("Nazwa grupy:");
        createGroupDialogBuilder.setView(createGroupView);

        createGroupDialogBuilder.setCancelable(true);

        createGroupDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String group_name = tv_groupName.getText().toString();
                Database.createGroupInDatabase(group_name, Database.GetUserUID());
            }
        });

        createGroupDialogBuilder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        Dialog createGroupDialog = createGroupDialogBuilder.create();
        createGroupDialog.show();
    }
}
