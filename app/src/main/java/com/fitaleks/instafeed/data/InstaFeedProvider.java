package com.fitaleks.instafeed.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by alex1101 on 10.09.14.
 */
public class InstaFeedProvider extends ContentProvider {
    private static final int PHOTOS = 100;
    private static final int COMMENTS  = 200;
    private static final int COMMENTS_BY_PHOTO_ID = 201;

    private static final UriMatcher uriMather = buildUriMatcher();
    private InstaFeedDbHelper openHelper;

    private static final String commentByPhotoIDSelection =
            InstaFeedContract.CommentEntry.TABLE_NAME + "." + InstaFeedContract.CommentEntry.COLUMN_PHOTO_ID + " = ?";

    @Override
    public boolean onCreate() {
        this.openHelper = new InstaFeedDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMather.match(uri);

        switch (match) {
            case PHOTOS:
                return InstaFeedContract.PhotoEntry.CONTENT_TYPE;
            case COMMENTS_BY_PHOTO_ID:
                return InstaFeedContract.CommentEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri:"+uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (uriMather.match(uri)) {
            case PHOTOS: {
                retCursor = openHelper.getReadableDatabase().query(
                        InstaFeedContract.PhotoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case COMMENTS_BY_PHOTO_ID: {
                retCursor = getCommentsByPhotoID(uri, projection, sortOrder);
                break;
            }
            default: throw new UnsupportedOperationException("Unknown uri: "+uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = uriMather.match(uri);
        int rowsAffected = 0;

        switch (match) {
            case PHOTOS:
            {
                rowsAffected = db.update(InstaFeedContract.PhotoEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case COMMENTS:
            {
                rowsAffected = db.update(InstaFeedContract.CommentEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsAffected != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }


        return rowsAffected;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = uriMather.match(uri);
        Uri returnUri = null;

        switch (match) {
            case PHOTOS:
            {
                long _id = db.insert(InstaFeedContract.PhotoEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = InstaFeedContract.PhotoEntry.buildPhotosUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case COMMENTS:
            {
                long _id = db.insert(InstaFeedContract.CommentEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = InstaFeedContract.CommentEntry.buildCommentsUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(returnUri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = uriMather.match(uri);
        int rowsAffected = 0;

        switch (match) {
            case PHOTOS:
            {
                rowsAffected = db.delete(InstaFeedContract.PhotoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case COMMENTS:
            {
                rowsAffected = db.delete(InstaFeedContract.CommentEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default: throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (selection == null || rowsAffected != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }


        return rowsAffected;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = openHelper.getWritableDatabase();
        final int match = uriMather.match(uri);

        switch (match) {
            case PHOTOS: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(InstaFeedContract.PhotoEntry.TABLE_NAME, null, value);
                        if (_id != 1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case COMMENTS_BY_PHOTO_ID: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(InstaFeedContract.CommentEntry.TABLE_NAME, null, value);
                        if (_id != 1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            default:
                return super.bulkInsert(uri, values);
        }
    }


    private static UriMatcher buildUriMatcher() {
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = InstaFeedContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, InstaFeedContract.PATH_PHOTOS, PHOTOS);
        uriMatcher.addURI(authority, InstaFeedContract.PATH_COMMENTS, COMMENTS);
        uriMatcher.addURI(authority, InstaFeedContract.PATH_COMMENTS + "/*", COMMENTS_BY_PHOTO_ID);

        return uriMatcher;
    }

    private Cursor getCommentsByPhotoID(Uri uri, String projection[], String sortOrder) {
        String photoId = InstaFeedContract.CommentEntry.getPhotoIDFromUri(uri);
        String[] selectionArgs = new String[]{photoId};
        String selection = commentByPhotoIDSelection;

        return openHelper.getReadableDatabase().query(
                InstaFeedContract.CommentEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

}
