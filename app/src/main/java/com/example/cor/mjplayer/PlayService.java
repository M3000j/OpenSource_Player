package com.example.cor.mjplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.example.cor.mjplayer.MainActivity.MusicPlayPause;
import static com.example.cor.mjplayer.MainActivity.isPlaying;


public class PlayService extends Service {


    public static MediaPlayer mediaPlayer;
    public static AudioManager audioManager;
    public static MediaMetadataRetriever mediaMetadataRetriever;
    private final int NOTIFY_ID = 1;
    private boolean ongoingCall = false;
    BroadcastReceiver pluggedReciver;
    BroadcastReceiver lockscreenRecivier;

    public static NotificationManager notimanger;
    TelephonyManager telephonyManager;
    PhoneStateListener phoneStateListener;
    MediaSession mediaSession;
    Music mtemp;
    List<Music> serviceMusics;
    int lentgh;
    String uri = null;
    int mposition = 0;
    String mode = null;
    Context context;
    public static boolean change = false;

    @Override
    public void onCreate() {
        super.onCreate();
        pluggedReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
                if (intent.getAction().equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
                    Intent i = new Intent(getApplicationContext(), PlayService.class);
                    if (isPlaying) {
                        if (!MainActivity.isMainPause)
                            MusicPlayPause.setImageResource(R.drawable.ic_play_circle_outline_white);
                        i.putExtra("mode", "pause");
                    } else {
                        if (!MainActivity.isMainPause)
                            MusicPlayPause.setImageResource(R.drawable.ic_pause_circle_outline);
                        i.putExtra("mode", "cont");
                    }
                    startService(i);
                }
            }
        };

        lockscreenRecivier = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent lock;
                if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_ON) || intent.getAction().equalsIgnoreCase(Intent.ACTION_SCREEN_OFF)) {
                    lock = new Intent(context, LockScreenActivity.class);
                    lock.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    lock.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    lock.putExtra("mp", MainActivity.musicPosition);
                    startActivity(lock);
                }
            }
        };


        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        /////////////////////////////////////////////
        //////////////////reviver for buttons
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(pluggedReciver, intentFilter);

        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter1.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(lockscreenRecivier, intentFilter1);

        mediaSession = new MediaSession(getApplicationContext(), "PlayService");
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS);

        mediaSession.setActive(true);
        PlaybackState state = new PlaybackState.Builder()
                .setActions(
                        PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE |
                                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID | PlaybackState.ACTION_PAUSE |
                                PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                .setState(PlaybackState.STATE_PLAYING, 0, 1, SystemClock.elapsedRealtime())
                .build();
        mediaSession.setPlaybackState(state);


    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        context = this;

        callStateListener();

        mediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public boolean onMediaButtonEvent(@NonNull Intent mediaButtonIntent) {
                KeyEvent event = (KeyEvent) mediaButtonIntent.getExtras().get(Intent.EXTRA_KEY_EVENT);
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    Intent i = new Intent(getApplicationContext(), PlayService.class);
                    int keycode = event.getKeyCode();
                    switch (keycode) {
                        case 127:
                            if (isPlaying)
                                i.putExtra("mode", "pause");
                            break;
                        case 126:
                            if (!isPlaying)
                                i.putExtra("mode", "cont");
                            break;
                        case 87:
                            i.putExtra("mode", "play");
                            i.putExtra("LMusics", (ArrayList<? extends Parcelable>) serviceMusics);
                            MainActivity.musicPosition--;
                            i.putExtra("mposition", MainActivity.musicPosition);
                            break;
                        case 88:
                            i.putExtra("mode", "play");
                            i.putExtra("LMusics", (ArrayList<? extends Parcelable>) serviceMusics);
                            MainActivity.musicPosition++;
                            i.putExtra("mposition", MainActivity.musicPosition);
                            break;
                    }
                    startService(i);
                }
                return false;
            }

        });

        notimanger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mode = intent.getStringExtra("mode");
        if (mode.equals("play")) {
            serviceMusics = intent.getParcelableArrayListExtra("LMusics");
            mposition = intent.getIntExtra("mposition", 0);
            if (mposition == serviceMusics.size()) {
                mposition = 0;
            } else if (mposition < 0) {
                mposition = serviceMusics.size() - 1;
            }
            MainActivity.musicPosition = mposition;
            mtemp = serviceMusics.get(mposition);
            Changes();
            uri = mtemp.getUri();
        }

        switch (mode) {
            case "pause":
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                    lentgh = mediaPlayer.getCurrentPosition();
                    isPlaying = false;
                }
                break;
            case "play":
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                isPlaying = true;
                mediaPlayer = MediaPlayer.create(context, Uri.parse(uri));
                mediaPlayer.start();
                break;
            case "cont": {
                isPlaying = true;
                mediaPlayer.seekTo(lentgh);
                mediaPlayer.start();
                break;
            }

        }
        Changes();
        Notification.Builder not = new Notification.Builder(context)
                .setOngoing(true)
                .setStyle(new Notification.MediaStyle().setMediaSession(mediaSession.getSessionToken()))
                .setAutoCancel(true)
                .setContentTitle(mtemp.getName())
                .setShowWhen(false)
                .setCategory(Notification.EXTRA_MEDIA_SESSION)
                .setContentText(mtemp.getArtist())
                .setLargeIcon(getBitMap(uri))
                .setSmallIcon(R.drawable.ic_music_note_white_24dp)
                .addAction(R.drawable.ic_fast_rewind_white, "Pre", chooseAction(1))
                .addAction(mode.equals("pause") ? R.drawable.ic_play_arrow_black : R.drawable.ic_pause_white_24dp, mode.equals("pause") ? "Play" : "Pause", chooseAction(2))
                .addAction(R.drawable.ic_fast_forward_white, "Next", chooseAction(0));
        notimanger.notify(NOTIFY_ID, not.build());

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (!mode.equals("pause")) {
                    mode = "play";
                    if (!Music_Detail.repeat) {
                        mposition++;
                        MainActivity.musicPosition = mposition;
                    }
                    if (mposition == serviceMusics.size()) {
                        mposition = 0;
                    }
                    Intent nextIntent = new Intent(context, PlayService.class);
                    nextIntent.putExtra("mode", mode);
                    nextIntent.putExtra("mposition", mposition);
                    nextIntent.putExtra("LMusics", (ArrayList<? extends Parcelable>) serviceMusics);
                    startService(nextIntent);
                } else {
                    return;
                }
            }
        });


        return START_STICKY;
    }

    public void Changes() {
        if (LockScreenActivity.lockActive) {
            change = true;
            LockScreenActivity.lock_position = MainActivity.musicPosition;
        } else {
            change = false;
        }
        if (!MainActivity.active) {
            try {
                Music_Detail.ShowDetail(mposition);
            } catch (Exception ex) {

            }

        } else {
            MainActivity.ChangeShowInfo(mposition);
        }
    }

    public PendingIntent chooseAction(int a) {
        if (a == 0) {
            Intent inte = new Intent(this, PlayService.class);
            inte.putExtra("mode", "play");
            inte.putExtra("LMusics", (ArrayList<? extends Parcelable>) serviceMusics);
            int s = MainActivity.musicPosition + 1;
            inte.putExtra("mposition", s);

            return PendingIntent.getService(this, 1, inte, PendingIntent.FLAG_UPDATE_CURRENT);
        } else if (a == 1) {
            Intent inte = new Intent(this, PlayService.class);
            inte.putExtra("mode", "play");
            inte.putExtra("LMusics", (ArrayList<? extends Parcelable>) serviceMusics);
            int s = MainActivity.musicPosition - 1;
            inte.putExtra("mposition", s);
            return PendingIntent.getService(this, 2, inte, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            if (isPlaying) {
                Intent i = new Intent(getApplicationContext(), PlayService.class);
                i.putExtra("mode", "pause");
                return PendingIntent.getService(this, 3, i, PendingIntent.FLAG_UPDATE_CURRENT);
            } else {
                Intent i = new Intent(getApplicationContext(), PlayService.class);
                i.putExtra("mode", "cont");
                return PendingIntent.getService(this, 3, i, PendingIntent.FLAG_UPDATE_CURRENT);
            }
        }
    }

    private Bitmap getBitMap(String urii) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(urii);
        byte[] bytes = mmr.getEmbeddedPicture();
        Bitmap bitmap;
        if (bytes != null) {
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            return bitmap;
        } else {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_music_note_black_24dp);
        }
    }


    private void callStateListener() {
        // Get the telephony manager
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //Starting listening for PhoneState changes
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                int g = 0;
                if (mediaPlayer != null) {
                    g = mediaPlayer.getCurrentPosition();
                }
                switch (state) {
                    //if at least one call exists or the phone is ringing
                    //pause the MediaPlayer
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            mediaPlayer.pause();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                mediaPlayer.seekTo(g);
                                mediaPlayer.start();
                            }
                        }
                        break;
                }
            }
        };
        // Register the listener with the telephony manager
        // Listen for changes to the device call state.
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

}
