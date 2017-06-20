package com.sunkin.itunessearch.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kaika on 6/19/2017.
 */

public class SearchDataBaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "search.db";

    public SearchDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                SearchContract.SearchEntry.TABLE_NAME + "(" +
                SearchContract.SearchEntry._ID + " INTEGER PRIMARY KEY," +
                SearchContract.SearchEntry.COLUMN_TRACK_ART_WORK_100 + " TEXT NOT NULL, " +
                SearchContract.SearchEntry.COLUMN_TRACK_ART_WORK_30 + " TEXT NOT NULL," +
                SearchContract.SearchEntry.COLUMN_TRACK_ISFAVORITE + " TEXT NOT NULL, " +
                SearchContract.SearchEntry.COLUMN_TRACK_PREVIEWURL + " TEXT NOT NULL, " +
                SearchContract.SearchEntry.COLUMN_TRACK_CENSORED_NAME + " TEXT NOT NULL, " +
                SearchContract.SearchEntry.COLUMN_TRACK_NAME + " TEXT NOT NULL, " +
                SearchContract.SearchEntry.COLUMN_TRACK_PRICE + " TEXT NOT NULL, " +
                SearchContract.SearchEntry.COLUMN_TRACK_ID + " TEXT NOT NULL " + " );";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + SearchContract.SearchEntry.TABLE_NAME);
        onCreate(db);
    }
}
