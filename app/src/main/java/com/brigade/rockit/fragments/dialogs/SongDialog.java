package com.brigade.rockit.fragments.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.brigade.rockit.R;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Song;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.database.UserManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

// Диалог параметров трека
public class SongDialog extends BottomSheetDialog {

    public SongDialog(@NonNull Context context, Song song) {
        super(context);
        setContentView(R.layout.dialog_song_settings);
        UserManager userManager = new UserManager();
        ContentManager contentManager = new ContentManager();

        // Если песня загружена пользователем, добавление возможности удалить песню из бд
        if (song.getAuthor().getId().equals(Data.getCurUser().getId())) {
            Button deleteBtn = findViewById(R.id.delete_btn);
            deleteBtn.setVisibility(View.VISIBLE);
            deleteBtn.setOnClickListener(v -> {
                    contentManager.deleteSong(song, new TaskListener() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(context, context.getString(R.string.song_deleted),
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ExceptionManager.showError(e, context);
                    }
                });
                hide();
            });
        }


        // Добавление и удаление из "Моей музыки"
        Button myMusicBtn = findViewById(R.id.my_music_btn);
        userManager.getUserMusic(Data.getCurUser().getId(), new GetObjectListener() {
            @Override
            public void onComplete(Object object) {

                ArrayList<String> musicIds = (ArrayList<String>) object;
                if (musicIds.contains(song.getId())) { // Удаление из "Моей музыки"
                    myMusicBtn.setText(R.string.delete_from_my_music);
                    myMusicBtn.setOnClickListener(v -> {
                        contentManager.deleteSongFromMyMusic(song, new TaskListener() {
                            @Override
                            public void onComplete() {
                                Toast.makeText(context, context.getString(R.string.deleted_from_my_music),
                                        Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                ExceptionManager.showError(e, getContext());
                            }
                        });
                        hide();
                    });
                } else { // Добавление в "Мою музыку"
                    myMusicBtn.setText(R.string.add_to_my_music);
                    myMusicBtn.setOnClickListener(v -> {
                        contentManager.addToMyMusic(song.getId(), new TaskListener() {
                            @Override
                            public void onComplete() {
                                Toast.makeText(context, context.getString(R.string.song_added),
                                        Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                ExceptionManager.showError(e, getContext());
                            }
                        });
                        hide();
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                ExceptionManager.showError(e, getContext());
            }
        });
    }
}
