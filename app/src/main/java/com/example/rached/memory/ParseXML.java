package com.example.rached.memory;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Fragment;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

/**
 * Created by rached on 18/12/16.
 */

public class ParseXML extends Fragment{
    private XmlPullParser myParser;
    private XmlPullParserFactory xmlFactoryObject;
    InputStream is;
    List<Collection> collections;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.fragment, parent, false);

        if (ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    100);
        }

        try {
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            myParser = xmlFactoryObject.newPullParser();

            File sdcard = Environment.getExternalStorageDirectory();
            File file = new File(sdcard, "/download/test.xml");
            is = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));

            System.out.println((file.getAbsolutePath()));

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
        collections = new ArrayList<Collection>();
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
                    collections.add(collection);
                }else if("card".equals(myParser.getName())){
                    collection.addCard(card);
                }
            }
            event = myParser.next();
        }
    }
}
