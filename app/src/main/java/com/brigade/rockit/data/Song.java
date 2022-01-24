package com.brigade.rockit.data;

import android.net.Uri;

import com.brigade.rockit.database.ContentManager;

import java.util.Objects;

public class Song {
    private Uri uri;
    private User author;
    private String name;
    private Uri coverUri;
    private String duration;
    private String id;
    private Genre genre;
    private String date;
    private long added;
    private long auditions;
    private String album;
    private String coverPath;


    public Song() {
        auditions = 0;
        added = 0;
    }


    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getCoverUri() {
        return coverUri;
    }

    public void setCoverUri(Uri coverUri) {
        this.coverUri = coverUri;
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
        Song song = (Song) o;
        return Objects.equals(id, song.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
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

    public void increaseAuditions() {
        auditions++;
        ContentManager contentManager = new ContentManager();
        contentManager.increaseAuditions("songs", id);
        if (album != null)
            contentManager.increaseAuditions("albums", album);
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbum() {
        return album;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }
}
