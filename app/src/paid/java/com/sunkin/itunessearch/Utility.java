package com.sunkin.itunessearch;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.sunkin.itunessearch.data.SearchData;
import com.sunkin.itunessearch.database.SearchContract;
import com.sunkin.itunessearch.ui.MainActivity;

import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by kaika on 6/16/2017.
 */

public class Utility {

    public static final String ACTION_DATA_UPDATED = "com.sunkin.itunessearch.ACTION_DATA_UPDATED";
    public static final String BASE_URL = "https://itunes.apple.com/";
    private static final String SEARCH_KEYWORD_KEY = "search_keyword";
    private static final String SEARCH_ENTITY_KEY = "entity_key";
    private static final String HOME_SCREEN_EVER_SHOWN = "home_screen_ever_shown";
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void saveHomeScreenShowed(Context context, boolean showed) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(HOME_SCREEN_EVER_SHOWN, showed);
        editor.apply();
    }

    public static boolean homeScreenEverShowed(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(HOME_SCREEN_EVER_SHOWN, false);
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
        return preferences.getString(SEARCH_ENTITY_KEY, context.getString(R.string.default_entity));
    }

    public static boolean toBoolean(String selection) {
        boolean fav = false;
        if (Boolean.parseBoolean(selection)) {
            fav = true;
        }
        return fav;
    }

    public static ArrayList<SearchData> getFavoriteCollection(Context context) {
        ArrayList<SearchData> searchDataArrayList = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(SearchContract.SearchEntry.CONTENT_URI,
                SearchContract.SearchEntry.SEARCH_COLUMNS,
                SearchContract.SearchEntry.COLUMN_TRACK_ISFAVORITE + " =?", new String[]{TRUE}, null);
        if (cursor != null && cursor.moveToFirst()) {
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
                data.setArtistName(cursor.getString(SearchContract.SearchEntry.COL_TRACK_ARTIST_NAME));
                data.setWrapperType(cursor.getString(SearchContract.SearchEntry.COL_WRAPPER_TYPE));
                searchDataArrayList.add(data);
            } while (cursor.moveToNext());

            cursor.close();
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
        values.put(SearchContract.SearchEntry.COLUMN_TRACK_ARTIST_NAME, searchData.getArtistName());
        values.put(SearchContract.SearchEntry.COLUMN_WRAPPER_TYPE, searchData.getWrapperType());

        context.getContentResolver().insert(SearchContract.SearchEntry.CONTENT_URI, values);

        updateWidget(context);
    }

    public static boolean isItemExists(Context context, SearchData data) {
        boolean exists = false;
        Cursor cursor = context.getContentResolver().query(SearchContract.SearchEntry.CONTENT_URI,
                SearchContract.SearchEntry.SEARCH_COLUMNS,
                SearchContract.SearchEntry.COLUMN_TRACK_ID + " =?",
                new String[]{data.getTrackId()},
                null);
        if (cursor.moveToFirst()) {
            exists = true;
        }
        cursor.close();
        return exists;
    }

    public static void removeFav(Context context, SearchData searchData) {
        context.getContentResolver().delete(SearchContract.SearchEntry.searchItemUri(searchData.getTrackId()), null, new String[]{searchData.getTrackId()});
        updateWidget(context);
    }

    private static void updateWidget(Context context) {
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED);
        context.sendBroadcast(dataUpdatedIntent);
    }

    public static void showNotification(Context context) {
        Bitmap largeIcon;
        largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.app_launcher);
        NotificationCompat.Builder mBuilder =
                null;
        mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.app_launcher)
                .setLargeIcon(largeIcon)
                .setContentTitle(context.getString(R.string.network_message))
                .setContentText(context.getString(R.string.notification_message));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            mBuilder.setPriority(NotificationManager.IMPORTANCE_HIGH);
        }
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        int mNotificationId = 99;
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
