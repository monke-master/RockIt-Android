package com.brigade.rockit.database;

import com.brigade.rockit.data.Music;
import com.brigade.rockit.data.Playlist;

import java.util.ArrayList;

public class DatabasePlaylist {

    private String name;
    private String author;
    private String date;
    private String cover;
    private ArrayList<String> songs;
    private String description;

    public DatabasePlaylist() {

    }

    public DatabasePlaylist(Playlist playlist) {
        songs = new ArrayList<>();
        this.author = playlist.getAuthor().getId();
        this.name = playlist.getName();
        this.date = playlist.getDate();
        this.songs = playlist.getSongIds();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
