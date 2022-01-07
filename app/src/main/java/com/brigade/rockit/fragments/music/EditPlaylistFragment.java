package com.brigade.rockit.fragments.music;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brigade.rockit.GlideApp;
import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.adapter.MusicAdapter;
import com.brigade.rockit.data.Constants;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Playlist;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.DateManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.fragments.dialogs.PhotoDialog;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class EditPlaylistFragment extends Fragment {

    private Playlist playlist;
    private boolean coverChanged;

    public EditPlaylistFragment(Playlist playlist) {
        this.playlist = playlist;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_playlist, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        EditText titleEdit = view.findViewById(R.id.title_edit);
        EditText descrEdit = view.findViewById(R.id.descr_edit);
        ImageView addMusicBtn = view.findViewById(R.id.add_music_btn);
        ImageView coverImg = view.findViewById(R.id.cover_img);
        RecyclerView songsList = view.findViewById(R.id.songs_list);
        MusicAdapter musicAdapter = new MusicAdapter(mainActivity);
        musicAdapter.setMode(Constants.POST_MODE);
        songsList.setAdapter(musicAdapter);
        songsList.setLayoutManager(new LinearLayoutManager(mainActivity));

        if (!titleEdit.equals(""))
            toolbar.getMenu().getItem(0).setVisible(true);

        titleEdit.setText(playlist.getName());
        descrEdit.setText(playlist.getDescription());
        for (String id: playlist.getSongIds())
            musicAdapter.addItem(id);
        GlideApp.with(mainActivity).load(playlist.getCoverUri()).into(coverImg);


        coverImg.setOnClickListener(v -> {
            PhotoDialog dialog = new PhotoDialog(1, new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    ArrayList<Uri> uris = (ArrayList)object;
                    playlist.setCoverUri(uris.get(0));
                    GlideApp.with(mainActivity).load(playlist.getCoverUri()).into(coverImg);
                    coverChanged = true;
                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, getContext());
                }
            });
            dialog.show(getParentFragmentManager(), "photo");
        });

        addMusicBtn.setOnClickListener(v -> {
            mainActivity.setFragment(new SelectMusicFragment(new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    ArrayList<String> songIds = (ArrayList<String>) object;
                    for (String id: songIds)
                        playlist.getSongIds().add(id);
                }

                @Override
                public void onFailure(Exception e) {

                }
            }));
        });

        toolbar.setNavigationOnClickListener(v -> {
            mainActivity.setFragment(new MusicFragment());
        });

        titleEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String title = s.toString();
                toolbar.getMenu().getItem(0).setVisible(!title.equals(""));
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.done_btn) {
                    playlist.setName(titleEdit.getText().toString());
                    playlist.setDescription(descrEdit.getText().toString());
                    DateManager dateManager = new DateManager();
                    playlist.setDate(dateManager.getDate());
                    ContentManager contentManager = new ContentManager();
                    contentManager.updatePlaylist(playlist, coverChanged, new TaskListener() {
                        @Override
                        public void onComplete() {

                        }

                        @Override
                        public void onFailure(Exception e) {
                            ExceptionManager.showError(e, getContext());
                        }
                    });
                    mainActivity.setFragment(new MusicFragment());
                }
                return true;
            }
        });

        return view;
    }
}
