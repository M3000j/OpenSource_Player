package com.example.cor.mjplayer;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcab.MaterialCab;
import com.squareup.picasso.Picasso;
import com.vansuita.gaussianblur.GaussianBlur;

import java.util.ArrayList;
import java.util.List;

import static com.example.cor.mjplayer.PlayListFragment.counter;
import static com.example.cor.mjplayer.PlayService.mediaMetadataRetriever;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private ViewPager mViewPager;
    public static Toolbar toolb;
    public static MaterialCab multi_select_cab;
    public static boolean isPlaying;
    public static boolean isMainPause = false;
    static ImageView _Img_Background;////background of screen
    static ImageView _Img_Picture;////image of small image view in the bottom
    static TextView _Txt_MusicName;
    static TextView _Txt_ArtistName;
    public static List<Music> musicss = new ArrayList<>();
    /*
    musicss used as temp value // all of the playlists musics,searches stores in the musics
    this app is based on the playlist clicking
     */
    public static List<Music> tempofMusics = new ArrayList<>();////////using for search in all of items
    RecyclerView _Rv_Musics;
    public static MusicAdpater musicAdpater;///////////fullfilling the recyclers view;
    Boolean next = true;
    String[] Projection;
    Integer[] columns;
    String[] permissions = new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static ImageView MusicPlayPause;//////using imageviews as button
    Button _Btn_Next;
    Button _Btn_Pre;
    static Context context;
    static byte[] msImage;
    ImageView _Img_Pic;
    public static boolean active = true;
    public static int musicPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Initilaze();

        context = getApplicationContext();
        mediaMetadataRetriever = new MediaMetadataRetriever();
        ChangeImageIcon();//////this function is used to change the play/pause button at the bottom
        //////define the toolbar
        toolb.setTitle("Music");
        toolb.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolb);
        ////////////////////////////////////////
        //###regin for event
        //this event is for play/pause the music
        MusicPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView imgTemp = (ImageView) view;
                Intent i = new Intent(getApplicationContext(), PlayService.class);
                if (isPlaying) {
                    try {
                        imgTemp.setImageResource(R.drawable.ic_play_circle_outline_white);
                        i.putExtra("mode", "pause");
                    } catch (Exception ex) {
                     //   Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    imgTemp.setImageResource(R.drawable.ic_pause_circle_outline);
                    i.putExtra("mode", "cont");

                }
                startService(i);
            }
        });

        /////this event is to play the previous music
        _Btn_Pre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PlayService.class);

                i.putExtra("mode", "play");
                i.putExtra("LMusics", (ArrayList<? extends Parcelable>) musicss);
                MainActivity.musicPosition--;

                i.putExtra("mposition", musicPosition);
                startService(i);
            }
        });
        ////////////
        _Btn_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), PlayService.class);
                i.putExtra("mode", "play");
                i.putExtra("LMusics", (ArrayList<? extends Parcelable>) musicss);
                musicPosition++;
                i.putExtra("mposition", musicPosition);
                startService(i);
            }
        });
        _Img_Pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(), Music_Detail.class);
                it.putExtra("mp", musicPosition);
                startActivity(it);
            }
        });///////////////////going to music_detail activity
        /////+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        columns = new Integer[6];/////////////////for getting data from mediastore database
        _Rv_Musics = (RecyclerView) findViewById(R.id._Rc_MusicItems);
        _Txt_MusicName.setSelected(true);
        _Txt_ArtistName.setSelected(true);
        ///////////////////intialize the recyclerview
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        _Rv_Musics.setLayoutManager(mLayoutManager);
        _Rv_Musics.setItemAnimator(new DefaultItemAnimator());
        _Rv_Musics.setHasFixedSize(true);
        _Rv_Musics.setItemViewCacheSize(100);
        musicAdpater = new MusicAdpater(musicss);
        _Rv_Musics.setAdapter(musicAdpater);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        Addmusic();

        ChangeShowInfo(MainActivity.musicPosition);

    }

    @Override
    protected void onResume() {
        super.onResume();
        isMainPause = false;
        ChangeShowInfo(MainActivity.musicPosition);
    }

    public static void ChangeImageIcon() {
        if (isPlaying) {
            MusicPlayPause.setImageResource(R.drawable.ic_pause_circle_outline);
        } else {
            MusicPlayPause.setImageResource(R.drawable.ic_play_circle_outline_white);
        }
    }

    public void Initilaze() {
        _Img_Background = (ImageView) findViewById(R.id._Img_Background);
        _Img_Picture = (ImageView) findViewById(R.id._Img_Music_Picture);
        //////initialize the toolbars
        toolb = (Toolbar) findViewById(R.id._toolb);
        multi_select_cab = new MaterialCab(MainActivity.this, R.id._multi_select_cab);
        multi_select_cab.setBackgroundColor(getResources().getColor(R.color.colorCabTransparent));
        _Txt_ArtistName = (TextView) findViewById(R.id._Txt_Artist_Name);
        _Txt_MusicName = (TextView) findViewById(R.id._Txt_Music_Name);
        MusicPlayPause = (ImageView) findViewById(R.id._Img_Play_Stop);
        _Btn_Next = (Button) findViewById(R.id._Img_Next);
        _Btn_Pre = (Button) findViewById(R.id._Img_Back);
        _Img_Pic = (ImageView) findViewById(R.id._Img_Music_Picture);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setQueryHint("Enter Music Title");
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                musicPosition = MusicAdpater.ttt;
                musicss.addAll(MainActivity.tempofMusics);
                musicAdpater.notifyDataSetChanged();
                return false;
            }
        });
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (multi_select_cab.isActive()) {
            multi_select_cab.finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isMainPause = true;
    }

    public static void ChangeShowInfo(int mp) {
        String name = musicss.get(mp).getName();
        String artistName = musicss.get(mp).getArtist();
        _Txt_ArtistName.setText(artistName);
        _Txt_MusicName.setText(name);
        mediaMetadataRetriever.setDataSource(musicss.get(mp).getUri());
        msImage = mediaMetadataRetriever.getEmbeddedPicture();
        if (msImage != null) {
            Bitmap songImage = BitmapFactory
                    .decodeByteArray(msImage, 0, msImage.length);
            _Img_Picture.setImageBitmap(Bitmap.createScaledBitmap(songImage, 150, 150, false));
            Bitmap blur = GaussianBlur.with(context).render(songImage);
            _Img_Background.setImageBitmap(blur);
        } else {
            Picasso.with(context).load(R.drawable.musicfill).resize(120, 120).into(_Img_Picture);
            _Img_Background.setImageBitmap(null);
            _Img_Background.setBackgroundColor(Color.DKGRAY);
        }
        if (isPlaying) {
            MusicPlayPause.setImageResource(R.drawable.ic_pause_circle_outline);
        } else {
            MusicPlayPause.setImageResource(R.drawable.ic_play_circle_outline_white);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        //Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        musicAdpater.getFilter().filter(s);
        return true;
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    PlayListFragment playListFragment = new PlayListFragment();
                    return playListFragment;
                case 1:
                    AlbumFragment albumFragment = new AlbumFragment();
                    return albumFragment;
                case 2:
                    ArtistFragment artistFragment = new ArtistFragment();
                    return artistFragment;
            }
            ArtistFragment artistFragment = new ArtistFragment();
            return artistFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "My Playlists";
                case 1:
                    return "My Albums";
                case 2:
                    return "My Artists";
            }
            return null;
        }
    }

    //////adding musics to the musiclist
    public void Addmusic() {
        musicss.clear();
        musicAdpater.notifyDataSetChanged();
        ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        //////////////////////////the columns the we want to retriv'e from media store database/////////////////////////////
        Projection = new String[]{
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM
                , MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION};
        try {
            Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Projection, null, null, null);

            cursor.moveToFirst();

            for (int i = 0; i < 6; i++) {
                columns[i] = cursor.getColumnIndex(Projection[i]);
            }

            Music tempMusic = new Music();

            while (next) {
                tempMusic.setName(cursor.getString(columns[0]));
                tempMusic.setAlbum(cursor.getString(columns[1]));
                tempMusic.setArtist(cursor.getString(columns[2]));
                tempMusic.setUri(cursor.getString(columns[3]));
                tempMusic.setAlbumId(cursor.getString(columns[4]));
                tempMusic.setSize(cursor.getDouble(columns[5]));

                if (!cursor.moveToNext()) {
                    next = false;
                }
                musicss.add(tempMusic);

                tempMusic = new Music();
            }
            if (counter == 0) {
                tempofMusics.addAll(musicss);
                PlayList temp = new PlayList(musicss);
                temp.setName("AllTracks");
                PlayListFragment.playLists.add(0, temp);
                PlayListFragment.playListAdapter.notifyDataSetChanged();
                musicAdpater.notifyDataSetChanged();
            }
            System.gc();
            cursor.close();/////end of reading files from media store databse
        } catch (Exception ex) {

        }
    }

}
