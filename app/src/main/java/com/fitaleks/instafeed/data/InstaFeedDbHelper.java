package com.fitaleks.instafeed.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.fitaleks.instafeed.data.InstaFeedContract.PhotoEntry;
import static com.fitaleks.instafeed.data.InstaFeedContract.CommentEntry;

/**
 * Created by alex1101 on 10.09.14.
 */
public class InstaFeedDbHelper extends SQLiteOpenHelper {
    private final static int DATABASE_VERSION = 1;
    public final static String DATABASE_NAME = "instafeed.db";

    public InstaFeedDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_SHOTS_TABLE = "CREATE TABLE " + PhotoEntry.TABLE_NAME + " (" +
                PhotoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PhotoEntry.COLUMN_INSTA_ID + " TEXT NOT NULL, " +
                PhotoEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                PhotoEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                PhotoEntry.COLUMN_CREATED_TIME + " INTEGER NOT NULL, " +
                " UNIQUE (" + PhotoEntry.COLUMN_INSTA_ID + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_COMMENTS_TABLE = "CREATE TABLE " + CommentEntry.TABLE_NAME + " (" +
                CommentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                CommentEntry.COLUMN_INSTA_ID + " TEXT NOT NULL, " +
                CommentEntry.COLUMN_TEXT + " TEXT NOT NULL, " +
                CommentEntry.COLUMN_AUTHORNAME + " TEXT NOT NULL, " +
                CommentEntry.COLUMN_TIME + " INTEGER NOT NULL, " +
                CommentEntry.COLUMN_PHOTO_ID + " TEXT NOT NULL, " +
                " UNIQUE (" + CommentEntry.COLUMN_INSTA_ID + ") ON CONFLICT REPLACE);";

        sqLiteDatabase.execSQL(SQL_CREATE_SHOTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_COMMENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PhotoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CommentEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
