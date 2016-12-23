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
    private static final int TRIVIAL_CARDS = 4;
    private static final int EASY_CARDS = 5;
    private static final int MEDIUM_CARDS = 6;
    private static final int HARD_CARDS = 7;
    private static final int JUST_ADDED_CARDS = 8;
    private static final int ONE_TRIVIAL_CARDS = 9;
    private static final int ONE_EASY_CARDS = 10;
    private static final int ONE_MEDIUM_CARDS = 11;
    private static final int ONE_HARD_CARDS = 12;
    private static final int ONE_JUST_ADDED_CARDS = 13;

    //private static final String[] path = new String[]{"author_table", "book_table"};

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(authority, "collections_table", COLLECTIONS);
        matcher.addURI(authority, "cards_table", CARDS);
        matcher.addURI(authority, "cards/*", ONE_CARD);
        matcher.addURI(authority, "just_added_cards_table", JUST_ADDED_CARDS);
        matcher.addURI(authority, "trivial_cards_table", TRIVIAL_CARDS);
        matcher.addURI(authority, "easy_cards_table", EASY_CARDS);
        matcher.addURI(authority, "medium_cards_table", MEDIUM_CARDS);
        matcher.addURI(authority, "hard_cards_table", HARD_CARDS);
        matcher.addURI(authority, "trivial_cards_table/*", ONE_TRIVIAL_CARDS);
        matcher.addURI(authority, "easy_cards_table/*", ONE_EASY_CARDS);
        matcher.addURI(authority, "medium_cards_table/*", ONE_MEDIUM_CARDS);
        matcher.addURI(authority, "hard_cards_table/*", ONE_HARD_CARDS);
        matcher.addURI(authority, "just_added_cards_table/*", ONE_JUST_ADDED_CARDS);
    }


    public MemoryContentProvider() {
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int code = matcher.match(uri);
        System.out.println(uri.getPath());
        int i;
        switch (code) {
            case CARDS:
                i=db.delete("cards_table", where, whereArgs);
                break;
            case ONE_CARD:
                i=db.delete("cards_table", where, whereArgs);
                break;
            case HARD_CARDS:
                i=db.delete("hard_cards_table", where, whereArgs);
                break;
            case ONE_HARD_CARDS:
                i=db.delete("hard_cards_table", where, whereArgs);
                break;
            case EASY_CARDS:
                i=db.delete("easy_cards_table", where, whereArgs);
                break;
            case ONE_EASY_CARDS:
                i=db.delete("easy_cards_table", where, whereArgs);
                break;
            case MEDIUM_CARDS:
                i=db.delete("medium_cards_table", where, whereArgs);
                break;
            case ONE_MEDIUM_CARDS:
                i=db.delete("medium_cards_table", where, whereArgs);
                break;
            case TRIVIAL_CARDS:
                i=db.delete("trivial_cards_table", where, whereArgs);
                break;
            case ONE_TRIVIAL_CARDS:
                i=db.delete("trivial_cards_table", where, whereArgs);
                break;
            case JUST_ADDED_CARDS:
                i=db.delete("just_added_cards_table", where, whereArgs);
                break;
            case ONE_JUST_ADDED_CARDS:
                i=db.delete("just_added_cards_table", where, whereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented "+ code);
        }
        getContext().getContentResolver().notifyChange(uri, null);
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
            case TRIVIAL_CARDS:
                id = db.insert("trivial_cards_table", null, values);
                builder.appendPath("trivial_cards_table");
                break;
            case EASY_CARDS:
                id = db.insert("easy_cards_table", null, values);
                builder.appendPath("easy_cards_table");
                break;
            case MEDIUM_CARDS:
                id = db.insert("medium_cards_table", null, values);
                builder.appendPath("medium_cards_table");
                break;
            case HARD_CARDS:
                id = db.insert("hard_cards_table", null, values);
                builder.appendPath("hard_cards_table");
                break;
            case JUST_ADDED_CARDS:
                id = db.insert("just_added_cards_table", null, values);
                builder.appendPath("just_added_cards_table");
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
                cursor = db.query("collections_table", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case CARDS:
                cursor = db.query("cards_table", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case HARD_CARDS:
                cursor = db.query("hard_cards_table", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case MEDIUM_CARDS:
                cursor = db.query("medium_cards_table", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case EASY_CARDS:
                cursor = db.query("easy_cards_table", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case TRIVIAL_CARDS:
                cursor = db.query("trivial_cards_table", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case JUST_ADDED_CARDS:
                cursor = db.query("just_added_cards_table", projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                Log.d("Uri provider =", uri.toString());
                throw new UnsupportedOperationException("Not yet implemented");
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int code = matcher.match(uri);
        int i;
        switch (code) {
            case COLLECTIONS:
                i=db.update("collections_table", values, selection, selectionArgs);
                break;
            case CARDS:
                i=db.update("cards_table", values, selection, selectionArgs);
                break;
            case ONE_CARD:
                i=db.update("cards_table", values, selection, selectionArgs);
                break;
            case HARD_CARDS:
                i=db.update("hard_cards_table", values, selection, selectionArgs);
                break;
            case ONE_HARD_CARDS:
                i=db.update("hard_cards_table", values, selection, selectionArgs);
                break;
            case EASY_CARDS:
                i=db.update("easy_cards_table", values, selection, selectionArgs);
                break;
            case ONE_EASY_CARDS:
                i=db.update("easy_cards_table", values, selection, selectionArgs);
                break;
            case MEDIUM_CARDS:
                i=db.update("medium_cards_table", values, selection, selectionArgs);
                break;
            case ONE_MEDIUM_CARDS:
                i=db.update("medium_cards_table", values, selection, selectionArgs);
                break;
            case TRIVIAL_CARDS:
                i=db.update("trivial_cards_table", values, selection, selectionArgs);
                break;
            case ONE_TRIVIAL_CARDS:
                i=db.update("trivial_cards_table", values, selection, selectionArgs);
                break;
            case JUST_ADDED_CARDS:
                i=db.update("just_added_cards_table", values, selection, selectionArgs);
                break;
            case ONE_JUST_ADDED_CARDS:
                i=db.update("just_added_cards_table", values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Not yet implemented "+ code);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return i;
    }
}