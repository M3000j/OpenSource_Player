package com.example.cor.mjplayer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import it.moondroid.seekbarhint.library.SeekBarHint;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.cor.mjplayer.MainActivity.context;
import static com.example.cor.mjplayer.MainActivity.isPlaying;
import static com.example.cor.mjplayer.MainActivity.msImage;
import static com.example.cor.mjplayer.MainActivity.musicss;
import static com.example.cor.mjplayer.PlayService.mediaMetadataRetriever;

public class Music_Detail extends AppCompatActivity {

    static ImageView _Img_Backgroundd;
    static TextView _Txt_Title;
    static TextView _Txt_Artist;
    static TextView _Txt_Lentgh;
    static SeekBarHint _Seek_Length;
    static Context detail_context;
    static int RecordNo;
    static List<String> mylyric;
    static String stemp;
    static int firstb;
    static int lastb;
    static int[] times;
    static String ttime;
    static RecyclerView _Lrc_List;
    static LyricAdapter lyricAdapter;
    static ImageView[] _Img_Controler = new ImageView[3];
    static int tbold = 0;
    LinearLayout _Linear_Sound;
    static String songLyric = null;
    SeekBar verticalSeekBar;
    ImageView _Img_Sound;
    private Handler mHandler;
    static int k = 0;
    ImageView[] _Img_maincont = new ImageView[5];
    int f = 0;
    String downloadedLyric = null;
    Intent gh = null;
    boolean ispaused = false;
    public static boolean repeat = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music__detail);
        Initlise();
        final int g = getIntent().getIntExtra("mp", 0);
        try {
            ShowDetail(g);
        } catch (Exception e) {

        }
        detail_context = getApplicationContext();
        mHandler = new Handler();
        _Img_Controler[0].setTag(R.drawable.fav);
        if (songLyric == null && musicss.get(RecordNo).getLyric() == null && musicss.get(RecordNo).getMadeLyrics().size() == 1) {
            _Img_Controler[1].setTag(R.drawable.lrc);
        } else {
            _Img_Controler[1].setTag(R.drawable.lrc_green);
        }
        /////add to favorites
        _Img_Controler[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImageView img = (ImageView) view;
                if ((Integer) img.getTag() == R.drawable.fav) {
                    img.setImageResource(R.drawable.fav_green);
                    img.setTag(R.drawable.fav_green);
                    Toast.makeText(Music_Detail.this, "Added", Toast.LENGTH_SHORT).show();
                } else {
                    img.setImageResource(R.drawable.fav);
                    img.setTag((Integer) R.drawable.fav);
                    Toast.makeText(Music_Detail.this, "Removed", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //////////img for lrc
        _Img_Controler[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView _Img = (ImageView) view;
                /////////////if doesnt have show toast and return and imgcontroller 2 that is maikng
                if (songLyric == null && musicss.get(RecordNo).getLyric() == null && musicss.get(RecordNo).getMadeLyrics().size() <= 1) {
                    Toast.makeText(Music_Detail.this, "No Lyrics", Toast.LENGTH_SHORT).show();
                    _Img_Controler[2].setVisibility(View.VISIBLE);
                    return;
                }
                _Img_Controler[2].setVisibility(View.GONE);
                if ((Integer) _Img.getTag() == R.drawable.lrc) {
                    _Img.setTag(R.drawable.lrc_green);
                    _Img.setImageResource(R.drawable.lrc_green);
                    _Lrc_List.setVisibility(View.VISIBLE);
                } else {
                    _Img.setTag(R.drawable.lrc);
                    _Img.setImageResource(R.drawable.lrc);
                    _Lrc_List.setVisibility(View.GONE);
                }
            }
        });

        //////using api to get lyric
        _Img_Controler[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = musicss.get(RecordNo).getName();
                String artist = musicss.get(RecordNo).getArtist();
                final OkHttpClient client = new OkHttpClient();
                final Request request = new Request.Builder()
                        /////////////////////for getting api you should have api keyid
                        .url("https://api.musixmatch.com/ws/1.1/matcher.lyrics.get?format=jsonp&callback=callback&q_track=" + title + "&q_artist=" + artist + "&apikey=41b7d1c77ab90a96979bac583bb0e31c")
                        .get()
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .build();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            /////make waiting dialog
                            final KProgressHUD[] hud = new KProgressHUD[1];
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hud[0] = KProgressHUD.create(Music_Detail.this)
                                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                            .setLabel("Please wait")
                                            .setDetailsLabel("Downloading Your Lrics")
                                            .setCancellable(true)
                                            .setAnimationSpeed(1)
                                            .setDimAmount(0.5f);
                                    hud[0].show();
                                }
                            });
                            ///geting response
                            final Response response = client.newCall(request).execute();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hud[0].dismiss();
                                }
                            });
                            String s = response.body().string();
                            s = s.substring(9, s.length());
                            s = s.substring(0, s.length() - 2);
                            /////up code is oomittng []{} extra
                            /////message is coming in the form of json that has status code and body
                            ////if status code is 404 no lyric is found
                            JSONObject js = new JSONObject(s).getJSONObject("message");
                            ////header check mishavad
                            String sa = js.getJSONObject("header").getString("status_code");
                            if (sa.equals("404")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        final AlertDialog alertDialog = new AlertDialog.Builder(Music_Detail.this)
                                                .setTitle("No Lyric found")
                                                .setCancelable(true)
                                                .setMessage("Do you want to create your lyric")
                                                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        gh = new Intent(getApplicationContext(), MakeLrcActivity.class);
                                                        gh.setAction(Intent.ACTION_MAIN);
                                                        gh.putExtra("Position", RecordNo);
                                                        startActivity(gh);
                                                    }
                                                })
                                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        return;
                                                    }
                                                }).create();
                                        alertDialog.show();

                                        return;
                                    }
                                });
                            } else {
                                downloadedLyric = js.getJSONObject("body").getJSONObject("lyrics").getString("lyrics_body");
                                musicss.get(RecordNo).setLyric(downloadedLyric);
                                List<String> mylyric = new ArrayList<>(Arrays.asList(musicss.get(RecordNo).getLyric().split("\n")));
                                int[] ti = new int[mylyric.size()];
                                lyricAdapter = new LyricAdapter(mylyric, ti, false);

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        _Lrc_List.setAdapter(lyricAdapter);
                                        MakeVisible();
                                        Toast.makeText(Music_Detail.this, "Done", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }).start();
            }
        });
        // Picasso.with(getApplicationContext()).load(R.drawable.kinectrush4).resize(500, 500).into(_Img_Backgroundd);

        //////events for _Img_maincont array

        ////turn on\off the repeat
        _Img_maincont[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView _Img = (ImageView) view;
                if (repeat) {
                    _Img.setImageResource(R.drawable.ic_replay_white_24dp);
                    repeat = false;
                } else {
                    _Img.setImageResource(R.drawable.ic_replay_green_24dp);
                    repeat = true;
                }
            }
        });

        //////play/pause
        _Img_maincont[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imgTemp = (ImageView) view;
                Intent i = new Intent(getApplicationContext(), PlayService.class);
                if (isPlaying) {
                    try {
                        imgTemp.setImageResource(R.drawable.ic_play_arrow_black);
                        i.putExtra("mode", "pause");
                    } catch (Exception ex) {
                        Toast.makeText(Music_Detail.this, ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    imgTemp.setImageResource(R.drawable.ic_pause_white_24dp);
                    i.putExtra("mode", "cont");
                }
                startService(i);
            }
        });

        ///EVENT OF BACK BUTTON
        _Img_maincont[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PlayService.class);
                i.putExtra("mode", "play");
                i.putExtra("LMusics", (ArrayList<? extends Parcelable>) musicss);
                MainActivity.musicPosition--;
                if (MainActivity.musicPosition < 0) {
                    MainActivity.musicPosition = musicss.size() - 1;
                }
                i.putExtra("mposition", MainActivity.musicPosition);

                startService(i);
            }
        });


        ///EVENT OF NEXT BUTTON
        _Img_maincont[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PlayService.class);
                i.putExtra("mode", "play");
                i.putExtra("LMusics", (ArrayList<? extends Parcelable>) musicss);
                MainActivity.musicPosition++;

                if (MainActivity.musicPosition == musicss.size()) {
                    MainActivity.musicPosition = 0;
                }
                i.putExtra("mposition", MainActivity.musicPosition);
                startService(i);
            }
        });


        _Seek_Length.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b)
                    PlayService.mediaPlayer.seekTo(i * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        _Img_Sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sound = PlayService.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                f = 0;//////using it for getting sound for

                verticalSeekBar.setProgress(sound);
                if (_Linear_Sound.getVisibility() == View.VISIBLE)
                    _Linear_Sound.setVisibility(View.GONE);
                else {
                    _Linear_Sound.setVisibility(View.VISIBLE);
                }
            }
        });

        //Make update Seekbar on UI thread and lyrcis
        Music_Detail.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (PlayService.mediaPlayer != null) {
                    double mCurrentPosition = PlayService.mediaPlayer.getCurrentPosition() / 1000;
                    _Txt_Lentgh.setText(GetTime(mCurrentPosition));
                    _Seek_Length.setProgress((int) mCurrentPosition);
                    mCurrentPosition += 1.75;
                    try {
                        if (k < times.length) {
                            if (musicss.get(RecordNo).getMadeLyrics().size() == 1) {
                                if ((songLyric != null && times.length > 3))
                                    if (times[k] < mCurrentPosition) {
                                        tbold = k;
                                        lyricAdapter.notifyDataSetChanged();
                                        if (k != times.length) {
                                            if (times[k + 1] < mCurrentPosition) {
                                                k++;
                                            }
                                        }
                                    }
                            } else {
                                if (times != null) {
                                    if (times[k] < mCurrentPosition) {
                                        tbold = k;
                                        lyricAdapter.notifyDataSetChanged();
                                        if (k != times.length) {
                                            if (times[k + 1] < mCurrentPosition) {
                                                k++;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception ex) {
                    }
                }
                mHandler.postDelayed(this, 1000);
            }
        });
        ///////////////////change the sound and the event
        verticalSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                PlayService.audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if ((f == 0)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            f = 1;
                            for (int g = 0; g < Math.pow(4000, 2); g++) {
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    _Linear_Sound.setVisibility(View.GONE);
                                }
                            });
                        }
                    }).start();


                }

            }
        });

    }


    public static void ShowDetail(int mp) throws Exception {
        RecordNo = mp;

        double ts = Math.ceil(musicss.get(mp).getSize());
        int m = (int) (ts / 60);
        int s = (int) (ts % 60);
        ttime = m + ":" + s;
        _Seek_Length.setMax((int) ts);
        _Txt_Title.setText(musicss.get(mp).getName());
        _Txt_Artist.setText(musicss.get(mp).getArtist());
        mediaMetadataRetriever.setDataSource(musicss.get(mp).getUri());
        msImage = mediaMetadataRetriever.getEmbeddedPicture();
        if (msImage != null) {
            Bitmap songImage = BitmapFactory
                    .decodeByteArray(msImage, 0, msImage.length);
            _Img_Backgroundd.setImageBitmap(songImage);
        } else {
            Picasso.with(context).load(R.drawable.musicfill).resize(120, 120).into(_Img_Backgroundd);
            _Img_Backgroundd.setImageBitmap(null);
            _Img_Backgroundd.setBackgroundColor(Color.DKGRAY);
        }
        //////end of the setting image for background preveiw
        /////start making and showing them lrc texts via _Lrc_List recyclerview in the activity

        songLyric = LyricsExtractor.getLyrics(new File(musicss.get(mp).getUri()));

        if (songLyric != null) {
            k = 0;/////index of times array
            tbold = 0;///index of text which should be hilighted
            MakeVisible();
            mylyric = new ArrayList<>(Arrays.asList(songLyric.split("\r\n")));


            //////for ommiting unform strings

            for (int h = 0; h < mylyric.size(); h++) {
                if (mylyric.get(h).equals("")) {
                    mylyric.remove(h);
                    h--;
                }
            }
//////for ommiting unform strings
            final String regex = "\\[[(a-z)]*:";
            final Pattern pattern = Pattern.compile(regex);
            //////removing the extra metadata from the strings
            for (int j = 0; j < mylyric.size(); j++) {
                if (pattern.matcher(mylyric.get(j)).find()) {
                    mylyric.remove(j);
                    j--;
                }
            }
            if (mylyric.size() <= 2) {
                if (musicss.get(mp).getMadeLyrics().size() > 1) {
                    InitiazingArray(mp);
                    /////
                    ///////end of removing "" home
                    ////////make the times
                    try {
                        mylyric.remove(0);
                        times = new int[mylyric.size()];
                        MakeArrayTime();
                        MakeVisible();
                        SetAdapters(times, true);
                        return;
                    } catch (Exception ex) {
                        Toast.makeText(detail_context, ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    MakeNull();
                    songLyric = null;
                    return;
                }
            }
////////////////////////////////////////////////////////////////////


            /////make the time array to show songlyric lyrics
            times = new int[mylyric.size()];
            /////
            ///////end of removing "" home
            ////////make the times


            MakeArrayTime();/////////do make times from the strings

            SetAdapters(times, true);
        } else if (musicss.get(mp).getMadeLyrics().size() > 1) {
            InitiazingArray(mp);
            /////
            ///////end of removing "" home
            ////////make the times
            try {
                mylyric.remove(0);
                times = new int[mylyric.size()];
                MakeArrayTime();/////removing [ ] and make time
                MakeVisible();
                SetAdapters(times, true);
            } catch (Exception ex) {
                Toast.makeText(detail_context, ex.toString(), Toast.LENGTH_SHORT).show();
            }

        } else if (musicss.get(mp).getLyric() != null) {
            MakeVisible();
            mylyric = new ArrayList<>(Arrays.asList(musicss.get(mp).getLyric().split("\n")));
            int[] ti = new int[mylyric.size()];
            SetAdapters(ti, false);
        } else {
            MakeNull();
        }

    }

    public static void SetAdapters(int[] time, boolean hasTimed) {
        lyricAdapter = new LyricAdapter(mylyric, time, hasTimed);
        _Lrc_List.setAdapter(lyricAdapter);
    }

    public static String GetTime(double m) {
        int mm = (int) m / 60;
        int ss = (int) m % 60;
        return mm + ":" + ss + "/" + ttime;
    }

    public static void MakeNull() {
        _Lrc_List.setAdapter(null);
        _Lrc_List.setVisibility(View.GONE);
        _Img_Controler[1].setImageDrawable(context.getResources().getDrawable(R.drawable.lrc));
        _Img_Controler[1].setTag(R.drawable.lrc);
    }

    public static void MakeVisible() {
        _Lrc_List.setVisibility(View.VISIBLE);
        _Img_Controler[1].setImageDrawable(context.getResources().getDrawable(R.drawable.lrc_green));
        _Img_Controler[1].setTag(R.drawable.lrc_green);
        _Img_Controler[2].setVisibility(View.GONE);/////adding imageview

    }

    /////intitalising the function
    public void Initlise() {
        MainActivity.active = false;
        _Lrc_List = (RecyclerView) findViewById(R.id._Lrc_List);
        //////congif the _Lrc_List recyclerview
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        _Lrc_List.setLayoutManager(mLayoutManager);
        _Lrc_List.setItemAnimator(new DefaultItemAnimator());
        /////end of the config

        _Linear_Sound = (LinearLayout) findViewById(R.id._Linear_Sound);
        verticalSeekBar = (SeekBar) findViewById(R.id.seekArc);
        _Img_Backgroundd = (ImageView) findViewById(R.id._Img_Background_Detail);
        _Img_Sound = (ImageView) findViewById(R.id._Img_Sound);
        _Img_Controler[0] = (ImageView) findViewById(R.id._Img_Fav);
        _Img_Controler[1] = (ImageView) findViewById(R.id._Img_Lrc);
        _Img_Controler[2] = (ImageView) findViewById(R.id._Img_Add);
        _Img_Controler[2].setVisibility(View.GONE);
        _Img_maincont[0] = (ImageView) findViewById(R.id._Img_Tekrar);
        _Img_maincont[1] = (ImageView) findViewById(R.id._Img_BackDetail);
        _Img_maincont[2] = (ImageView) findViewById(R.id._Img_Play_Stop_Detail);
        _Img_maincont[3] = (ImageView) findViewById(R.id._Img_NextDetail);
        _Img_maincont[4] = (ImageView) findViewById(R.id._Img_Sound);
        /////////////////////////////////////////////////////////////
        _Txt_Artist = (TextView) findViewById(R.id._Txt_Artis_Detail_Name);
        _Txt_Title = (TextView) findViewById(R.id._Txt_Music_Detail_Name);
        _Txt_Lentgh = (TextView) findViewById(R.id._Txt_lentgh);
        ////////////////////////////////////////////////////////////
        _Seek_Length = (SeekBarHint) findViewById(R.id._seek_lentgh);
        ///////////////////////////////////////////////////////////
        if (repeat) {
            _Img_maincont[0].setImageResource(R.drawable.ic_replay_green_24dp);
        } else {
            _Img_maincont[0].setImageResource(R.drawable.ic_replay_white_24dp);
        }


        ///////////////////////////////////////////////////////////
        if (isPlaying) {
            _Img_maincont[2].setImageResource(R.drawable.ic_pause_white_24dp);
        } else {
            _Img_maincont[2].setImageResource(R.drawable.ic_play_arrow_black);
        }

    }

    public static void MakeArrayTime() {
        for (int j = 0; j < mylyric.size() && times.length != 0; j++) {
            firstb = mylyric.get(j).indexOf("[");
            lastb = mylyric.get(j).indexOf("]");
            stemp = mylyric.get(j).substring(firstb, lastb);
            times[j] = LyricPruning.ExtractTime(stemp);
            stemp = mylyric.get(j).substring(lastb + 1, mylyric.get(j).length());
            mylyric.remove(j);
            mylyric.add(j, stemp);
        }

    }

    public static void InitiazingArray(int mp) {
        mylyric = new ArrayList<>(musicss.get(mp).getMadeLyrics());
        k = 0;
        tbold = 0;
        MakeVisible();
        times = new int[musicss.get(mp).getMadeLyrics().size()];
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ispaused = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MainActivity.active = true;

    }
}
