package com.example.rached.memory;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class CollectionsFragmentContent extends ListFragment implements  LoaderManager.LoaderCallbacks<Cursor> {
    private static final String AUTHORITY = "authority";
    private static final String TABLE = "table";
    private static final String COLUMN = "column";
    private OnFragmentInteractionListener mListener;
    private SimpleCursorAdapter adapter;
    private String mAuthority, mTable, mColumn;
    private static final String LOG = "FragmentContent";
    private SwipeRefreshLayout swipeRefreshLayout;

    public CollectionsFragmentContent() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AfficherFragment.
     */
    public static CollectionsFragmentContent newInstance(String authority, String table, String column) {
        CollectionsFragmentContent fragment = new CollectionsFragmentContent();
        Bundle args = new Bundle();
        args.putString(AUTHORITY, authority);
        args.putString(TABLE, table);
        args.putString(COLUMN, column);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() == null) {
            throw new RuntimeException(LOG + " missing Arguments");
        }

        mAuthority = getArguments().getString(AUTHORITY);
        mTable = getArguments().getString(TABLE);
        mColumn = getArguments().getString(COLUMN);

        adapter = new SimpleCursorAdapter(
                    getActivity(),/*context*/
                    android.R.layout.simple_list_item_1,
                    null, /*Cursor - null initialement */
                    new String[]{mColumn},
                    new int[]{android.R.id.text1}, 0);

        setListAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    refreshContent();
                }
        });
        getLoaderManager().initLoader(100, null, this);
    }


    private void refreshContent(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(getActivity() != null) {
                    ContentResolver resolver = getActivity().getContentResolver();
                    Uri.Builder builder = new Uri.Builder();
                    builder.scheme("content").authority(mAuthority).appendPath(mTable);
                    Uri uri = builder.build();
                    Cursor cursor = resolver.query(uri, null, null, null, null);
                    if (cursor != null) System.out.println("Elements dans la table collections = "+cursor.getCount());
                    adapter.swapCursor(cursor);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        },500);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        getLoaderManager().initLoader(100, null, this);
        if(swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
        adapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        if (data != null) {
            Log.d(LOG, "load finished size = " + data.getCount() + "");
        } else {
            Log.d(LOG, "load finished data null");
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = (new Uri.Builder()).scheme("content")
                .authority(mAuthority)
                .appendPath(mTable)
                .build();
        Log.d(LOG, "onCreateLoader uri=" + uri.toString());
        return new android.support.v4.content.CursorLoader(getActivity(), uri,
                new String[]{"_id", mColumn},
                null, null, null);
    }


    /**
     * Returns whether the {@link android.support.v4.widget.SwipeRefreshLayout} is currently
     * refreshing or not.
     *
     * @see android.support.v4.widget.SwipeRefreshLayout#isRefreshing()
     */

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            refreshContent();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(mListener == null){return;}
        mListener.onIdSelection(id);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onIdSelection(long id);
    }

}
