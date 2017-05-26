package com.sunkin.itunessearch.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kaika on 5/25/2017.
 */

public class SearchData implements Parcelable {

    @SerializedName("trackName")
    private String trackName;

    @SerializedName("artworkUrl30")
    private String artworkUrl30;

    @SerializedName("trackCensoredName")
    private String trackCensoredName;

    @SerializedName("trackPrice")
    private String trackPrice;

    @SerializedName("artworkUrl100")
    private String artworkUrl100;

    @SerializedName("previewUrl")
    private String previewUrl;

    protected SearchData(Parcel in) {
        trackName = in.readString();
        artworkUrl30 = in.readString();
        trackCensoredName = in.readString();
        trackPrice = in.readString();
        artworkUrl100 = in.readString();
        previewUrl = in.readString();
    }

    public static final Creator<SearchData> CREATOR = new Creator<SearchData>() {
        @Override
        public SearchData createFromParcel(Parcel in) {
            return new SearchData(in);
        }

        @Override
        public SearchData[] newArray(int size) {
            return new SearchData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(trackName);
        dest.writeString(artworkUrl30);
        dest.writeString(trackCensoredName);
        dest.writeString(trackPrice);
        dest.writeString(artworkUrl100);
        dest.writeString(previewUrl);
    }

    @Override
    public String toString() {
        return "SearchData{" +
                "trackName='" + trackName + '\'' +
                ", artworkUrl30='" + artworkUrl30 + '\'' +
                ", trackCensoredName='" + trackCensoredName + '\'' +
                ", trackPrice='" + trackPrice + '\'' +
                ", artworkUrl100='" + artworkUrl100 + '\'' +
                ", previewUrl='" + previewUrl + '\'' +
                '}';
    }
}
