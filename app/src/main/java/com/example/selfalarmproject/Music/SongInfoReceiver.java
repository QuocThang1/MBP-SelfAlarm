package com.example.selfalarmproject.Music;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;


public class SongInfoReceiver extends BroadcastReceiver {
    private static TextView tvSongTitle;
    private static TextView tvArtist;
    private static ImageView songImageView;


    public SongInfoReceiver() {

    }


    public static void setTextViews(TextView songTitle, TextView artist) {
        tvSongTitle = songTitle;
        tvArtist = artist;
    }

    public static void setImageView(ImageView imageView) {
        songImageView = imageView;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.example.selfalarmproject.SONG_INFO_UPDATED")) {
            String title = intent.getStringExtra("title");
            String artist = intent.getStringExtra("artist");
            int backgroundId = intent.getIntExtra("backgroundId", 0);

            tvSongTitle.setText(title);
            tvArtist.setText(artist);
            songImageView.setImageResource(backgroundId);
        }
    }
}