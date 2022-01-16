package com.brigade.rockit.data;

import android.net.Uri;

import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.TaskListener;

import java.util.Objects;

public class Song {
    private Uri uri;
    private User author;
    private String name;
    private Uri cover;
    private String duration;
    private String id;
    private String genre;
    private String date;
    private long added;
    private long auditions;
    private Playlist playlist;
    private Album album;


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

    public Uri getCover() {
        return cover;
    }

    public void setCover(Uri cover) {
        this.cover = cover;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
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
        if (playlist != null)
            contentManager.increaseAuditions("playlists", playlist.getId());
        if (album != null)
            contentManager.increaseAuditions("albums", album.getId());
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }
}
