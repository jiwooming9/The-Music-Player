package com.nothing.unnamedplayer;

import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ArtistFragment extends Fragment {

    private RecyclerView recyclerView;
    private MusicManager musicManager;

    public ArtistFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        recyclerView = view.findViewById(R.id.rvArtist);
        ArtistListAdapter artistListAdapter = new ArtistListAdapter(musicManager.getArtistSortMusicList() ,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(artistListAdapter);
        return view;
    }

    @Override
    public void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicManager = MusicManager.getInstance();
        musicManager.setArtistSortMusicList(musicManager.getStoredMusicList());
        musicManager.sortArtistList();
    }

}