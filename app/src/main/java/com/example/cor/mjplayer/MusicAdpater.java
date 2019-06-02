package com.example.cor.mjplayer;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcab.MaterialCab;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.example.cor.mjplayer.PlayListAdapter.name;

/**
 * Created by M3000 on 10/13/2017.
 */

public class MusicAdpater extends RecyclerView.Adapter<MusicAdpater.Myholder> implements Filterable {
    private List<Music> musics;
    private List<Music> tempMusics;///////////for making playlists
    public static List<Music> filtermusic;
    private boolean ismlutiselect = false;
    public static int ttt;
    private int multi_select_counter = 0;
    private boolean isplaylist = false;
    private boolean isSelectall = false;
    private Music temp;
    private MediaMetadataRetriever mediaMetadataRetriever;


    public boolean isSelectall() {
        return isSelectall;
    }

    public void setSelectall(boolean selectall) {
        isSelectall = selectall;
    }


    public void setIsplaylist(boolean isplaylist) {
        this.isplaylist = isplaylist;
    }

    public void setIsmlutiselect(boolean ismlutiselect) {
        this.ismlutiselect = ismlutiselect;
    }

    public MusicAdpater(List<Music> musics) {
        this.musics = musics;
        tempMusics = new ArrayList<>();
        mediaMetadataRetriever = new MediaMetadataRetriever();

    }

    @Override
    public Myholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.musicitem, parent, false);
        return new MusicAdpater.Myholder(itemView);
    }

    @Override
    public void onBindViewHolder(final Myholder holder, final int position) {
        ttt = position;
        if (isplaylist) {
            holder._selectCheckBox.setVisibility(View.VISIBLE);
            if (musics.get(position).isSelected()) {
                holder._selectCheckBox.setChecked(true);
            } else {
                holder._selectCheckBox.setChecked(false);
            }
            /////////click on the items to check for the
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (musics.get(position).isSelected()) {
                        musics.get(position).setSelected(false);
                        holder._selectCheckBox.setChecked(false);
                        MainActivity.multi_select_cab.setTitle(String.valueOf(--multi_select_counter));
                    } else {
                        musics.get(position).setSelected(true);
                        holder._selectCheckBox.setChecked(true);
                        MainActivity.multi_select_cab.setTitle(String.valueOf(++multi_select_counter));
                    }
                }
            });


            MainActivity.multi_select_cab.start(new MaterialCab.Callback() {
                @Override
                public boolean onCabCreated(MaterialCab cab, Menu menu) {
                    MainActivity.toolb.setVisibility(View.GONE);
                    cab.setTitle(String.valueOf(multi_select_counter));
                    cab.setMenu(R.menu.selectmenu);
                    return true;
                }

                //////////////////////make the playlist
                @Override
                public boolean onCabItemClicked(MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id._ic_done) {

                        for (Music ite :
                                musics) {
                            if (ite.isSelected()) {
                                tempMusics.add(ite);
                            }
                        }
                        PlayList templist = new PlayList(tempMusics);
                        templist.setName(name);
                        PlayListFragment.playLists.add(PlayListFragment.playLists.size(), templist);
                        PlayListFragment.playListAdapter.notifyDataSetChanged();
                        Toast.makeText(holder.itemView.getContext(), "Done", Toast.LENGTH_SHORT).show();
                        MainActivity.multi_select_cab.finish();
                        tempMusics.clear();
                    } else if (id == R.id._Ic_Chekall) {///////check and unckeck the items in recyclerview

                        if (!isSelectall()) {
                            for (Music ite :
                                    musics) {
                                ite.setSelected(true);
                            }
                            setSelectall(true);
                            multi_select_counter = musics.size();
                            MainActivity.multi_select_cab.setTitle(String.valueOf(multi_select_counter));
                        } else {
                            for (Music ite :
                                    musics) {
                                ite.setSelected(false);
                            }
                            setSelectall(false);
                            multi_select_counter = 0;
                            MainActivity.multi_select_cab.setTitle(String.valueOf(multi_select_counter));
                        }
                        notifyDataSetChanged();
                    }
                    return true;
                }

                @Override
                public boolean onCabFinished(MaterialCab cab) {
                    MainActivity.toolb.setVisibility(View.VISIBLE);
                    for (Music ite :
                            musics) {
                        ite.setSelected(false);
                    }
                    ismlutiselect = false;
                    isSelectall = false;
                    isplaylist = false;
                    multi_select_counter = 0;
                    notifyDataSetChanged();
                    return true;
                }
            });

