package com.nothing.unnamedplayer;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nothing.unnamedplayer.Model.Music;
import com.nothing.unnamedplayer.Util.musicInfoConverter;

import java.io.File;
import java.util.ArrayList;


import de.hdodenhof.circleimageview.CircleImageView;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.ViewHolder>{
    private MusicManager musicManager = MusicManager.getInstance();
    private int temp = 0;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView musicImage;
        TextView musicTitle;
        TextView musicArtist;
        TextView musicDuration;
        ImageView musicOption;
        ImageView isPlaying;
        RelativeLayout parent_layout;


        public ViewHolder(View itemView){
            super(itemView);
            musicImage = itemView.findViewById(R.id.musicImage);
            musicTitle = itemView.findViewById(R.id.musicTitle);
            musicArtist = itemView.findViewById(R.id.musicArtist);
            musicDuration = itemView.findViewById(R.id.musicDuration);
            parent_layout = itemView.findViewById(R.id.track_list);
            musicOption = itemView.findViewById(R.id.musicOption);
            isPlaying = itemView.findViewById(R.id.imgIsPlay);

        }
    }

    private ArrayList<Music> musicList;
    private Context mContext;

    public TrackListAdapter(ArrayList<Music> musiclist, Context context) {
        musicList = musiclist;
        mContext = context;
    }

    public TrackListAdapter() {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_listitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder,final int i) {
        final Music m = musicList.get(i);
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

        //Cái này đề phòng thôi
        viewHolder.musicOption.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                PopupMenu popup = new PopupMenu(mContext,view);
                popup.getMenuInflater().inflate(R.menu.menu_trackitem,popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item){
                        switch(item.getItemId()){
                            case R.id.trackitem_menu_addlist:
                                Toast.makeText(mContext,"Clicked: 'Nút vô dụng'",Toast.LENGTH_SHORT);
                                break;
                            case R.id.trackitem_menu_delete:
                                //Xóa lúc được lúc ko
                                Toast.makeText(mContext.getApplicationContext(),"Clicked: 'Xóa'",Toast.LENGTH_SHORT);
                                File to_delete = new File(m.getMusicPath());
                                if (to_delete.exists()){
                                    //to_delete.delete();
                                    musicList.remove(i);
                                    Log.d("Lỗi gì đó", "Path: "+m.getMusicPath());
                                }
                                break;

                            default:
                                break;
                        }
                        return true;
                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }
}
