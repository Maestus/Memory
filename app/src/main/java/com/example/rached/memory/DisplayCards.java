package com.example.rached.memory;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rached on 19/12/16.
 */

public class DisplayCards extends Activity {
    private static String authority = "com.example.rached.memorycontentprovider";
    private String hard,medium,easy,trivial;
    private String column_question,column_answer;
    private  Cursor hard_cursor,medium_cursor,easy_cursor,trivial_cursor,just_added_cursor;
    List<String> cards_id = new ArrayList<String>();
    Cursor card;
    String card_id,question,answer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_display);

        hard = "hard_cards_table";
        medium = "medium_cards_table";
        easy = "easy_cards_table";
        trivial = "trivial_cards_table";
        column_question = "question";
        column_answer = "answer";

        createCursorJustAdded();

        createCursorHard();
        createCursorMedium();
        createCursorEasy();
        createCursorTrivial();

        ListedAllCardsGet();

        System.out.println("Diplay : "+cards_id.size());

        while(cards_id.size() > 0)
            play();
    }


    public void answer(){
        TextView t2 = (TextView) findViewById(R.id.answer);
        t2.setVisibility(View.VISIBLE);
    }

    public void play() throws NullPointerException{
        Random r = new Random();
        int i = r.nextInt(cards_id.size());
        card_id = cards_id.get(i);
        Uri uri;
        ContentResolver resolver = getContentResolver();
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath("cards_table")
                .build();
        card = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "_id = '"+card_id+"'", null, null);
        card.moveToFirst();
        question = card.getString(card.getColumnIndex(column_question));
        answer = card.getString(card.getColumnIndex(column_answer));
        TextView t1 = (TextView) findViewById(R.id.question);
        TextView t2 = (TextView) findViewById(R.id.answer);
        t1.append(question);
        t2.append(answer);
    }

    public void ListedAllCardsGet() throws NullPointerException{
        Uri uri;
        Intent intent = getIntent();
        ContentResolver resolver = getContentResolver();
        Cursor just,hard,medium,easy,trivial;
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath("cards_table")
                .build();
        if(just_added_cursor.getCount() > 0) {
            just_added_cursor.moveToFirst();
            just = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='"+intent.getLongExtra("key",1L)+"' and '"+just_added_cursor.getString(just_added_cursor.getColumnIndex("card_id"))+"' = _id", null, null);
            if(just.getCount() > 0) {
                just.moveToFirst();
                cards_id.add(just.getString(just.getColumnIndex("_id")));
            }
            while (just_added_cursor.moveToNext()) {
                just = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='"+intent.getLongExtra("key",1L)+"' and '"+just_added_cursor.getString(just_added_cursor.getColumnIndex("card_id"))+"' = _id", null, null);
                if(just.getCount() > 0) {
                    just.moveToFirst();
                    cards_id.add(just.getString(just.getColumnIndex("_id")));
                }
            }
        }if(hard_cursor.getCount() > 0) {
            hard_cursor.moveToFirst();
            hard = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='"+intent.getLongExtra("key",1L)+"' and '"+hard_cursor.getString(hard_cursor.getColumnIndex("card_id"))+"' = _id", null, null);
            if(hard.getCount() > 0) {
                hard.moveToFirst();
                cards_id.add(hard.getString(hard.getColumnIndex("_id")));
            }
            while (hard_cursor.moveToNext()) {
                hard = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='"+intent.getLongExtra("key",1L)+"' and '"+hard_cursor.getString(hard_cursor.getColumnIndex("card_id"))+"' = _id", null, null);
                if(hard.getCount() > 0) {
                    hard.moveToFirst();
                    cards_id.add(hard.getString(hard.getColumnIndex("_id")));
                }
            }
        }if(medium_cursor.getCount() > 0) {
            medium_cursor.moveToFirst();
            medium = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='"+intent.getLongExtra("key",1L)+"' and '"+medium_cursor.getString(medium_cursor.getColumnIndex("card_id"))+"' = _id", null, null);
            if(medium.getCount() > 0) {
                medium.moveToFirst();
                cards_id.add(medium.getString(medium.getColumnIndex("_id")));
            }
            while (medium_cursor.moveToNext()) {
                medium = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='"+intent.getLongExtra("key",1L)+"' and '"+medium_cursor.getString(medium_cursor.getColumnIndex("card_id"))+"' = _id", null, null);
                if(medium.getCount() > 0) {
                    medium.moveToFirst();
                    cards_id.add(medium.getString(medium.getColumnIndex("_id")));
                }
            }
        }if(easy_cursor.getCount() > 0) {
            easy_cursor.moveToFirst();
            easy = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='"+intent.getLongExtra("key",1L)+"' and '"+easy_cursor.getString(easy_cursor.getColumnIndex("card_id"))+"' = _id", null, null);
            if(easy.getCount() > 0) {
                easy.moveToFirst();
                cards_id.add(easy.getString(easy.getColumnIndex("_id")));
            }
            while (easy_cursor.moveToNext()) {
                easy = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='"+intent.getLongExtra("key",1L)+"' and '"+easy_cursor.getString(easy_cursor.getColumnIndex("card_id"))+"' = _id", null, null);
                if(easy.getCount() > 0) {
                    easy.moveToFirst();
                    cards_id.add(easy.getString(easy.getColumnIndex("_id")));
                }
            }
        }if(trivial_cursor.getCount() > 0) {
            trivial_cursor.moveToFirst();
            trivial = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='"+intent.getLongExtra("key",1L)+"' and '"+trivial_cursor.getString(trivial_cursor.getColumnIndex("card_id"))+"' = _id", null, null);
            if(trivial.getCount() > 0) {
                trivial.moveToFirst();
                cards_id.add(trivial.getString(trivial.getColumnIndex("_id")));
            }
            while (trivial_cursor.moveToNext()) {
                trivial = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='"+intent.getLongExtra("key",1L)+"' and '"+trivial_cursor.getString(trivial_cursor.getColumnIndex("card_id"))+"' = _id", null, null);
                if(trivial.getCount() > 0) {
                    trivial.moveToFirst();
                    cards_id.add(trivial.getString(trivial.getColumnIndex("_id")));
                }
            }
        }
    }


    public void createCursorJustAdded() {
        Uri uri;
        ContentResolver resolver = getContentResolver();
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath("just_added_cards_table")
                .build();
        just_added_cursor = resolver.query(uri, new String[]{"_id", "card_id"}, null, null, null);
    }


    public void createCursorHard() {
        Uri uri;
        ContentResolver resolver = getContentResolver();
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath(hard)
                .build();
        hard_cursor = resolver.query(uri, new String[]{"_id", "card_id"}, "strftime('%s','now') - strftime('%s',last_time) >= 3500", null, null);
    }

    public void createCursorMedium() {
        Uri uri;
        ContentResolver resolver = getContentResolver();
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath(medium)
                .build();
        medium_cursor = resolver.query(uri, new String[]{"_id", "card_id"}, "strftime('%s','now') - strftime('%s',last_time) >= 10500", null, null);
    }

    public void createCursorEasy() {
        Uri uri;
        ContentResolver resolver = getContentResolver();
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath(easy)
                .build();
        easy_cursor = resolver.query(uri, new String[]{"_id", "card_id"}, "strftime('%s','now') - strftime('%s',last_time) >= 13500", null, null);
    }

    public void createCursorTrivial() {
        Uri uri;
        ContentResolver resolver = getContentResolver();
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath(trivial)
                .build();
        trivial_cursor = resolver.query(uri, new String[]{"_id", "card_id"}, "strftime('%s','now') - strftime('%s',last_time) >= 23500", null, null);
    }

}
