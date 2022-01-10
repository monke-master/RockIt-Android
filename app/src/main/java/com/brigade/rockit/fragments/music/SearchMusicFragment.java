package com.brigade.rockit.fragments.music;

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

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.adapter.MusicAdapter;
import com.brigade.rockit.adapter.PlaylistAdapter;
import com.brigade.rockit.database.AdapterManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;

import java.util.Locale;

// Поиск плейлистов, артистов и музыки
public class SearchMusicFragment extends Fragment {

    private MusicAdapter musicAdapter;
    private PlaylistAdapter playlistAdapter;
    private MainActivity mainActivity;

    // Фрагмент с результатами поиска музыки
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_music, container, false);
        mainActivity = (MainActivity)getActivity();

        // Получение виджета списка песен
        RecyclerView songsList = view.findViewById(R.id.songs_list);
        RecyclerView playlistsList = view.findViewById(R.id.playlists);
        ImageView backBtn = view.findViewById(R.id.back_btn);
        EditText searchEdit = view.findViewById(R.id.search_music);

        // Отображение песен
        songsList.setLayoutManager(new LinearLayoutManager(mainActivity));
        musicAdapter = new MusicAdapter(mainActivity);
        songsList.setAdapter(musicAdapter);

        playlistsList.setLayoutManager(new LinearLayoutManager(mainActivity,
                LinearLayoutManager.HORIZONTAL, false));
        playlistAdapter = new PlaylistAdapter(mainActivity);
        playlistsList.setAdapter(playlistAdapter);

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
        viewer.showSearchedPlaylists(playlistAdapter, searching.toLowerCase(Locale.ROOT), new TaskListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });
        viewer.showSearchedMusic(musicAdapter, searching.toLowerCase(Locale.ROOT), new TaskListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });
    }

    // Очистка выводимого списка
    public void clear() {
        if (musicAdapter != null)
            musicAdapter.clear();
        if (playlistAdapter != null)
            playlistAdapter.clear();
    }

}