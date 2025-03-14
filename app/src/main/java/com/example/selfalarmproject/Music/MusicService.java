package com.example.selfalarmproject.Music;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.example.selfalarmproject.R;

public class MusicService extends Service {
    MediaPlayer mymusic;
    private int currentSongId = R.raw.motbaihatkhongvuimay;
    private String currentSongTitle = "";
    private String currentSongArtist = "";
    private int currentBackgroundId = R.drawable.motbaihatkhongvuimay;
    private List<Song> songList;
    private int currentSongIndex = 0;
    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializeSongList();
    }

    private void initializeSongList() {
        songList = new ArrayList<>();
        songList.add(new Song("Một bài hát không vui mấy", "Trí", R.raw.motbaihatkhongvuimay, R.drawable.motbaihatkhongvuimay));
        songList.add(new Song("Mất kết nối", "Dương Domic", R.raw.matketnoi, R.drawable.matketnoi));
        songList.add(new Song("Giờ thì", "Bùi Trường Linh", R.raw.giothi, R.drawable.giothi));
        songList.add(new Song("Hư không", "Khánh", R.raw.hukhong, R.drawable.hukhong));
        songList.add(new Song("Wrong Times", "dangrangto ft puppy", R.raw.wrongtime, R.drawable.wrongtimes));
    }

    private int findSongIndexById(int resourceId) {
        for (int i = 0; i < songList.size(); i++) {
            if (songList.get(i).getResourceId() == resourceId) {
                return i;
            }
        }
        return 0; // Default to first song if not found
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Check bài hát trong SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MusicPrefs", MODE_PRIVATE);
        int selectedSongId = prefs.getInt("selectedSongId", R.raw.motbaihatkhongvuimay);
        String selectedSongTitle = prefs.getString("selectedSongTitle", "Không có tên");
        String selectedSongArtist = prefs.getString("selectedSongArtist", "Không có nghệ sĩ");
        int selectedBackgroundId = prefs.getInt("selectedBackgroundId", R.drawable.motbaihatkhongvuimay);

        Log.d("ID bai hat", String.valueOf(selectedSongId));
        currentSongIndex = findSongIndexById(selectedSongId);
        String action = intent.getStringExtra("action");

        if ("PREVIOUS".equals(action)) {
            Log.d("MusicService", "PREVIOUS button pressed, changing to previous song");
            currentSongIndex = (currentSongIndex - 1 + songList.size()) % songList.size();
            Song previousSong = songList.get(currentSongIndex);

            // Update preferences with the new song
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("selectedSongId", previousSong.getResourceId());
            editor.putString("selectedSongTitle", previousSong.getTitle());
            editor.putString("selectedSongArtist", previousSong.getArtist());
            editor.putInt("selectedBackgroundId", previousSong.getBackgroundResourceId());
            editor.apply();

            // Force the media player to change
            if (mymusic != null) {
                if (mymusic.isPlaying()) {
                    mymusic.stop();
                }
                mymusic.release();
                mymusic = null;
            }

            mymusic = MediaPlayer.create(this, previousSong.getResourceId());
            mymusic.setLooping(true);
            currentSongId = previousSong.getResourceId();
            currentSongTitle = previousSong.getTitle();
            currentSongArtist = previousSong.getArtist();
            currentBackgroundId = previousSong.getBackgroundResourceId();
        }

        if ("NEXT".equals(action)) {
            Log.d("MusicService", "NEXT button pressed, changing to next song");
            currentSongIndex = (currentSongIndex + 1) % songList.size();
            Song nextSong = songList.get(currentSongIndex);

            // Update preferences with the new song
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("selectedSongId", nextSong.getResourceId());
            editor.putString("selectedSongTitle", nextSong.getTitle());
            editor.putString("selectedSongArtist", nextSong.getArtist());
            editor.putInt("selectedBackgroundId", nextSong.getBackgroundResourceId());
            editor.apply();

            // Force the media player to change
            if (mymusic != null) {
                if (mymusic.isPlaying()) {
                    mymusic.stop();
                }
                mymusic.release();
                mymusic = null;
            }

            mymusic = MediaPlayer.create(this, nextSong.getResourceId());
            mymusic.setLooping(true);
            currentSongId = nextSong.getResourceId();
            currentSongTitle = nextSong.getTitle();
            currentSongArtist = nextSong.getArtist();
            currentBackgroundId = nextSong.getBackgroundResourceId();
        }

        if ((mymusic != null && currentSongId != selectedSongId) || "CHANGE_SONG".equals(action)) {
            if (mymusic != null && mymusic.isPlaying()) {
                mymusic.stop();
            }

            //Giải phóng media player
            if (mymusic != null) {
                mymusic.release();
                mymusic = null;
            }

            // Create new media player
            mymusic = MediaPlayer.create(MusicService.this, selectedSongId);
            mymusic.setLooping(true);
            currentSongId = selectedSongId;
            currentSongTitle = selectedSongTitle;
            currentSongArtist = selectedSongArtist;
            currentBackgroundId = selectedBackgroundId;


        }

        // Khởi tạo MediaPlayer nếu cần
        if (mymusic == null) {
            mymusic = MediaPlayer.create(MusicService.this, selectedSongId);
            mymusic.setLooping(true);
            currentSongId = selectedSongId;
            currentSongTitle = selectedSongTitle;
            currentSongArtist = selectedSongArtist;
            currentBackgroundId = selectedBackgroundId;

        }

        if ("REPEAT".equals(action)) {
            if (mymusic != null) {
                if (mymusic.isPlaying()) {
                    mymusic.stop();
                }
                mymusic.release();
                mymusic = null;
            }
            mymusic = MediaPlayer.create(MusicService.this, selectedSongId);
            mymusic.setLooping(true);
        }

        if ("PLAY_PAUSE".equals(action)) {
            if (mymusic.isPlaying())
                mymusic.pause();
            else
                mymusic.start();
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        if (mymusic != null) {
            if (mymusic.isPlaying()) {
                mymusic.stop();
            }
            mymusic.release();
            mymusic = null;
        }
        super.onDestroy();
    }
}