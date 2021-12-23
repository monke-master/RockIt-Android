package com.brigade.rockit.fragments.music;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.data.Constants;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.adapter.MusicAdapter;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.UserManager;

import java.util.ArrayList;


public class MyMusicFragment extends Fragment {

    private MusicAdapter adapter;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_music, container, false);
        mainActivity = (MainActivity)getActivity();

        // Получение виджетов
        Button newMusic = view.findViewById(R.id.new_music_btn);
        RecyclerView recyclerView = view.findViewById(R.id.songs_list);

        // Отображение песен
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        adapter = new MusicAdapter(mainActivity);
        adapter.setMode(Constants.PLAYLIST_MODE);
        recyclerView.setAdapter(adapter);
        UserManager manager = new UserManager();
        manager.getUserMusic(Data.getCurUser().getId(), new GetObjectListener() {
            @Override
            public void onComplete(Object object) {
                ArrayList<String> musicIds = (ArrayList<String>) object;
                for (String id: musicIds)
                    adapter.addItem(id);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        // Добавление музыки
        newMusic.setOnClickListener(v -> {
            mainActivity.setFragment(new NewMusicFragment());
        });

        return view;
    }



}