package com.brigade.rockit.fragments.music;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.adapter.MusicAdapter;
import com.brigade.rockit.database.AdapterManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;

import java.util.Locale;


public class SearchMusicFragment extends Fragment {

    private MusicAdapter adapter;
    private MainActivity mainActivity;

    // Фрагмент с результатами поиска музыки
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_music, container, false);
        mainActivity = (MainActivity)getActivity();

        // Получение виджета списка песен
        RecyclerView recyclerView = view.findViewById(R.id.songs_list);

        // Отображение песен
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        adapter = new MusicAdapter(mainActivity);
        recyclerView.setAdapter(adapter);

        return view;
    }

    // Поиск
    public void search(String searching) {
        AdapterManager viewer = new AdapterManager();
        viewer.showSearchedMusic(adapter, searching.toLowerCase(Locale.ROOT), new TaskListener() {
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
        adapter.clear();
    }
}