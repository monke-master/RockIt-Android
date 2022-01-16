package com.brigade.rockit.database;

import com.brigade.rockit.data.Album;

import java.util.ArrayList;

public class DatabaseAlbum {

    private String name;
    private String author;
    private String date;
    private String cover;
    private ArrayList<String> songs;
    private String genre;
    private int added;
    private int auditions;
    private String duration;

    public DatabaseAlbum(Album album) {
        this.name = album.getName();
        this.author = album.getAuthor().getId();
        this.date = album.getDate();
        this.duration = album.getDuration();
        this.added = album.getAdded();
        this.genre = album.getGenre();
        this.auditions = album.getAuditions();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public ArrayList<String> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<String> songs) {
        this.songs = songs;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAdded() {
        return added;
    }

    public void setAdded(int added) {
        this.added = added;
    }

    public int getAuditions() {
        return auditions;
    }

    public void setAuditions(int auditions) {
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
}
