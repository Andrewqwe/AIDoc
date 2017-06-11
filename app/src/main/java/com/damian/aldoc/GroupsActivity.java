package com.damian.aldoc;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.google.firebase.database.*;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class GroupsActivity extends AppCompatActivity {

    /*wiąże view grupy z id grupy której dane view dotyczy*/
    private HashMap<String, View> m_hmap_groupId_groupView = new HashMap<>();
    /*wiąże view usera z id usera którego dane view dotyczy*/
    private HashMap<String, View> m_hmap_userId_userView = new HashMap<>();
    /*wiąże listenery z id grup których dotyczą*/
    private HashMap<String, ChildEventListener> m_hmap_groupId_groupListener = new HashMap<>();
    /*wiąże view usera z id grupy w której dane view się znajduje*/
    private  HashMap<View, String> m_hmap_userView_groupId = new HashMap<>();
    /*wiąże view usera z id usera ktorego dane view dotyczy*/
    private  HashMap<View, String> m_hmap_userView_userId = new HashMap<>();

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
                        final View group_view = m_hmap_groupId_groupView.get(gid);
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

                        /*dodajemy context menu aby umożliwić różne operacje na użytkownikach w grupie*/
                        registerForContextMenu(user_row_view);

                        /*dodajemy view z rzedem danego uzytkownika do view grupy*/
                        LinearLayout group_layout = (LinearLayout)group_view.findViewById(R.id.userGroup_usersLayout);
                        group_layout.addView(user_row_view);

                        /*dodajemy view z rzedem do hashmapy z kluczem uid uzytkownika
                        * zeby potem moc usunac dane view kiedy uzytkownik zostanie usuniety
                        * albo zeby zmodyfikowac view kiedy uzytkownik zostanie zmodyfikowany*/
                        m_hmap_userId_userView.put(user_uid, user_row_view);
                        /*dodajemy view usera z id grupy oraz z id usera do hashmap
                         aby rozpoznać użytkownika i grupe po samym view użytkownika*/
                        m_hmap_userView_groupId.put(user_row_view, gid);
                        m_hmap_userView_userId.put(user_row_view, user_uid);
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
                        final View group_view = m_hmap_groupId_groupView.get(gid);
                        LinearLayout group_layout = (LinearLayout)group_view.findViewById(R.id.userGroup_usersLayout);
                        group_layout.removeView(m_hmap_userId_userView.get(user_uid));

                        /*usuwamy view z rzedem danego uzytkownika z hashmapy*/
                        m_hmap_userId_userView.remove(user_uid);
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
                m_hmap_groupId_groupListener.put(gid, listener);

                /*dla każdej grupy dodajemy View zawierające listę członków danej grupy*/
                LinearLayout groupsLayout = (LinearLayout)findViewById(R.id.activityGroups_layout);

                /*tworzymy view grupy*/
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

                /*znajdujemy w view grupy button służący do dodawania nowych użytkowników
                * i ustawiamy mu onClickListenera*/
                final Button btn_addUser = (Button)group_view.findViewById(R.id.userGroup_addUserButton);
                btn_addUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAddUserClick(v, gid);
                    }
                });

                /*dodajemy view wyświetlające członków grupy do layoutu*/
                groupsLayout.addView(group_view);

                /*dodajemy stworzone view do hashmapy z kluczem uid grupy
                * żeby potem móc usunąć konkretne view kiedy zostanie usunięta
                * konkretna grupa*/
                m_hmap_groupId_groupView.put(gid, group_view);
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
                group_ref.removeEventListener(m_hmap_groupId_groupListener.get(gid));

                /*usuwamy z layoutu view odpowiadające danej grupie (view znajdujemy w hashmapie po kluczu uid grupy)*/
                LinearLayout groupsLayout = (LinearLayout)findViewById(R.id.activityGroups_layout);
                groupsLayout.removeView(m_hmap_groupId_groupView.get(gid));

                /*usuwamy view i listenera z hashmap*/
                m_hmap_groupId_groupView.remove(gid);
                m_hmap_groupId_groupListener.remove(gid);
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

    private View contextView;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_group_user, menu);
        contextView = v;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final View user_view = contextView;

        switch (item.getItemId())
        {
            case R.id.userGroupMenu_delete:
                AlertDialog.Builder alert_delete = new AlertDialog.Builder(this);
                alert_delete.setTitle("Czy na pewno chcesz usunąć użytkownika z grupy?");

                alert_delete.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String gid = m_hmap_userView_groupId.get(user_view);
                        String uid = m_hmap_userView_userId.get(user_view);
                        Database.removeUserFromGroup(uid, gid);
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
            default:
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void onAddUserClick(View view, final String group_id)
    {
        AlertDialog.Builder alert_add = new AlertDialog.Builder(this);
        alert_add.setTitle("Dodaj użytkownika");

        View dialog_view = (LayoutInflater.from(this)).inflate(R.layout.group_add_user, null);
        alert_add.setView(dialog_view);

        final EditText email_et = (EditText)dialog_view.findViewById(R.id.groupAddUser_email);

        alert_add.setCancelable(true).setPositiveButton("Dodaj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                /*czytamy z pola tekstowego email podany przez użytkownika*/
                final String email = email_et.getText().toString();
                /*wyszukujemy użytkownika o podanym emailu i jeżeli istnieje to dodajemy go do grupy*/
                Query q = Database.SetLocation(Database.getUsersDirName()).orderByChild("email").equalTo(email);
                q.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Database.addUserToGroup(dataSnapshot.getKey(), group_id, Long.valueOf(1));
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        alert_add.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        Dialog add_dialog = alert_add.create();
        add_dialog.show();
    }
}

