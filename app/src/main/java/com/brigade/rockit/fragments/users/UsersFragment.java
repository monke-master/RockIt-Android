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
import com.brigade.rockit.fragments.music.SearchMusicFragment;

import java.util.concurrent.atomic.AtomicReference;

public class UsersFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        setFragment(new ForYouUserFragment());
        AtomicReference<SearchUserFragment> fragment = new AtomicReference<>(new SearchUserFragment());
        // Получение виджетов
        Button backBtn = view.findViewById(R.id.back_btn);
        backBtn.setVisibility(View.INVISIBLE);
        EditText searchEdit = view.findViewById(R.id.search_user);
        // При изменении вводимого текста
        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                // Поиск пользователя
                fragment.get().clear();
                if (!s.toString().equals(""))
                    fragment.get().search(s.toString());
            }
        });
        // Начало ввода
        searchEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                fragment.set(new SearchUserFragment());
                setFragment(fragment.get());
                backBtn.setVisibility(View.VISIBLE);
            }
        });

        // Прекращение ввода
        backBtn.setOnClickListener(v -> {
            setFragment(new ForYouUserFragment());
            backBtn.setVisibility(View.INVISIBLE);
            searchEdit.clearFocus();
            searchEdit.setText("");
            InputMethodManager imm = (InputMethodManager) getContext().
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        });

        return view;

    }

    public void setFragment(Fragment fragment) {
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_view, fragment).commit();
    }
}