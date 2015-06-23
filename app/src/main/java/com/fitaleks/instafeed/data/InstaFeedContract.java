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
public class InstaFeedContract {

    public static final String CONTENT_AUTHORITY = "com.fitaleks.instafeed";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths
    public static final String PATH_PHOTOS = "photos";

    /* Class that defines the table contents of the shots table */
    public static final class PhotoEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PHOTOS).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_PHOTOS;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_PHOTOS;



        public static final String TABLE_NAME = "photos";

        /** Inner instagram ID */
        public static final String COLUMN_INSTA_ID = "insta_id";

        /** Image url */
        public static final String COLUMN_IMAGE_URL = "image_url";

        /** Photo description */
        public static final String COLUMN_DESCRIPTION = "description";

        /** Time of photo creation */
        public static final String COLUMN_CREATED_TIME = "created_time";

        public static Uri buildPhotosUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static  final String PATH_COMMENTS = "comments";

    public static final class CommentEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COMMENTS).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_COMMENTS;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_COMMENTS;

        public static final String TABLE_NAME = "comments";

        /** Inner instagram ID */
        public static final String COLUMN_INSTA_ID = "comment_id";

        /** Time of comment */
        public static final String COLUMN_TIME = "created_time";

        /** Comment text url */
        public static final String COLUMN_TEXT = "text";

        /** Author of comment */
        public static final String COLUMN_AUTHORNAME = "author_username";

        /** Value to use as a foreign key to photo */
        public static final String COLUMN_PHOTO_ID = "photo_id";

        public static Uri buildCommentsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildCommentsListUri(String photoId) {
            return CONTENT_URI.buildUpon().appendPath(photoId).build();
        }

        public static String getPhotoIDFromUri(final Uri  uri) {
            return uri.getPathSegments().get(1);
        }

    }
}
