package com.nothing.unnamedplayer.Model;

import android.graphics.Bitmap;

public class Album {
    private Bitmap imgAlbum;
    private String albumName;

    public Album(Bitmap imgAlbum, String albumName) {
        this.imgAlbum = imgAlbum;
        this.albumName = albumName;
    }

    public Bitmap getImgAlbum() {
        return imgAlbum;
    }

    public void setImgAlbum(Bitmap imgAlbum) {
        this.imgAlbum = imgAlbum;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
    public Album(String albumName){
        this.albumName = albumName;
        imgAlbum = null;

    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        Album a = (Album) obj;
        if (albumName.compareTo(a.getAlbumName())==0) return true;
        else return false;
    }
}
