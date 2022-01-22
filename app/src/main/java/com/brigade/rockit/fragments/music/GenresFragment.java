package com.brigade.rockit.fragments.music;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.adapter.GenreAdapter;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.TaskListener;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;


public class GenresFragment extends Fragment {

    private GetObjectListener listener;
    private int maxGenres;

    public GenresFragment(int maxGenres) {
        this.maxGenres = maxGenres;
    }

    public void setListener(GetObjectListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity)getActivity();
        View view = inflater.inflate(R.layout.fragment_genres, container, false);

        RecyclerView genresList = view.findViewById(R.id.genres_list);
        TextView header = view.findViewById(R.id.header);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        GenreAdapter adapter = new GenreAdapter(getContext(), maxGenres);
        genresList.setAdapter(adapter);
        genresList.setLayoutManager(new GridLayoutManager(getContext(), 3));

        new ContentManager().getGenresList(new GetObjectListener() {
            @Override
            public void onComplete(Object object) {
                ArrayList<String> ids = (ArrayList<String>) object;
                for (String id: ids)
                    adapter.addItem(id);
            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });

        adapter.setOnClickListener(new TaskListener() {
            @Override
            public void onComplete() {
                toolbar.getMenu().getItem(0).setVisible(adapter.getSelectedGenres().size() > 0);
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        if (maxGenres == 1)
            header.setVisibility(View.GONE);

        toolbar.setOnMenuItemClickListener(item -> {
            listener.onComplete(adapter.getSelectedGenres());
            mainActivity.previousFragment();
            return true;
        });

        toolbar.setNavigationOnClickListener(v -> mainActivity.previousFragment());

        return view;
    }
}