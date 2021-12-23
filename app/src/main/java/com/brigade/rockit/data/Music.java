package com.brigade.rockit.data;

import android.net.Uri;

import com.brigade.rockit.database.DatabaseMusic;

import java.util.Objects;

public class Music {
    private Uri uri;
    private String authorId;
    private String name;
    private String artist;
    private Uri cover;
    private String duration;
    private String id;

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
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

    public Uri getCover() {
        return cover;
    }

    public void setCover(Uri cover) {
        this.cover = cover;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Music music = (Music) o;
        return Objects.equals(id, music.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
