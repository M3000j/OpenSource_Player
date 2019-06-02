package com.example.cor.mjplayer;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by M3000 on 10/13/2017.
 */

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.Myholder> {

    private List<PlayList> playLists;
    public static String name;

    public PlayListAdapter(List<PlayList> playLists) {
        this.playLists = playLists;
    }

    @Override
    public Myholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.playlistitem, parent, false);

        return new Myholder(itemView);
    }

    @Override
    public void onBindViewHolder(final Myholder holder, final int position) {

        if (playLists.get(position).getMusics().size() == 0) {
            holder.frameLayout.setVisibility(View.GONE);
            holder._Img_add.setVisibility(View.VISIBLE);
            holder._Txt_Name.setText("Create PlayList");
            holder._Txt_Name.setSelected(true);
            holder._Img_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final View view1 = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.alert_playlist_dialog, null);
                    AlertDialog alertDialog = new AlertDialog.Builder(holder.itemView.getContext())
                            .setTitle("Name your playlist")
                            .setView(view1)
                            .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    EditText editText = (EditText) view1.findViewById(R.id._Etx_PlayList_Name);
                                    name = editText.getText().toString();
                                    MainActivity.musicAdpater.setIsplaylist(true);
                                    MainActivity.musicAdpater.setIsmlutiselect(true);
                                    MainActivity.musicAdpater.notifyDataSetChanged();
                                    Toast.makeText(holder.itemView.getContext(), "Added", Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            })
                            .setCancelable(true)
                            .create();
                    alertDialog.show();
                }
            });
        } else {
            holder.frameLayout.setVisibility(View.VISIBLE);
            holder.frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        MainActivity.musicss.clear();
                        MainActivity.musicAdpater.notifyDataSetChanged();
                        List<Music> tempmusic = playLists.get(position).getMusics();
                        for (int i = 0; i < tempmusic.size(); i++) {
                            MainActivity.musicss.add(tempmusic.get(i));
                            MainActivity.musicAdpater.notifyDataSetChanged();
                        }
                    } catch (Exception ex) {
                        Toast.makeText(holder.itemView.getContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder._Img_add.setVisibility(View.GONE);
            holder._Txt_Count.setText(String.valueOf(playLists.get(position).getMusics().size()));
            holder._Txt_Name.setText(playLists.get(position).getName());
            holder._Txt_Name.setSelected(true);
            getBitMap(playLists.get(position).getMusics().get(0).getUri(), holder);
        }
    }

    private void getBitMap(String urii, Myholder mh) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(urii);
        byte[] bytes = mmr.getEmbeddedPicture();
        Bitmap bitmap;
        if (bytes != null) {
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            mh._Img_Playlist_Picture.setImageBitmap(bitmap);
            return;
        } else {
            Picasso.with(mh.itemView.getContext()).load(R.drawable.no_music_found).into(mh._Img_Playlist_Picture);
        }
    }

    @Override
    public int getItemCount() {
        return playLists.size();
    }

    public class Myholder extends RecyclerView.ViewHolder {
        ImageView _Img_Playlist_Picture;
        ImageView _Img_add;
        FrameLayout frameLayout;
        TextView _Txt_Count;
        TextView _Txt_Name;

        public Myholder(View itemView) {
            super(itemView);
            _Img_Playlist_Picture = (ImageView) itemView.findViewById(R.id._Img_PlayList);
            _Txt_Count = (TextView) itemView.findViewById(R.id._Txt_PlayList_Count);
            _Img_add = (ImageView) itemView.findViewById(R.id._Img_Add_Item);
            frameLayout = (FrameLayout) itemView.findViewById(R.id._Fram_Items);
            _Txt_Name = (TextView) itemView.findViewById(R.id._Txt_Playlist_Name);
        }
    }
}
//}playLists.get(position).getMusics().get(0).getUri())
