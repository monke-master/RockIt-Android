package com.brigade.rockit.fragments.music;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.adapter.PlaylistAdapter;
import com.brigade.rockit.data.Constants;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.adapter.SongAdapter;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.UserManager;
import com.brigade.rockit.fragments.dialogs.NewMusicDialog;

import java.util.ArrayList;
import java.util.Collections;

// Фрагмент с плейлистами и музыкой пользователя
public class MyMusicFragment extends Fragment {

    private SongAdapter songAdapter;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_music, container, false);
        mainActivity = (MainActivity)getActivity();

        // Получение виджетов
        ImageView addMusic = view.findViewById(R.id.add_music_btn);
        RecyclerView songsList = view.findViewById(R.id.songs_list);
        RecyclerView playlists = view.findViewById(R.id.playlists);
        ImageView addPlaylist = view.findViewById(R.id.add_playlist_btn);

        // Отображение песен
        songsList.setLayoutManager(new LinearLayoutManager(mainActivity));
        songAdapter = new SongAdapter(mainActivity);
        songsList.setAdapter(songAdapter);
        UserManager manager = new UserManager();
        manager.getUserMusic(Data.getCurUser().getId(), new GetObjectListener() {
            @Override
            public void onComplete(Object object) {
                ArrayList<String> musicIds = (ArrayList<String>) object;
                Collections.reverse(musicIds);
                for (String id: musicIds)
                    songAdapter.addItem(id);
            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });

        playlists.setLayoutManager(new LinearLayoutManager(mainActivity, RecyclerView.HORIZONTAL, false));
        PlaylistAdapter playlistAdapter = new PlaylistAdapter(mainActivity);
        playlists.setAdapter(playlistAdapter);
        manager.getUserPlaylists(Data.getCurUser().getId(), new GetObjectListener() {
            @Override
            public void onComplete(Object object) {
                ArrayList<String> playlistIds = (ArrayList<String>) object;
                for (int i = playlistIds.size() - 1; i >= 0; i--)
                    playlistAdapter.addItem(playlistIds.get(i));
            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });

        // Добавление музыки
        addMusic.setOnClickListener(v -> {
            NewMusicDialog dialog = new NewMusicDialog();
            dialog.show(getParentFragmentManager(), "music");
        });

        addPlaylist.setOnClickListener(v -> {
            mainActivity.setFragment(new NewPlaylistFragment());
        });

        return view;
    }



}