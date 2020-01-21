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

import com.nothing.unnamedplayer.Model.Music;

import java.util.ArrayList;

public class TrackFragment extends Fragment {

    private RecyclerView recyclerView;
    private MusicManager musicManager;
    private ArrayList<Music> current;

    public TrackFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_track, container, false);
        recyclerView = view.findViewById(R.id.track_recycler_view);
        TrackListAdapter trackListAdapter = new TrackListAdapter(current, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(trackListAdapter);
        return view;
    }

    @Override
    public void onCreate (@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicManager = MusicManager.getInstance();
        current = musicManager.getStoredMusicList();
        Log.d("TAG","Music List Size : "+musicManager.getMusicSize());
    }

}