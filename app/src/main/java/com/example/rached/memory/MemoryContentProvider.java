package com.example.rached.memory;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by rached on 18/12/16.
 */

public class MemoryContentProvider extends ContentProvider {
    private static String authority = "com.example.rached.memorycontentprovider";
    private MainDB helper;
    private static final int COLLECTIONS = 1;
    private static final int CARDS = 2;
    private static final int ONE_CARD = 3;
    //private static final String[] path = new String[]{"author_table", "book_table"};

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(authority, "collections_table", COLLECTIONS);
        matcher.addURI(authority, "cards_table", CARDS);
        matcher.addURI(authority, "cards/*", ONE_CARD);
    }


    public MemoryContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int code = matcher.match(uri);
        int i;
        switch (code) {
            case ONE_CARD:
                long id = ContentUris.parseId(uri);
                i=db.delete("cards_table", "_id=" + id, null);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
        return i;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int code = matcher.match(uri);
        Log.d("Uri = ", uri.toString());
        long id = 0;
        Uri.Builder builder = new Uri.Builder();

        switch (code) {
            case COLLECTIONS:
                id = db.insert("collections_table", null, values);
                builder.appendPath("collections_table");
                break;
            case CARDS:
                id = db.insert("cards_table", null, values);
                builder.appendPath("cards_table");
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        builder.authority(authority);

        builder = ContentUris.appendId(builder, id);
        return builder.build();
    }

    @Override
    public boolean onCreate() {
        helper = MainDB.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        int code = matcher.match(uri);
        Cursor cursor;
        switch (code) {
            case COLLECTIONS:
                cursor = db.query("collections_table", new String[]{"rowid","_id","name"}, selection,
                        selectionArgs, null, null, sortOrder);
                /*
                cursor.moveToFirst();
                while(cursor.moveToNext()){
                    int i= cursor.getInt(0);
                    int j= cursor.getInt(1);
                    String s=cursor.getString(2);
                    Log.d("pepe1=",i+"");
                    Log.d("pepe2=",j+"");
                    Log.d("pepe3",s);
                }
*/
                //cursor = db.query("collections_table", projection, selection,selectionArgs, null, null, sortOrder);
                /*
                cursor.moveToFirst();
                while(cursor.moveToNext()){
                    int i= cursor.getInt(0);
                    String s=cursor.getString(1);
                    Log.d("pepe1=",i+"");
                    Log.d("pepe2",s);
                }
*/

                break;
            case CARDS:
                cursor = db.query("cards_table", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                Log.d("Uri provider =", uri.toString());
                throw new UnsupportedOperationException("Not yet implemented");
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}