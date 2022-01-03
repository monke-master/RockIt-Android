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
import com.brigade.rockit.fragments.main.NewContentFragment;

import java.util.ArrayList;


public class SelectMusicFragment extends Fragment {

    private MusicAdapter adapter;
    private int request;

    public SelectMusicFragment(int request) {
        this.request = request;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select_music, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();

        Button backBtn = view.findViewById(R.id.back_btn);
        Button nextBtn = view.findViewById(R.id.next_btn);
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

        backBtn.setOnClickListener(v -> mainActivity.setFragment(new NewContentFragment()));
        nextBtn.setOnClickListener(v -> {
            switch (request){
                case Constants.CREATING_POST:
                    Data.getNewPost().setMusicIds(adapter.getSelectedList());
                    mainActivity.setFragment(new NewContentFragment());
                    break;
                case Constants.CREATING_PLAYLIST:
                    Data.getNewPlaylist().setSongIds(adapter.getSelectedList());
                    mainActivity.setFragment(new NewPlaylistFragment());
                    break;
            }

        });

        return view;
    }

}