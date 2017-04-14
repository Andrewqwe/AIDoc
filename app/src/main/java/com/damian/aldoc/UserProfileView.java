package com.damian.aldoc;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class UserProfileView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user_profile_view);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            this.setUserName("Jan Kowalski"); //TODO: wczytywanie danych
        }catch (Exception e){ e.printStackTrace();}
        ListView lista = (ListView) findViewById(R.id.user_data_list);
        lista.setSelector(android.R.color.transparent);
    }

    public void setUserName(String userName)
    {
        TextView userNameView = (TextView) findViewById(R.id.profile_full_name);
        userNameView.setText(userName);
    }

    public void setPesel(String pesel)
    {

    }

}
