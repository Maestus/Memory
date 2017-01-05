package com.example.rached.memory;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;


public class AddCard extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{
    private static String authority = "com.example.rached.memorycontentprovider";
    Spinner spinner;
    SimpleCursorAdapter adapter;
    EditText question,answer,collection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        spinner = (Spinner) findViewById(R.id.collections);
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item, null,
                new String[]{"name"},
                new int[]{android.R.id.text1}, 0);
        spinner.setAdapter(adapter);

        LoaderManager manager = getLoaderManager();
        manager.initLoader(0, null, this);

        question = (EditText)findViewById(R.id.question);
        answer = (EditText)findViewById(R.id.answer);

        collection = (EditText)findViewById(R.id.collection);

    }

    public void ajouterCollection(View view){
        Editable editcollec = collection.getText();
        String collection = editcollec.toString();
        if(collection.contentEquals("")){
            Toast.makeText(this,"Answer empty",Toast.LENGTH_SHORT).show();
            return;
        }

        ContentResolver resolver = getContentResolver();

        ContentValues values = new ContentValues();
        values.put("name",collection);
        values.put("time",System.currentTimeMillis()/1000);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("collections_table");
        Uri uri = builder.build();
        resolver.insert(uri,values);
        editcollec.clear();
    }

    public void ajouter(View view){
        Editable editquestion = question.getText();
        String question = editquestion.toString();
        Editable editanswer = answer.getText();
        String answer = editanswer.toString();
        if(question.contentEquals("")){
            Toast.makeText(this,"Question empty",Toast.LENGTH_SHORT).show();
            return;
        }if(answer.contentEquals("")){
            Toast.makeText(this,"Answer empty",Toast.LENGTH_SHORT).show();
            return;
        }

        long id = spinner.getSelectedItemId();

        ContentResolver resolver = getContentResolver();

        ContentValues values = new ContentValues();
        values.put("question",question);
        values.put("answer",answer);
        values.put("collection_id",id);

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("cards_table");
        Uri uri = builder.build();
        uri = resolver.insert(uri,values);
        editanswer.clear();
        editquestion.clear();

        long id_card = ContentUris.parseId(uri);

        values = new ContentValues();
        values.put("card_id", id_card);

        builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("just_added_cards_table");
        uri = builder.build();
        resolver.insert(uri, values);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri;
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath("collections_table")
                .build();
        return new CursorLoader(this, uri, new String[]{"_id", "name"},
                null, null, null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        Button ajouter = (Button) findViewById(R.id.ajouter);
        ajouter.setActivated(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
