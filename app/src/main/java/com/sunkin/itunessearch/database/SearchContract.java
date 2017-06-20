package com.sunkin.itunessearch.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kaika on 6/19/2017.
 */

public class SearchContract {
    public static final String CONTENT_AUTHORITY = "com.sunkin.itunessearch";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String SEARCH_ITEM_PATH = "search";

    public static final class SearchEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(SEARCH_ITEM_PATH).build();
        //creating cursor of base type directory for multiple entries
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + SEARCH_ITEM_PATH;
        //creating cursor of base type item for single entries
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + SEARCH_ITEM_PATH;
        //table name
        public static final String TABLE_NAME = "search";
        //columns
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TRACK_ART_WORK_100 = "artworkUrl100";
        public static final String COLUMN_TRACK_ART_WORK_30 = "artworkUrl30";
        public static final String COLUMN_TRACK_ISFAVORITE = "isFavorite";
        public static final String COLUMN_TRACK_PREVIEWURL = "previewUrl";
        public static final String COLUMN_TRACK_CENSORED_NAME = "trackCensoredName";
        public static final String COLUMN_TRACK_NAME = "trackName";
        public static final String COLUMN_TRACK_PRICE = "trackPrice";
        public static final String COLUMN_TRACK_ID = "trackId";

        public static final String[] SEARCH_COLUMNS =
                {
                        COLUMN_TRACK_ART_WORK_100,
                        COLUMN_TRACK_ART_WORK_30,
                        COLUMN_TRACK_ISFAVORITE,
                        COLUMN_TRACK_PREVIEWURL,
                        COLUMN_TRACK_CENSORED_NAME,
                        COLUMN_TRACK_NAME,
                        COLUMN_TRACK_PRICE,
                        COLUMN_TRACK_ID
                };

        public static final int COL_TRACK_ART_WORK_100 = 0;
        public static final int COL_TRACK_ART_WORK_30 = 1;
        public static final int COL_TRACK_ISFAVORITE = 2;
        public static final int COL_TRACK_PREVIEWURL = 3;
        public static final int COL_TRACK_CENSORED_NAME = 4;
        public static final int COL_TRACK_NAME = 5;
        public static final int COL_TRACK_PRICE = 6;
        public static final int COL_TRACK_ID = 7;


        public static Uri buildSearchItemUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri searchItemUri(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }
    }
}
