package com.damian.aldoc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity //34.AuthStateListener
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int RC_SIGN_IN = 1;

    //Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mVisitsDatabaseReference;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();
      //  mFirebaseDatabase = FirebaseDatabase.getInstance();   Do bazy - uzywane w Edit Visit Acitivity - Radosław
       // mVisitsDatabaseReference = mFirebaseDatabase.getReference().child("Visits");



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    //signed in
                    Toast.makeText(MainActivity.this, "You're now signed in. Welcome to AIDoc system.", Toast.LENGTH_SHORT).show();
                }
                else{
                    //signed out
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                    );

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(providers)
                                    .build(),
                            RC_SIGN_IN);

                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_sign_out:
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) { //TODO: dopisac kod dla poszczegolnych wyborow
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.user_profile) {
        } else if (id == R.id.user_calendar) {
            Intent intent_calendar = new Intent(this, CalendarActivity.class);
            startActivity(intent_calendar);
        } else if (id == R.id.user_family) {

        } else if (id == R.id.user_stats) {

        } else if (id == R.id.user_share) {

        } else if (id == R.id.user_upcoming_events) {
            Intent intent_visits = new Intent(this, VisitsActivity.class);
            //tu moze bedzie trzeba zrobic startActivityForResult jakby mialo cos zwrocic
            //ale na razie wyjebane, byle tylko przechodzilo do okienka
            startActivity(intent_visits);

        } else if (id == R.id.user_alerts){
            Intent intent_diseases = new Intent(this, DiseasesActivity.class);
            startActivity(intent_diseases);

        } else if (id == R.id.user_settings){

        }  //wszystkie ktore dodalem zaczynaja sie na user_ zeby bylo latwo odroznic

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause(){
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}

//TODO 1.Usunąć Login activity - już niepotrzebne 2. Dodać opcję przerwania podczas rejsracji 3. Podczepic baze danych pod program 4.Dodac lokalne przechowywanie bazy danych 5.Dodac warunki dostepu do bazy danych (po user ID) 6.Dostęp do materiałów po przynależności do grupy --- http://stackoverflow.com/questions/38246751/how-to-retrieve-data-that-matches-a-firebase-userid 7. Pomysl na baze danych (Q_Q) 8.Metody przechowywania zmiennych w bazie (podpiac wizyty itd...)
//TODO v2 Podpiąć obrazek pod ekran logowania //zaimplementowac rodziny w bazie (grupy)