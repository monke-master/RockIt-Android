package com.brigade.rockit.fragments.dialogs;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.data.Album;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class AlbumDialog extends BottomSheetDialog {

    public AlbumDialog(@NonNull Context context, MainActivity activity, Album album) {
        super(context);
        setContentView(R.layout.dialog_album);

        // Получение виджетов
        Button addBtn = findViewById(R.id.add_btn);
        Button deleteBtn = findViewById(R.id.delete_btn);

        ContentManager contentManager = new ContentManager();

        if (album.getAuthor().getId().equals(Data.getCurUser().getId())) {
            addBtn.setText(R.string.delete_from_my_playlists);
            addBtn.setOnClickListener(v -> {
                contentManager.deleteFromMyPlaylists("albums/" + album.getId(), new TaskListener() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(getContext(), getContext().getString(R.string.deleted_from_my_playlists),
                                Toast.LENGTH_LONG).show();
                        activity.previousFragment();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ExceptionManager.showError(e, getContext());
                    }
                });
                hide();
            });
            deleteBtn.setOnClickListener(v -> {
                contentManager.deleteAlbum(album, new TaskListener() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(getContext(), getContext().getString(R.string.album_deleted),
                                Toast.LENGTH_LONG).show();
                        activity.previousFragment();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ExceptionManager.showError(e, getContext());
                    }
                });
                hide();
            });
        } else {
            addBtn.setText(R.string.add_playlist);
            addBtn.setOnClickListener(v -> {
                contentManager.addPlaylist("albums/" + album.getId(), new TaskListener() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(getContext(), getContext().getString(R.string.playlist_added),
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ExceptionManager.showError(e, getContext());
                    }
                });
                hide();
            });
            deleteBtn.setVisibility(View.GONE);
        }

    }
}
