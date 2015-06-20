package com.fitaleks.instafeed.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by alex1101 on 10.09.14.
 */
public class InstaPhotosContract {

    public static final String CONTENT_AUTHORITY = "com.appstockus.instafeed";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths
    public static final String PATH_SHOTS = "photos";

    /* Class that defines the table contents of the shots table */
    public static final class ShotsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SHOTS).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_SHOTS;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_SHOTS;



        public static final String TABLE_NAME = "shots";

        // Count of views of this shot
        public static final String COLUMN_DESCRIPTION = "description";

        // Count of likes of this shot
        public static final String COLUMN_IMAGE_URL = "image_url";

        public static Uri buildShotsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildShotsListUri(String listSetting) {
            return CONTENT_URI.buildUpon().appendPath(listSetting).build();
        }

    }
}
