package com.sunkin.itunessearch.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;
import com.sunkin.itunessearch.R;
import com.sunkin.itunessearch.data.SearchData;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kaika on 5/25/2017.
 */

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.photo)
    ImageView artImageView;
    @BindView(R.id.track_title)
    TextView titleText;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.video_preview)
    VideoView previewVideo;


    private SearchData searchData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_layout);
        ButterKnife.bind(this);

        searchData = getIntent().getExtras().getParcelable(Intent.EXTRA_TEXT);
        if (searchData != null) {
            Picasso.with(this)
                    .load(searchData.getArtworkUrl100().trim())
                    .noFade().placeholder(R.drawable.place_holder_image)
                    .error(R.drawable.error_loading_image)
                    .into(artImageView);
            titleText.setText(searchData.getTrackName());
            setTitle(searchData.getTrackName());
            price.setText(String.format("$%s", searchData.getTrackPrice()));
            previewVideo.setVideoURI(Uri.parse(searchData.getPreviewUrl()));
            previewVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                    previewVideo.start();
                }
            });
        }
    }
}
