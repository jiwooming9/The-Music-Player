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

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder>{

    private MusicManager musicManager = MusicManager.getInstance();
    private ArrayList<Music> musicList;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgPlListImg;
        TextView txPlListTitle;
        TextView txPlListArtist;
        TextView txPlListDur;
        ImageView imgPlListIsPlay;
        RelativeLayout rlListPlaylist;


        public ViewHolder(View itemView){
            super(itemView);
            imgPlListImg = itemView.findViewById(R.id.imgPlListImg);
            txPlListTitle = itemView.findViewById(R.id.txPlListTitle);
            txPlListArtist = itemView.findViewById(R.id.txPlListArtist);
            txPlListDur = itemView.findViewById(R.id.txPlListDur);
            rlListPlaylist = itemView.findViewById(R.id.rlListPlaylist);
            imgPlListIsPlay = itemView.findViewById(R.id.imgPlListIsPlay);

        }
    }
    public PlaylistAdapter(ArrayList<Music> musiclist, Context context) {
        musicList = musiclist;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_playlist, parent, false);
       ViewHolder holder = new PlaylistAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder viewHolder, final int i) {
        final Music m = musicList.get(i);
        Music curM = new Music();
        try {
            curM = musicManager.getMusicByIndex(musicManager.getCurrentIndex());
        } catch (Exception e) {

        }
        Glide.with(mContext)
                .load(m.getMusicImage())
                .placeholder(R.drawable.ic_music_basic)
                .into(viewHolder.imgPlListImg);
        viewHolder.txPlListTitle.setText(m.getMusicTitle());
        viewHolder.txPlListArtist.setText(m.getMusicArtist());
        viewHolder.txPlListDur.setText(musicInfoConverter.durationConvert(m.getMusicDuration()));
        if (curM != null && curM.getMusicIndex() == m.getMusicIndex()) {
            Glide.with(mContext).asGif().load(R.raw.playing).into(viewHolder.imgPlListIsPlay);
        }
        viewHolder.rlListPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Chuyển intent service
                musicManager.setCurrentMusicList(musicList);
                Intent in = new Intent(mContext, PlayActivity.class);
                in.putExtra("Index", i);
                mContext.startActivity(in);

            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }


}
