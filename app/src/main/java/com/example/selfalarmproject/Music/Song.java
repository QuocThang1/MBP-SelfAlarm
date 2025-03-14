package com.example.selfalarmproject.Music;

public class Song {
    private String title;
    private String artist;
    private int resourceId;
    private int backgroundResourceId;

    public Song(String title, String artist, int resourceId, int backgroundResourceId) {
        this.title = title;
        this.artist = artist;
        this.resourceId = resourceId;
        this.backgroundResourceId = backgroundResourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getResourceId() {
        return resourceId;
    }

    public int getBackgroundResourceId() {
        return backgroundResourceId;
    }
}