package com.brigade.rockit.fragments.users;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.fragments.music.SearchMusicFragment;

import java.util.concurrent.atomic.AtomicReference;

public class UsersFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity mainActivity = (MainActivity)getActivity();
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        setFragment(new ForYouUserFragment());
        // Получение виджетов

        EditText searchEdit = view.findViewById(R.id.search_user);
        // При изменении вводимого текста
        searchEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                mainActivity.setFragment(new SearchUserFragment());
        });


        return view;

    }

    public void setFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_view, fragment).commit();
    }
}