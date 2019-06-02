package com.example.cor.mjplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by M3000 on 10/11/2017.
 */

public class PlayListFragment extends Fragment {
    RecyclerView recyclerView;
    public static PlayListAdapter playListAdapter;
    public static List<PlayList> playLists = new ArrayList<>();
    List<Music> musicsa = new ArrayList<>();
    static int counter = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlist_fragment, container, false);

        for (int i = 0; i < playLists.size(); i++) {
            if (playLists.get(i).getMusics().size() == 0) {
                counter += 1;
            }
        }
        ////////////avoid making lot items for playi
        recyclerView = (RecyclerView) view.findViewById(R.id._Rw_PlayList);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        mLayoutManager.setStackFromEnd(false);
        playListAdapter = new PlayListAdapter(playLists);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setAdapter(playListAdapter);
        if (counter == 0) {
            playLists.add(0, new PlayList(musicsa));
            playListAdapter.notifyDataSetChanged();
        }
        return view;
    }
}