//////////////////end of is playlist
        } else {
            if (ismlutiselect) {
                holder._selectCheckBox.setVisibility(View.VISIBLE);
                if (musics.get(position).isSelected())
                    holder._selectCheckBox.setChecked(true);

            } else {
                holder._selectCheckBox.setVisibility(View.GONE);
                holder._selectCheckBox.setChecked(false);
            }

            ////event for long touch recyclerview
            ////enable multiselect in recycerview
            holder.linearLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    if (!ismlutiselect) {
                        /////show multiple selections and callback
                        holder._selectCheckBox.setVisibility(View.VISIBLE);
                        ismlutiselect = true;
                        //////////select the touched item
                        musics.get(position).setSelected(true);

                        ///////set the cab events int the callback set

                        MainActivity.multi_select_cab.start(new MaterialCab.Callback() {
                            @Override
                            public boolean onCabCreated(MaterialCab cab, Menu menu) {
                                MainActivity.toolb.setVisibility(View.GONE);
                                cab.setMenu(R.menu.edit_menu);
                                return true;
                            }

                            @Override
                            public boolean onCabItemClicked(MenuItem item) {

                                return true;
                            }

                            @Override
                            public boolean onCabFinished(MaterialCab cab) {

                                for (Music item : musics) {
                                    if (item.isSelected()) {
                                        item.setSelected(false);
                                    }
                                }
                                ismlutiselect = false;
                                multi_select_counter = 0;
                                notifyDataSetChanged();
                                MainActivity.toolb.setVisibility(View.VISIBLE);

                                return true;
                            }
                        });

                        MainActivity.multi_select_cab.setTitle(String.valueOf(++multi_select_counter));
                        notifyDataSetChanged();
                    }
                    return true;
                }
            });

            /////event for single touch/click on the recyclerview item
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ismlutiselect) {
                        ////////select and deselect of checkbox via linearlayout in multiselect mode
                        Toast.makeText(holder.itemView.getContext(), "Ggg", Toast.LENGTH_LONG).show();
                        if (musics.get(position).isSelected()) {
                            musics.get(position).setSelected(false);
                            holder._selectCheckBox.setChecked(false);
                            MainActivity.multi_select_cab.setTitle(String.valueOf(--multi_select_counter));
                        } else {
                            musics.get(position).setSelected(true);
                            holder._selectCheckBox.setChecked(true);
                            MainActivity.multi_select_cab.setTitle(String.valueOf(++multi_select_counter));
                        }
                    } else {
                        Intent playmusic = new Intent(holder.itemView.getContext(), PlayService.class);
                        MainActivity.musicPosition = position;
                        playmusic.putExtra("mode", "play");
                        playmusic.putExtra("mposition", position);
                        playmusic.putParcelableArrayListExtra("LMusics", (ArrayList<? extends Parcelable>) musics);
                        MainActivity.isPlaying = true;
                        MainActivity.ChangeImageIcon();
                        holder.itemView.getContext().startService(playmusic);
                        Toast.makeText(holder.itemView.getContext(), "Playing", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        mediaMetadataRetriever.setDataSource(musics.get(position).getUri());////for getting the mussics image
        byte[] musicimages;
        musicimages = mediaMetadataRetriever.getEmbeddedPicture();
        if (musicimages != null) {
            Bitmap songImage = BitmapFactory
                    .decodeByteArray(musicimages, 0, musicimages.length);
            holder._Img_Music.setImageBitmap(Bitmap.createScaledBitmap(songImage, 130, 130, false));
        } else {
            Picasso.with(holder.itemView.getContext()).load(R.drawable.musicfill).resize(125, 125).into(holder._Img_Music);
        }

        temp = musics.get(position);
        holder._Txt_Music_Name.setText(musics.get(position).getName());
        holder._Txt_Music_Artist.setText(musics.get(position).getArtist());
        holder._Txt_Music_Artist.setSelected(true);
        holder._Txt_Music_Name.setSelected(true);

    }

    @Override
    public int getItemCount() {
        return musics.size();
    }

    @Override
    public Filter getFilter() {
        final Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                String searchName = charSequence.toString();
                filtermusic = new ArrayList<>();
                if (searchName.equalsIgnoreCase("")) {
                    ttt = MainActivity.tempofMusics.indexOf(musics.get(ttt));
                } else {
                    for (Music i : MainActivity.tempofMusics) {
                        if (i.getName().toLowerCase().contains(searchName)) {
                            filtermusic.add(i);
                        }
                    }
                }
                filterResults.values = filtermusic;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults == null) {
                    return;
                }
                musics.clear();
                musics.addAll((ArrayList<Music>) filterResults.values);
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public class Myholder extends RecyclerView.ViewHolder {
        TextView _Txt_Music_Name;
        TextView _Txt_Music_Artist;
        ImageView _Img_Music;
        CheckBox _selectCheckBox;
        LinearLayout linearLayout;

        public Myholder(View itemView) {
            super(itemView);
            _Txt_Music_Artist = (TextView) itemView.findViewById(R.id._Txt_Artist_NameSingle);
            _Txt_Music_Name = (TextView) itemView.findViewById(R.id._Txt_MusicName_Single);
            ////single in the above lines mean that is for showing music items in recycler view
            _Img_Music = (ImageView) itemView.findViewById(R.id._Img_Music_Single);
            _selectCheckBox = (CheckBox) itemView.findViewById(R.id._CheckBox_select);
            linearLayout = (LinearLayout) itemView.findViewById(R.id._Linear_Music);
        }
    }
}
