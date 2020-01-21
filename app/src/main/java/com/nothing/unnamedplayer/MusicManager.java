package com.nothing.unnamedplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import android.util.Log;

import com.nothing.unnamedplayer.Model.Album;
import com.nothing.unnamedplayer.Model.Music;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


class MusicManager {
    private static MusicManager musicManager = null;

    private ArrayList<Music> storedMusicList;   //list đã lưu
    private ArrayList<Music> currentMusicList;  //list hiện tại
    private ArrayList<Music> artistSortMusicList; //list theo artist

    ArrayList<Album> listAlbum = new ArrayList<>();

    private MediaPlayer mediaPlayer = new MediaPlayer();

    public boolean isShuffling = false; //check shuffle

    private int currentIndex;   // vị trí hiện tại

    public int getCurrentIndex(){
        return currentIndex;
    }
    public void setCurrentIndex(int i){
        currentIndex = i;
    }

    public void setShuffling(boolean b) {
        isShuffling = b;
    }

    public MediaPlayer createAndGetMusicPlayer(){ //khởi tạo ban đầu
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer();
        return mediaPlayer;
    }

    public int getMusicSize() {  //lấy số bài
        return currentMusicList.size();
    }

    private MusicManager() {
        storedMusicList = new ArrayList<>();
        currentMusicList = new ArrayList<>();
        artistSortMusicList = new ArrayList<>();
    }

    public static MusicManager getInstance() { //getMediaplay
        if (musicManager == null) {
            musicManager = new MusicManager();
        }
        return musicManager;
    }

    public MediaPlayer getMusicPlayer(){
        return mediaPlayer;
    }
    public Music getMusicByIndex(int i) { // lấy vị trí
        return currentMusicList.get(i);
    }

    public void setCurrentMusicList(ArrayList<Music> currentMusicList) {
        this.currentMusicList = currentMusicList;
    }

    public ArrayList<Music> getCurrentMusicList() {
        return currentMusicList;
    }
    public ArrayList<Music> getStoredMusicList() {
        return storedMusicList;
    }
    public ArrayList<Music> getArtistSortMusicList(){
        return artistSortMusicList;
    }
    public void setArtistSortMusicList(ArrayList<Music> listtoset){
        artistSortMusicList = listtoset;
    }

    public ArrayList<Album> getListAlbum(){
        return listAlbum;
    }


    //quét bộ nhớ
    public void initMusicList(Context context) {
            if (storedMusicList.size() != 0)
                storedMusicList.clear();

        ContentResolver contentResolver = context.getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI; //content URI bang AudioMedia
        Cursor songCursor = contentResolver.query(songUri, null, MediaStore.Audio.Media.DURATION + ">= 20000", null, MediaStore.Audio.Media.TITLE);

        if (songCursor != null && songCursor.moveToFirst()) {
            int titleIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artistIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int albumIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int durationIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int pathIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                Log.d("Chỗ này bị", "Quét den : " + songCursor.getPosition());
                String currentTitle = songCursor.getString(titleIndex);
                String currentArtist = songCursor.getString(artistIndex);
                String currentAlbum = songCursor.getString(albumIndex);
                int currentDuration = songCursor.getInt(durationIndex); //milliseconds
                String currentPath = songCursor.getString(pathIndex);

                //Lấy ảnh cover
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                try{ //Android P bị sao ấy
                    mmr.setDataSource(currentPath);
                } catch (Exception e){
                    Log.d("ANDROID P", e.toString());
                }

                byte[] data = mmr.getEmbeddedPicture();
                Bitmap bitmap = null;
                if (data != null) {
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                }
                storedMusicList.add(new Music(currentTitle, currentArtist, currentAlbum, currentDuration, currentPath, bitmap, songCursor.getPosition()));
                Album tempAlbum = new Album(currentAlbum);   //Lấy list album
                if (!listAlbum.contains(tempAlbum)){
                    listAlbum.add(new Album(bitmap, currentAlbum));
                }
            } while (songCursor.moveToNext());
        }
    }

    //Reset list hiện tại
    public void resetMusicList(){
        currentMusicList.clear();
        for (Music m : storedMusicList){
            currentMusicList.add(m.clone());
        }
    }

    // chế độ shuffle
    public void shuffleList() {
        if (isShuffling == true) {
            Collections.sort(currentMusicList, new Comparator<Music>() {
                @Override
                public int compare(Music m1, Music m2) {
                    if (m1.getMusicTitle().compareTo(m2.getMusicTitle())>=0) return 1;
                    else return -1;
                }
            });
            isShuffling = false;
        } else {
            Collections.shuffle(currentMusicList);
            isShuffling = true;
        }
    }
    public int getPositionByIdx(int idx){
        for (Music m : currentMusicList){
            int songIdx = m.getMusicIndex();
            if (idx == songIdx)
                return songIdx;
        }
        return -1;
    }

    public void sortArtistList(){
        Collections.sort(artistSortMusicList, new Comparator<Music>() {
            @Override
            public int compare(Music m1, Music m2) {
                if (m1.getMusicArtist().compareTo(m2.getMusicArtist())>=0) return 1;
                else return -1;
            }
        });
    }

}
