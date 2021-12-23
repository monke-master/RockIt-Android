package com.brigade.rockit.fragments.music;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.brigade.rockit.GlideApp;
import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Music;
import com.brigade.rockit.fragments.main.HomeFragment;


public class PlayerFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();

        // Получение виджетов
        Button backBtn = view.findViewById(R.id.back_btn);
        SeekBar seekBar = view.findViewById(R.id.seekBar);
        TextView nameTxt = view.findViewById(R.id.song_name_txt);
        TextView artistTxt = view.findViewById(R.id.artist_txt);
        ImageView coverImg = view.findViewById(R.id.cover_img);
        ImageView playBtn = view.findViewById(R.id.play_btn);

        // Отображение информации о песне
        Music music = Data.getMusicPlayer().getMusic();
        nameTxt.setText(music.getName());
        artistTxt.setText(music.getArtist());
        GlideApp.with(getContext()).load(music.getCover()).into(coverImg);
        seekBar.setMax(Data.getMusicPlayer().getDuration() / 1000);

        // Содержимое кнопки в зависимости от состояния плеера
        if (!Data.getMusicPlayer().isPlaying())
            playBtn.setImageDrawable(mainActivity.getDrawable(R.drawable.play));
        playBtn.setOnClickListener(v -> {
            if (!Data.getMusicPlayer().isPlaying()) {
                Data.getMusicPlayer().continueSong();
                playBtn.setImageDrawable(mainActivity.getDrawable(R.drawable.pause));
            } else {
                Data.getMusicPlayer().stopSong();
                playBtn.setImageDrawable(mainActivity.getDrawable(R.drawable.play));
            }
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
        mainActivity.runOnUiThread(runnable);

        // Переключение на предыдущий фрагмент
        backBtn.setOnClickListener(v -> {
            mainActivity.findViewById(R.id.bottom_frgmnt_view).setVisibility(View.VISIBLE);
            mainActivity.setBottomFragment(new BottomPlayerFragment());
            mainActivity.setFragment(new HomeFragment());
        });

        return view;
    }
}