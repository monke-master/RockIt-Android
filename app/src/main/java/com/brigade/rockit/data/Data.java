package com.brigade.rockit.data;

import com.brigade.rockit.database.DatabaseUser;

public class Data {
    private static DatabaseUser newUser;
    private static User curUser;
    private static Post curPost;
    private static Music curMusic;
    private static MusicPlayer musicPlayer;

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

    public static Post getCurPost() {
        return curPost;
    }

    public static void setCurPost(Post post) { Data.curPost = post;}

    public static Music getCurMusic() {
        return curMusic;
    }

    public static void setCurMusic(Music curMusic) {
        Data.curMusic = curMusic;
    }

    public static MusicPlayer getMusicPlayer() {
        return musicPlayer;
    }

    public static void setMusicPlayer(MusicPlayer musicPlayer) {
        Data.musicPlayer = musicPlayer;
    }
}
