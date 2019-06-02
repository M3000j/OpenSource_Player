package com.example.cor.mjplayer;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import static com.example.cor.mjplayer.Music_Detail.tbold;

/**
 * Created by M3000j on 12/19/2017.
 */

public class LyricAdapter extends RecyclerView.Adapter<LyricAdapter.MyviewHolder> {
    private List<String> lyrics;
    private int[] timess;
    private boolean time;

    public LyricAdapter(List<String> li, int[] ti, boolean tie) {
        lyrics = li;
        timess = ti;
        time = tie;////if the lyrics has time or not
    }

    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lyrictext, parent, false);

        return new MyviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyviewHolder holder, int position) {
        holder._Txt_Lyric.setText(lyrics.get(position));
        if (time) {
            if (tbold == position) {
                holder._Txt_Lyric.setTextColor(Color.BLACK);
            } else {
                holder._Txt_Lyric.setTextColor(Color.parseColor("#757575"));
            }
        }

    }

    @Override
    public int getItemCount() {
        return lyrics.size();
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView _Txt_Lyric;
        public MyviewHolder(View itemView) {
            super(itemView);
            _Txt_Lyric = (TextView) itemView.findViewById(R.id._Txt_Lyric);
        }
    }
}
