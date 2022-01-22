package com.brigade.rockit.data;

import android.net.Uri;

import java.util.ArrayList;

public class Playlist {

    private String name;
    private User author;
    private Uri coverUri;
    private ArrayList<Song> songs;
    private ArrayList<String> songIds;
    private String date;
    private String description;
    private String id;
    private long added;
    private String duration;

    public Playlist() {
        name = "";
        description = "";
        songs = new ArrayList<>();
        songIds = new ArrayList<>();
        added = 0;
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

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
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

    public long getAdded() {
        return added;
    }

    public void setAdded(long added) {
        this.added = added;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


}
