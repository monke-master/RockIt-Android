package com.brigade.rockit.fragments.music;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.brigade.rockit.GlideApp;
import com.brigade.rockit.R;
import com.brigade.rockit.activities.PlayerActivity;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Song;

// Полноэкранный плеер
public class PlayerFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        PlayerActivity playerActivity = (PlayerActivity) getActivity();

        // Получение виджетов
        SeekBar seekBar = view.findViewById(R.id.seekBar);
        TextView nameTxt = view.findViewById(R.id.song_name_txt);
        TextView artistTxt = view.findViewById(R.id.author_txt);
        ImageView coverImg = view.findViewById(R.id.cover_img);
        ImageView playBtn = view.findViewById(R.id.play_btn);
        ImageView nextBtn = view.findViewById(R.id.next_btn);
        ImageView prevBtn = view.findViewById(R.id.previous_btn);

        // Отображение информации о песне
        Song song = Data.getMusicPlayer().getMusic();
        nameTxt.setText(song.getName());
        artistTxt.setText(song.getAuthor().getLogin());
        GlideApp.with(getContext()).load(song.getCoverUri()).into(coverImg);
        seekBar.setMax(Data.getMusicPlayer().getDuration() / 1000);

        // Содержимое кнопки в зависимости от состояния плеера
        if (!Data.getMusicPlayer().isPlaying())
            playBtn.setImageDrawable(playerActivity.getDrawable(R.drawable.play));
        playBtn.setOnClickListener(v -> {
            if (!Data.getMusicPlayer().isPlaying()) {
                Data.getMusicPlayer().continueSong();
                playBtn.setImageDrawable(playerActivity.getDrawable(R.drawable.pause));
            } else {
                Data.getMusicPlayer().stopSong();
                playBtn.setImageDrawable(playerActivity.getDrawable(R.drawable.play));
            }
        });

        nextBtn.setOnClickListener(v -> {
            Data.getMusicPlayer().playNext();
            playerActivity.setFragment(new PlayerPagerFragment());
        });
        prevBtn.setOnClickListener(v -> {
            Data.getMusicPlayer().playPrevious();
            playerActivity.setFragment(new PlayerPagerFragment());
        });

        // Перематывание песни
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    Data.getMusicPlayer().seekTo(progress*1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Отображение на ползунке текущего момента песни
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int pos = Data.getMusicPlayer().getPosition() / 1000;
                seekBar.setProgress(pos);
                handler.postDelayed(this, 1000);
            }
        };
        playerActivity.runOnUiThread(runnable);


        return view;
    }
}