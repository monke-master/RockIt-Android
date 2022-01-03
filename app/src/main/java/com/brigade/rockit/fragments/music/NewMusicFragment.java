package com.brigade.rockit.fragments.music;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.brigade.rockit.GlideApp;
import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.data.Constants;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Music;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;
import com.google.firebase.auth.FirebaseAuth;

public class NewMusicFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_music, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();

        // Получение виджетов
        Button backBtn = view.findViewById(R.id.back_btn_nm);
        Button doneBtn = view.findViewById(R.id.done_btn_nm);
        Button addCoverBtn = view.findViewById(R.id.add_cover_btn);
        EditText nameEdit = view.findViewById(R.id.song_name_edit);
        EditText artistEdit = view.findViewById(R.id.artist_edit);
        ImageView coverImage = view.findViewById(R.id.cover_img);

        // Отображение введенной информации, если она есть
        if (Data.getNewMusic() == null) {
            Data.setNewMusic(new Music());
            mainActivity.setRequest(Constants.PICK_AUDIO);
            mainActivity.pickAudio();
        } else {
            Music music = Data.getNewMusic();
            if (music.getName() != null)
                nameEdit.setText(music.getName());
            if (music.getArtist() != null)
                artistEdit.setText(music.getArtist());
            if (music.getCover() != null)
                GlideApp.with(getActivity()).load(music.getCover()).into(coverImage);

        }

        // Добавление обложки
        addCoverBtn.setOnClickListener(v -> {
            String name = nameEdit.getText().toString();
            String artist = artistEdit.getText().toString();
            if (!name.equals(""))
                Data.getNewMusic().setName(name);
            if (!artist.equals(""))
                Data.getNewMusic().setArtist(artist);
            mainActivity.setRequest(Constants.PICK_COVER_IMAGE);
            mainActivity.pickPhotos(1);
        });

        // Загрузка песни
        doneBtn.setOnClickListener(v -> {
            String name = nameEdit.getText().toString();
            String artist = artistEdit.getText().toString();
            // Проверка на пустые поля
            if (!(artist.equals("") && name.equals(""))) {
                Data.getNewMusic().setArtist(artist);
                Data.getNewMusic().setName(name);
                Data.getNewMusic().setAuthorId(FirebaseAuth.getInstance().getUid());
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(mainActivity.getApplicationContext(), Data.getNewMusic().
                        getUri());
                int millis =  Integer.parseInt(retriever.
                        extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                int seconds = millis / 1000;
                String duration = seconds / 60 + ":" + seconds % 60;
                Data.getNewMusic().setDuration(duration);
                ContentManager contentManager = new ContentManager();
                contentManager.uploadMusic(Data.getNewMusic(), new TaskListener() {
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

            }
            if (name.equals(""))
                Toast.makeText(mainActivity, getString(R.string.song_name_error),
                        Toast.LENGTH_LONG).show();
            if (artist.equals(""))
                Toast.makeText(mainActivity, getString(R.string.song_artist_error),
                        Toast.LENGTH_LONG).show();

        });

        // Возвращение на предыдущий фрагмент
        backBtn.setOnClickListener(v -> {
            mainActivity.setFragment(new MusicFragment());
            Data.setNewMusic(null);
        });


        return view;
    }
}