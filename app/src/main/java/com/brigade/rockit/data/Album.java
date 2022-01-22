package com.brigade.rockit.data;

import android.net.Uri;

import java.util.ArrayList;

public class Album {

    private String name;
    private User author;
    private Uri coverUri;
    private String coverPath;
    private ArrayList<Song> songs;
    private ArrayList<String> songIds;
    private String date;
    private String genre;
    private String id;
    private long auditions;
    private String duration;

    public Album() {
        songs = new ArrayList<>();
        auditions = 0;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getAuditions() {
        return auditions;
    }

    public void setAuditions(long auditions) {
        this.auditions = auditions;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }
}
