package com.nothing.unnamedplayer;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class AlbumFragment extends Fragment {
    private GridView gridView;
    private MusicManager musicManager;

    public AlbumFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        gridView = view.findViewById(R.id.gvAlbum);
        AlbumListAdapter albumListAdapter = new AlbumListAdapter(musicManager.getStoredMusicList(),getContext());
        gridView.setAdapter(albumListAdapter);
        return view;
    }

    @Override
    public void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicManager = MusicManager.getInstance();
    }


}
