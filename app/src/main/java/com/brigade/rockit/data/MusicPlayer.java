package com.brigade.rockit.data;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;

public class MusicPlayer {
    private MediaPlayer player;
    private Music music;
    private Context context;
    private ArrayList<Music> queue;
    private int curPosition;

    public MusicPlayer(Context context) {
        this.context = context;
        this.queue = new ArrayList<>();
    }

    public void playSong(Music music) {
        this.music = music;
        player = MediaPlayer.create(context, music.getUri());
        player.start();
        player.setOnCompletionListener(mp -> {
            playNext();
        });
    }

    public void stopSong() {
        player.pause();
    }

    public void continueSong() {
        player.start();
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public int getPosition() {
        return player.getCurrentPosition();
    }

    public int getDuration() {
        return player.getDuration();
    }

    public void seekTo(int progress) {
        player.seekTo(progress);
    }

    public void setQueue(ArrayList<Music> queue) {
        this.queue = queue;
    }

    public void setCurPosition(int curPosition) {
        this.curPosition = curPosition;
    }

    public void playNext() {
        curPosition++;
        if (curPosition > queue.size())
            curPosition = 0;
        playSong(queue.get(curPosition));
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }
}
