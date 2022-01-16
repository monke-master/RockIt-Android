package com.brigade.rockit.data;

import android.net.Uri;

import java.util.ArrayList;

public class Genre {

    private String name;
    private Uri picture;
    private ArrayList<String> subgenres;
    private String id;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getPicture() {
        return picture;
    }

    public void setPicture(Uri picture) {
        this.picture = picture;
    }

    public ArrayList<String> getSubgenres() {
        return subgenres;
    }

    public void setSubgenres(ArrayList<String> subgenres) {
        this.subgenres = subgenres;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
