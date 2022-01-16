package com.brigade.rockit.data;

import com.brigade.rockit.database.DatabaseUser;

public class Data {
    private static DatabaseUser newUser;
    private static User curUser;
    private static Post newPost;
    private static Song newSong;
    private static MusicPlayer musicPlayer;
    private static Playlist newPlaylist;

    public static DatabaseUser getNewUser() {
        return newUser;
    }

    public static void setNewUser(DatabaseUser newUser) {
        Data.newUser = newUser;
    }

    public static User getCurUser() {
        return curUser;
    }

    public static void setCurUser(User curUser) {
        Data.curUser = curUser;
    }

    public static Post getNewPost() {
        return newPost;
    }

    public static void setNewPost(Post post) { Data.newPost = post;}

    public static Song getNewMusic() {
        return newSong;
    }

    public static void setNewMusic(Song newSong) {
        Data.newSong = newSong;
    }

    public static MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public static void setMusicPlayer(MusicPlayer musicPlayer) {
        Data.musicPlayer = musicPlayer;
    }

    public static Playlist getNewPlaylist() {
        return newPlaylist;
    }

    public static void setNewPlaylist(Playlist newPlaylist) {
        Data.newPlaylist = newPlaylist;
    }
}
