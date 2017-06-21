package com.sunkin.itunessearch;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;


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
}
