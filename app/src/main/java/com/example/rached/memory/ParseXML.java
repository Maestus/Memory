package com.example.rached.memory;

import android.Manifest;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static java.lang.System.exit;

/**
 * Created by rached on 18/12/16.
 */

public class ParseXML extends Fragment{
    private static String authority = "com.example.rached.memorycontentprovider";
    private XmlPullParser myParser;
    private XmlPullParserFactory xmlFactoryObject;
    InputStream is;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.fragment_download, parent, false);

        try {
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            myParser = xmlFactoryObject.newPullParser();


            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, "/Memory/source.xml");
            is = new BufferedInputStream(new FileInputStream(file.getPath()));

            myParser.setInput(is, null);

            parsefile();

        } catch (FileNotFoundException | XmlPullParserException e) {
            e.printStackTrace();
            exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            exit(1);
        }
        return(result);
    }

    public void parsefile() throws XmlPullParserException,IOException{
        int event = myParser.getEventType();
        Collection collection = new Collection("Undefined");
        Card card = new Card();
        while (event != XmlPullParser.END_DOCUMENT) {
            if(event == XmlPullParser.START_TAG) {
                if("question".equals(myParser.getName())){
                    card.addQuestion(myParser.nextText());
                } else if("answer".equals(myParser.getName())){
                    card.addAnswer(myParser.nextText());
                } else if("card".equals(myParser.getName())){
                    card = new Card();
                } else if("collection".equals(myParser.getName())){
                    collection = new Collection(myParser.getAttributeValue(0));
                }
            } else if(event==XmlPullParser.END_TAG) {
                if("collection".equals(myParser.getName())){
                    addToDb(collection);//collections.add(collection);
                }else if("card".equals(myParser.getName())){
                    collection.addCard(card);
                }
            }
            event = myParser.next();
        }
    }

    public void addToDb(Collection c){
        String n = c.name;
        ContentValues values = new ContentValues();
        values.put("name", n);


        ContentResolver resolver = getActivity().getContentResolver();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("content").authority(authority).appendPath("collections_table");
        Uri uri = builder.build();
        uri = resolver.insert(uri, values);
        for(int i = 0; i < c.cards.size(); i++) {
            String question = c.cards.get(i).question;
            String answer = c.cards.get(i).answer;


            long id = ContentUris.parseId(uri);
            values = new ContentValues();
            values.put("question", question);
            values.put("answer", answer);
            values.put("collection_id", id);

            //builder = builder.clearQuery();
            builder = new Uri.Builder();
            builder.scheme("content").authority(authority).appendPath("cards_table");
            uri = builder.build();
            uri = resolver.insert(uri, values);
        }
    }
}
