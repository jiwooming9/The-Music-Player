package com.nothing.unnamedplayer;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nothing.unnamedplayer.Model.Music;
import com.nothing.unnamedplayer.Util.musicInfoConverter;

import java.util.ArrayList;


import de.hdodenhof.circleimageview.CircleImageView;

public class ArtistListAdapter extends RecyclerView.Adapter<ArtistListAdapter.ViewHolder>{
    private MusicManager musicManager = MusicManager.getInstance();
    private String tempArtist = "Khoi tao nek";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView artistName;
        CircleImageView musicImage;
        TextView musicTitle;
        TextView musicArtist;
        TextView musicDuration;
        ImageView musicOption;
        ImageView isPlaying;
        RelativeLayout parent_layout;


        public ViewHolder(View itemView){
            super(itemView);
            artistName = itemView.findViewById(R.id.txArtistName);
            musicImage = itemView.findViewById(R.id.musicImage);
            musicTitle = itemView.findViewById(R.id.musicTitle);
            musicArtist = itemView.findViewById(R.id.musicArtist);
            musicDuration = itemView.findViewById(R.id.musicDuration);
            parent_layout = itemView.findViewById(R.id.rlArtist);
            musicOption = itemView.findViewById(R.id.musicOption);
            isPlaying = itemView.findViewById(R.id.imgIsPlay);

        }
    }
    private ArrayList<Music> musicList;
    private Context mContext;

    public ArtistListAdapter(ArrayList<Music> musiclist, Context context) {
        musicList = musiclist;
        mContext = context;
    }

    public ArtistListAdapter() {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder,final int i) {
        final Music m = musicList.get(i);
        String artistIReum = m.getMusicArtist();
        if (tempArtist.compareTo(artistIReum)!=0){    //đoạn này in tên ca sĩ trên view
            viewHolder.artistName.setText(artistIReum);
        } else{
            viewHolder.artistName.setVisibility(View.GONE);
        }
        tempArtist = artistIReum;
        Glide.with(mContext)
                .load(musicList.get(i).getMusicImage())
                .placeholder(R.drawable.ic_music_basic)
                .into(viewHolder.musicImage);

        viewHolder.musicTitle.setText(m.getMusicTitle());
        viewHolder.musicArtist.setText(m.getMusicArtist());
        viewHolder.musicDuration.setText(musicInfoConverter.durationConvert(m.getMusicDuration()));
        viewHolder.parent_layout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Chuyển intent service
                musicManager.resetMusicList();
                musicManager.setShuffling(false);
                Intent in = new Intent(mContext,PlayActivity.class);
                in.putExtra("Index",i);
                mContext.startActivity(in);
            }
        });

    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }
}
