package com.brigade.rockit.fragments.music;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;


// Фрагмент с музыкой
public class MusicFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity)getActivity();
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        setFragment(new MusicPagerFragment());

        // Получение виджетов
        EditText searchEdit = view.findViewById(R.id.search_music);


        searchEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                mainActivity.setFragment(new SearchMusicFragment());
        });

        return view;
    }

    public void setFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_view, fragment).commit();
    }


}