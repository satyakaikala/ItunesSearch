package com.sunkin.itunessearch.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sunkin.itunessearch.R;
import com.sunkin.itunessearch.database.SearchContract;

import static com.sunkin.itunessearch.database.SearchContract.SearchEntry.COLUMN_TRACK_ARTIST_NAME;
import static com.sunkin.itunessearch.database.SearchContract.SearchEntry.COLUMN_TRACK_NAME;
import static com.sunkin.itunessearch.database.SearchContract.SearchEntry.COLUMN_TRACK_PRICE;
import static com.sunkin.itunessearch.database.SearchContract.SearchEntry.COL_TRACK_ID;
import static com.sunkin.itunessearch.database.SearchContract.SearchEntry.TABLE_NAME;

/**
 * Created by skai0001 on 4/2/17.
 */

public class DetailWidgetRemoteViewService extends RemoteViewsService {

    private static final String[] SEARCH_COLUMNS =
            {
                    TABLE_NAME + "." + SearchContract.SearchEntry._ID,
                    COLUMN_TRACK_NAME,
                    COLUMN_TRACK_PRICE,
                    COLUMN_TRACK_ARTIST_NAME,
            };

    public static final int COL_ID = 0;
    public static final int COL_TRACK_NAME = 1;
    public static final int COL_TRACK_PRICE = 2;
    public static final int COL_TRACK_ARTIST_NAME = 3;


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                final long identityToken = Binder.clearCallingIdentity();
                Uri stockUri = SearchContract.SearchEntry.getUriForFav();
                data = getContentResolver().query(stockUri,
                        SEARCH_COLUMNS,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0: data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews remoteViews = new RemoteViews(getPackageName(),
                        R.layout.widget_list_item);
                String trackName = data.getString(COL_TRACK_NAME);
                String trackPrice = data.getString(COL_TRACK_PRICE);
                String trackArtistName = data.getString(COL_TRACK_ARTIST_NAME);

                remoteViews.setTextViewText(R.id.widget_track_name, trackName);
                remoteViews.setTextViewText(R.id.widget_track_price, String.format("$%s", trackPrice));
                remoteViews.setTextViewText(R.id.widget_track_artistName, String.format("By %s", trackArtistName));

                final Intent fillIntent = new Intent();

                Uri favUri = SearchContract.SearchEntry.getUriForFav();
                fillIntent.setData(favUri);
                remoteViews.setOnClickFillInIntent(R.id.widget_list, fillIntent);
                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                if (data.moveToPosition(i))
                    return data.getLong(COL_ID);
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
