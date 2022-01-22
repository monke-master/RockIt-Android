package com.brigade.rockit.database;

import com.brigade.rockit.data.Song;

public class DatabaseSong {
    private String authorId;
    private String name;
    private String duration;
    private String cover;
    private String genre;
    private String date;
    private long added;
    private long auditions;
    private String album;


    public DatabaseSong(Song song) {
        this.name = song.getName();
        this.authorId = song.getAuthor().getId();
        this.duration = song.getDuration();
        this.genre = song.getGenre();
        this.date = song.getDate();
        this.added = song.getAdded();
        this.auditions = song.getAuditions();
        this.cover = song.getCoverPath();
        this.album = song.getAlbum();
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getAdded() {
        return added;
    }

    public void setAdded(long added) {
        this.added = added;
    }

    public long getAuditions() {
        return auditions;
    }

    public void setAuditions(long auditions) {
        this.auditions = auditions;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
