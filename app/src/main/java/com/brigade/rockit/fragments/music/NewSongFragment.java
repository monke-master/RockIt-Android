package com.brigade.rockit.fragments.music;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.brigade.rockit.GlideApp;
import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Genre;
import com.brigade.rockit.data.Song;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.TimeManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.fragments.dialogs.PhotoDialog;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

// Добавление новой музыки
public class NewSongFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_song, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();

        // Получение виджетов
        EditText nameEdit = view.findViewById(R.id.song_name_edit);
        ImageView coverImage = view.findViewById(R.id.cover_img);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        Button pickGenre = view.findViewById(R.id.pick_genre);

        // Отображение введенной информации, если она есть
        if (Data.getNewMusic() == null) {
            Data.setNewMusic(new Song());
            mainActivity.pickAudio(new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    Data.getNewMusic().setUri((Uri) object);
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        }

        Song song = Data.getNewMusic();
        if (song.getCoverUri() != null) {
            GlideApp.with(mainActivity).load(song.getCoverUri()).into(coverImage);
        }

        coverImage.setOnClickListener(v -> {
            PhotoDialog dialog = new PhotoDialog(1, new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    ArrayList<Uri> uris = (ArrayList<Uri>) object;
                    song.setCoverUri(uris.get(0));
                    GlideApp.with(mainActivity).load(song.getCoverUri()).into(coverImage);
                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, getContext());
                }
            });
            dialog.show(getParentFragmentManager(), "photo");
        });


        // Отображение кнопки подтверждения в зависимоти от введенных полей
        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                toolbar.getMenu().getItem(0).setVisible(!(s.toString().equals("") || (
                        Data.getNewMusic().getGenre() == null)));
            }
        });

        // Загрузка музыки
        toolbar.setOnMenuItemClickListener(item -> {
            String name = nameEdit.getText().toString();
            Data.getNewMusic().setAuthor(Data.getCurUser());
            Data.getNewMusic().setName(name);
            Data.getNewMusic().setDate(new TimeManager().getDate());
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mainActivity.getApplicationContext(), Data.getNewMusic().
                    getUri());
            int millis =  Integer.parseInt(retriever.
                    extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            Data.getNewMusic().setDuration(new TimeManager().formatDuration(millis));
            ContentManager contentManager = new ContentManager();
            contentManager.uploadSong(Data.getNewMusic(), new TaskListener() {
                @Override
                public void onComplete() {
                    Data.setNewMusic(null);
                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, getContext());
                }
            });
            mainActivity.setFragment(new MusicFragment());

            return true;
        });

        pickGenre.setOnClickListener(v -> {
            GenresFragment fragment = new GenresFragment(1);
            fragment.setListener(new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    ArrayList<String> genre = (ArrayList<String>)object;
                    Data.getNewMusic().setGenre(genre.get(0));
                    toolbar.getMenu().getItem(0).setVisible(!nameEdit.getText().toString().equals(""));
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
            mainActivity.setFragment(fragment);
        });

        toolbar.setNavigationOnClickListener(v -> {
            mainActivity.setFragment(new MusicFragment());
        });



        return view;
    }
}