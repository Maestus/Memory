package com.example.rached.memory;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by rached on 19/12/16.
 */

public class DisplayCards extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static String authority = "com.example.rached.memorycontentprovider";
    private CollectionsFragmentContent.OnFragmentInteractionListener mListener;
    private SimpleCursorAdapter adapter;
    private String mAuthority, mTable, mColumn;
    private static final String LOG = "DisplayCards";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_display);

        mAuthority = authority;
        mTable = "cards_table";
        mColumn = "_id";

        adapter = new SimpleCursorAdapter(
                this,/*context*/
                android.R.layout.simple_list_item_1,
                null, /*Cursor - null initialement */
                new String[]{mColumn},
                new int[]{android.R.id.text1}, 0);

        final ListView view = (ListView) findViewById(R.id.cards_display);
        view.setAdapter(adapter);

        LoaderManager manager = getLoaderManager();
        manager.initLoader(0, null, this);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.d(LOG, "clicked dans la liste : ");
        if(mListener == null){
            Log.d(LOG,"onListItemClick mListener=null");
        }
        mListener.onIdSelection(id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath("author_table")
                .build();
        return new CursorLoader(this, uri, new String[]{"_id", "nom"},
                null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        while (data.moveToNext()) {
            int i = data.getInt(0);
            String s = data.getString(1);
            Log.d("tata1=", i + "");
            Log.d("tatal2", s);
        }

        adapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

}
