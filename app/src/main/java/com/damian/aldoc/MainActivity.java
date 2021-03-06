package com.damian.aldoc;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.widget.*;


import com.bumptech.glide.Glide;
import com.damian.aldoc.calendar.CalendarActivity;
import com.damian.aldoc.userProfile.UserProfileView;
import com.damian.aldoc.visits.Visit;
import com.damian.aldoc.visits.VisitActivity;
import com.damian.aldoc.visits.VisitsActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends AppCompatActivity //34.AuthStateListener
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int RC_SIGN_IN = 1;

    //Firebase instance variables
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //nadchodzące wizyty
    private ListView listView;
    private ArrayAdapter<Visit> visitArrayAdapter;
    private List<Visit> visits = new ArrayList<>();

    private static boolean isAlreadyLoggedIn = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Database.Initialize(true);
        //Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        String[] details = Database.GetUserInfo();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            TextView nav_mail = (TextView) hView.findViewById(R.id.emailView);
            nav_mail.setText(details[1]);
            TextView nav_user = (TextView) hView.findViewById(R.id.nameView);
            nav_user.setText(details[0]);                                       // Zrobić tak żeby po rejestracji lub bezpośrednio po zalogowaniu czytalo. Nie po ponowym uruchomieniu.
            ImageView nav_image = (ImageView) hView.findViewById(R.id.imageView);
            Glide.with(this).load(Database.GetUserImage()).into(nav_image);
        }
        navigationView.setNavigationItemSelectedListener(this);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {


                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //signed in
                    if (!isAlreadyLoggedIn) {
                        Database.setCurrentUid(Database.GetUserUID());
                        isAlreadyLoggedIn = true;
                        Database.SetLocation(Database.getVisitsPath()).addChildEventListener(mChildEventListener);
                    }
                } else {
                    //signed out
                    List<AuthUI.IdpConfig> providers = Arrays.asList(
                            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                            // new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                    );

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setTheme(R.style.GreenTheme)
                                    .setLogo(R.drawable.logo)
                                    .setProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }

        };

        //nadchodzące wizyty
        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Visit visit = dataSnapshot.getValue(Visit.class);
                visit.setUid(dataSnapshot.getKey());

                if (checkVisit(visit)) {
                    visits.add(visit);
                    Collections.sort(visits, new Visit.DateComparatorRev());
                    visitArrayAdapter.notifyDataSetChanged();
                }
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Visit visit = dataSnapshot.getValue(Visit.class);
                String uid = dataSnapshot.getKey();
                visit.setUid(uid);

                for (int v = 0; v < visits.size(); v++) {
                    if (visits.get(v).getUid().equals(uid)) {
                        visits.remove(v);
                        if (checkVisit(visit)) {
                            visits.add(visit);
                        }
                        visitArrayAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String uid = dataSnapshot.getKey();
                for (int v = 0; v < visits.size(); v++) {
                    if (visits.get(v).getUid().equals(uid)) {
                        visits.remove(v);
                        visitArrayAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(DatabaseError databaseError) {
            }
        };

        //tworzymy visitArrayAdapter i przypisujemy go do listView zeby wyswietlac wizyty
        visitArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, visits);

        listView = (ListView) findViewById(R.id.visits_listView);
        listView.setAdapter(visitArrayAdapter);
        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                visitOnClick(visits.get(position));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == RC_SIGN_IN) {
                Database.setCurrentUid(Database.GetUserUID());
                Database.SendUserInfoToDatabase();
                isAlreadyLoggedIn = true;
            }
        }
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
            case R.id.action_settings:  //Miejsce testowe Radosława ( tego od bazy)  //Nie klikać Settings - Jak klikasz to delikatnie i na własną odpowiedzialność
                //Database.ModifyValueInDatabase("-KivIPsb0iuUBuOns6Bv","location","Breslav");
                //Database.SendUserPeselToDatabase("1111");
                //Database.DeleteVisitFromDatabase("-Kiv9bXMgN0W3SyqUksW");
                // Database.GetVisitByValueFromDatabase("time","time");
                // Database.UploadImageToDatabaseStorageUsingPath("/storage/emulated/0/DCIM/Camera/IMG_20170412_140913.jpg");
                //   Database.UploadImageToDatabaseStorageUsingUriAndUpdatePrescription(Uri.parse("file:///storage/emulated/0/DCIM/Camera/IMG_20170510_135615.jpg"),"-KkMu0rK7Wpngd2Z0H51");
                //System.out.println(Database.GetUserUID());
                //System.out.println(String.valueOf(Database.aaa));
                //Toast.makeText(getApplicationContext(), String.valueOf(Database.aaa), Toast.LENGTH_SHORT).show();
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
            Intent intent_user_profile = new Intent(this, UserProfileView.class);
            startActivity(intent_user_profile);
        } else if (id == R.id.user_calendar) {
            Intent intent_calendar = new Intent(this, CalendarActivity.class);
            startActivity(intent_calendar);
        } else if (id == R.id.user_family) {
            Intent intent_family = new Intent(this, GroupsActivity.class);
            startActivity(intent_family);

        } else if (id == R.id.user_stats) {

        } else if (id == R.id.user_share) {

        } else if (id == R.id.user_upcoming_events) {
            Intent intent_visits = new Intent(this, VisitsActivity.class);
            startActivity(intent_visits);

        } else if (id == R.id.user_alerts) {
            Intent intent_diseases = new Intent(this, Diseases0Activity.class);
            startActivity(intent_diseases);

        } else if (id == R.id.user_settings) {

        }  //wszystkie ktore dodalem zaczynaja sie na user_ zeby bylo latwo odroznic

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void visitOnClick(Visit visit) {
        Intent intent = new Intent(this, VisitActivity.class);
        intent.putExtra("visit", visit.getUid());
        startActivity(intent);
    }

    private Date getDateFromVisit(Visit visit) {
        String date[] = visit.getDate().split("-");
        Calendar cal = Calendar.getInstance();

        cal.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]));
        return cal.getTime();
    }

    private boolean checkVisit(Visit visit) {
        ArrayList<String> dates = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < 8; i++) {
            dates.add(getDateAsString(cal.getTime()));
            cal.add(Calendar.DATE, 1);
        }

        for (String date : dates) {
            if (date.equals(getDateAsString(getDateFromVisit(visit)))) {
                return true;
            }
        }
        return false;
    }

    private String getDateAsString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }
}


//TODO  2. Dodać opcję przerwania podczas rejsracji  6.Dostęp do materiałów po przynależności do grupy --- http://stackoverflow.com/questions/38246751/how-to-retrieve-data-that-matches-a-firebase-userid 7. Pomysl na baze danych (Q_Q)
//TODO v2  //zaimplementowac rodziny w bazie (grupy)