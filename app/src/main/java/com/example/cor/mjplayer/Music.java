package com.example.cor.mjplayer;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by M3000 on 10/12/2017.
 */

public class Music implements Parcelable {


    private String name;
    private String album;
    private String artist;
    private String uri;
    private String albumId;
    private String lyric = null;
    private List<String> madeLyrics = new ArrayList<>();

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    private double size;
    private double duritation;

    public double getDuritation() {
        return duritation;
    }

    public void setDuritation(double duritation) {
        this.duritation = duritation;
    }

    private int id;
    private boolean selected;

    protected Music(Parcel in) {
        name = in.readString();
        album = in.readString();
        artist = in.readString();
        uri = in.readString();
        size = in.readDouble();
        id = in.readInt();
        duritation = in.readDouble();
        selected = in.readByte() != 0;
    }

    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public List<String> getMadeLyrics() {
        return madeLyrics;
    }


    public Music() {
        madeLyrics = new ArrayList<>();
        madeLyrics.add("Add lyric");
    }

    public Music(String name, String album, String artist, String uri, double sizee) {
        this.name = name;
        this.album = album;
        this.artist = artist;
        this.uri = uri;
        this.size = sizee;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public double getSize() {
        return size / 1000;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(album);
        parcel.writeString(artist);
        parcel.writeString(uri);
        parcel.writeDouble(size);
        parcel.writeInt(id);
        parcel.writeDouble(duritation);
        parcel.writeByte((byte) (selected ? 1 : 0));
    }
}
