package com.example.rached.memory;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,CollectionsFragmentContent.OnFragmentInteractionListener{
    private String mAuthority;
    private String mTagAuteurs = "collections", mTagLivres = "cards";
    private CollectionsFragmentContent mAuteursFragment;
    private long mId = -1;
    private static final String LOG = "CollectionsBD";
    private static final String SAVE_ID = "saveId";
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 101;
    String collection_max_open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        collection_max_open = prefs.getString("collections_max_sleep","10");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
            } else {
                File sdcard = Environment.getExternalStorageDirectory();
                sdcard.mkdirs();
                File source = new File(sdcard, "/Memory/source.xml");
                if (!source.exists()) {
                    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                            .detectNetwork()
                            .penaltyDeath()
                            .build());
                    getFragmentManager().beginTransaction().add(R.id.content_main, new DownloadActivity()).commit();
                }else{
                    notifyContentToSee();
                }
            }
        }
        mAuthority = getResources().getString(R.string.authority);
        if (savedInstanceState != null) {
            mId = savedInstanceState.getLong(SAVE_ID, -1L);
        }
        /* si l'activite recreee alors enlever les anciens fragments et refaire tout */
        if (savedInstanceState != null) {
            FragmentManager fm = getSupportFragmentManager();
            getSupportFragmentManager()
                    .popBackStack("debut", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();

        mAuteursFragment = CollectionsFragmentContent.newInstance(mAuthority,
                "collections_table", "name");

        transaction.add(R.id.list_collection, mAuteursFragment, mTagAuteurs);

        transaction.commit();

        if (mId >= 0) {
            Log.d(LOG, "mId=" + mId + " call onIdSelection");
            onIdSelection(mId);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    File sdcard = Environment.getExternalStorageDirectory();
                    sdcard.mkdirs();
                    File source = new File(sdcard, "/Memory/source.xml");
                    if (!source.exists()) {
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                .detectNetwork()
                                .penaltyDeath()
                                .build());

                        getFragmentManager().beginTransaction().add(R.id.content_main, new DownloadActivity()).commit();
                    } else {
                        notifyContentToSee();
                    }
                }
        }
    }


    public void notifyContentToSee(){
        ContentResolver resolver = getContentResolver();
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content")
                .authority("com.example.rached.memorycontentprovider")
                .appendPath("collections_table")
                .build();
        String ask = (System.currentTimeMillis()/1000)+ " - time > " + collection_max_open;
        Cursor collections = resolver.query(uri,new String[]{"_id","name","time"},ask,null,null);//"strftime('%s','now') - strftime('%s',time) >?",arg,null);
        System.out.println(collections.getCount());
        if(collections != null && collections.getCount() > 0){
            collections.moveToFirst();
            System.out.println(collections.getString(collections.getColumnIndex("time")));
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Memory")
                    .setContentText("La collection "+ collections.getString(collections.getColumnIndex("name")) +" commence à prendre la poussière")
                    .setSmallIcon(R.drawable.small)
                    .setAutoCancel(true)
                    .build();
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .notify(Integer.parseInt(collections.getString(collections.getColumnIndex("_id"))), notification);
            while (collections.moveToNext()){
                if(collections != null) {
                    System.out.println(collections.getString(collections.getColumnIndex("time")));
                    notification = new NotificationCompat.Builder(this)
                            .setContentTitle("Memory")
                            .setContentText("La collection " + collections.getString(collections.getColumnIndex("name")) + " commence à prendre la poussière")
                            .setSmallIcon(R.drawable.small)
                            .setAutoCancel(true)
                            .build();
                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                            .notify(Integer.parseInt(collections.getString(collections.getColumnIndex("_id"))), notification);
                }
            }
            collections.close();
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tool) {
            Intent intent = new Intent(this,AddCard.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onIdSelection(long id) {
        ContentResolver resolver = getContentResolver();
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content")
                .authority("com.example.rached.memorycontentprovider")
                .appendPath("collections_table")
                .build();

        ContentValues values = new ContentValues();
        values.put("time", System.currentTimeMillis()/1000);

        String [] arg = new String[]{""+id};
        resolver.update(uri,values,"_id=?",arg);

        Intent intent = new Intent(this, DisplayCards.class);
        Bundle b = new Bundle();
        b.putLong("key", id); //Your id
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
    }

}
