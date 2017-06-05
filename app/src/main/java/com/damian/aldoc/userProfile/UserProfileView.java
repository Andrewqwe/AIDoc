package com.damian.aldoc.userProfile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.damian.aldoc.Database;
import com.damian.aldoc.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

//TODO: Zaladowanie i ustawianie zdjecia uzytkownika
public class UserProfileView extends AppCompatActivity {

    private ListView lista;
    private ImageView image_view;
    private ArrayList<HashMap<String,String>> rekordy;
    private UserProfileListAdapter adapter;
    private FloatingActionButton myFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        rekordy = new ArrayList<HashMap<String, String>>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        image_view = (ImageView) findViewById(R.id.user_profile_imageView);
        lista = (ListView) findViewById(R.id.user_data_list);
        lista.setSelector(android.R.color.transparent); //pozbywa sie animacji po kliknieciu w element listy
        lista.setOverScrollMode(View.OVER_SCROLL_NEVER);//wylaczenie efektu ktory pozwala
        // na przeciagniecie listy dalej niz sa w niej dane, pojawia sie na koncu wkurzajacy niebieski cien
        //ktorego nie wymyslilem jak wylaczyc

        final ArrayList<String> arr = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.user_data_key_array)));
        // Linijka wyzej odwoluje sie do slownika zdefiniowanego w res->values->strings.xml

        String[] a = Database.GetUserInfo();    //a[0]- name // a[1]- mail // a[2]- UID
        if (a != null) {
            this.setUserName(a[0]);
        }

        lista.requestLayout();
        //utworzenie obiektu adaptera napisanego na potrzeby tego ekranu
        adapter = new UserProfileListAdapter(rekordy, this);
        lista.setAdapter(adapter);
        //Kod dziala jednak jest do refaktoryzacji przed stworzeniem opcji edytowania
        //Przyda sie zapamietywanie ID pol ktore zostaly zaladowane na ekran
        Database.Initialize(true);
        DatabaseReference ref;
        if (a != null) {
            //nie kazdy profil jest jakos sensownie uzupelniony dlatego zostawiam
            //w komentarzu kod w ktorym jest "sztywno" wpisane uid uzupelnionego profilu
            ref = Database.SetLocation("users/" + a[2]);
            ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    if(objectMap!=null) {
                        rekordy.clear();
                        //lista.invalidateViews();
                        for (String str : arr) {
                            String temp[] = str.split("//");
                            String value = String.valueOf(objectMap.get(temp[0]));
                            if (value != null) {
                                if (!value.equals("null")) {
                                    addValue(temp[1], value);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            ref.addValueEventListener(postListener);
            //ref.addListenerForSingleValueEvent(postListener);
            Glide.with(this).load(Database.GetUserImage()).into(image_view);
        }

        this.myFab = (FloatingActionButton) findViewById(R.id.myFloatingActionButton);
        myFab.setOnClickListener(new View.OnClickListener() {   // guzik do edytowania profilu
                                     @Override
                                     public void onClick(View v) {
                                         Intent intent_user_profile = new Intent(getApplicationContext(), UserProfileEditActivity.class);
                                         startActivity(intent_user_profile);
                                     }
                                 }
        );
    }

    public void setUserName(String userName)
    {
        TextView userNameView = (TextView) findViewById(R.id.profile_full_name);
        userNameView.setText(userName);
    }

    public void addValue(String name, String value)
    {
        HashMap<String,String> temp= new HashMap<String,String>();
        temp.put("name",name);
        temp.put("value",value);
        rekordy.add(temp);
    }
}
