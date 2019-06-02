package com.example.cor.mjplayer;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by M3000j on 12/22/2017.
 */

public class MakeRecycleAdapter extends RecyclerView.Adapter<MakeRecycleAdapter.ViewHolder> {

    private List<MakeLyr> myplyr;

    public MakeRecycleAdapter(List<MakeLyr> plyr) {
        myplyr = plyr;
    }

    @Override
    public MakeRecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lrcmakeritem_layout, parent, false);
        return new MakeRecycleAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MakeRecycleAdapter.ViewHolder holder, int position) {
        holder._Txt_Lyr.setText(myplyr.get(position).getText());
        if (myplyr.get(position).isTick()) {
            holder._Img_Tick.setVisibility(View.VISIBLE);
        } else {
            holder._Img_Tick.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return myplyr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView _Txt_Lyr;
        ImageView _Img_Tick;

        public ViewHolder(View itemView) {
            super(itemView);
            _Txt_Lyr = (TextView) itemView.findViewById(R.id._Txt_Lyric_item);
            _Img_Tick = (ImageView) itemView.findViewById(R.id._Img_Tick);
        }
    }
}
