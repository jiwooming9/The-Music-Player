package com.nothing.unnamedplayer;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.nothing.unnamedplayer.Model.Album;
import com.nothing.unnamedplayer.Model.Music;

import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MusicManager musicManager;
    private ArrayList<Music> currentStoredList;
    private ArrayList<Music> checkList;
    private ArrayList<Music> currentPlayingList;
    private ImageButton btnPlBack;
    private TextView txLabelPlaylistl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        btnPlBack = findViewById(R.id.btnPlBack);
        txLabelPlaylistl = findViewById(R.id.txLabelPlaylist);
        btnPlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        musicManager = MusicManager.getInstance();
        currentStoredList = musicManager.getStoredMusicList();
        currentPlayingList = musicManager.getCurrentMusicList();
        checkList = new ArrayList<>();
        Intent intentPList = getIntent();
        int ialbum = intentPList.getIntExtra("ALBUM", -1);
        int iartist = intentPList.getIntExtra("ARTIST", -1);
        int iplayer = intentPList.getIntExtra("PLAYER", -1);
        if (ialbum > -1){
            intentPList.removeExtra("ALBUM");
            ArrayList<Album> album = musicManager.getListAlbum();
            Album albumTarget = album.get(ialbum);
            txLabelPlaylistl.setText(albumTarget.getAlbumName());
            for (Music m : currentStoredList){
                if (m.getMusicAlbum().compareTo(albumTarget.getAlbumName())==0) {
                    checkList.add(m);
                }
            }
        } else if (iartist > -1){
            intentPList.removeExtra("ARTIST");
            //Xu ly artist
        } else if (iplayer > -1){
            intentPList.removeExtra("PLAYER");
            txLabelPlaylistl.setText("Đang chơi");
            checkList = currentPlayingList;
        }

        recyclerView = findViewById(R.id.rvPlList);
        PlaylistAdapter playlistAdapter = new PlaylistAdapter(checkList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(playlistAdapter);
    }


}
