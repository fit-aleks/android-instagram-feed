package com.fitaleks.instafeed;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.fitaleks.instafeed.data.InstagramPhoto;
import com.fitaleks.instafeed.data.LoadDataTask;
import com.fitaleks.instafeed.data.Utils;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private EditText            mEditText;
    private InstaFeedAdapter    mAdapter;
    private LinearLayoutManager mLayoutManager;

    private boolean loading     = true;
    private int previousTotal   = 0;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mEditText = (EditText) rootView.findViewById(R.id.edit_text_user_name);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    LoadDataTask loadDataTask = new LoadDataTask(getActivity(), onAsyncTaskCompleteCallback);
                    loadDataTask.execute(mEditText.getText().toString());

                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        final RecyclerView mRecyclerView = (RecyclerView)rootView.findViewById(R.id.instafeed_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new InstaFeedAdapter(getActivity(), new ArrayList<InstagramPhoto>());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(onScrollListener);

        return rootView;
    }

    private final LoadDataTask.OnTaskComplete onAsyncTaskCompleteCallback = new LoadDataTask.OnTaskComplete() {
        @Override
        public void onPhotosFetchCompleted(ArrayList<InstagramPhoto> listOfPhotoData, boolean isNewUser) {
            if (isNewUser) {
                mAdapter.resetAndAddItems(listOfPhotoData);
            } else {
                mAdapter.addItems(listOfPhotoData);
            }
        }
    };

    private final RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = mLayoutManager.getChildCount();
            int totalItemCount = mLayoutManager.getItemCount();
            int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if ( totalItemCount > previousTotal ) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (pastVisiblesItems + 2) ) {
                LoadDataTask loadDataTask = new LoadDataTask(getActivity(), onAsyncTaskCompleteCallback);
                loadDataTask.execute(mEditText.getText().toString());
                loading = true;
            }
        }
    };
}
