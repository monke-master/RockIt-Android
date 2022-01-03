package com.brigade.rockit.data;

import android.net.Uri;

import com.brigade.rockit.database.DatabasePlaylist;

import java.util.ArrayList;

public class Playlist {

    private String name;
    private User author;
    private Uri coverUri;
    private ArrayList<Music> songs;
    private ArrayList<String> songIds;
    private String date;
    private String description;
    private String id;

    public Playlist() {
        name = "";
        description = "";
        songs = new ArrayList<>();
        songIds = new ArrayList<>();
    }

    public Playlist(DatabasePlaylist dbPlaylist) {
        this.name = dbPlaylist.getName();
        this.date = dbPlaylist.getDate();
        this.songIds = dbPlaylist.getSongs();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Uri getCoverUri() {
        return coverUri;
    }

    public void setCoverUri(Uri coverUri) {
        this.coverUri = coverUri;
    }

    public ArrayList<Music> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Music> songs) {
        this.songs = songs;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<String> getSongIds() {
        return songIds;
    }

    public void setSongIds(ArrayList<String> songIds) {
        this.songIds = songIds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
