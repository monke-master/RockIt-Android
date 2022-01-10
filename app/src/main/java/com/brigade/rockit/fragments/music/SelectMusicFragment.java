package com.brigade.rockit.fragments.music;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toolbar;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.data.Constants;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.adapter.MusicAdapter;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.UserManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

// Фрагмент выбора песен
public class SelectMusicFragment extends Fragment {

    private MusicAdapter adapter;
    private GetObjectListener listener;


    public SelectMusicFragment(GetObjectListener listener) {
        this.listener = listener;
    }

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
        adapter = new MusicAdapter(mainActivity);
        adapter.setMode(Constants.SELECTING_MODE);
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
            listener.onComplete(adapter.getSelectedList());
            mainActivity.previousFragment();
            return true;
        });

        toolbar.setNavigationOnClickListener(v -> mainActivity.previousFragment());

        return view;
    }

}