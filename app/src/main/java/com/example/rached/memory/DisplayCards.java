package com.example.rached.memory;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import static java.text.DateFormat.getDateTimeInstance;

public class DisplayCards extends AppCompatActivity {
    private static String authority = "com.example.rached.memorycontentprovider";
    ContentResolver resolver;
    private String hard,medium,easy,trivial,just_added;
    private String column_question,column_answer;
    private String re_ask_hard, re_ask_medium, re_ask_easy, re_ask_trivial;
    private String hard_timer, medium_timer, easy_timer, trivial_timer;
    private  Cursor card,hard_cursor,medium_cursor,easy_cursor, trivial_cursor, just_added_cursor;
    ArrayList<MyCard> cards_id = new ArrayList<>();
    String card_id,question,answer;
    static final String STATE_LEVEL = "Card_id";
    static final String STATE_ANSWER = "pressed_give_answer";
    static final String STATE_PASS = "pressed_choice";
    int myCurrentCard;
    boolean on_going;
    CharSequence time_to_display;
    boolean pressed_choice = false, pressed_give_answer = false, choosed_card = false;
    Chronometer myChronometer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cards_display);

        myChronometer = (Chronometer) findViewById(R.id.chronometer1);

        if(savedInstanceState == null) {
            time_to_display="";
            on_going = true;
        }

        if((savedInstanceState != null)) {
            myChronometer.setBase(savedInstanceState.getLong("ChronoTime"));
            on_going = savedInstanceState.getBoolean("running");
            if(!on_going){
                myChronometer.stop();
                myChronometer.setText(savedInstanceState.getString("time"));
                time_to_display = savedInstanceState.getString("time");
                myChronometer.setBackgroundColor(Color.RED);
            }else {
                myChronometer.start();
                myChronometer.setBackgroundColor(Color.WHITE);
            }
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        re_ask_easy = prefs.getString("easysleeptime","72");
        re_ask_medium = prefs.getString("mediumsleeptime","48");
        re_ask_hard = prefs.getString("hardsleeptime","24");
        re_ask_trivial = prefs.getString("trivialsleeptime","Never");

        easy_timer = prefs.getString("easy_seconds","2");
        medium_timer = prefs.getString("medium_seconds","3");
        hard_timer = prefs.getString("hard_seconds","5");
        trivial_timer = prefs.getString("trivial_seconds","1");


        hard = "hard_cards_table";
        medium = "medium_cards_table";
        easy = "easy_cards_table";
        trivial = "trivial_cards_table";
        just_added = "just_added_cards_table";
        column_question = "question";
        column_answer = "answer";


        resolver = getContentResolver();

        createCursorJustAdded();

        createCursorHard();
        createCursorMedium();
        createCursorEasy();
        createCursorTrivial();


        if(savedInstanceState == null)
            ListedAllCardsGet();
        else {
            System.out.println("In there");
            cards_id = savedInstanceState.getParcelableArrayList("savedList");
        }
        if(savedInstanceState == null){
            if(cards_id.size() > 0) {
                Random r = new Random();
                myCurrentCard = r.nextInt(cards_id.size());
                choosed_card = true;
            }
        }else {
            myCurrentCard = savedInstanceState.getInt(STATE_LEVEL);
            pressed_give_answer = savedInstanceState.getBoolean(STATE_ANSWER);
            choosed_card = true;
            if(pressed_give_answer){
                answer_display();
            }
        }

        myChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if(cards_id.size() > 0 && cards_id.get(myCurrentCard).getValue() / 10 < 1 ) {
                    if (chronometer.getText().toString().equalsIgnoreCase("00:0" + cards_id.get(myCurrentCard).getValue())) {
                        chronometer.stop();
                        time_to_display = chronometer.getText();
                        on_going = false;
                        chronometer.setBackgroundColor(Color.RED);
                        pressed_give_answer = true;
                        answer_display();
                    }
                }else if(cards_id.size() > 0 && cards_id.get(myCurrentCard).getValue() / 10 >= 1 ) {
                    if (chronometer.getText().toString().equalsIgnoreCase("00:" + cards_id.get(myCurrentCard).getValue())) {
                        chronometer.stop();
                        time_to_display = chronometer.getText();
                        on_going = false;
                        chronometer.setBackgroundColor(Color.RED);
                        pressed_give_answer = true;
                        answer_display();
                    }
                }
            }
        });
        play();
    }

    public void play(){
        if(cards_id.size() > 0) {
            if(!choosed_card) {
                Random r = new Random();
                myCurrentCard = r.nextInt(cards_id.size());
            }
            card_id = cards_id.get(myCurrentCard).getKey();
            Uri.Builder builder = new Uri.Builder();
            Uri uri = builder.scheme("content")
                    .authority(authority)
                    .appendPath("cards_table")
                    .build();
            card = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "'" + card_id + "' = _id", null, null);
            if (card != null) {
                card.moveToFirst();
                question = card.getString(card.getColumnIndex("question"));
                answer = card.getString(card.getColumnIndex("answer"));
                answer = card.getString(card.getColumnIndex("answer"));
                TextView t1 = (TextView) findViewById(R.id.question);
                TextView t2 = (TextView) findViewById(R.id.answer);
                t1.setText("");
                t1.append(question);
                t2.setText("");
                t2.append(answer);
                if (cards_id.get(myCurrentCard).getValue() == 0) {
                    myChronometer.setVisibility(View.INVISIBLE);
                } else if(on_going){
                    myChronometer.setVisibility(View.VISIBLE);
                    myChronometer.start();
                    myChronometer.setBackgroundColor(Color.WHITE);
                }
            }
        }else{
            TextView t1 = (TextView) findViewById(R.id.question);
            t1.setText("");
            t1.append("No more work to do");
            Button response = (Button) findViewById(R.id.give_answer);
            response.setVisibility(View.INVISIBLE);
            myChronometer.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList("savedList", cards_id);
        savedInstanceState.putInt(STATE_LEVEL, myCurrentCard);
        savedInstanceState.putBoolean(STATE_ANSWER, pressed_give_answer);
        savedInstanceState.putBoolean(STATE_PASS, pressed_choice);
        savedInstanceState.putLong("ChronoTime", myChronometer.getBase());
        savedInstanceState.putBoolean("running", on_going);
        savedInstanceState.putCharSequence("time", time_to_display);
        super.onSaveInstanceState(savedInstanceState);
    }


    public void removeFromOtherCollections(String requestFrom){
        String [] collections = {just_added, trivial, easy, medium, hard};
        String[] arg = new String[] { card_id };
        Uri.Builder builder;
        for(String collection : collections) {
            if(!collection.equals(requestFrom)) {
                builder = new Uri.Builder();
                Uri uri = builder.scheme("content")
                        .authority(authority)
                        .appendPath(collection)
                        .build();
                resolver.delete(uri,"card_id=?",arg);
            }
        }
    }

    public void setInvisibleAnswerAndButtons(){
        myChronometer.setBase(SystemClock.elapsedRealtime());
        View separator = findViewById(R.id.separator);
        separator.setVisibility(View.INVISIBLE);
        TextView t2 = (TextView) findViewById(R.id.answer);
        t2.setVisibility(View.INVISIBLE);
        Button response = (Button) findViewById(R.id.give_answer);
        response.setVisibility(View.VISIBLE);

        Button trivial = (Button) findViewById(R.id.trivial);
        trivial.setVisibility(View.INVISIBLE);

        Button easy = (Button) findViewById(R.id.easy);
        easy.setVisibility(View.INVISIBLE);

        Button medium = (Button) findViewById(R.id.medium);
        medium.setVisibility(View.INVISIBLE);

        Button hard = (Button) findViewById(R.id.hard);
        hard.setVisibility(View.INVISIBLE);
    }

    public void trivial(View v){
        card.moveToFirst();
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content")
                .authority(authority)
                .appendPath(trivial)
                .build();

        long id = card.getLong(card.getColumnIndex("_id"));

        Cursor tmp = resolver.query(uri,new String[]{"_id","last_time"},"card_id='"+card_id+"'",null,null);

        if(tmp != null && tmp.getCount() > 0) {
            ContentValues values = new ContentValues();
            values.put("last_time", System.currentTimeMillis()/1000);
            String [] arg = new String[]{card_id};
            resolver.update(uri,values,"card_id=?",arg);
            tmp.close();
        }else {
            ContentValues values = new ContentValues();
            values.put("last_time", System.currentTimeMillis()/1000);
            values.put("card_id", id);
            resolver.insert(uri, values);
        }

        removeFromOtherCollections(trivial);
        setInvisibleAnswerAndButtons();
        card.close();
        cards_id.remove(myCurrentCard);
        on_going = true;
        choosed_card = false;
        pressed_give_answer = false;
        play();
    }

    public void easy(View v){
        card.moveToFirst();
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content")
                .authority(authority)
                .appendPath(easy)
                .build();

        long id = card.getLong(card.getColumnIndex("_id"));

        Cursor tmp = resolver.query(uri,new String[]{"_id","last_time"},"card_id='"+card_id+"'",null,null);

        if(tmp != null && tmp.getCount() > 0) {
            ContentValues values = new ContentValues();
            values.put("last_time", System.currentTimeMillis()/1000);
            String [] arg = new String[]{card_id};
            resolver.update(uri,values,"card_id=?",arg);
            tmp.close();
        }else {
            ContentValues values = new ContentValues();
            values.put("last_time", System.currentTimeMillis()/1000);
            values.put("card_id", id);
            resolver.insert(uri, values);
        }

        removeFromOtherCollections(easy);
        setInvisibleAnswerAndButtons();
        card.close();
        cards_id.remove(myCurrentCard);
        on_going = true;
        choosed_card = false;
        pressed_give_answer = false;
        play();
    }

    public void medium(View v){
        card.moveToFirst();
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content")
                .authority(authority)
                .appendPath(medium)
                .build();

        long id = card.getLong(card.getColumnIndex("_id"));

        Cursor tmp = resolver.query(uri,new String[]{"_id","last_time"},"card_id='"+card_id+"'",null,null);

        if(tmp != null && tmp.getCount() > 0) {
            ContentValues values = new ContentValues();
            values.put("last_time", System.currentTimeMillis()/1000);
            String [] arg = new String[]{card_id};
            resolver.update(uri,values,"card_id=?",arg);
            tmp.close();
        }else {
            ContentValues values = new ContentValues();
            values.put("last_time", System.currentTimeMillis()/1000);
            values.put("card_id", id);
            resolver.insert(uri, values);
        }
        removeFromOtherCollections(medium);
        setInvisibleAnswerAndButtons();
        card.close();
        cards_id.remove(myCurrentCard);
        on_going = true;
        choosed_card = false;
        pressed_give_answer = false;
        play();
    }

    public void hard(View v){
        card.moveToFirst();
        Uri.Builder builder = new Uri.Builder();
        Uri uri = builder.scheme("content")
                .authority(authority)
                .appendPath(hard)
                .build();

        long id = card.getLong(card.getColumnIndex("_id"));

        Cursor tmp = resolver.query(uri,new String[]{"_id","last_time"},"card_id='"+card_id+"'",null,null);

        if(tmp != null && tmp.getCount() > 0) {
            ContentValues values = new ContentValues();
            values.put("last_time", System.currentTimeMillis()/1000);
            String [] arg = new String[]{card_id};
            resolver.update(uri,values,"card_id=?",arg);
            tmp.close();
        }else {
            ContentValues values = new ContentValues();
            values.put("last_time", System.currentTimeMillis()/1000);
            values.put("card_id", id);
            resolver.insert(uri, values);
        }

        removeFromOtherCollections(hard);
        setInvisibleAnswerAndButtons();
        card.close();
        cards_id.remove(myCurrentCard);
        on_going = true;
        choosed_card = false;
        pressed_give_answer = false;
        play();
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
        response.setVisibility(View.INVISIBLE);

        Button trivial = (Button) findViewById(R.id.trivial);
        trivial.setVisibility(View.VISIBLE);

        Button easy = (Button) findViewById(R.id.easy);
        easy.setVisibility(View.VISIBLE);

        Button medium = (Button) findViewById(R.id.medium);
        medium.setVisibility(View.VISIBLE);

        Button hard = (Button) findViewById(R.id.hard);
        hard.setVisibility(View.VISIBLE);
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
            if (just_added_cursor != null && just_added_cursor.getCount() > 0) {
                just_added_cursor.moveToFirst();
                just = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + just_added_cursor.getString(just_added_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                if (just != null && just.getCount() > 0) {
                    just.moveToFirst();
                    cards_id.add(new MyCard(just.getString(just.getColumnIndex("_id")),0));
                    just.close();
                }
                while (just_added_cursor.moveToNext()) {
                    just = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + just_added_cursor.getString(just_added_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                    if (just != null && just.getCount() > 0) {
                        just.moveToFirst();
                        cards_id.add(new MyCard(just.getString(just.getColumnIndex("_id")),0));
                        just.close();
                    }
                }
                just_added_cursor.close();
            }
            if (hard_cursor != null && hard_cursor.getCount() > 0) {
                hard_cursor.moveToFirst();
                hard = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + hard_cursor.getString(hard_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                if (hard != null && hard.getCount() > 0) {
                    hard.moveToFirst();
                    cards_id.add(new MyCard(hard.getString(hard.getColumnIndex("_id")),Integer.parseInt(hard_timer)));
                    hard.close();
                }
                while (hard_cursor.moveToNext()) {
                    hard = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + hard_cursor.getString(hard_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                    if (hard != null && hard.getCount() > 0) {
                        hard.moveToFirst();
                        cards_id.add(new MyCard(hard.getString(hard.getColumnIndex("_id")),Integer.parseInt(hard_timer)));
                        hard.close();
                    }
                }
                hard_cursor.close();
            }
            if (medium_cursor != null && medium_cursor.getCount() > 0) {
                medium_cursor.moveToFirst();
                medium = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + medium_cursor.getString(medium_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                if (medium != null && medium.getCount() > 0) {
                    medium.moveToFirst();
                    cards_id.add(new MyCard(medium.getString(medium.getColumnIndex("_id")),Integer.parseInt(medium_timer)));
                    medium.close();
                }
                while (medium_cursor.moveToNext()) {
                    medium = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + medium_cursor.getString(medium_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                    if (medium != null && medium.getCount() > 0) {
                        medium.moveToFirst();
                        cards_id.add(new MyCard(medium.getString(medium.getColumnIndex("_id")),Integer.parseInt(medium_timer)));
                        medium.close();
                    }
                }
                medium_cursor.close();
            }
            if (easy_cursor != null && easy_cursor.getCount() > 0) {
                easy_cursor.moveToFirst();
                easy = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + easy_cursor.getString(easy_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                if (easy != null && easy.getCount() > 0) {
                    easy.moveToFirst();
                    cards_id.add(new MyCard(easy.getString(easy.getColumnIndex("_id")),Integer.parseInt(easy_timer)));
                    easy.close();
                }
                while (easy_cursor.moveToNext()) {
                    easy = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + easy_cursor.getString(easy_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                    if (easy != null && easy.getCount() > 0) {
                        easy.moveToFirst();
                        cards_id.add(new MyCard(easy.getString(easy.getColumnIndex("_id")),Integer.parseInt(easy_timer)));
                        easy.close();
                    }
                }
                easy_cursor.close();
            }
            if (trivial_cursor != null && trivial_cursor.getCount() > 0) {
                trivial_cursor.moveToFirst();
                trivial = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + trivial_cursor.getString(trivial_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                if (trivial != null && trivial.getCount() > 0) {
                    trivial.moveToFirst();
                    cards_id.add(new MyCard(trivial.getString(trivial.getColumnIndex("_id")),Integer.parseInt(trivial_timer)));
                    trivial.close();
                }
                while (trivial_cursor.moveToNext()) {
                    trivial = resolver.query(uri, new String[]{"_id", column_question, column_answer}, "collection_id='" + intent.getLongExtra("key", 1L) + "' and '" + trivial_cursor.getString(trivial_cursor.getColumnIndex("card_id")) + "' = _id", null, null);
                    if (trivial != null && trivial.getCount() > 0) {
                        trivial.moveToFirst();
                        cards_id.add(new MyCard(trivial.getString(trivial.getColumnIndex("_id")),Integer.parseInt(trivial_timer)));
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
                .appendPath(just_added)
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

        String request = (System.currentTimeMillis()/1000) + " - last_time > " + re_ask_hard;

        hard_cursor = resolver.query(uri, new String[]{"_id", "card_id", "last_time"}, request, null, null);
    }

    public void createCursorMedium() {
        Uri uri;
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath(medium)
                .build();

        String request = (System.currentTimeMillis()/1000) + " - last_time > " + re_ask_medium;

        medium_cursor = resolver.query(uri, new String[]{"_id", "card_id"}, request, null, null);
    }

    public void createCursorEasy() {
        Uri uri;
        Uri.Builder builder = new Uri.Builder();
        uri = builder.scheme("content")
                .authority(authority)
                .appendPath(easy)
                .build();

        String request = (System.currentTimeMillis()/1000) + " - last_time > " + re_ask_easy;

        easy_cursor = resolver.query(uri, new String[]{"_id", "card_id"}, request, null, null);
    }

    public void createCursorTrivial() {
        if (!re_ask_trivial.equals("Never") && !re_ask_trivial.equals("") ) {
            Uri uri;
            Uri.Builder builder = new Uri.Builder();
            uri = builder.scheme("content")
                    .authority(authority)
                    .appendPath(trivial)
                    .build();

            String request = (System.currentTimeMillis()/1000) + " - last_time > " + re_ask_trivial;

            trivial_cursor = resolver.query(uri, new String[]{"_id", "card_id"}, request, null, null);
        }
    }
}
