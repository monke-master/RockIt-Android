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
import com.brigade.rockit.adapter.AlbumSongsAdapter;
import com.brigade.rockit.data.Album;
import com.brigade.rockit.adapter.AdapterListener;
import com.brigade.rockit.data.CaseManager;
import com.brigade.rockit.data.Song;
import com.brigade.rockit.data.TimeManager;
import com.brigade.rockit.fragments.dialogs.AlbumDialog;
import com.google.android.material.appbar.MaterialToolbar;


public class AlbumFragment extends Fragment {

    private Album album;

    public AlbumFragment(Album album) {
        this.album = album;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();

        // Получение виджетов
        ImageView coverImg = view.findViewById(R.id.cover_img);
        TextView nameTxt = view.findViewById(R.id.name_txt);
        TextView authorTxt = view.findViewById(R.id.author_txt);
        TextView dateTxt = view.findViewById(R.id.date_txt);
        TextView durationTxt = view.findViewById(R.id.duration_txt);
        TextView auditionsTxt = view.findViewById(R.id.auditions_txt);
        RecyclerView songsList = view.findViewById(R.id.songs_list);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        // Отображение данных плейлиста
        GlideApp.with(mainActivity).load(album.getCoverUri()).into(coverImg);
        nameTxt.setText(album.getName());
        authorTxt.setText(album.getAuthor().getLogin());
        dateTxt.setText(album.getGenre() + " " + album.getDate());
        CaseManager caseManager = new CaseManager(getContext());
        TimeManager timeManager = new TimeManager();
        durationTxt.setText(album.getSongIds().size() + " " + caseManager.getSongsCase(album.getSongIds().size())
                + " " + timeManager.albumDurationFormat(album.getDuration(), getContext()));
        auditionsTxt.setText(album.getAuditions() + "" + caseManager.getAuditionsCase(album.getAuditions()));

        AlbumSongsAdapter songAdapter = new AlbumSongsAdapter(mainActivity);
        songsList.setAdapter(songAdapter);
        songsList.setLayoutManager(new LinearLayoutManager(getContext()));
        songAdapter.setAdapterListener(new AdapterListener() {
            @Override
            public void onAdded(Object object) {
                album.getSongs().add((Song) object);
            }

            @Override
            public void onDelete(Object object) {

            }
        });
        for (String id: album.getSongIds()) {
            songAdapter.addItem(id);
        }

        // Параметры плейлиста
        toolbar.setOnMenuItemClickListener(item -> {
            AlbumDialog dialog = new AlbumDialog(getContext(), mainActivity, album);
            dialog.show();
            return true;
        });

        // Возвращение на предыдущий фрагмент
        toolbar.setNavigationOnClickListener(v -> mainActivity.setFragment(new MusicFragment()));

        return view;

    }
}