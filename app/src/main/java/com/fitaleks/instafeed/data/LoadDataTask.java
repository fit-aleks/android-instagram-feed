package com.fitaleks.instafeed.data;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.fitaleks.instafeed.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by alexanderkulikovskiy on 20.06.15.
 */
public class LoadDataTask extends AsyncTask<String, Void, ArrayList<InstagramPhoto>> {

    public interface OnTaskComplete {
        void onPhotosFetchCompleted(ArrayList<InstagramPhoto> listOfPhotoData, boolean isNewUser);
    }

    private static final String LOG_TAG = LoadDataTask.class.getSimpleName();

    private static final String INSTAGRAM_URL       = "https://api.instagram.com/v1/users/";
    private static final String INSTAGRAM_USERS_URL = INSTAGRAM_URL + "search?";

    private OnTaskComplete  mCallback;
    private Context         mContext;
    private boolean         mIsNewUser = false;

    public LoadDataTask(@NonNull Context context, @NonNull OnTaskComplete callback) {
        this.mContext   = context;
        this.mCallback  = callback;
    }

    @Override
    protected ArrayList<InstagramPhoto> doInBackground(String... params) {
        if (params.length == 0) {
            return null;
        }
        if (fetchUserData(params[0])) {
            Utils.setNextPageUrl(this.mContext, "");
            mIsNewUser = true;
        }
        return this.fetchPhotosData();
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
            if (Utils.getName(mContext).equals(userNick)) {
                return false;
            }
            Utils.setName(mContext, userNick);
            Utils.setUserId(mContext, userId);
        } catch (JSONException ex) {
            Log.e(LOG_TAG, "Error parsing json", ex);
            Utils.setUserId(mContext, -1);
        }
        return true;
    }

    private ArrayList<InstagramPhoto> fetchPhotosData() {
        final ArrayList<InstagramPhoto> instagramPhotoArrayList = new ArrayList<>();
        String urlString = Utils.getNextPageUrl(this.mContext);
        if (urlString.equals("")) {
            final long userId = Utils.getUserId(this.mContext);
            final String MEDIA_URL = INSTAGRAM_URL + userId + "/media/recent?";
            final Uri builtUri = Uri.parse(MEDIA_URL).buildUpon()
                    .appendQueryParameter("count", "10")
                    .appendQueryParameter("client_id", MainActivity.INSTA_CLIENT_ID)
                    .build();
            urlString = builtUri.toString();
        }

        final String photosJsonString = sendResponse(urlString);
        try {
            final JSONObject userMedias = new JSONObject(photosJsonString);
            final JSONObject pagination = userMedias.getJSONObject("pagination");
            Utils.setNextPageUrl(mContext, pagination.getString("next_url"));

            final JSONArray mediaArray = userMedias.getJSONArray("data");
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
                instagramPhotoArrayList.add(new InstagramPhoto(urlOfImage, descr));
            }
        } catch (JSONException ex) {
            Log.e(LOG_TAG, "Error parsing json", ex);
        }
        return instagramPhotoArrayList;
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

    @Override
    protected void onPostExecute(ArrayList<InstagramPhoto> instagramPhotos) {
        if (this.mCallback != null) {
            this.mCallback.onPhotosFetchCompleted(instagramPhotos, mIsNewUser);
        }
    }
}
