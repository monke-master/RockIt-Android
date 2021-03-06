package com.brigade.rockit.fragments.music;

import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.activities.PlayerActivity;
import com.brigade.rockit.data.Data;

// Нижний музыкальный плеер
public class BottomPlayerFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_player, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();

        // Получение виджетов
        ImageView playBtn = view.findViewById(R.id.play_btn);
        ImageView closeBtn = view.findViewById(R.id.close_btn);
        TextView nameTxt = view.findViewById(R.id.song_name_txt);
        TextView artistTxt = view.findViewById(R.id.author_txt);
        ConstraintLayout layout = view.findViewById(R.id.main_layout);


        // Вывод информации о песне
        nameTxt.setText(Data.getMusicPlayer().getMusic().getName());
        artistTxt.setText(Data.getMusicPlayer().getMusic().getAuthor().getLogin());

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



        // Переход на полноэкранный плеер
        layout.setOnClickListener(v -> {
            Intent intent = new Intent(mainActivity, PlayerActivity.class);
            startActivity(intent);
        });

        // Закрытие плеера
        closeBtn.setOnClickListener(v -> {
            mainActivity.findViewById(R.id.player_fragment).setVisibility(View.INVISIBLE);
            Data.getMusicPlayer().stopSong();
        });

        return view;
    }
}