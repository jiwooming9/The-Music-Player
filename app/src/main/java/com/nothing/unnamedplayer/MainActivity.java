package com.nothing.unnamedplayer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;

import com.google.android.material.tabs.TabLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatSeekBar;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nothing.unnamedplayer.Model.Music;
import com.nothing.unnamedplayer.Util.Actions;

//Anh Nguyen 01/15/2020

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSION_REQUEST = 1;
    BroadcastReceiver bReceiver;
    Handler barHandler;           //      xử lý seekbar
    Runnable barRunnable;          //
    private Intent mainIntent;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MusicManager musicManager;
    private MediaPlayer mediaPlayer;
    private ImageButton imgSongTool;
    private TextView txtToolTitle;
    private TextView txtToolArtist;
    private TextView txtToolAlbum;
    private AppCompatSeekBar sbToolBar;
    private ImageButton btnPlayBar;
    private ImageButton btnBarNext;
    private ImageButton btnBarPrev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        musicManager = MusicManager.getInstance();
        mediaPlayer = musicManager.getMusicPlayer();
        Log.d("TAG", "Here");
        checkPermission();
        tabLayout = (TabLayout) findViewById(R.id.main_tablayout);
        viewPager = (ViewPager) findViewById(R.id.main_pager);
        imgSongTool = findViewById(R.id.tool_image);
        txtToolTitle = findViewById(R.id.tool_title);
        txtToolArtist = findViewById(R.id.tool_artist);
        txtToolAlbum = findViewById(R.id.tool_album);
        sbToolBar = findViewById(R.id.tool_seekbar);
        btnBarNext = findViewById(R.id.btnBarNext);
        btnBarPrev = findViewById(R.id.btnBarPrev);
        btnPlayBar = findViewById(R.id.btnBarPlay);

        if (musicManager.getMusicSize()==0){
            musicManager.initMusicList(this);
        }
        ViewPagerAdapter adaptor = new ViewPagerAdapter(getSupportFragmentManager());
        adaptor.AddFragment(new TrackFragment(), "Bài hát");
        adaptor.AddFragment(new ArtistFragment(), "Nghệ sĩ");
        adaptor.AddFragment(new AlbumFragment(), "Album");

        viewPager.setAdapter(adaptor);
        tabLayout.setupWithViewPager(viewPager);

        barHandler = new Handler();

        registerReceiver();
        if(musicManager.getMusicSize()!=0) {
            updateBarDisplay(musicManager.getCurrentIndex());
        }
        initToolBar();
        playBarCycle();


        imgSongTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnBarNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ci = musicManager.getCurrentIndex();
                updateBarDisplay((ci + 1) % musicManager.getMusicSize());
                goPlayService(Actions.ACTION_NEXT);
            }
        });

        btnBarPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int ci = musicManager.getCurrentIndex();
                ci -= 1;
                if (ci == -1) ci = musicManager.getMusicSize() - 1;
                updateBarDisplay(ci);
                goPlayService(Actions.ACTION_PREV);
            }
        });

        btnPlayBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    btnPlayBar.setImageResource(R.drawable.ic_pause_black_24dp);
                    goPlayService(Actions.ACTION_PAUSE);
                } else {
                    btnPlayBar.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    goPlayService(Actions.ACTION_RESUME);
                    playBarCycle();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "Đã phân quyền.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Chưa phân quyền.", Toast.LENGTH_SHORT).show();
                    }
                }
        }
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
                }
            } else {

            }
        }
    }
    public void onClick(View v)
    {

    }

    public void updateBarDisplay(int in){
        Music temp = musicManager.getMusicByIndex(in);
        if (in >= 0 && in < musicManager.getMusicSize()) {
            Glide.with(this)
                    .load(temp.getMusicImage())
                    .placeholder(R.drawable.ic_music_basic)
                    .into(imgSongTool);

            txtToolTitle.setText(temp.getMusicTitle());
            txtToolArtist.setText(temp.getMusicArtist());
            txtToolAlbum.setText(temp.getMusicAlbum());
            sbToolBar.setProgress(0);
            sbToolBar.setMax(temp.getMusicDuration());
            sbToolBar.setKeyProgressIncrement(1000);
            if (mediaPlayer.isPlaying()) {
                btnPlayBar.setImageResource(R.drawable.ic_pause_black_24dp);
            } else {
                btnPlayBar.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            }

        }

    }

    private void registerReceiver() {
        bReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateBarDisplay(musicManager.getCurrentIndex());
            }
        };
        registerReceiver(bReceiver, new IntentFilter(Actions.ACTION_UPDATE));
    }

    private void initToolBar() {
        sbToolBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekbar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekbar) {
                mediaPlayer.seekTo(sbToolBar.getProgress());
            }
        });
    }

    public void goPlayService(String action) {
        mainIntent = new Intent(this, PlayerService.class);
        mainIntent.setAction(action);
        startService(mainIntent);
    }

    public void playBarCycle() {
        if (musicManager.getMusicPlayer() == null)
            return;
        barRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    sbToolBar.setProgress(Math.min(sbToolBar.getMax(), mediaPlayer.getCurrentPosition()));
                    playBarCycle();
                }
            }
        };
        barHandler.postDelayed(barRunnable, 100);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        barHandler.removeCallbacks(barRunnable);
        unregisterReceiver(bReceiver);
    }
}

