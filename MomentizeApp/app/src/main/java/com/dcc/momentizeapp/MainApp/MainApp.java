package com.dcc.momentizeapp.MainApp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import com.dcc.momentizeapp.QR.QrActivity;
import com.dcc.momentizeapp.R;
import com.dcc.momentizeapp.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sdsmdg.tastytoast.TastyToast;

import layout.MemoriesFragment;

public class MainApp extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    private TextView mTextMessage;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private MemoriesFragment memoriesFragment;
    public FirebaseAuth mAuth;
    public static String myUID;
    public static DatabaseReference mDatabase;
    public static final String LinkedUser = "LinkedUser";
    public static final String BlobId = "BlobId";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
//                case R.id.navigation_home:
//
//                    return true;
//                case R.id.navigation_dashboard:
//
//                    return true;
                case R.id.navigation_notifications:
                    manager = getFragmentManager();
                    transaction = manager.beginTransaction();
                    memoriesFragment = new MemoriesFragment();
                    transaction.replace(R.id.content, memoriesFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
            }
            return false;
        }

    };


    public void onFabPressed (View view){
        Intent intent = new Intent(this, EditMemory.class);
        startActivity(intent);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        mAuth = FirebaseAuth.getInstance();
        myUID = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_2015) {
            Intent qrIntent = new Intent(this, QrActivity.class);
            startActivity(qrIntent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
