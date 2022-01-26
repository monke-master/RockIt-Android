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
import com.brigade.rockit.adapter.SelectingSongAdapter;
import com.brigade.rockit.data.Constants;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.adapter.SongAdapter;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.UserManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

// Фрагмент выбора песен
public class SelectMusicFragment extends Fragment {

    private SelectingSongAdapter adapter;
    private GetObjectListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_music, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();

        // Получение виджетов
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        RecyclerView recyclerView = view.findViewById(R.id.songs_list);

        // Отображение песен
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        adapter = new SelectingSongAdapter();
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

        toolbar.getMenu().getItem(0).setVisible(true);
        toolbar.setOnMenuItemClickListener(item -> {
            listener.onComplete(adapter.getSelectedSongs());
            mainActivity.previousFragment();
            return true;
        });

        toolbar.setNavigationOnClickListener(v -> mainActivity.previousFragment());

        return view;
    }

    public void setListener(GetObjectListener listener) {
        this.listener = listener;
    }
}