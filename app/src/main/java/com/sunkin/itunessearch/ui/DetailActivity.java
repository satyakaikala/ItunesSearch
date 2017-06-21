package com.sunkin.itunessearch.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;
import com.sunkin.itunessearch.R;
import com.sunkin.itunessearch.Utility;
import com.sunkin.itunessearch.data.SearchData;

import java.util.Objects;

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
    @BindView(R.id.artistName)
    TextView artistName;
    @BindView(R.id.video_preview)
    VideoView previewVideo;
    @BindView(R.id.play_button)
    Button playButton;
    @BindView(R.id.pause_button)
    Button pauseButton;
    @BindView(R.id.share_button)
    Button shareButton;
    @BindView(R.id.buttons_layout)
    LinearLayout buttonsLayout;

    private SearchData searchData;
    private MediaController mediaController;
    private static final String TRACK = "track";

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
            artistName.setText(String.format("By %s", searchData.getArtistName()));
            if (TRACK.equals(searchData.getWrapperType())) {
                buttonsLayout.setVisibility(View.VISIBLE);
                handleButtons();
            }
        }
    }

    private void handleButtons() {
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchData.getPreviewUrl() != null) {
                    playPreview();
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (previewVideo.isPlaying())
                pausePreview();
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareButton();
            }
        });
    }

    private void shareButton() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Found Track via Itunes Search");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hey ! I've found an awesome track '"
                + searchData.getTrackName()+"'"
                + " By '" + searchData.getArtistName()+ "'"
                + ". Found using Itunes Search App.");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private void pausePreview() {
        previewVideo.pause();
    }


    private void playPreview() {
        try {
            mediaController = new MediaController(this);
            mediaController.setAnchorView(previewVideo);
            previewVideo.setVideoURI(Uri.parse(searchData.getPreviewUrl()));
            previewVideo.start();
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
    }
}
