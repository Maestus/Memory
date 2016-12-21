package com.example.rached.memory;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DisplayCards extends Activity {
    private static String authority = "com.example.rached.memorycontentprovider";
    ContentResolver resolver;
    private String hard,medium,easy,trivial;
    private String column_question,column_answer;
    private  Cursor card,hard_cursor,medium_cursor,easy_cursor,trivial_cursor,just_added_cursor;
    List<String> cards_id = new ArrayList<>();
    String card_id,question,answer;
    static final String STATE_LEVEL = "Card_id";
    static final String STATE_ANSWER = "pressed_give_answer";
    int myCurrentCard;
    boolean pressed_give_answer = false;

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


        resolver = getContentResolver();

        createCursorJustAdded();

        createCursorHard();
        createCursorMedium();
        createCursorEasy();
        createCursorTrivial();

        ListedAllCardsGet();

        //System.out.println("Display : "+cards_id.size());

        if(savedInstanceState == null){
            Random r = new Random();
            myCurrentCard = r.nextInt(cards_id.size());
        }else {
            myCurrentCard = savedInstanceState.getInt(STATE_LEVEL);
            pressed_give_answer = savedInstanceState.getBoolean(STATE_ANSWER);
            if(pressed_give_answer){
                answer_display();
            }
        }

        play();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putInt(STATE_LEVEL, myCurrentCard);
        savedInstanceState.putBoolean(STATE_ANSWER, pressed_give_answer);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    public void answer(View v){
        pressed_give_answer = true;
        answer_display();
    }

    public void answer_display(){
        View separator = findViewById(R.id.separator);
        separator.setVisibility(View.VISIBLE);
        TextView t2 = (TextView) findViewById(R.id.answer);
        t2.setVisibility(View.VISIBLE);
        Button response = (Button) findViewById(R.id.give_answer);
        response.setEnabled(false);

        Button trivial = (Button) findViewById(R.id.trivial);
        trivial.setVisibility(View.VISIBLE);

        Button easy = (Button) findViewById(R.id.easy);
        easy.setVisibility(View.VISIBLE);

        Button medium = (Button) findViewById(R.id.medium);
        medium.setVisibility(View.VISIBLE);

        Button hard = (Button) findViewById(R.id.hard);
        hard.setVisibility(View.VISIBLE);
    }

    public void play(){
        card_id = cards_id.get(myCurrentCard);
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content")
                .authority(authority)
                .appendPath("cards_table")
                .build();
        card = resolver.query(uri, new String[]{"_id", column_question, column_answer},"'"+card_id+"' = _id", null, null);
        if(card != null) {
            card.moveToFirst();
            question = card.getString(card.getColumnIndex("question"));
            answer = card.getString(card.getColumnIndex("answer"));
            TextView t1 = (TextView) findViewById(R.id.question);
            TextView t2 = (TextView) findViewById(R.id.answer);
            t1.append(question);
            t2.append(answer);
        }
    }

    public void ListedAllCardsGet(){
        Uri uri;
        Intent intent = getIntent();
        Cursor just,hard,medium,easy,trivial;
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath("cards_table")
                .build();
        try {
            if (just_added_cursor.getCount() > 0) {
                just_added_cursor.moveToFirst();
                just = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + just_added_cursor.getString(just_added_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                if (just != null && just.getCount() > 0) {
                    just.moveToFirst();
                    cards_id.add(just.getString(just.getColumnIndex("_id")));
                    just.close();
                }
                while (just_added_cursor.moveToNext()) {
                    just = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + just_added_cursor.getString(just_added_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                    if (just != null && just.getCount() > 0) {
                        just.moveToFirst();
                        cards_id.add(just.getString(just.getColumnIndex("_id")));
                        just.close();
                    }
                }
                just_added_cursor.close();
            }
            if (hard_cursor.getCount() > 0) {
                hard_cursor.moveToFirst();
                hard = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + hard_cursor.getString(hard_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                if (hard != null && hard.getCount() > 0) {
                    hard.moveToFirst();
                    cards_id.add(hard.getString(hard.getColumnIndex("_id")));
                    hard.close();
                }
                while (hard_cursor.moveToNext()) {
                    hard = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + hard_cursor.getString(hard_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                    if (hard != null && hard.getCount() > 0) {
                        hard.moveToFirst();
                        cards_id.add(hard.getString(hard.getColumnIndex("_id")));
                        hard.close();
                    }
                }
                hard_cursor.close();
            }
            if (medium_cursor.getCount() > 0) {
                medium_cursor.moveToFirst();
                medium = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + medium_cursor.getString(medium_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                if (medium != null && medium.getCount() > 0) {
                    medium.moveToFirst();
                    cards_id.add(medium.getString(medium.getColumnIndex("_id")));
                    medium.close();
                }
                while (medium_cursor.moveToNext()) {
                    medium = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + medium_cursor.getString(medium_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                    if (medium != null && medium.getCount() > 0) {
                        medium.moveToFirst();
                        cards_id.add(medium.getString(medium.getColumnIndex("_id")));
                        medium.close();
                    }
                }
                medium_cursor.close();
            }
            if (easy_cursor.getCount() > 0) {
                easy_cursor.moveToFirst();
                easy = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + easy_cursor.getString(easy_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                if (easy != null && easy.getCount() > 0) {
                    easy.moveToFirst();
                    cards_id.add(easy.getString(easy.getColumnIndex("_id")));
                    easy.close();
                }
                while (easy_cursor.moveToNext()) {
                    easy = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + easy_cursor.getString(easy_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                    if (easy != null && easy.getCount() > 0) {
                        easy.moveToFirst();
                        cards_id.add(easy.getString(easy.getColumnIndex("_id")));
                        easy.close();
                    }
                }
                easy_cursor.close();
            }
            if (trivial_cursor.getCount() > 0) {
                trivial_cursor.moveToFirst();
                trivial = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + trivial_cursor.getString(trivial_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                if (trivial != null && trivial.getCount() > 0) {
                    trivial.moveToFirst();
                    cards_id.add(trivial.getString(trivial.getColumnIndex("_id")));
                    trivial.close();
                }
                while (trivial_cursor.moveToNext()) {
                    trivial = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + trivial_cursor.getString(trivial_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                    if (trivial != null && trivial.getCount() > 0) {
                        trivial.moveToFirst();
                        cards_id.add(trivial.getString(trivial.getColumnIndex("_id")));
                        trivial.close();
                    }
                }
                trivial_cursor.close();
            }
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }
    }


    public void createCursorJustAdded() {
        Uri uri;
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath("just_added_cards_table")
                .build();
        just_added_cursor = resolver.query(uri, new String[]{"_id", "card_id"}, null, null, null);
    }


    public void createCursorHard() {
        Uri uri;
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath(hard)
                .build();
        hard_cursor = resolver.query(uri, new String[]{"_id", "card_id"}, "strftime('%s','now') - strftime('%s',last_time) >= 3500", null, null);
    }

    public void createCursorMedium() {
        Uri uri;
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath(medium)
                .build();
        medium_cursor = resolver.query(uri, new String[]{"_id", "card_id"}, "strftime('%s','now') - strftime('%s',last_time) >= 10500", null, null);
    }

    public void createCursorEasy() {
        Uri uri;
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath(easy)
                .build();
        easy_cursor = resolver.query(uri, new String[]{"_id", "card_id"}, "strftime('%s','now') - strftime('%s',last_time) >= 13500", null, null);
    }

    public void createCursorTrivial() {
        Uri uri;
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath(trivial)
                .build();
        trivial_cursor = resolver.query(uri, new String[]{"_id", "card_id"}, "strftime('%s','now') - strftime('%s',last_time) >= 23500", null, null);
    }

}
