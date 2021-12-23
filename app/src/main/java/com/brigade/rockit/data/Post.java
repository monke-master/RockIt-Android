package com.brigade.rockit.data;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;

public class Post {
    private User author;
    private String date;
    private String text;
    private ArrayList<Uri> imagesList;
    private ArrayList<Music> musicList;
    private String id;
    private ArrayList<String> imagesIds;
    private ArrayList<String> musicIds;

    public Post() {
        imagesList = new ArrayList<>();
        musicList = new ArrayList<>();
        imagesIds = new ArrayList<>();
        musicIds = new ArrayList<>();
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


    public void setMusicList(ArrayList<Music> musicList) {
        this.musicList = musicList;
    }


    public ArrayList<Uri> getImagesList() {
        return imagesList;
    }

    public ArrayList<Music> getMusicList() {
        return musicList;
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

    public ArrayList<String> getMusicIds() {
        return musicIds;
    }

    public void setMusicIds(ArrayList<String> musicIds) {
        this.musicIds = musicIds;
    }
}
