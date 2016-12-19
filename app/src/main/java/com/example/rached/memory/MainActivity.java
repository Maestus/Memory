package com.example.rached.memory;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,CollectionsFragmentContent.OnFragmentInteractionListener{
    private String mAuthority;
    private String mTagAuteurs = "collections", mTagLivres = "cards";
    private CollectionsFragmentContent mAuteursFragment;
    private long mId = -1;
    private static final String LOG = "CollectionsBD";
    private static final String SAVE_ID = "saveId";
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 101;

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
                        // permission denied!
                    }
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_tool) {

        } else if (id == R.id.nav_manage) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onIdSelection(long id) {
        Intent intent = new Intent(this, DisplayCards.class);
        Bundle b = new Bundle();
        b.putLong("key", id); //Your id
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
    }

}
