package com.sunkin.itunessearch;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sunkin.itunessearch.data.SearchAdapter;
import com.sunkin.itunessearch.data.SearchData;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by kaika on 6/23/2017.
 */

public class FirebaseHelper {
    private static final String TAG = FirebaseHelper.class.getSimpleName();
    DatabaseReference databaseReference;
    Boolean saved = null;
    Boolean removed = null;
    private Context context;
    ArrayList<SearchData> searchData = new ArrayList<>();

    public FirebaseHelper(Context ctx, DatabaseReference db) {
        this.databaseReference = db;
        this.context = ctx;
    }

    //writing into database
    public Boolean save(SearchData saveItem) {
        if (saveItem == null) {
            Log.d(TAG, "no item to save");
            saved = false;
        } else {
            Log.d(TAG, "Item to save is :" + saveItem.getTrackName());
            try {
                databaseReference.child("SearchItemData").push().setValue(saveItem);
                saved = true;
            } catch (DatabaseException e) {
                e.printStackTrace();
                saved = false;
            }
        }
        return saved;
    }

    public Boolean itemAlreadyExists(SearchData item) {
    return false;
    }

    public Boolean delete(SearchData removeItem) {
        if (removeItem == null) {
            Log.d(TAG, "no item to remove");
            removed = false;
        } else {
            Log.d(TAG, "Found item to remove is : " + removeItem.getTrackName());
            Query removeItemQuery = databaseReference.child("SearchItemData")
                    .orderByChild("trackName").equalTo(removeItem.getTrackName());
            removeItemQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot revomeItemSnapshot : dataSnapshot.getChildren()) {
                        revomeItemSnapshot.getRef().removeValue();
                        removed = true;
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, "onCanceled", databaseError.toException());
                    removed = false;
                }
            });
        }
        return removed;
    }

    //read from database
    public ArrayList<SearchData> retrieve() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                retrieveData(dataSnapshot);
                Log.d(TAG, "Value added event triggered, updating data change");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return searchData;
    }

    private void retrieveData(DataSnapshot dataSnapshot) {
        searchData.clear();
        Iterable<DataSnapshot> snapshotIterable = dataSnapshot.getChildren();
        Iterator<DataSnapshot> snapshotIterator = snapshotIterable.iterator();
        while (snapshotIterator.hasNext()) {
            SearchData newFavData = snapshotIterator.next().getValue(SearchData.class);
           searchData.add(newFavData);
        }
//        for (DataSnapshot ds : dataSnapshot.getChildren()) {
//            String ArtworkUrl100 = ds.getValue(SearchData.class).getArtworkUrl100();
//            String ArtworkUrl30 = ds.getValue(SearchData.class).getArtworkUrl30();
//            String isFavorite = ds.getValue(SearchData.class).getIsFavorite();
//            String previewUrl = ds.getValue(SearchData.class).getPreviewUrl();
//            String trackCensoredName = ds.getValue(SearchData.class).getTrackCensoredName();
//            String trackName = ds.getValue(SearchData.class).getTrackName();
//            String trackPrice = ds.getValue(SearchData.class).getTrackPrice();
//            SearchData data = new SearchData(trackName, ArtworkUrl30,trackCensoredName,trackPrice,ArtworkUrl100,previewUrl,isFavorite);
//
//        }
    }
}