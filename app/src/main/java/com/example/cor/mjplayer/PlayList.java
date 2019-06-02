package com.example.cor.mjplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by M3000 on 10/13/2017.
 */

public class PlayList {

    private List<Music> musics = new ArrayList<>();
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlayList() {
    }

    public PlayList(List<Music> musics) {
        this.musics=new ArrayList<>(musics);
    }

    public List<Music> getMusics() {
        return musics;
    }

}
