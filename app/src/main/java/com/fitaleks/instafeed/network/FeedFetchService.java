package com.fitaleks.instafeed.network;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.fitaleks.instafeed.MainActivity;
import com.fitaleks.instafeed.data.CommentEntry;
import com.fitaleks.instafeed.data.PhotoEntry;
import com.fitaleks.instafeed.data.User;
import com.fitaleks.instafeed.data.Utils;

import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by alexanderkulikovskiy on 23.06.15.
 */
public class FeedFetchService extends IntentService {
    private static final String LOG_TAG = FeedFetchService.class.getSimpleName();

    public static final String USER_NAME_EXTRA = "user_name";

    public FeedFetchService() {
        super("FeedFetchService");
    }
    private InstagramService instagramService;

    @Override
    protected void onHandleIntent(Intent intent) {
        String userName = intent.getStringExtra(USER_NAME_EXTRA);
        if (!NetworkHelper.isConnected(this)) {
            return;
        }
        this.instagramService = NetworkHelper.getInstaRestAdapter();

        if (fetchUserData(userName)) {
            Utils.setNextPageUrl(this, "");
            Utils.setIsNewUser(this, true);
            new Delete().from(PhotoEntry.class).execute();
            new Delete().from(CommentEntry.class).execute();
        }
        this.fetchPhotosData();
    }

    /**
     * Accepts user name from edit text. Sends request to Instagram API, parses it.
     * If correct answer accepted then saves userId to SharedPreferences.
     * @param userName - Name of instagram user from EditText
     * @return true if requested user is not the same as previous one
     */
    private boolean fetchUserData(@NonNull final String userName) {
        try {
            List<User> appropriateUsers = this.instagramService.getUserList(userName, MainActivity.INSTA_CLIENT_ID);
            if (appropriateUsers.size() == 0) {
                return true;
            }
            User firstUser = appropriateUsers.get(0);
            if (Utils.getName(this).equals(firstUser.userName)) {
                return false;
            }
            Utils.setName(this, firstUser.userName);
            Utils.setUserId(this, firstUser.id);
        } catch (RetrofitError retrofitError) {
            Utils.setUserId(this, -1);
            return true;
        }

        return true;
    }

    private void fetchPhotosData() {
        final List<PhotoEntry> listPhotos = instagramService.getPhotos(Utils.getUserId(this), 10, MainActivity.INSTA_CLIENT_ID);
        Log.d(LOG_TAG, "resService = " + listPhotos.size());
        ActiveAndroid.beginTransaction();
        try {
            for (PhotoEntry photoEntry : listPhotos) {
                photoEntry.saveModel();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }

    }

}
