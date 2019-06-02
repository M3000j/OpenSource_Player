package com.example.cor.mjplayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.example.cor.mjplayer.MainActivity.context;
import static com.example.cor.mjplayer.MainActivity.isPlaying;
import static com.example.cor.mjplayer.MainActivity.musicss;
import static com.example.cor.mjplayer.MainActivity.tempofMusics;

public class LockScreenActivity extends AppCompatActivity {

    public static int lock_position;
    TextView[] _Txt_Music_Lock = new TextView[2];
    ImageView _Img_Lock;
    ImageView _Img_Lrc;
    RecyclerView _Lrc_List_Lock;
    ImageView[] _Img_controllers_Lock = new ImageView[3];
    Music temp;
    String lyric_String;
    LyricAdapter lyricAdapterLock;
    List<String> lock_Lyrics;
    int[] lock_Times;
    int kk = 0;//////shomarande baraye peymayesh arraye time
    int ttbold = 0;
    public static boolean lockActive;
    Handler mh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.activity_lock_screen);
        Initilise();
        lockActive = true;
        lock_position = getIntent().getIntExtra("mp", 0);
        try {
            showChanges();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mh = new Handler();
        /////////////handling lyrics in ui


        LockScreenActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {


                if (PlayService.change) {
                    try {
                        PlayService.change = false;
                        showChanges();
                    }
                    catch (Exception ex)
                    {

                    }
                }


                if (PlayService.mediaPlayer != null) {
                    double mCurrentPosition = PlayService.mediaPlayer.getCurrentPosition() / 1000;
                    mCurrentPosition += 1.75;
                    try {
                        if (tempofMusics.get(lock_position).getMadeLyrics().size() == 1) {
                            if ((lyric_String != null && lock_Times.length > 3))
                                if (lock_Times[kk] < mCurrentPosition) {
                                    ttbold = kk;
                                    lyricAdapterLock.notifyDataSetChanged();
                                    if (kk != lock_Times.length) {
                                        if (lock_Times[kk + 1] < mCurrentPosition) {
                                            kk++;
                                        }
                                    }
                                }
                        } else {
                            if (lock_Times != null) {
                                if (lock_Times[kk] < mCurrentPosition) {
                                    ttbold = kk;
                                    lyricAdapterLock.notifyDataSetChanged();
                                    if (kk != lock_Times.length) {
                                        if (lock_Times[kk + 1] < mCurrentPosition) {
                                            kk++;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {

                    }
                }

                mh.postDelayed(this, 1000);
            }
        });


        /////start of events
        _Img_Lrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (lyric_String == null && musicss.get(lock_position).getMadeLyrics().size() <= 2
                        && musicss.get(lock_position).getLyric() == null) {
                    Toast.makeText(LockScreenActivity.this, "No lyric", Toast.LENGTH_SHORT).show();
                } else {
                    if ((Integer) _Img_Lrc.getTag() == R.drawable.lrc) {
                        _Img_Lrc.setTag(R.drawable.lrc_green);
                        _Img_Lrc.setImageResource(R.drawable.lrc_green);
                        _Lrc_List_Lock.setVisibility(View.VISIBLE);
                    } else {
                        _Img_Lrc.setTag(R.drawable.lrc);
                        _Img_Lrc.setImageResource(R.drawable.lrc);
                        _Lrc_List_Lock.setVisibility(View.GONE);
                    }
                }
            }
        });
        //////////////////////////

        _Img_controllers_Lock[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(getApplicationContext(), PlayService.class);

                    i.putExtra("mode", "play");
                    i.putExtra("LMusics", (ArrayList<? extends Parcelable>) musicss);
                    MainActivity.musicPosition -= 1;
                    lock_position = MainActivity.musicPosition;
                    if (lock_position < 0) {
                        lock_position = musicss.size() - 1;
                    }
                    showChanges();
                    i.putExtra("mposition", lock_position);

                    startService(i);
                }
                catch (Exception ex)
                {

                }

            }
        });
        /////////////////////
        _Img_controllers_Lock[2].

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            showChanges();
                            Intent i = new Intent(getApplicationContext(), PlayService.class);
                            i.putExtra("mode", "play");
                            i.putExtra("LMusics", (ArrayList<? extends Parcelable>) musicss);
                            MainActivity.musicPosition += 1;
                            lock_position = MainActivity.musicPosition;
                            if (lock_position == musicss.size())
                                lock_position = 0;
                            showChanges();
                            i.putExtra("mposition", lock_position);
                            startService(i);
                        } catch (Exception e) {

                        }


                    }
                });
        ///////////////////
        _Img_controllers_Lock[1].

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), PlayService.class);
                        ImageView _Img = (ImageView) view;

                        if (isPlaying) {
                            try {
                                _Img.setImageResource(R.drawable.ic_play_arrow_black);
                                i.putExtra("mode", "pause");
                            } catch (Exception ex) {
                                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            _Img.setImageResource(R.drawable.ic_pause_white_24dp);
                            i.putExtra("mode", "cont");
                        }
                        startService(i);
                    }
                });
    }

    private void showChanges() throws Exception {
        LoadLyric();
        temp = MainActivity.musicss.get(lock_position);
        _Txt_Music_Lock[0].setText(temp.getName());
        _Txt_Music_Lock[1].setText(temp.getArtist());
        getBitMap(temp.getUri());

        if (isPlaying) {
            _Img_controllers_Lock[1].setImageResource(R.drawable.ic_pause_white_24dp);
        } else {
            _Img_controllers_Lock[1].setImageResource(R.drawable.ic_play_arrow_black);
        }


    }

    private void LoadLyric() throws Exception {
        lyric_String = LyricsExtractor.getLyrics(new File(tempofMusics.get(lock_position).getUri()));
        if (lyric_String != null) {
            kk = 0;
            ttbold = 0;
            MakeVisible();
            lock_Lyrics = new ArrayList<>(Arrays.asList(lyric_String.split("\r\n")));


            //////for ommiting unform strings

            for (int h = 0; h < lock_Lyrics.size(); h++) {
                if (lock_Lyrics.get(h).equals("")) {
                    lock_Lyrics.remove(h);
                    h--;
                }
            }
//////for ommiting unform strings
            final String regex = "\\[[(a-z)]*:";
            final Pattern pattern = Pattern.compile(regex);
            //////removing the extra metadata from the strings
            for (int j = 0; j < lock_Lyrics.size(); j++) {
                if (pattern.matcher(lock_Lyrics.get(j)).find()) {
                    lock_Lyrics.remove(j);
                    j--;
                }
            }
            if (lock_Lyrics.size() <= 2) {
                if (tempofMusics.get(lock_position).getMadeLyrics().size() > 1) {
                    InitiazingArray(lock_position);
                    /////
                    ///////end of removing "" home
                    ////////make the times
                    try {
                        lock_Lyrics.remove(0);
                        lock_Times = new int[lock_Lyrics.size()];
                        MakeArrayTime();
                        MakeVisible();
                        SetAdapters(lock_Times, true);
                        return;
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    MakeNull();
                    lyric_String = null;
                    return;
                }
            }
////////////////////////////////////////////////////////////////////
            lock_Times = new int[lock_Lyrics.size()];
            /////
            ///////end of removing "" home
            ////////make the times


            MakeArrayTime();

            SetAdapters(lock_Times, true);
        } else if (tempofMusics.get(lock_position).getMadeLyrics().size() > 1) {
            InitiazingArray(lock_position);
            /////
            ///////end of removing "" home
            ////////make the times
            try {
                lock_Lyrics.remove(0);
                lock_Times = new int[lock_Lyrics.size()];
                MakeArrayTime();/////removing [ ] and make time
                MakeVisible();
                SetAdapters(lock_Times, true);
            } catch (Exception ex) {
                Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_SHORT).show();
            }

        } else if (tempofMusics.get(lock_position).getLyric() != null) {
            MakeVisible();
            lock_Lyrics = new ArrayList<>(Arrays.asList(tempofMusics.get(lock_position).getLyric().split("\n")));
            int[] ti = new int[lock_Lyrics.size()];
            SetAdapters(ti, false);
        } else {
            MakeNull();
        }
    }

    private void Initilise() {
        lockActive = true;
        _Txt_Music_Lock[0] = (TextView) findViewById(R.id._Txt_Music_Lock_Name);
        _Txt_Music_Lock[1] = (TextView) findViewById(R.id._Txt_Artis_Lock_Name);
        _Img_Lock = (ImageView) findViewById(R.id._Img_Background_Lock);
        _Img_Lrc = (ImageView) findViewById(R.id._Img_Lrc_Lock);
        _Lrc_List_Lock = (RecyclerView) findViewById(R.id._Lrc_List_Lock);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        _Lrc_List_Lock.setLayoutManager(mLayoutManager);
        _Lrc_List_Lock.setItemAnimator(new DefaultItemAnimator());

        _Img_controllers_Lock[0] = (ImageView) findViewById(R.id._Img_BackLock);
        _Img_controllers_Lock[1] = (ImageView) findViewById(R.id._Img_Play_Stop_Lock);
        _Img_controllers_Lock[2] = (ImageView) findViewById(R.id._Img_NextLock);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lockActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        lockActive = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lockActive = false;
    }

    private void getBitMap(String urii) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(urii);
        byte[] bytes = mmr.getEmbeddedPicture();
        Bitmap bitmap;
        if (bytes != null) {
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            _Img_Lock.setImageBitmap(bitmap);
            return;
        } else {
            Picasso.with(getApplicationContext()).load(R.drawable.no_music_found).into(_Img_Lock);
        }
    }

    public void SetAdapters(int[] time, boolean hasTimed) {
        lyricAdapterLock = new LyricAdapter(lock_Lyrics, time, hasTimed);
        _Lrc_List_Lock.setAdapter(lyricAdapterLock);
    }

    public void MakeNull() {
        _Lrc_List_Lock.setAdapter(null);
        _Lrc_List_Lock.setVisibility(View.GONE);
        _Img_Lrc.setImageDrawable(context.getResources().getDrawable(R.drawable.lrc));
        _Img_Lrc.setTag(R.drawable.lrc);
    }

    public void MakeVisible() {
        _Lrc_List_Lock.setVisibility(View.VISIBLE);
        _Img_Lrc.setImageDrawable(context.getResources().getDrawable(R.drawable.lrc_green));
        _Img_Lrc.setTag(R.drawable.lrc_green);
        _Img_Lrc.setVisibility(View.VISIBLE);
    }

    public void MakeArrayTime() {
        int firstb;
        int lastb;
        String stemp;
        for (int j = 0; j < lock_Lyrics.size() && lock_Times.length != 0; j++) {
            firstb = lock_Lyrics.get(j).indexOf("[");
            lastb = lock_Lyrics.get(j).indexOf("]");
            stemp = lock_Lyrics.get(j).substring(firstb, lastb);
            lock_Times[j] = LyricPruning.ExtractTime(stemp);
            stemp = lock_Lyrics.get(j).substring(lastb + 1, lock_Lyrics.get(j).length());
            lock_Lyrics.remove(j);
            lock_Lyrics.add(j, stemp);
        }

    }/////make time from strings and filling the arrays

    public void InitiazingArray(int mp) {
        lock_Lyrics = new ArrayList<>(tempofMusics.get(mp).getMadeLyrics());
        kk = 0;
        ttbold = 0;
        MakeVisible();
        lock_Times = new int[tempofMusics.get(mp).getMadeLyrics().size()];
    }

}
