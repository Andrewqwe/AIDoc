package com.damian.aldoc.userProfile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class UserProfileEditActivity extends AppCompatActivity {
    private ListView listView;
    private ImageView image_view;
    private  ArrayList<UserProfileEditListItem> items;
    private UserProfileEditListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //======= Budowanie widoku
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.user_data_list);
        listView.setSelector(android.R.color.transparent);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        image_view = (ImageView) findViewById(R.id.edit_profile_imageView);
        //======= Inicjalizacja
        items = new ArrayList<>();
        adapter = new UserProfileEditListAdapter(items,this);
        listView.requestLayout();
        listView.setAdapter(adapter);
        final ArrayList<String> arr = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.user_data_key_array)));
        // Linijka wyzej odwoluje sie do slownika zdefiniowanego w res->values->strings.xml

        //======= Rozpoczecie dialogu z baza danych
        String[] a = Database.GetUserInfo(); //a[0]- name // a[1]- mail // a[2]- UID
        if(a!=null)
        {
            Database.Initialize(true);
            DatabaseReference ref = Database.SetLocation("users/" + a[2]);
            setName(a[0]);
            /*ValueEventListener postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    if(objectMap!=null) {
                        for(String str : arr) {
                            String temp[]=str.split("//");
                            String value = String.valueOf(objectMap.get(temp[0]));
                            addValue(temp[1],value,temp[0],temp[2]); // tlumaczenie, wartosc, pytany klucz
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            ref.addListenerForSingleValueEvent(postListener);
            */

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //TODO: Rozwiazanie bardzo tymczasowe, nie znalazlem lepszego sposobu na odswierzanie
                    if(!items.isEmpty()){refresh();}
                    else {
                        Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                        if (objectMap != null) {
                            for (String str : arr) {
                                String temp[] = str.split("//");
                                String value = String.valueOf(objectMap.get(temp[0]));
                                addValue(temp[1], value, temp[0], temp[2]); // tlumaczenie, wartosc, pytany klucz
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            Glide.with(this).load(Database.GetUserImage()).into(image_view);
        }
    }

    private void addValue(String translation, String value, String key, String data_type)
    {
        UserProfileEditListItem item;
        if(value==null || value.equals("null")){
            item = new UserProfileEditListItem(translation,key,data_type);
            items.add(item);
        }else {
            item = new UserProfileEditListItem(translation, value, key, data_type);
            items.add(item);
        }
        adapter.notifyDataSetChanged();
    }

    private void setName(String name)
    {
        TextView textView = (TextView) findViewById(R.id.edit_profile_full_name);
        textView.setText(name);
    }

    public void refresh()  //metoda tymczasowa do odswierzania profilu uzytkownika po zmianie danego pola
    {
        //Intent refresh = new Intent(this, UserProfileEditActivity.class);
        //startActivity(refresh);
        //this.finish(); //
    }
}
