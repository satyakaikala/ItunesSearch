package com.sunkin.itunessearch.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by kaika on 6/19/2017.
 */

public class SearchProvider extends ContentProvider {
    private static final String LOG = SearchProvider.class.getSimpleName();
    private static final UriMatcher URI_MATCHER = buildUriMatcher();
    private SearchDataBaseHelper searchDataBaseHelper;
    static final int SEARCH = 100;
    static final int SEARCH_ID = 200;

    static UriMatcher buildUriMatcher(){
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SearchContract.CONTENT_AUTHORITY;
        uriMatcher.addURI(authority, SearchContract.SEARCH_ITEM_PATH, SEARCH);
        uriMatcher.addURI(authority, SearchContract.SEARCH_ITEM_PATH + "/#", SEARCH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        searchDataBaseHelper = new SearchDataBaseHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (URI_MATCHER.match(uri)) {
            case SEARCH:
                cursor = searchDataBaseHelper.
                        getReadableDatabase().
                        query(SearchContract.SearchEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);

                break;
            case SEARCH_ID:
                cursor = searchDataBaseHelper.getReadableDatabase().
                        query(SearchContract.SearchEntry.COLUMN_TRACK_NAME,
                                projection,
                                SearchContract.SearchEntry.COLUMN_TRACK_ID + " =?" ,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
            default:
                throw new UnsupportedOperationException("Uri not found:" + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = URI_MATCHER.match(uri);
        switch (match){
            case SEARCH:
                return SearchContract.SearchEntry.CONTENT_DIR_TYPE;
            case SEARCH_ID:
                return SearchContract.SearchEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("uri not found:" + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase database = searchDataBaseHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        Uri returnUri;
        switch (match) {
            case SEARCH: {
                long id = database.insert(SearchContract.SearchEntry.TABLE_NAME, null, values);
                if (id > 0)
                    returnUri = SearchContract.SearchEntry.buildSearchItemUri(id);
                else
                    throw new android.database.SQLException("failed to insert row into" + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Uri not found:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase database = searchDataBaseHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);

        int rowDeleted;
        if ( null == selection)
            selection = "1";
        switch (match){
            case SEARCH:
                rowDeleted = database.delete(SearchContract.SearchEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SEARCH_ID:
                rowDeleted = database.delete(SearchContract.SearchEntry.TABLE_NAME, SearchContract.SearchEntry.COLUMN_TRACK_ID + " = ?", selectionArgs);
                break;
            default:
                throw  new UnsupportedOperationException("Uri not found:" + uri);
        }
        if (rowDeleted !=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase database = searchDataBaseHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);
        int rowsUpdated;
        if (values == null){
            throw new IllegalArgumentException("content values not found");
        }
        switch (match){
            case SEARCH:
                rowsUpdated = database.update(SearchContract.SearchEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SEARCH_ID:
                rowsUpdated = database.update(SearchContract.SearchEntry.TABLE_NAME, values, SearchContract.SearchEntry.COLUMN_TRACK_ID + "= ?" , selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Uri not found:" + uri);
        }
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
