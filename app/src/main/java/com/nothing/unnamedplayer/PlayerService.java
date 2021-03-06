package com.nothing.unnamedplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.nothing.unnamedplayer.Model.Music;
import com.nothing.unnamedplayer.Util.Actions;
import com.nothing.unnamedplayer.Util.App;

import java.io.IOException;


public class PlayerService extends Service {
    private final static String TAG = "PlayerService";
    private MusicManager musicManager;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;

    enum Status {
        RESUME,
        PAUSE
    }

    float actVolume, curVolume, maxVolume;

    private NotificationManagerCompat notificationManagerCompat;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManagerCompat = NotificationManagerCompat.from(this);
        musicManager = MusicManager.getInstance();
        mediaPlayer = musicManager.getMusicPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                sendDisplayUpdate();
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                    playNextMusic();
            }
        });
    }

    private void playMusic(Music m) {
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        curVolume = actVolume / maxVolume;

        Notification channel = createNotificationWithStatus(m,Status.PAUSE);
        startForeground(1, channel);
        Uri uri = Uri.parse(m.getMusicPath());
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.d("Loi gi day", "Loi noti: "+e.toString());
        }
    }

    private void sendDisplayUpdate(){
        Intent intent = new Intent(Actions.ACTION_UPDATE);
        sendBroadcast(intent);
    }
    private void playPrevMusic() {
        int ci = musicManager.getCurrentIndex();
        ci -= 1;
        if (ci == -1) {
            ci = musicManager.getMusicSize() - 1;
        }
        musicManager.setCurrentIndex(ci);
        playMusic(musicManager.getMusicByIndex(musicManager.getCurrentIndex()));

    }

    private void playNextMusic() {
        musicManager.setCurrentIndex((musicManager.getCurrentIndex() + 1) % musicManager.getMusicSize());
        playMusic(musicManager.getMusicByIndex(musicManager.getCurrentIndex()));
    }

    private Notification createNotificationWithStatus(Music m, Status status) {
        RemoteViews notificationLayout = null;
        Intent pauseIntent, resumeIntent, nextIntent, prevIntent, endIntent;
        PendingIntent pPause, pNext, pPrev, pEnd, pResume;

        switch (status) {
            //Resume player
            case PAUSE:
                notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_service);

                pauseIntent = new Intent(Actions.ACTION_PAUSE);
                pPause = PendingIntent.getBroadcast(this, 0, pauseIntent, 0);

                nextIntent = new Intent(Actions.ACTION_NEXT);
                pNext = PendingIntent.getBroadcast(this, 0, nextIntent, 0);

                prevIntent = new Intent(Actions.ACTION_PREV);
                pPrev = PendingIntent.getBroadcast(this, 0, prevIntent, 0);

                endIntent = new Intent(Actions.ACTION_END);
                pEnd = PendingIntent.getBroadcast(this, 0, endIntent, 0);

                notificationLayout.setTextViewText(R.id.notification_title, m.getMusicTitle());
                notificationLayout.setTextViewText(R.id.notification_artist, m.getMusicArtist());
                notificationLayout.setImageViewBitmap(R.id.notification_image, m.getMusicImage());
                notificationLayout.setImageViewResource(R.id.notification_play, R.drawable.ic_pause_black_24dp);
                notificationLayout.setOnClickPendingIntent(R.id.notification_prev, pPrev);
                notificationLayout.setOnClickPendingIntent(R.id.notification_next, pNext);
                notificationLayout.setOnClickPendingIntent(R.id.notification_play, pPause);
                notificationLayout.setOnClickPendingIntent(R.id.notification_end, pEnd);
                break;
                //Pause player
            case RESUME:
                notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_service);

                resumeIntent = new Intent(Actions.ACTION_RESUME);
                pResume = PendingIntent.getBroadcast(this, 0, resumeIntent, 0);

                nextIntent = new Intent(Actions.ACTION_NEXT);
                pNext = PendingIntent.getBroadcast(this, 0, nextIntent, 0);

                prevIntent = new Intent(Actions.ACTION_PREV);
                pPrev = PendingIntent.getBroadcast(this, 0, prevIntent, 0);

                endIntent = new Intent(Actions.ACTION_END);
                pEnd = PendingIntent.getBroadcast(this, 0, endIntent, 0);

                notificationLayout.setTextViewText(R.id.notification_title, m.getMusicTitle());
                notificationLayout.setTextViewText(R.id.notification_artist, m.getMusicArtist());
                notificationLayout.setImageViewBitmap(R.id.notification_image, m.getMusicImage());
                notificationLayout.setImageViewResource(R.id.notification_play, R.drawable.ic_play_arrow_black_24dp);
                notificationLayout.setOnClickPendingIntent(R.id.notification_prev, pPrev);
                notificationLayout.setOnClickPendingIntent(R.id.notification_next, pNext);
                notificationLayout.setOnClickPendingIntent(R.id.notification_play, pResume);
                notificationLayout.setOnClickPendingIntent(R.id.notification_end, pEnd);
                break;
        }

        Notification channel = new NotificationCompat.Builder(getApplicationContext(), App.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_video_black_24dp)
                .setCustomContentView(notificationLayout)
                .setCustomBigContentView(notificationLayout)
                .build();

        return channel;


    }

    @Override
    @Nullable
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void pauseMusic(Music m) {
        Notification channel = createNotificationWithStatus(m, Status.RESUME);
        startForeground(1, channel);
        sendDisplayUpdate();
        mediaPlayer.pause();
    }

    private void resumeMusic(Music m) {
        Notification channel = createNotificationWithStatus(m, Status.PAUSE);
        startForeground(1, channel);
        sendDisplayUpdate();
        mediaPlayer.start();
    }


    public void stopPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();
        }
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG", "Choi bai : " + musicManager.getMusicByIndex(musicManager.getCurrentIndex()).getMusicTitle());
        String actionToDo = intent.getAction();
        Music m = musicManager.getMusicByIndex(musicManager.getCurrentIndex());
        if (actionToDo.equals(Actions.ACTION_PLAY)) {
            playMusic(m);
        } else if (actionToDo.equals(Actions.ACTION_PREV)) {
            playPrevMusic();
        } else if (actionToDo.equals(Actions.ACTION_NEXT)) {
            playNextMusic();
        } else if (actionToDo.equals(Actions.ACTION_PAUSE)) {
            pauseMusic(m);
        } else if (actionToDo.equals(Actions.ACTION_RESUME)) {
            resumeMusic(m);
        } else if (actionToDo.equals(Actions.ACTION_END)) {
            stopPlayer();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPlayer();
    }
}
