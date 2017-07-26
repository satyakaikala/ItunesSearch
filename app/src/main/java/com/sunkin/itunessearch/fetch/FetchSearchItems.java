package com.sunkin.itunessearch.fetch;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sunkin.itunessearch.ResponseHandler;
import com.sunkin.itunessearch.Utility;
import com.sunkin.itunessearch.data.SearchData;
import com.sunkin.itunessearch.data.SearchResponse;
import com.sunkin.itunessearch.network.ApiClient;
import com.sunkin.itunessearch.network.SearchNetworkInterface;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;

/**
 * Created by kaika on 6/17/2017.
 */

public class FetchSearchItems extends AsyncTask<String, Void, ArrayList<SearchData>> {

    private static final String TAG = FetchSearchItems.class.getSimpleName();
    private static SearchNetworkInterface searchNetwork;
    private ResponseHandler responseHandler;
    private Context context;

    public FetchSearchItems(ResponseHandler handler, Context context) {
        this.responseHandler = handler;
        searchNetwork = ApiClient.getClient().create(SearchNetworkInterface.class);
        this.context = context;

    }
//
//    public interface ResponseHandler {
//        void updateSearchResults(ArrayList<SearchData> searchData);
//
//        void searchStarted();
//
//        void searchCompleted();
//    }

    @Override
    protected ArrayList<SearchData> doInBackground(String... params) {

        ArrayList<SearchData> searchData = new ArrayList<>();
        String keyword = params[0];
        String entity = params[1];
        responseHandler.searchStarted();
        if (Utility.isOnline(context)) {
            Observable<SearchResponse> call = searchNetwork.getSearchResults(keyword, entity);
//            try {
//                searchData = call.execute().body().getResults();
//            } catch (IOException e) {
//                Log.e(TAG, e.getMessage());
//                e.printStackTrace();
//            }
        }
        return searchData;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        responseHandler.searchStarted();
    }

    @Override
    protected void onPostExecute(ArrayList<SearchData> searchData) {
        super.onPostExecute(searchData);
        Log.d(TAG, "Search completed");
        responseHandler.searchCompleted();
        responseHandler.updateSearchResults(searchData);
    }
}
