package com.sunkin.itunessearch;

import com.sunkin.itunessearch.data.SearchData;

import java.util.ArrayList;

/**
 * Created by kaika on 7/19/2017.
 */

public interface ResponseHandler {

    void updateSearchResults(ArrayList<SearchData> searchData);

    void searchStarted();

    void searchCompleted();
}
