package com.example.rached.memory;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;


/**
 * Created by rached on 18/12/16.
 */

public class MainContentActivity extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static String authority = "com.example.rached.memorycontentprovider";
    Spinner spinner;
    SimpleCursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_panel_main, parent, false);

        spinner = (Spinner) result.findViewById(R.id.spinner);
        adapter = new SimpleCursorAdapter(this.getActivity(),
                android.R.layout.simple_spinner_item, null,
                new String[]{"name"},
                new int[]{android.R.id.text1}, 0);
        spinner.setAdapter(adapter);

        LoaderManager manager = getLoaderManager();
        manager.initLoader(0, null, this);
        return result;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath("collections_table")
                .build();
        return new CursorLoader(this.getActivity(), uri, new String[]{"_id", "name"},
                null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
