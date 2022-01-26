package com.brigade.rockit.fragments.music;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
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

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.adapter.SongAdapter;
import com.brigade.rockit.adapter.PlaylistAdapter;
import com.brigade.rockit.database.AdapterManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;

import java.util.Locale;

// Поиск плейлистов, артистов и музыки
public class SearchMusicFragment extends Fragment {

    private SongAdapter songAdapter;
    private PlaylistAdapter playlistAdapter;
    private PlaylistAdapter albumAdapter;
    private MainActivity mainActivity;
    private ConstraintLayout songsResult, playlistsResult, albumsResult;

    // Фрагмент с результатами поиска музыки
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_music, container, false);
        mainActivity = (MainActivity)getActivity();

        // Получение виджета списка песен
        ImageView backBtn = view.findViewById(R.id.back_btn);
        EditText searchEdit = view.findViewById(R.id.search_music);
        RecyclerView songsList = view.findViewById(R.id.songs_list);
        RecyclerView playlistsList = view.findViewById(R.id.playlists);
        RecyclerView albumsList = view.findViewById(R.id.albums);
        songsResult = view.findViewById(R.id.songs_result);
        playlistsResult = view.findViewById(R.id.playlists_result);
        albumsResult = view.findViewById(R.id.albums_result);

        // Отображение песен
        songsList.setLayoutManager(new LinearLayoutManager(mainActivity));
        songAdapter = new SongAdapter(mainActivity);
        songsList.setAdapter(songAdapter);

        playlistsList.setLayoutManager(new LinearLayoutManager(mainActivity,
                LinearLayoutManager.HORIZONTAL, false));
        playlistAdapter = new PlaylistAdapter(mainActivity);
        playlistsList.setAdapter(playlistAdapter);

        albumsList.setLayoutManager(new LinearLayoutManager(mainActivity,
                LinearLayoutManager.HORIZONTAL, false));
        albumAdapter = new PlaylistAdapter(mainActivity);
        albumsList.setAdapter(albumAdapter);

        clear();

        searchEdit.requestFocus();
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            // При изменении вводимого текста фрагмент отображает результаты поиска
            @Override
            public void afterTextChanged(Editable s) {
                clear();
                if (!s.toString().equals(""))
                    search(s.toString());
            }
        });


        // Переключение фокуса на предыдущий фрагмент
        backBtn.setOnClickListener(v -> {
            mainActivity.previousFragment();
        });

        return view;
    }

    // Поиск
    public void search(String searching) {
        AdapterManager viewer = new AdapterManager();
        viewer.showSearchedSong(songAdapter, searching.toLowerCase(Locale.ROOT), new TaskListener() {
            @Override
            public void onComplete() {
                songsResult.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });
        viewer.showSearchedPlaylists(playlistAdapter, searching.toLowerCase(Locale.ROOT), new TaskListener() {
            @Override
            public void onComplete() {
                playlistsResult.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });
        viewer.showSearchedAlbums(albumAdapter, searching.toLowerCase(Locale.ROOT), new TaskListener() {
            @Override
            public void onComplete() {
                albumsResult.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });
    }

    // Очистка выводимого списка
    public void clear() {
        if (songAdapter != null)
            songAdapter.clear();
        if (playlistAdapter != null)
            playlistAdapter.clear();
        if (albumAdapter != null)
            albumAdapter.clear();

        songsResult.setVisibility(View.GONE);
        playlistsResult.setVisibility(View.GONE);
        albumsResult.setVisibility(View.GONE);

    }

}