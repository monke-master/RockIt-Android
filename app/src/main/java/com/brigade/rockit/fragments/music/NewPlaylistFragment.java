package com.brigade.rockit.fragments.music;


import android.net.Uri;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.brigade.rockit.GlideApp;
import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.adapter.AdapterListener;
import com.brigade.rockit.adapter.PostingSongsAdapter;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Song;
import com.brigade.rockit.data.Playlist;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.TimeManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.fragments.dialogs.PhotoDialog;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

// Добавление нового плейлиста
public class NewPlaylistFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_playlist, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();
        // Получение виджетов
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        EditText titleEdit = view.findViewById(R.id.title_edit);
        EditText descrEdit = view.findViewById(R.id.descr_edit);
        ImageView addMusicBtn = view.findViewById(R.id.add_music_btn);
        ImageView coverImg = view.findViewById(R.id.cover_img);
        RecyclerView songsList = view.findViewById(R.id.songs_list);
        PostingSongsAdapter songsAdapter = new PostingSongsAdapter();
        songsList.setAdapter(songsAdapter);
        songsList.setLayoutManager(new LinearLayoutManager(mainActivity));
        // Если пользователь уже создавал плейлист, то отображаем черновик
        if (Data.getNewPlaylist() == null)
            Data.setNewPlaylist(new Playlist());
        Playlist playlist = Data.getNewPlaylist();
        // Отображение обложки
        if (playlist.getCoverUri() != null)
            coverImg.setImageURI(playlist.getCoverUri());
        // Отображение списка песен
        for (Song song: playlist.getSongs())
            songsAdapter.addItem(song);
        // Отслеживание удалений прикрепленных песен
        songsAdapter.setOnItemDeleteListener(new AdapterListener() {
            @Override
            public void onAdded(Object object) {

            }

            @Override
            public void onDelete(Object object) {
                playlist.getSongs().remove((Song) object);
            }
        });

        // Добавление обложки
        coverImg.setOnClickListener(v -> {
            PhotoDialog dialog = new PhotoDialog(1, new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    ArrayList<Uri> uris = (ArrayList)object;
                    playlist.setCoverUri(uris.get(0));
                    GlideApp.with(mainActivity).load(playlist.getCoverUri()).into(coverImg);
                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, getContext());
                }
            });
            dialog.show(getParentFragmentManager(), "photo");
        });

        // Добавление песен
        addMusicBtn.setOnClickListener(v -> {
            SelectMusicFragment fragment = new SelectMusicFragment();
            fragment.setListener(new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    ArrayList<Song> songs = (ArrayList<Song>) object;
                    for (Song song : songs)
                        playlist.getSongs().add(song);
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
            mainActivity.setFragment(new SelectMusicFragment());
        });
        // Отображение кнопки подтверждения в зависимости от введенных полей
        titleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String title = s.toString();
                toolbar.getMenu().getItem(0).setVisible(!title.equals(""));
            }
        });
        // Нажатие на кнопку подтверждение
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.done_btn) {
                Playlist newPlaylist = Data.getNewPlaylist();
                newPlaylist.setName(titleEdit.getText().toString());
                newPlaylist.setDescription(descrEdit.getText().toString());
                newPlaylist.setAuthor(Data.getCurUser());
                TimeManager timeManager = new TimeManager();
                newPlaylist.setDate(timeManager.getDate());
                int duration = 0;
                for (Song song : newPlaylist.getSongs())
                    duration += timeManager.getMillis(song.getDuration());
                newPlaylist.setDuration(timeManager.formatDuration(duration));
                ContentManager contentManager = new ContentManager();
                contentManager.uploadPlaylist(newPlaylist, new TaskListener() {
                    @Override
                    public void onComplete() {
                        Data.setNewPlaylist(null);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ExceptionManager.showError(e, getContext());
                    }
                });
                mainActivity.setFragment(new MusicFragment());
            }
            return true;
        });

        // Возвращение на предыдущий фрагмент
        toolbar.setNavigationOnClickListener(v -> {
            mainActivity.setFragment(new MusicFragment());
        });

        return view;
    }

}