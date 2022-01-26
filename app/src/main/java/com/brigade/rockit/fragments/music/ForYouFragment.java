package com.brigade.rockit.fragments.music;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.adapter.PlaylistAdapter;
import com.brigade.rockit.adapter.SongAdapter;
import com.brigade.rockit.data.Album;
import com.brigade.rockit.data.Song;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.RecommendationManager;

import org.w3c.dom.Text;

import java.util.ArrayList;

// Фрагмент с рекомендациями
public class ForYouFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_for_you, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();

        // Получение виджетов
        LinearLayout layout = view.findViewById(R.id.layout);

        RecommendationManager manager = new RecommendationManager();
        // Получение новых песен от избранных исполнителей
        manager.getNewSongs(new GetObjectListener() {
            @Override
            public void onComplete(Object object) {
                ArrayList<Song> songs = (ArrayList<Song>) object;
                // Если таковы имеются, то
                if (songs.size() > 0) {
                    // Создаем заголовок
                    if (layout.findViewById(R.id.from_follow_txt) == null) {
                        TextView fromFollowTxt = new TextView(getContext());
                        fromFollowTxt.setId(R.id.from_follow_txt);
                        fromFollowTxt.setText(getString(R.string.new_from_following));
                        TextViewCompat.setTextAppearance(fromFollowTxt, R.style.Text);
                        layout.addView(fromFollowTxt);
                    }

                    // Создаем список с песнями и выводим на экран
                    RecyclerView recyclerView = new RecyclerView(getContext());
                    recyclerView.setLayoutParams(new RecyclerView.LayoutParams(
                            RecyclerView.LayoutParams.MATCH_PARENT,
                            RecyclerView.LayoutParams.WRAP_CONTENT));
                    recyclerView.setId(R.id.songs_from_follow);
                    recyclerView.setNestedScrollingEnabled(true);
                    SongAdapter songAdapter = new SongAdapter(mainActivity);
                    recyclerView.setAdapter(songAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    layout.addView(recyclerView);
                    for (Song song: songs) {
                        songAdapter.addItem(song.getId());
                    }
                }

            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });

        // Получение новых альбомов от избранных исполнителей
        manager.getNewAlbums(new GetObjectListener() {
            @Override
            public void onComplete(Object object) {
                ArrayList<Album> albums = (ArrayList<Album>) object;
                // Если таковые имеются, то
                if (albums.size() > 0) {
                    // Создаем заголовок
                    if (layout.findViewById(R.id.from_follow_txt) == null) {
                        TextView fromFollowTxt = new TextView(getContext());
                        fromFollowTxt.setId(R.id.from_follow_txt);
                        fromFollowTxt.setText(getString(R.string.new_from_following));
                        TextViewCompat.setTextAppearance(fromFollowTxt, R.style.Text);
                        layout.addView(fromFollowTxt);
                    }
                    // Создаем список с альбомами и выводим на экран
                    RecyclerView recyclerView = new RecyclerView(getContext());
                    recyclerView.setLayoutParams(new RecyclerView.LayoutParams(
                            RecyclerView.LayoutParams.MATCH_PARENT,
                            RecyclerView.LayoutParams.WRAP_CONTENT));
                    recyclerView.setId(R.id.albums_from_follow);
                    recyclerView.setNestedScrollingEnabled(true);
                    PlaylistAdapter albumAdapter = new PlaylistAdapter(mainActivity);
                    recyclerView.setAdapter(albumAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                            LinearLayoutManager.HORIZONTAL, false));
                    layout.addView(recyclerView);
                    for (Album album: albums)
                        albumAdapter.addItem(album.getId());
                }
            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });

        // Получаем песни, которые могут понравиться пользователю
        manager.getMayLikeSongs(new GetObjectListener() {
            @Override
            public void onComplete(Object object) {
                ArrayList<Song> songs = (ArrayList<Song>) object;
                // Если таковы имеются, то
                if (songs.size() > 0) {
                    // Создаем заголовок
                    if (layout.findViewById(R.id.may_like_txt) == null) {
                        TextView fromFollowTxt = new TextView(getContext());
                        fromFollowTxt.setId(R.id.may_like_txt);
                        fromFollowTxt.setText(getString(R.string.you_may_like));
                        TextViewCompat.setTextAppearance(fromFollowTxt, R.style.Text);
                        layout.addView(fromFollowTxt);
                    }
                    // Создаем список с песнями и выводим на экран
                    RecyclerView recyclerView = new RecyclerView(getContext());
                    recyclerView.setLayoutParams(new RecyclerView.LayoutParams(
                            RecyclerView.LayoutParams.MATCH_PARENT,
                            RecyclerView.LayoutParams.WRAP_CONTENT));
                    recyclerView.setId(R.id.songs_may_like);
                    recyclerView.setNestedScrollingEnabled(true);
                    SongAdapter songAdapter = new SongAdapter(mainActivity);
                    recyclerView.setAdapter(songAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    layout.addView(recyclerView);
                    for (Song song: songs) {
                        songAdapter.addItem(song.getId());
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });

        // Получаем альбомы, которые могут понравиться пользователю
        manager.getMayLikeAlbums(new GetObjectListener() {
            @Override
            public void onComplete(Object object) {
                ArrayList<Album> albums = (ArrayList<Album>) object;
                // Если таковы имеются, то
                if (albums.size() > 0) {
                    // Создаем заголовок
                    if (layout.findViewById(R.id.may_like_txt) == null) {
                        TextView fromFollowTxt = new TextView(getContext());
                        fromFollowTxt.setId(R.id.may_like_txt);
                        fromFollowTxt.setText(getString(R.string.you_may_like));
                        TextViewCompat.setTextAppearance(fromFollowTxt, R.style.Text);
                        layout.addView(fromFollowTxt);
                    }
                    // Создаем список с альбомами и выводим на экран
                    RecyclerView recyclerView = new RecyclerView(getContext());
                    recyclerView.setLayoutParams(new RecyclerView.LayoutParams(
                            RecyclerView.LayoutParams.MATCH_PARENT,
                            RecyclerView.LayoutParams.WRAP_CONTENT));
                    recyclerView.setId(R.id.songs_may_like);
                    recyclerView.setNestedScrollingEnabled(true);
                    recyclerView.setNestedScrollingEnabled(true);
                    PlaylistAdapter albumAdapter = new PlaylistAdapter(mainActivity);
                    recyclerView.setAdapter(albumAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                            LinearLayoutManager.HORIZONTAL, false));
                    layout.addView(recyclerView);
                    for (Album album: albums)
                        albumAdapter.addItem(album.getId());
                }
            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });

        return view;
    }
}