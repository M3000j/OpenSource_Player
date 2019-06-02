package com.example.cor.mjplayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.moondroid.seekbarhint.library.SeekBarHint;

import static com.example.cor.mjplayer.MainActivity.isPlaying;
import static com.example.cor.mjplayer.MainActivity.musicss;

public class MakeLrcActivity extends AppCompatActivity {

    Toolbar _Toolb_MakeLyric;
    RecyclerView _Lrc_Maker_List;
    ImageView[] ImageLrcMakers = new ImageView[4];
    SeekBarHint _LrcSeekBar;
    static String lyric = "";
    TextView _Txt_Length;
    List<String> mylyr;
    private Handler mHandler;
    int nom;
    int where = 0;
    List<MakeLyr> makeLyrs;
    MakeRecycleAdapter makeRecycleAdapter;
    MediaPlayer mediaP;
    String ttim = "";
    int textCounter = 0;
    int cPositon = 0;
    ImageView _Img_Done;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_lrc);
        Initalize();
        _Toolb_MakeLyric.setTitle("Create Lyric");
        _Toolb_MakeLyric.setTitleTextColor(Color.parseColor("#FF757575"));
        mHandler = new Handler();
        nom = getIntent().getIntExtra("Position", 0);
        MakePause();
        mediaP = MediaPlayer.create(getApplicationContext(), Uri.parse(musicss.get(nom).getUri()));
        double ts = Math.ceil(musicss.get(nom).getSize());
        int m = (int) (ts / 60);
        int s = (int) (ts % 60);
        ttim = m + ":" + s;
        _LrcSeekBar.setMax(mediaP.getDuration() / 1000);
        setSupportActionBar(_Toolb_MakeLyric);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //////put prelyric
        ImageLrcMakers[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textCounter--;
                if (textCounter < 0) {
                    Toast.makeText(MakeLrcActivity.this, "No lyrics", Toast.LENGTH_SHORT).show();
                    textCounter = 0;
                } else {
                    String s = makeLyrs.get(textCounter).getText();
                    makeLyrs.get(textCounter).setText(s.substring(s.indexOf("]") + 1, s.length()));
                    makeLyrs.get(textCounter).setTick(false);
                    mediaP.seekTo((cPositon - 10) * 1000);
                    makeRecycleAdapter.notifyDataSetChanged();
                }
            }
        });

        /////////play/pausr
        ImageLrcMakers[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView _Img = (ImageView) view;
                if (mediaP.isPlaying()) {
                    where = mediaP.getCurrentPosition();
                    mediaP.pause();
                    _Img.setImageResource(R.drawable.ic_play_arrow_black);
                } else {
                    _Img.setImageResource(R.drawable.ic_pause_white_24dp);
                    mediaP.seekTo(where);
                    mediaP.start();
                }

            }
        });

        ///////////put next lyric
        ImageLrcMakers[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (textCounter == makeLyrs.size()) {
                    ImageLrcMakers[3].setVisibility(View.GONE);
                    _Img_Done.setVisibility(view.VISIBLE);
                } else if (makeLyrs.size() != 0) {
                    int nextTime = mediaP.getCurrentPosition() / 1000;
                    int mm = nextTime / 60;
                    int ss = nextTime % 60;
                    String temp = "";
                    if (ss < 10 && mm < 10) {
                        temp = "[" + "0" + mm + ":" + "0" + ss + "]";
                    } else if (mm < 10) {
                        temp = "[" + "0" + mm + ":" + ss + "]";
                    } else if (ss < 10) {
                        temp = "[" + mm + ":" + "0" + ss + "]";
                    } else {
                        temp = "[" + mm + ":" + ss + "]";
                    }

                    makeLyrs.get(textCounter).setTick(true);
                    makeLyrs.get(textCounter).setText(temp + makeLyrs.get(textCounter).getText());
                    makeRecycleAdapter.notifyDataSetChanged();
                    cPositon = mediaP.getCurrentPosition() / 1000;
                    textCounter++;
                }
            }
        });

        //////upload lyric
        ImageLrcMakers[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PastlyricActivity.class);
                i.putExtra("Position", nom);
                startActivity(i);
            }
        });

        /////event for dragging sickbar
        _LrcSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    mediaP.seekTo(i * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        MakeLrcActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {

                if (mediaP != null) {
                    double mCurrentPosition = mediaP.getCurrentPosition() / 1000;
                    _Txt_Length.setText(GetTime(mCurrentPosition));
                    _LrcSeekBar.setProgress((int) mCurrentPosition);
                }
                mHandler.postDelayed(this, 1000);
            }
        });


        _Img_Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int y = 0; y < makeLyrs.size(); y++) {
                    musicss.get(nom).getMadeLyrics().add(makeLyrs.get(y).getText());
                }
            }
        });
    }


    public String GetTime(double m) {
        int mm = (int) m / 60;
        int ss = (int) m % 60;
        return mm + ":" + ss + "/" + ttim;
    }

    public void Initalize() {
        _Toolb_MakeLyric = (Toolbar) findViewById(R.id._Toolb_MakeLyric);
        _Lrc_Maker_List = (RecyclerView) findViewById(R.id._Lrc_List_Lrc);
        //////config the recyclerview
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        _Lrc_Maker_List.setLayoutManager(mLayoutManager);
        _Lrc_Maker_List.setItemAnimator(new DefaultItemAnimator());
        /////end of the config
        ////
        _Txt_Length = (TextView) findViewById(R.id._Txt_lentgh_Lrc);
        ///
        ImageLrcMakers[0] = (ImageView) findViewById(R.id._Img_BackDetail_Lrc);
        ImageLrcMakers[1] = (ImageView) findViewById(R.id._Img_Play_Stop_Detail_Lrc);
        ImageLrcMakers[2] = (ImageView) findViewById(R.id._Img_NextDetail_Lrc);
        ImageLrcMakers[3] = (ImageView) findViewById(R.id._Img_LoadFile);
        _Img_Done = (ImageView) findViewById(R.id._Img_Done);
        _LrcSeekBar = (SeekBarHint) findViewById(R.id._seek_lentgh_Lrc);
    }

    public void SplitString() {
        String s = lyric;
        mylyr = new ArrayList<>(Arrays.asList(s.split("\\n")));
        makeLyrs = new ArrayList<>();
        for (int i = 0; i < mylyr.size(); i++) {
            makeLyrs.add(new MakeLyr(mylyr.get(i), false));
        }
        makeRecycleAdapter = new MakeRecycleAdapter(makeLyrs);
        _Lrc_Maker_List.setAdapter(makeRecycleAdapter);
        lyric = "";

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!lyric.equals(""))
            SplitString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    public void MakePause() {
        Intent i = new Intent(getApplicationContext(), PlayService.class);
        if (isPlaying) {
            try {
                i.putExtra("mode", "pause");
                PlayService.notimanger.cancelAll();
            } catch (Exception ex) {
            }
        } else {
            i.putExtra("mode", "cont");

        }
        startService(i);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaP.stop();
        MakePause();
    }


}
