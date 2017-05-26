package com.sunkin.itunessearch.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by kaika on 5/25/2017.
 */

public class SearchResponse {

    @SerializedName("results")
    private ArrayList<SearchData> results;
    @SerializedName("resultCount")
    private int resultCount;

    public ArrayList<SearchData> getResults() {
        return results;
    }

    public void setResults(ArrayList<SearchData> results) {
        this.results = results;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }
}
