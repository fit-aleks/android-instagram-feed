package com.fitaleks.instafeed.data;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.fitaleks.instafeed.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by alexanderkulikovskiy on 23.06.15.
 */
public class FeedFetchService extends IntentService {
    private static final String LOG_TAG = FeedFetchService.class.getSimpleName();

    private static final String INSTAGRAM_URL       = "https://api.instagram.com/v1/users/";
    private static final String INSTAGRAM_USERS_URL = INSTAGRAM_URL + "search?";

    public static final String USER_NAME_EXTRA      = "user_name";

    public FeedFetchService() {
        super("FeedFetchService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String userName = intent.getStringExtra(USER_NAME_EXTRA);
        if (fetchUserData(userName)) {
            Utils.setNextPageUrl(this, "");
            Utils.setIsNewUser(this, true);
            this.getContentResolver().delete(InstaFeedContract.PhotoEntry.CONTENT_URI, null, null);
            this.getContentResolver().delete(InstaFeedContract.CommentEntry.CONTENT_URI, null, null);
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
        final Uri builtUri = Uri.parse(INSTAGRAM_USERS_URL).buildUpon()
                .appendQueryParameter("q", userName)
                .appendQueryParameter("client_id", MainActivity.INSTA_CLIENT_ID)
                .build();
        final String userJsonString = sendResponse(builtUri.toString());
        if (userJsonString == null) {
            return true;
        }
        try {
            final JSONObject userJson = new JSONObject(userJsonString);
            final JSONArray userDetailsArray = userJson.getJSONArray("data");
            final JSONObject wishedUserDetails = userDetailsArray.getJSONObject(0);
            final String userNick = wishedUserDetails.getString("username");
            final long userId = wishedUserDetails.getLong("id");
            if (Utils.getName(this).equals(userNick)) {
                return false;
            }
            Utils.setName(this, userNick);
            Utils.setUserId(this, userId);
        } catch (JSONException ex) {
            Log.e(LOG_TAG, "Error parsing json", ex);
            Utils.setUserId(this, -1);
        }
        return true;
    }

    private void fetchPhotosData() {
        String urlString = Utils.getNextPageUrl(this);
        if (urlString.equals("")) {
            final long userId = Utils.getUserId(this);
            final String MEDIA_URL = INSTAGRAM_URL + userId + "/media/recent?";
            final Uri builtUri = Uri.parse(MEDIA_URL).buildUpon()
                    .appendQueryParameter("count", "10")
                    .appendQueryParameter("client_id", MainActivity.INSTA_CLIENT_ID)
                    .build();
            urlString = builtUri.toString();
        }

        final String photosJsonString = sendResponse(urlString);
        try {
            final JSONObject baseUserMediaObject = new JSONObject(photosJsonString);
            final JSONObject pagination = baseUserMediaObject.getJSONObject("pagination");
            Utils.setNextPageUrl(this, pagination.getString("next_url"));

            final JSONArray mediaArray = baseUserMediaObject.getJSONArray("data");
            Vector<ContentValues> cVVector = new Vector<>(mediaArray.length());
            Vector<ContentValues> cVVectorComments = new Vector<>();
            for (int i = 0; i < mediaArray.length(); ++i) {
                JSONObject userMedia = mediaArray.getJSONObject(i);
                JSONObject allImageSizes = userMedia.getJSONObject("images");
                JSONObject standardImageSize = allImageSizes.getJSONObject("standard_resolution");
                String urlOfImage = standardImageSize.getString("url");

                String descr = "";
                if (!userMedia.isNull("caption")) {
                    JSONObject captionObject = userMedia.getJSONObject("caption");
                    descr = captionObject.getString("text");
                }
                String instaPhotoId = userMedia.getString("id");
                long timeOfPhotoCreation = userMedia.getLong("created_time");
                ContentValues photosValues = new ContentValues();
                photosValues.put(InstaFeedContract.PhotoEntry.COLUMN_INSTA_ID, instaPhotoId);
                photosValues.put(InstaFeedContract.PhotoEntry.COLUMN_DESCRIPTION, descr);
                photosValues.put(InstaFeedContract.PhotoEntry.COLUMN_IMAGE_URL, urlOfImage);
                photosValues.put(InstaFeedContract.PhotoEntry.COLUMN_CREATED_TIME, timeOfPhotoCreation);

                cVVector.add(photosValues);

                JSONObject baseCommentsObject = userMedia.getJSONObject("comments");
                JSONArray commentsData = baseCommentsObject.getJSONArray("data");
                for (int j = 0; j < commentsData.length(); ++j) {
                    JSONObject commentJsonData = commentsData.getJSONObject(j);
                    String id = commentJsonData.getString("id");
                    String text = commentJsonData.getString("text");
                    long createdAt = commentJsonData.getLong("created_time");
                    JSONObject authorJsonObject = commentJsonData.getJSONObject("from");
                    String authorName = authorJsonObject.getString("username");
                    ContentValues commentsValues = new ContentValues();
                    commentsValues.put(InstaFeedContract.CommentEntry.COLUMN_AUTHORNAME, authorName);
                    commentsValues.put(InstaFeedContract.CommentEntry.COLUMN_INSTA_ID, id);
                    commentsValues.put(InstaFeedContract.CommentEntry.COLUMN_PHOTO_ID, instaPhotoId);
                    commentsValues.put(InstaFeedContract.CommentEntry.COLUMN_TEXT, text);
                    commentsValues.put(InstaFeedContract.CommentEntry.COLUMN_TIME, createdAt);
                    cVVectorComments.add(commentsValues);
                }
            }
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                this.getContentResolver().bulkInsert(InstaFeedContract.PhotoEntry.CONTENT_URI, cvArray);
            }
            if (cVVectorComments.size() > 0) {
                ContentValues[] cvArrayOfComments = new ContentValues[cVVectorComments.size()];
                cVVectorComments.toArray(cvArrayOfComments);
                this.getContentResolver().bulkInsert(InstaFeedContract.CommentEntry.CONTENT_URI, cvArrayOfComments);
            }
        } catch (JSONException ex) {
            Log.e(LOG_TAG, "Error parsing json", ex);
        }
    }

    private String sendResponse(@NonNull final String urlString) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String jsonResponse = null;
        try {
            URL url = new URL(urlString);
            Log.v(LOG_TAG, "Built URI " + urlString);

            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            jsonResponse = buffer.toString();

            Log.v(LOG_TAG, "Response string: " + jsonResponse);
        } catch (IOException ex) {
            Log.e(LOG_TAG, "Error", ex);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try{
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return jsonResponse;
    }

}
