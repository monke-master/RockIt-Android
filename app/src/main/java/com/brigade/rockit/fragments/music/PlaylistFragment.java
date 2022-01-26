package com.brigade.rockit.fragments.music;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.brigade.rockit.GlideApp;
import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.adapter.SongAdapter;
import com.brigade.rockit.data.Playlist;
import com.brigade.rockit.fragments.dialogs.PlaylistDialog;
import com.google.android.material.appbar.MaterialToolbar;

// Фрагмент с плейлистом
public class PlaylistFragment extends Fragment {
    private Playlist playlist;

    public PlaylistFragment(Playlist playlist) {
        this.playlist = playlist;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playlist, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        // Получение виджетов
        ImageView coverImg = view.findViewById(R.id.cover_img);
        TextView nameTxt = view.findViewById(R.id.name_txt);
        TextView authorTxt = view.findViewById(R.id.author_txt);
        TextView dateTxt = view.findViewById(R.id.date_txt);
        RecyclerView songsList = view.findViewById(R.id.songs_list);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        // Отображение данных плейлиста
        GlideApp.with(mainActivity).load(playlist.getCoverUri()).into(coverImg);
        nameTxt.setText(playlist.getName());
        authorTxt.setText(playlist.getAuthor().getLogin());
        dateTxt.setText(playlist.getDate());

        SongAdapter songAdapter = new SongAdapter(mainActivity);
        songsList.setAdapter(songAdapter);
        songsList.setLayoutManager(new LinearLayoutManager(getContext()));
        for (String id: playlist.getSongIds()) {
            songAdapter.addItem(id);

        }

        // Параметры плейлиста
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.option_btn) {
                PlaylistDialog playlistDialog = new PlaylistDialog(getContext(), mainActivity,
                        playlist);
                playlistDialog.show();
            }
            return true;
        });

        // Возвращение на предыдущий фрагмент
        toolbar.setNavigationOnClickListener(v -> mainActivity.setFragment(new MusicFragment()));

        return view;
    }
}