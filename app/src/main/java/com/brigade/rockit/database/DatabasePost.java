package com.brigade.rockit.database;

import java.util.ArrayList;

public class DatabasePost {
    private String date;
    private String authorId;
    private ArrayList<String> songsIds;
    private ArrayList<String> imageIds;
    private String text;

    public DatabasePost() {
        songsIds = new ArrayList<>();
        imageIds = new ArrayList<>();
        text = "";
    }


    public void setDate(String date) {
        this.date = date;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public void setSongsIds(ArrayList<String> songsIds) {
        this.songsIds = songsIds;
    }

    public void setImageIds(ArrayList<String> imageIds) {
        this.imageIds = imageIds;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public String getAuthorId() {
        return authorId;
    }

    public ArrayList<String> getSongsIds() {
        return songsIds;
    }

    public ArrayList<String> getImageIds() {
        return imageIds;
    }

    public String getText() {
        return text;
    }
}
