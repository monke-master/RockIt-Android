package com.brigade.rockit.fragments.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Playlist;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.database.UserManager;
import com.brigade.rockit.fragments.music.EditPlaylistFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

// Диалог параметров плейлиста
public class PlaylistDialog extends BottomSheetDialog {


    public PlaylistDialog(@NonNull Context context, MainActivity mainActivity, Playlist playlist) {
        super(context);
        setContentView(R.layout.dialog_playlist);

        // Получение виджетов
        Button addBtn = findViewById(R.id.add_btn);
        Button editBtn = findViewById(R.id.edit_btn);
        Button deleteBtn = findViewById(R.id.delete_btn);

        // Кнопка добавления/удаления
        ContentManager contentManager = new ContentManager();
        UserManager userManager = new UserManager();
        userManager.getUserPlaylists(Data.getCurUser().getId(), new GetObjectListener() {
            @Override
            public void onComplete(Object object) {
                ArrayList<String> playlists = (ArrayList<String>) object;
                if (playlists.contains(playlist.getId())) {
                    addBtn.setText(context.getString(R.string.delete_my_playlists));
                    addBtn.setOnClickListener(v -> {
                        contentManager.deleteFromMyPlaylists(playlist.getId(), new TaskListener() {
                            @Override
                            public void onComplete() {
                                Toast.makeText(context, context.getString(R.string.delete_my_playlists),
                                        Toast.LENGTH_LONG).show();
                                mainActivity.previousFragment();
                            }

                            @Override
                            public void onFailure(Exception e) {
                                ExceptionManager.showError(e, context);
                            }
                        });
                        hide();
                    });
                } else {
                    addBtn.setText(context.getString(R.string.add_playlist));
                    addBtn.setOnClickListener(v -> {
                        contentManager.addPlaylist(playlist.getId(), new TaskListener() {
                            @Override
                            public void onComplete() {
                                Toast.makeText(context, context.getString(R.string.playlist_added),
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
            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });

        // Кнопка удаления
        if (Data.getCurUser().getId().equals(playlist.getAuthor().getId())) {
            deleteBtn.setOnClickListener(v -> {
                contentManager.deletePlaylist(playlist.getId(), new TaskListener() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(context, context.getString(R.string.playlist_deleted),
                                Toast.LENGTH_LONG).show();
                        mainActivity.previousFragment();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ExceptionManager.showError(e, getContext());
                    }
                });
                hide();
            });
        } else
            deleteBtn.setVisibility(View.GONE);
        // Кнопка редактирования
        if (Data.getCurUser().getId().equals(playlist.getAuthor().getId())) {
            editBtn.setOnClickListener(v -> {
                mainActivity.setFragment(new EditPlaylistFragment(playlist));
                hide();
            });
        } else
            editBtn.setVisibility(View.GONE);
    }
}
