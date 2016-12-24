package com.example.rached.memory;

import android.app.DownloadManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class DownloadActivity extends Fragment {
    Uri uri = Uri.parse("https://drive.google.com/uc?export=download&id=0B3Q4cpPks70WbEpOLUxjMnFPTVU");
    private DownloadManager mgr=null;
    private long lastDownload=-1L;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.fragment_download, parent, false);
        mgr = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

        System.out.println("ONE MORE TIME");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        startRequest();
        queryStatus();
        getActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        return(result);
    }

    BroadcastReceiver onComplete=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            Toast.makeText(ctxt, "Finished", Toast.LENGTH_LONG).show();
            getFragmentManager().beginTransaction().add(R.id.content_main, new ParseXML()).commit();

        }
    };

    @Override
    public void onResume() {
        super.onResume();

        IntentFilter f= new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        f.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);

        getActivity().registerReceiver(onEvent, f);
        getActivity().registerReceiver(onComplete, f);

    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(onEvent);
        getActivity().unregisterReceiver(onComplete);

        super.onPause();
    }

    public void startRequest(){

        Environment.getExternalStoragePublicDirectory("Memory")
                .mkdirs();

        DownloadManager.Request req = new DownloadManager.Request(uri);

        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle("---")
                .setDescription("Something useful. No, really.")
                .setDestinationInExternalPublicDir("Memory","source.xml");

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
            } else {
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
                break;

            default:
                msg = getActivity().getString(R.string.download_is_nowhere_in_sight);
                break;
        }

        return(msg);
    }

}
