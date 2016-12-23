package com.example.rached.memory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rached on 18/12/16.
 */

public class MainDB extends SQLiteOpenHelper {
    private static int VERSION = 10;
    private static MainDB instance;
    private static String MemoryDB = "MemoryDB";
    private String collections_table = "create table collections_table( " +
            " name varchar(30) not null," +
            " time datetime DEFAULT CURRENT_TIMESTAMP," +
            " _id integer primary key "  +
            ");";
    private String cards_table = "create table cards_table (" +
            " question varchar(100) not null,"                   +
            " answer varchar(100) not null,"                   +
            " collection_id int references collections_table ,"          +
            " _id integer primary key "        +
            ");";
    private String trivial_cards_table = "create table trivial_cards_table (" +
            " last_time datetime DEFAULT CURRENT_TIMESTAMP,"                   +
            " card_id int references collections_table ,"          +
            " _id integer primary key "        +
            ");";
    private String easy_cards_table = "create table easy_cards_table (" +
            " last_time datetime DEFAULT CURRENT_TIMESTAMP,"                   +
            " card_id int references collections_table ,"          +
            " _id integer primary key "        +
            ");";
    private String medium_cards_table = "create table medium_cards_table (" +
            " last_time datetime DEFAULT CURRENT_TIMESTAMP,"                   +
            " card_id int references collections_table ,"          +
            " _id integer primary key "        +
            ");";
    private String hard_cards_table = "create table hard_cards_table (" +
            " last_time datetime DEFAULT CURRENT_TIMESTAMP,"                   +
            " card_id int references collections_table ,"          +
            " _id integer primary key "        +
            ");";
    private String just_added_cards_table = "create table just_added_cards_table (" +
            " card_id int references collections_table ,"          +
            " _id integer primary key "        +
            ");";

    public static MainDB getInstance(Context context){
        if( instance == null ){
            instance = new MainDB(context);
        }
        return instance;
    }

    private MainDB(Context context) {
        super(context, MemoryDB, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(collections_table);
        db.execSQL(cards_table);
        db.execSQL(trivial_cards_table);
        db.execSQL(easy_cards_table);
        db.execSQL(medium_cards_table);
        db.execSQL(hard_cards_table);
        db.execSQL(just_added_cards_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop table if exists collections_table ;");
            db.execSQL("drop table if exists cards_table ;");
            db.execSQL("drop table if exists trivial_cards_table ;");
            db.execSQL("drop table if exists easy_cards_table ;");
            db.execSQL("drop table if exists medium_cards_table ;");
            db.execSQL("drop table if exists hard_cards_table ;");
            db.execSQL("drop table if exists just_added_cards_table ;");
            onCreate(db);
        }
    }
}
