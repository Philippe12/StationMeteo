package com.philippefouquet.stationmeteo;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    final static int MAX_SCREEN_TIME = 15;
    final static String TAG="Meteo";
    private int mCpt = 0;

    private TimerTask mTimerTask = new TimerTask(){
        @Override
        public void run(){
            if( mCpt < (MAX_SCREEN_TIME-1)) {
                mCpt++;
            }else if( mCpt < MAX_SCREEN_TIME ) {
                mCpt++;
                setScreenState(0);
                Log.i(TAG, "Screen off");
            }
        }
    };

    private Timer mTimer = new Timer("TimerScreen", true);

    private void setScreenState(int state){
        File f;
        FileOutputStream out;
        try {
            f = new File("/dev/esaio/alim_screen/value");
            out = new FileOutputStream(f);
            out.write(String.valueOf(state).getBytes());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //run service
        Intent intent = new Intent(this, comi2c.class);
        startService(intent);

        mTimer.scheduleAtFixedRate(mTimerTask, 0, 1000);

        //display default view
        openFrame(R.id.nav_camera);

        //pwer on
        //PowerManager pm = (PowerManager)getSystemService(Activity.POWER_SERVICE);
        //PowerManager.WakeLock w1 = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
        //w1.acquire();

        //unlock screen
        //KeyguardManager kgManager = (KeyguardManager)getSystemService(Activity.KEYGUARD_SERVICE);
        //KeyguardManager.KeyguardLock kLock = kgManager.newKeyguardLock(KEYGUARD_SERVICE);
        //kLock.disableKeyguard();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if(mCpt > (MAX_SCREEN_TIME-1) ){
            setScreenState(1);
        }
        mCpt = 0;
        return super.dispatchTouchEvent(event);
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return openFrame(item.getItemId());
    }

    private boolean openFrame(int id){
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        // Handle navigation view item clicks here.

        if (id == R.id.nav_camera) {
            fragment = new ResumeFragment();
            title  = "News";
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
