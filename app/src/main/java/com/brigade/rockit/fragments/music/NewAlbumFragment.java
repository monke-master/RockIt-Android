package com.brigade.rockit.fragments.music;

import android.media.MediaMetadataRetriever;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.adapter.PostingSongsAdapter;
import com.brigade.rockit.data.Album;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Song;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.data.TimeManager;
import com.brigade.rockit.fragments.dialogs.NewSongDialog;
import com.brigade.rockit.fragments.dialogs.PhotoDialog;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;


public class NewAlbumFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_album, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();

        // Получение виджетов
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        ImageView coverImg = view.findViewById(R.id.cover_img);
        EditText nameEdit = view.findViewById(R.id.name_edit);
        ImageView addMusicBtn = view.findViewById(R.id.add_music_btn);
        Button pickGenreBtn = view.findViewById(R.id.pick_genre_btn);
        RecyclerView songsList = view.findViewById(R.id.songs_list);
        songsList.setLayoutManager(new LinearLayoutManager(getContext()));
        PostingSongsAdapter songsAdapter = new PostingSongsAdapter();
        songsList.setAdapter(songsAdapter);

        // Если есть черновик альбома, загружаем его
        if (Data.getNewAlbum() == null)
            Data.setNewAlbum(new Album());
        Album album = Data.getNewAlbum();

        for (Song song: album.getSongs())
            songsAdapter.addItem(song);
        if (album.getCoverUri() != null)
            Glide.with(getContext()).load(album.getCoverUri()).into(coverImg);

        // Загрузка обложки
        coverImg.setOnClickListener(v -> {
            PhotoDialog dialog = new PhotoDialog(1, new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    ArrayList<Uri> uris = (ArrayList<Uri>) object;
                    album.setCoverUri(uris.get(0));
                    Glide.with(getContext()).load(album.getCoverUri()).into(coverImg);
                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, getContext());
                }
            });
            dialog.show(getParentFragmentManager(), "photo");
        });

        // Выбор жанра
        pickGenreBtn.setOnClickListener(v -> {
            GenresFragment fragment = new GenresFragment(1);
            fragment.setListener(new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    ArrayList<String> genres = (ArrayList<String>) object;
                    album.setGenre(genres.get(0));
                    toolbar.getMenu().getItem(0).setVisible(!(nameEdit.toString().equals("") ||
                            (album.getSongs().size() == 0) || (album.getGenre() == null)));
                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, getContext());
                }
            });
            mainActivity.setFragment(fragment);
        });

        // Добавление песни
        addMusicBtn.setOnClickListener(v -> {
            mainActivity.pickAudio(new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    Song song = new Song();
                    song.setUri((Uri) object);
                    song.setAuthor(Data.getCurUser());
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(mainActivity.getApplicationContext(), song.getUri());
                    int millis =  Integer.parseInt(retriever.
                            extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                    song.setDuration(new TimeManager().formatDuration(millis));
                    NewSongDialog dialog = new NewSongDialog();
                    dialog.setListener(new GetObjectListener() {
                        @Override
                        public void onComplete(Object object) {
                            song.setName((String) object);
                            album.getSongs().add(song);
                            songsAdapter.addItem(song);
                            toolbar.getMenu().getItem(0).setVisible(!(nameEdit.toString().equals("") ||
                                    (album.getSongs().size() == 0) || (album.getGenre() == null)));
                        }

                        @Override
                        public void onFailure(Exception e) {

                        }
                    });
                    dialog.show(getParentFragmentManager(), "song");
                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, getContext());
                }
            });

        });

        // Если прикреплена хотя бы одна песня и введено название, отображается кнопка подтверждения
        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                toolbar.getMenu().getItem(0).setVisible(!(s.toString().equals("") ||
                        (album.getSongs().size() == 0) || (album.getGenre() == null)));
            }
        });

        // Загрузка альбома
        toolbar.setOnMenuItemClickListener(item -> {
            TimeManager timeManager = new TimeManager();
            album.setName(nameEdit.getText().toString());
            album.setAuthor(Data.getCurUser());
            album.setDate(timeManager.getDate());
            int duration = 0;
            for (Song song: album.getSongs()) {
                duration += timeManager.getMillis(song.getDuration());
                if (album.getCoverUri() != null)
                    song.setCoverUri(album.getCoverUri());
                song.setGenre(album.getGenre());
                song.setDate(album.getDate());
            }

            album.setDuration(timeManager.formatDuration(duration));

            ContentManager contentManager = new ContentManager();
            contentManager.uploadAlbum(album, new TaskListener() {
                @Override
                public void onComplete() {

                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, getContext());
                }
            });
            mainActivity.setFragment(new MusicFragment());
            return true;
        });

        // Возвращение на предыдущий фрагмент
        toolbar.setNavigationOnClickListener(v -> mainActivity.previousFragment());

        return view;
    }
}