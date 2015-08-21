package com.fitaleks.instafeed;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.activeandroid.Cache;
import com.activeandroid.content.ContentProvider;
import com.fitaleks.instafeed.data.CommentEntry;

/**
 * Created by alexanderkulikovskiy on 23.06.15.
 */
public class CommentsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int COMMENTS_LOADER = 0;

    private CommentsAdapter mCommentsAdapter;
    private long mPhotoId;

    public static CommentsFragment newInstance(long photoId) {
        CommentsFragment commentsFragment = new CommentsFragment();

        Bundle bundle = new Bundle();
        bundle.putLong(CommentsActivity.KEY_PHOTO_ID, photoId);
        commentsFragment.setArguments(bundle);

        return commentsFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Bundle arguments = getArguments();
        if (arguments != null) {
            mPhotoId = arguments.getLong(CommentsActivity.KEY_PHOTO_ID, 0);
        }

        final View rootView = inflater.inflate(R.layout.fragment_comments, container, false);

        final ListView listView = (ListView) rootView.findViewById(R.id.comments_list_view);
        mCommentsAdapter = new CommentsAdapter(getActivity(), null, 0);
        listView.setAdapter(mCommentsAdapter);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(CommentsActivity.KEY_PHOTO_ID) && mPhotoId != 0) {
            getLoaderManager().restartLoader(COMMENTS_LOADER, null, this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            getLoaderManager().initLoader(COMMENTS_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                ContentProvider.createUri(CommentEntry.class, null),
                null,
                "photo = " + this.mPhotoId,
                null,
                " time DESC ");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCommentsAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCommentsAdapter.swapCursor(null);
    }
}
