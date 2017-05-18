package com.damian.aldoc;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UserProfileEditActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<UserProfileEditListItem> items;
    private static UserProfileEditListAdapter adapter;

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
            ValueEventListener postListener = new ValueEventListener() {
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

    public static void notifyAdapter() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
