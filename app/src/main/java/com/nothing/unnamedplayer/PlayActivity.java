package com.nothing.unnamedplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.nothing.unnamedplayer.Model.Music;
import com.nothing.unnamedplayer.Util.Actions;
import com.nothing.unnamedplayer.Util.musicInfoConverter;


public class PlayActivity extends AppCompatActivity {
    private TextView musicTitle;
    private TextView musicArtist;
    private TextView musicAlbum;
    private TextView musicDuration;
    private ImageView musicImage;

    private MusicManager musicManager;
    private MediaPlayer mediaPlayer;
    private Intent serviceIntent;

    private TextView currentDurationText;

    private ImageButton repeatButton;
    private ImageButton shuffleButton;
    int playButtonIcon, playButtonBackgroundColor;
    private FloatingActionButton playButton;

    private AppCompatSeekBar durationBar;
    private AppCompatSeekBar speedBar;

    private ImageButton btnList;
    private ImageButton btnBack;

    Runnable runnable;
    Handler handler;
    BroadcastReceiver bReceiver;
    private static final String TAG = "PlayActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Đang ở đây", "createPlay");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        musicImage = findViewById(R.id.imageView);
        musicTitle = findViewById(R.id.txt_title);
        musicArtist = findViewById(R.id.txt_artist);
        musicAlbum = findViewById(R.id.txt_album);
        musicDuration = findViewById(R.id.txt_song_total_duration);
        currentDurationText = findViewById(R.id.txt_song_current_duration);
        btnBack = findViewById(R.id.btnBack);

        repeatButton = findViewById(R.id.btn_repeat);
        playButton = findViewById(R.id.btn_play);
        shuffleButton = findViewById(R.id.btn_shuffle);

        durationBar = findViewById(R.id.song_progressbar);
        handler = new Handler();
        musicManager = MusicManager.getInstance();

        btnList = findViewById(R.id.btnList);
        final Intent in = getIntent();
        musicManager.setCurrentIndex(in.getIntExtra("Index", -1));

        registerReceiver();
        updateDisplay(musicManager.getCurrentIndex());
        initProgressBars();
        playMusic();

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tin = new Intent(PlayActivity.this, PlaylistActivity.class);
                tin.putExtra("PLAYER", musicManager.getCurrentIndex());
                startActivity(tin);
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gotoMain = new Intent(PlayActivity.this, MainActivity.class);
                startActivity(gotoMain);
            }
        });

    }

    //Update view theo vị trí idx
    private void updateDisplay(int idx) {
        Log.d("TAG", "updateDisplay Called.");
        Log.d("TAG", "updateDisplay index : " + idx);
        Music m = musicManager.getMusicByIndex(idx);
        if (idx >= 0 && idx < musicManager.getMusicSize()) {

            Glide.with(this)
                    .load(m.getMusicImage())
                    .placeholder(R.drawable.ic_music_basic)
                    .into(musicImage);

            musicTitle.setText(m.getMusicTitle());
            musicArtist.setText(m.getMusicArtist());
            musicAlbum.setText(m.getMusicAlbum());
            musicDuration.setText(musicInfoConverter.durationConvert(m.getMusicDuration()));
            durationBar.setProgress(0);
            durationBar.setMax(m.getMusicDuration());
            durationBar.setKeyProgressIncrement(1000);
            currentDurationText.setText(musicInfoConverter.durationConvert(0));

            if (musicManager.getMusicPlayer() == null)
                mediaPlayer = musicManager.createAndGetMusicPlayer();
            else
                mediaPlayer = musicManager.getMusicPlayer();
            setPlayButtonBackground();

            if (musicManager.isShuffling == true)
                toggleButtonDisplay(1,"Shuffle");
            else
                toggleButtonDisplay(0,"Shuffle");

            if (mediaPlayer.isLooping() != false) {
                toggleButtonDisplay(1,"Repeat");
            } else {
                toggleButtonDisplay(0,"Repeat");
            }
        } else
            Log.d("Lỗi ở đây", "display");
    }

    private void initProgressBars() {
        durationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int i, boolean b) {
                if (b) {
                    currentDurationText.setText(musicInfoConverter.durationConvert(i));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekbar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekbar) {
                mediaPlayer.seekTo(durationBar.getProgress());
            }
        });
    }
    //gửi sự kiện cho Service
    public void doPlayService(String action) {
        serviceIntent = new Intent(this, PlayerService.class);
        serviceIntent.setAction(action);
        startService(serviceIntent);
    }

    private void playMusic() {
        doPlayService(Actions.ACTION_PLAY);
        playCycle();
    }

    private void pauseMusic() {
        setPlayButtonBackground();
        doPlayService(Actions.ACTION_PAUSE);
    }

    private void resumeMusic() {
        setPlayButtonBackground();
        doPlayService(Actions.ACTION_RESUME);
        playCycle();
    }

    private void playPrevMusic() {
        int ci = musicManager.getCurrentIndex();
        ci -= 1;
        if (ci == -1) ci = musicManager.getMusicSize() - 1;
        updateDisplay(ci);
        doPlayService(Actions.ACTION_PREV);

    }

    private void playNextMusic() {
        int ci = musicManager.getCurrentIndex();
        updateDisplay((ci + 1) % musicManager.getMusicSize());
        doPlayService(Actions.ACTION_NEXT);

    }

    private void setPlayButtonBackground() {
        if (mediaPlayer.isPlaying()) {
            playButtonIcon = R.drawable.ic_pause_black_24dp;
            playButtonBackgroundColor = R.color.playingColor;
        } else {
            playButtonIcon = R.drawable.ic_play_arrow_black_24dp;
            playButtonBackgroundColor = R.color.pausedColor;
        }
        playButton.setImageDrawable(ContextCompat.getDrawable(getApplication(), playButtonIcon));
    }
    //Đoạn này copy đéo biết nữa
    private void toggleButtonDisplay(int state, String buttonType){
        if (buttonType.equals("Repeat")){
            if(state == 0){
                repeatButton.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.ic_repeat_black_24dp));
            }
            else{
                repeatButton.setImageDrawable(ContextCompat.getDrawable(getApplication(), R.drawable.ic_repeat_one_black_24dp));
            }
        }
        else if (buttonType.equals("Shuffle")){
            if(state == 0){
            }
            else{

            }
        }
    }

    public void controlClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_prev:
                playPrevMusic();
                break;
            case R.id.btn_play:
                if (mediaPlayer.isPlaying()) {
                    pauseMusic();
                } else {
                    resumeMusic();
                }
                break;
            case R.id.btn_next:
                playNextMusic();
                break;
            case R.id.btn_repeat:
                if (mediaPlayer.isLooping() == false) {
                    mediaPlayer.setLooping(true);
                    toggleButtonDisplay(1,"Repeat");
                    Toast.makeText(getApplicationContext(), "Bật repeat.", Toast.LENGTH_SHORT).show();
                } else {
                    mediaPlayer.setLooping(false);
                    toggleButtonDisplay(0,"Repeat");
                    Toast.makeText(getApplicationContext(), "Tắt repeat.", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_shuffle:
                //Đoạn này chưa tìm đc icon
                //Vector asset đéo có icon bình thường ưtf
                if (musicManager.isShuffling == false) {
                    toggleButtonDisplay(1,"Shuffle");
                    musicManager.shuffleList();
                    musicManager.setCurrentIndex(musicManager.getPositionByIdx(musicManager.getCurrentIndex()));
                    Toast.makeText(getApplicationContext(), "Bật shuffle.", Toast.LENGTH_SHORT).show();
                } else {
                    toggleButtonDisplay(0,"Shuffle");
                    musicManager.setCurrentIndex(musicManager.getMusicByIndex(musicManager.getCurrentIndex()).getMusicIndex());
                    musicManager.shuffleList();
                    Toast.makeText(getApplicationContext(), "Tắt shuffle.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    //Giữ seekbar hoạt động
    public void playCycle() {
        if (musicManager.getMusicPlayer() == null)
            return;

        runnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    durationBar.setProgress(Math.min(durationBar.getMax(), mediaPlayer.getCurrentPosition()));
                    currentDurationText.setText(musicInfoConverter.durationConvert(Math.min(durationBar.getMax(), mediaPlayer.getCurrentPosition())));
                    playCycle();
                }
            }
        };
        handler.postDelayed(runnable, 100);
    }

    @Override
    public void onResume() {
        updateDisplay(musicManager.getCurrentIndex());
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(runnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        unregisterReceiver(bReceiver);
    }

    private void registerReceiver() {
        bReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateDisplay(musicManager.getCurrentIndex());
            }
        };
        registerReceiver(bReceiver, new IntentFilter(Actions.ACTION_UPDATE));
    }
}
