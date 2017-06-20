package com.sunkin.itunessearch;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sunkin.itunessearch.data.SearchData;
import com.sunkin.itunessearch.database.SearchContract;

import java.util.ArrayList;

/**
 * Created by kaika on 6/16/2017.
 */

public class Utility {

    public static final String BASE_URL = "https://itunes.apple.com/";
    private static final String SEARCH_KEYWORD_KEY = "search_keyword";
    private static final String SEARCH_ENTITY_KEY = "entity_key";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void saveSearchKeyword(Context context, String keyword) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SEARCH_KEYWORD_KEY, keyword);
        editor.apply();
    }

    public static String getSearchKeyword(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SEARCH_KEYWORD_KEY, "");
    }

    public static void saveSearchEntity(Context context, String entity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(SEARCH_ENTITY_KEY, entity);
        editor.apply();
    }

    public static String getSearchEntity(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SEARCH_ENTITY_KEY, "musicVideo");
    }

    public static boolean toBoolean(String selection) {
        boolean fav = false;
        if (Boolean.parseBoolean(selection)){
            fav = true;
        }
        return fav;
    }

     public static ArrayList<SearchData> getFavoriteCollection(Context context) {
        ArrayList<SearchData> searchDataArrayList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(SearchContract.SearchEntry.CONTENT_URI,
                SearchContract.SearchEntry.SEARCH_COLUMNS,
                SearchContract.SearchEntry.COLUMN_TRACK_ISFAVORITE + " =?", new String[]{"true"} , null);
        if (cursor.moveToFirst()) {
            do {
                SearchData data = new SearchData();
                data.setArtworkUrl100(cursor.getString(SearchContract.SearchEntry.COL_TRACK_ART_WORK_100));
                data.setArtworkUrl30(cursor.getString(SearchContract.SearchEntry.COL_TRACK_ART_WORK_30));
                data.setIsFavorite(cursor.getString(SearchContract.SearchEntry.COL_TRACK_ISFAVORITE));
                data.setPreviewUrl(cursor.getString(SearchContract.SearchEntry.COL_TRACK_PREVIEWURL));
                data.setTrackCensoredName(cursor.getString(SearchContract.SearchEntry.COL_TRACK_CENSORED_NAME));
                data.setTrackName(cursor.getString(SearchContract.SearchEntry.COL_TRACK_NAME));
                data.setTrackPrice(cursor.getString(SearchContract.SearchEntry.COL_TRACK_PRICE));
                data.setTrackId(cursor.getString(SearchContract.SearchEntry.COL_TRACK_ID));
                searchDataArrayList.add(data);
            } while (cursor.moveToNext());
        }
        return searchDataArrayList;
    }

    public static void saveFav(Context context, SearchData searchData) {
        ContentValues values = new ContentValues();
        values.put(SearchContract.SearchEntry.COLUMN_TRACK_ART_WORK_100, searchData.getArtworkUrl100());
        values.put(SearchContract.SearchEntry.COLUMN_TRACK_ART_WORK_30, searchData.getArtworkUrl30());
        values.put(SearchContract.SearchEntry.COLUMN_TRACK_ISFAVORITE, searchData.getIsFavorite());
        values.put(SearchContract.SearchEntry.COLUMN_TRACK_PREVIEWURL, searchData.getPreviewUrl());
        values.put(SearchContract.SearchEntry.COLUMN_TRACK_CENSORED_NAME, searchData.getTrackCensoredName());
        values.put(SearchContract.SearchEntry.COLUMN_TRACK_NAME, searchData.getTrackName());
        values.put(SearchContract.SearchEntry.COLUMN_TRACK_PRICE, searchData.getTrackPrice());
        values.put(SearchContract.SearchEntry.COLUMN_TRACK_ID, searchData.getTrackId());

        context.getContentResolver().insert(SearchContract.SearchEntry.CONTENT_URI, values);
    }

    public static void removeFav(Context context, SearchData searchData) {
        context.getContentResolver().delete(SearchContract.SearchEntry.searchItemUri(searchData.getTrackId()), null, new String[]{searchData.getTrackId()});
    }
}
