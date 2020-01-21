package com.nothing.unnamedplayer;

import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nothing.unnamedplayer.Model.Album;
import com.nothing.unnamedplayer.Model.Music;

import java.util.ArrayList;

public class AlbumListAdapter extends BaseAdapter {
    private MusicManager musicManager = MusicManager.getInstance();

    private ArrayList<Album> albumList;
    private ArrayList<Music> musicList;
    private Context mContext;
    private LayoutInflater layoutInflater;

    public AlbumListAdapter(ArrayList<Music> musiclist, Context context) {
        musicList = musiclist;
        mContext = context;
        layoutInflater = LayoutInflater.from(context);
        albumList = musicManager.getListAlbum();

    }

    public AlbumListAdapter() {

    }

    static class ViewHolder {
        ImageView imgAlbumImg;
        TextView txtAlbumName;
    }

    @Override
    public int getCount() {
        return albumList.size();
    }

    @Override
    public Object getItem(int i) {
        return albumList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.album_listitem, null);
            holder = new ViewHolder();
            holder.imgAlbumImg = convertView.findViewById(R.id.imgAlbumImg);
            holder.txtAlbumName = convertView.findViewById(R.id.txAlbumAlbum);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Album album = this.albumList.get(i);
        holder.txtAlbumName.setText(album.getAlbumName());
        Glide.with(mContext)
                .load(album.getImgAlbum())
                .placeholder(R.drawable.ic_music_basic)
                .into(holder.imgAlbumImg);
        holder.imgAlbumImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent switchPlaylist = new Intent(mContext, PlaylistActivity.class);
                switchPlaylist.putExtra("ALBUM", i);
                mContext.startActivity(switchPlaylist);
            }
        });

        return convertView;
    }


}
