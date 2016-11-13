package com.ats_qatar.smscampaign.activities;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ats_qatar.smscampaign.R;
import com.ats_qatar.smscampaign.fragments.FragmentAbout;
import com.ats_qatar.smscampaign.fragments.FragmentDashboard;
import com.ats_qatar.smscampaign.fragments.FragmentSchedule;
import com.ats_qatar.smscampaign.fragments.FragmentSetting;
import com.ats_qatar.smscampaign.models.Scope;
import com.ats_qatar.smscampaign.receivers.SmsDeliveredReceiver;
import com.ats_qatar.smscampaign.receivers.SmsSentReceiver;
import com.ats_qatar.smscampaign.services.SmsDetail;
import com.ats_qatar.smscampaign.services.SmsDispatcher;


public class ActivityMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected PowerManager.WakeLock wakeLock;

    SmsSentReceiver smsSentReceiver = new SmsSentReceiver();
    SmsDeliveredReceiver smsDeliveredReceiver = new SmsDeliveredReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, new FragmentDashboard())
                .commit();

        int requestCode = PackageManager.PERMISSION_GRANTED;
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE

        }, requestCode);

        registerReceiver(smsSentReceiver, new IntentFilter("SENT"));
        registerReceiver(smsDeliveredReceiver, new IntentFilter("DELIVERED"));


        Scope.smsDetail = new SmsDetail();
        Scope.smsDispatcher = new SmsDispatcher();

        final PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Tag");
        this.wakeLock.acquire();


//        Resource resource = Resource.get(this);

//        if (Scope.isExpired(resource.dtExpire)) {
//            Toast.makeText(this,"App is Expired...", Toast.LENGTH_LONG).show();
//            this.finish();
//        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(smsSentReceiver);
        unregisterReceiver(smsDeliveredReceiver);
        this.wakeLock.release();
        super.onDestroy();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        FragmentManager fragmentManager = getFragmentManager();

        if (id == R.id.nav_dashboard) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new FragmentDashboard())
                    .commit();
        } else if (id == R.id.nav_setting) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new FragmentSetting())
                    .commit();
        } else if (id == R.id.nav_schedule) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new FragmentSchedule())
                    .commit();
        } else if (id == R.id.nav_about) {
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, new FragmentAbout())
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Setting
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Back Press
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
