package com.example.rached.memory;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,CollectionsFragmentContent.OnFragmentInteractionListener{
    Uri uri = Uri.parse("https://drive.google.com/uc?export=download&id=0B3Q4cpPks70WbEpOLUxjMnFPTVU");
    private DownloadManager mgr=null;
    private long lastDownload=-1L;
    private String mAuthority;
    private String mTagAuteurs = "collections";
    private CollectionsFragmentContent mAuteursFragment;
    private static final String LOG = "CollectionsBD";
    private static final String SAVE_ID = "saveId";
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 101;
    String collection_max_open;
    boolean notification;
    FragmentTransaction transaction;
    boolean transfer_over;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mgr = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if(savedInstanceState == null){
            mAuthority = getResources().getString(R.string.authority);
            transfer_over = false;
            notification = true;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        collection_max_open = prefs.getString("collections_max_sleep","10");


        if(!transfer_over) {
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
                        startRequest();
                        queryStatus();
                        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                }
            }
            transfer_over = true;
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.list_collection, CollectionsFragmentContent.newInstance(mAuthority, "collections_table", "name"), mTagAuteurs).commit();
        }
        notifyContentToSee();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    File sdcard = Environment.getExternalStorageDirectory();
                    sdcard.mkdirs();
                    File source = new File(sdcard, "/Memory/source.xml");
                    if (!source.exists()) {
                        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                                .detectNetwork()
                                .penaltyDeath()
                                .build());
                        startRequest();
                        queryStatus();
                        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    } else {
                        notifyContentToSee();
                    }
                }
        }
    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(ctxt, "Finished", Toast.LENGTH_LONG).show();
            getFragmentManager().beginTransaction().add(R.id.content_main, new ParseXML()).commit();
            getFragmentManager().executePendingTransactions();
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter f= new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        f.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);

        registerReceiver(onEvent, f);
        registerReceiver(onComplete, f);

    }

    @Override
    public void onPause() {
        unregisterReceiver(onEvent);
        unregisterReceiver(onComplete);

        super.onPause();
    }


    public void startRequest(){

        Environment.getExternalStoragePublicDirectory("Memory")
                .mkdirs();

        DownloadManager.Request req = new DownloadManager.Request(uri);

        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("---")
                .setDescription("Something useful. No, really.")
                .setDestinationInExternalPublicDir("Memory","source.xml");

        lastDownload = mgr.enqueue(req);

    }

    private BroadcastReceiver onEvent=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent i) {
            if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(i.getAction())) {
                Toast.makeText(ctxt, "receive", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void queryStatus() {
        Cursor c = mgr.query(new DownloadManager.Query().setFilterById(lastDownload));

        if (c == null) {
            Toast.makeText(this, R.string.download_not_found,
                    Toast.LENGTH_LONG).show();
        } else {
            c.moveToFirst();

            Log.d(getClass().getName(),
                    "COLUMN_ID: "
                            + c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)));
            Log.d(getClass().getName(),
                    "COLUMN_BYTES_DOWNLOADED_SO_FAR: "
                            + c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
            Log.d(getClass().getName(),
                    "COLUMN_LAST_MODIFIED_TIMESTAMP: "
                            + c.getLong(c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)));
            Log.d(getClass().getName(),
                    "COLUMN_LOCAL_URI: "
                            + c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
            Log.d(getClass().getName(),
                    "COLUMN_STATUS: "
                            + c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)));
            Log.d(getClass().getName(),
                    "COLUMN_REASON: "
                            + c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON)));

            Toast.makeText(this, statusMessage(c), Toast.LENGTH_LONG)
                    .show();

            c.close();
        }
    }

    private String statusMessage(Cursor c) {
        String msg="???";

        switch (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_FAILED:
                msg=getString(R.string.download_failed);
                break;

            case DownloadManager.STATUS_PAUSED:
                msg=getString(R.string.download_paused);
                break;

            case DownloadManager.STATUS_PENDING:
                msg=getString(R.string.download_pending);
                break;

            case DownloadManager.STATUS_RUNNING:
                msg =getString(R.string.download_in_progress);
                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                msg =getString(R.string.download_complete);
                break;

            default:
                msg =getString(R.string.download_is_nowhere_in_sight);
                break;
        }

        return(msg);
    }



    public void notifyContentToSee(){
        if(notification) {
            ContentResolver resolver = getContentResolver();
            Uri.Builder builder = new Uri.Builder();
            Uri uri = builder.scheme("content")
                    .authority("com.example.rached.memorycontentprovider")
                    .appendPath("collections_table")
                    .build();
            String ask = (System.currentTimeMillis() / 1000) + " - time > " + collection_max_open;
            Cursor collections = resolver.query(uri, new String[]{"_id", "name", "time"}, ask, null, null);
            if (collections != null && collections.getCount() > 0) {
                collections.moveToFirst();
                Notification notification = new NotificationCompat.Builder(this)
                        .setContentTitle("Memory")
                        .setContentText("La collection " + collections.getString(collections.getColumnIndex("name")) + " commence à prendre la poussière")
                        .setSmallIcon(R.drawable.ic_stat_name)
                        .setAutoCancel(true)
                        .build();
                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                        .notify(Integer.parseInt(collections.getString(collections.getColumnIndex("_id"))), notification);
                while (collections.moveToNext()) {
                    notification = new NotificationCompat.Builder(this)
                            .setContentTitle("Memory")
                            .setContentText("La collection " + collections.getString(collections.getColumnIndex("name")) + " commence à prendre la poussière")
                            .setSmallIcon(R.drawable.ic_stat_name)
                            .setAutoCancel(true)
                            .build();
                    ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                            .notify(Integer.parseInt(collections.getString(collections.getColumnIndex("_id"))), notification);
                }
                collections.close();
            }
            notification = false;
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tool) {
            Intent intent = new Intent(this,AddCard.class);
            startActivity(intent);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle){
        super.onRestoreInstanceState(bundle);
        notification = bundle.getBoolean("notification");
        transfer_over = bundle.getBoolean("transfer");
        getSupportFragmentManager().popBackStack("debut", FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle){
        bundle.putBoolean("notification",notification);
        bundle.putBoolean("transfer",transfer_over);
        super.onSaveInstanceState(bundle);
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
