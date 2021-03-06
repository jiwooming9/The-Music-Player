package com.nothing.unnamedplayer.Model;

import android.graphics.Bitmap;
import androidx.annotation.Nullable;

public class Music {
    private String musicTitle;
    private String musicAlbum;
    private String musicArtist;
    private int musicDuration;
    private String musicPath;
    private Bitmap musicImage;
    private int musicIndex; //Vị trí bài nhạc trong list

    public Music( String title, @Nullable String artist, @Nullable String album, int duration, String path, @Nullable Bitmap image, int originalIdx) {
        musicTitle = title;
        musicAlbum = album;
        musicArtist = artist;
        musicDuration = duration;
        musicPath = path;
        musicImage = image;
        musicIndex = originalIdx;
    }
    public Music(){
        musicIndex = -10;
    }

    public String getMusicAlbum() {
        return musicAlbum;
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public String getMusicArtist() {
        return musicArtist;
    }

    public int getMusicDuration() {
        return musicDuration;
    }

    public String getMusicPath() {
        return musicPath;
    }

    public int getMusicIndex() { return musicIndex; }

    public Bitmap getMusicImage() {
        return musicImage;
    }

    @Override
    public Music clone(){
        String mt = this.musicTitle;
        String mal = this.musicAlbum;
        String mat = this.musicArtist;
        int md = this.musicDuration;
        String mp = this.musicPath;
        Bitmap mimg = this.musicImage;
        int midx = this.musicIndex;
        return new Music(mt,mat,mal,md,mp,mimg,midx);
    }
}
