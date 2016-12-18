/***
 Copyright (c) 2008-2014 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain	a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 From _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.example.rached.memory;

import android.Manifest;
import android.app.DownloadManager;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.support.v4.content.ContextCompat;

public class DownloadActivity extends Fragment {
    Uri uri = Uri.parse("https://drive.google.com/uc?export=download&id=0B3Q4cpPks70WbEpOLUxjMnFPTVU");
    private DownloadManager mgr=null;
    private long lastDownload=-1L;
    boolean ended = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.fragment, parent, false);
        mgr = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

        if (ContextCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        100);
        }

        startRequest();
        while (!ended)
            queryStatus();

        return(result);
    }



    @Override
    public void onResume() {
        super.onResume();

        IntentFilter f= new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        f.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);

        getActivity().registerReceiver(onEvent, f);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(onEvent);

        super.onPause();
    }


    public void startRequest(){
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .mkdirs();

            DownloadManager.Request req = new DownloadManager.Request(uri);

            req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                    | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle("Demo")
                    .setDescription("Something useful. No, really.")
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                            "test.xml");

            lastDownload = mgr.enqueue(req);
    }

    private BroadcastReceiver onEvent=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent i) {
            if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(i.getAction())) {
                Toast.makeText(ctxt, "lqd", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void queryStatus() {
        Cursor c = mgr.query(new DownloadManager.Query().setFilterById(lastDownload));

        if (c == null) {
            Toast.makeText(getActivity(), R.string.download_not_found,
                    Toast.LENGTH_LONG).show();
        }
        else {
            c.moveToFirst();

            Log.d(getClass().getName(),
                    "COLUMN_ID: "
                            + c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)));
            Log.d(getClass().getName(),
                    "COLUMN_BYTES_DOWNLOADED_SO_FAR: "
                            + c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
            Log.d(getClass().getName(),
                    "COLUMN_LAST_MODIFIED_TIMESTAMP: "
                            + c.getLong(c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)));
            Log.d(getClass().getName(),
                    "COLUMN_LOCAL_URI: "
                            + c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
            Log.d(getClass().getName(),
                    "COLUMN_STATUS: "
                            + c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)));
            Log.d(getClass().getName(),
                    "COLUMN_REASON: "
                            + c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON)));

            Toast.makeText(getActivity(), statusMessage(c), Toast.LENGTH_LONG)
                    .show();

            c.close();
        }
    }

    private String statusMessage(Cursor c) {
        String msg="???";

        switch (c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_FAILED:
                msg=getActivity().getString(R.string.download_failed);
                break;

            case DownloadManager.STATUS_PAUSED:
                msg=getActivity().getString(R.string.download_paused);
                break;

            case DownloadManager.STATUS_PENDING:
                msg= getActivity().getString(R.string.download_pending);
                break;

            case DownloadManager.STATUS_RUNNING:
                msg = getActivity().getString(R.string.download_in_progress);
                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                msg = getActivity().getString(R.string.download_complete);
                ended = true;
                break;

            default:
                msg = getActivity().getString(R.string.download_is_nowhere_in_sight);
                break;
        }

        return(msg);
    }
}
