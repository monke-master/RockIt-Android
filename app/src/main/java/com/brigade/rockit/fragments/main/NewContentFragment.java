package com.brigade.rockit.fragments.main;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.adapter.MusicAdapter;
import com.brigade.rockit.adapter.PostImagesAdapter;
import com.brigade.rockit.data.Constants;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Post;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.fragments.dialogs.PhotoDialog;
import com.brigade.rockit.fragments.music.SelectMusicFragment;
import com.brigade.rockit.fragments.profile.ProfileFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class NewContentFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_content, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();

        // Получение виджетов
        Button backBtn = view.findViewById(R.id.back_btn_nc);
        Button postBtn = view.findViewById(R.id.post_btn);
        EditText textEdit = view.findViewById(R.id.post_text_edit);
        RecyclerView imagesList = view.findViewById(R.id.images_list);
        RecyclerView songsList = view.findViewById(R.id.songs_list);
        ImageView addPhotoBtn = view.findViewById(R.id.add_photo_btn);
        ImageView addMusicBtn = view.findViewById(R.id.add_music_btn);

        // Отображение прикрепленных фото
        LinearLayoutManager manager = new LinearLayoutManager(requireContext(),
                LinearLayoutManager.HORIZONTAL, false);
        imagesList.setLayoutManager(manager);
        PostImagesAdapter imageAdapter = new PostImagesAdapter(getContext());
        imagesList.setAdapter(imageAdapter);

        // Отображение прикрепленных песен
        songsList.setLayoutManager(new LinearLayoutManager(requireContext()));
        MusicAdapter musicAdapter = new MusicAdapter(mainActivity);
        musicAdapter.setMode(Constants.POST_MODE);
        songsList.setAdapter(musicAdapter);

        if (Data.getNewPost() == null)
            Data.setNewPost(new Post());
        Post post = Data.getNewPost();
        for (Uri uri: post.getImagesList()) {
            imageAdapter.addItem(uri);
        }

        for (String musicId: post.getMusicIds())
            musicAdapter.addItem(musicId);


        // Добавление фото
        addPhotoBtn.setOnClickListener(v -> {
            if (Data.getNewPost().getImagesList().size() < Constants.MAX_POST_IMAGES) {
                PhotoDialog dialog = new PhotoDialog(Constants.MAX_POST_IMAGES,
                        new GetObjectListener() {
                            @Override
                            public void onComplete(Object object) {
                                ArrayList<Uri> uris = (ArrayList<Uri>)object;
                                post.setImagesList((ArrayList<Uri>) object);
                                for (Uri uri: post.getImagesList()) {
                                    imageAdapter.addItem(uri);
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                ExceptionManager.showError(e, getContext());
                            }
                        });
                dialog.show(getParentFragmentManager(), "Photo");
            } else
                Toast.makeText(mainActivity, getString(R.string.pick_photo_error) +
                        Constants.MAX_POST_IMAGES, Toast.LENGTH_LONG).show();
        });

        // Добавление музыки
        addMusicBtn.setOnClickListener(v -> {
            if (Data.getNewPost().getMusicIds().size() < Constants.MAX_POST_SONGS) {
                mainActivity.setFragment(new SelectMusicFragment(new GetObjectListener() {
                    @Override
                    public void onComplete(Object object) {
                        ArrayList<String> songIds = (ArrayList<String>) object;
                        for (String id: songIds)
                            post.getMusicIds().add(id);
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                }));
            } else
                Toast.makeText(mainActivity, getString(R.string.select_music_error) +
                        Constants.MAX_POST_SONGS, Toast.LENGTH_LONG).show();

        });

        // Публикация поста
        postBtn.setOnClickListener(v -> {
            String text = textEdit.getText().toString();
            post.setText(text);
            post.setMusicIds(musicAdapter.getMusicIds());
            if (!post.getText().equals("") || (post.getImagesList().size() > 0) ||
                    (post.getMusicIds().size() > 0)) {
                ContentManager contentManager = new ContentManager();
                contentManager.uploadPost(post, new TaskListener() {
                    @Override
                    public void onComplete() {
                        Data.setNewPost(null);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ExceptionManager.showError(e, getContext());
                    }
                });
                mainActivity.setFragment(new ProfileFragment(Data.getCurUser()));

            } else
                Toast.makeText(mainActivity, getString(R.string.empty_post), Toast.LENGTH_LONG).show();


        });

        // Возвращение на предыдущий фрагмент
        backBtn.setOnClickListener(v -> {
            Data.setNewPost(null);
            mainActivity.setFragment(new HomeFragment());
        });
        return view;
    }
}