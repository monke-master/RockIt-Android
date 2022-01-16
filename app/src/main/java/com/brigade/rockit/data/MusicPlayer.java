package com.brigade.rockit.data;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.ArrayList;

public class MusicPlayer {
    private MediaPlayer player;
    private Song song;
    private Context context;
    private ArrayList<Song> queue;
    private int curPosition;

    public MusicPlayer(Context context) {
        this.context = context;
        this.queue = new ArrayList<>();
        player = new MediaPlayer();
    }

    public void playSong(Song song) {
        this.song = song;
        player.stop();
        player = MediaPlayer.create(context, song.getUri());
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

    public Song getMusic() {
        return song;
    }

    public void setMusic(Song song) {
        this.song = song;
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

    public void setQueue(ArrayList<Song> queue) {
        this.queue = queue;
    }

    public void setCurPosition(int curPosition) {
        this.curPosition = curPosition;
    }

    public void playNext() {
        curPosition++;
        if (curPosition == queue.size())
            curPosition = 0;
        if (queue.size() > 0)
            playSong(queue.get(curPosition));
    }

    public void playPrevious() {
        curPosition--;
        if (curPosition == -1)
            curPosition = queue.size() - 1;
        playSong(queue.get(curPosition));
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public ArrayList<Song> getQueue() {
        return queue;
    }


}
