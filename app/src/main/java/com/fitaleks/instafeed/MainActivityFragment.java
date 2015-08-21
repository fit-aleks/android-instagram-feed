package com.fitaleks.instafeed;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.content.ContentProvider;
import com.fitaleks.instafeed.data.BaseModel;
import com.fitaleks.instafeed.network.FeedFetchService;
import com.fitaleks.instafeed.data.PhotoEntry;
import com.fitaleks.instafeed.data.Utils;


public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PHOTOS_LOADER = 0;

    private EditText            mEditText;
    private InstaFeedAdapter    mAdapter;

    private boolean loading     = true;
    private int previousTotal   = 0;

    public static final int COL_PHOTO_ID            = 0;
    public static final int COL_PHOTO_CREATION_TIME = 1;
    public static final int COL_PHOTO_DESCR         = 2;
    public static final int COL_PHOTO_URL           = 3;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mEditText = (EditText) rootView.findViewById(R.id.edit_text_user_name);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    loadMorePhotos();
                    return true;
                }
                return false;
            }
        });

        final String nameOfLastUser = Utils.getName(getActivity());
        if (!nameOfLastUser.equals(""))
            mEditText.setText(nameOfLastUser);

        final ListView listView = (ListView)rootView.findViewById(R.id.instafeed_recycler_view);
        mAdapter = new InstaFeedAdapter(getActivity(), null, 0);
        listView.setAdapter(mAdapter);
        listView.setOnScrollListener(onScrollListener);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    final long photoId =  cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
                    Intent goToComments = new Intent(getActivity(), CommentsActivity.class)
                            .putExtra(CommentsActivity.KEY_PHOTO_ID, photoId);
                    startActivity(goToComments);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(PHOTOS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(PHOTOS_LOADER, null, this);
    }


    private final ListView.OnScrollListener onScrollListener = new ListView.OnScrollListener(){
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (totalItemCount < previousTotal) {
                previousTotal = totalItemCount;
                if (totalItemCount == 0) {
                    loading = true;
                }
            }

            if (loading && (totalItemCount > previousTotal)) {
                loading = false;
                previousTotal = totalItemCount;
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + 2) ) {
                loadMorePhotos();
                loading = true;
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }
    };

    private void loadMorePhotos() {
        // TODO: fix loading more photos
//        Intent intent = new Intent(getActivity(), FeedFetchService.class);
//        intent.putExtra(FeedFetchService.USER_NAME_EXTRA, mEditText.getText().toString());
//        getActivity().startService(intent);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                ContentProvider.createUri(PhotoEntry.class, null),
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
