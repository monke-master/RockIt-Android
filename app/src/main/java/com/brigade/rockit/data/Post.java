package com.brigade.rockit.data;

import android.net.Uri;

import java.util.ArrayList;

public class Post {
    private User author;
    private String date;
    private String text;
    private ArrayList<Uri> imagesList;
    private ArrayList<Song> songList;
    private String id;
    private ArrayList<String> imagesIds;
    private ArrayList<String> songsIds;

    public Post() {
        imagesList = new ArrayList<>();
        songList = new ArrayList<>();
        imagesIds = new ArrayList<>();
        songsIds = new ArrayList<>();
        text = "";
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImagesList(ArrayList<Uri> imagesList) {
        this.imagesList = imagesList;
    }


    public void setMusicList(ArrayList<Song> songList) {
        this.songList = songList;
    }


    public ArrayList<Uri> getImagesList() {
        return imagesList;
    }

    public ArrayList<Song> getMusicList() {
        return songList;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }

    public User getAuthor() {
        return author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getImagesIds() {
        return imagesIds;
    }

    public void setImagesIds(ArrayList<String> imagesIds) {
        this.imagesIds = imagesIds;
    }

    public ArrayList<String> getSongsIds() {
        return songsIds;
    }

    public void setSongsIds(ArrayList<String> songsIds) {
        this.songsIds = songsIds;
    }
}
