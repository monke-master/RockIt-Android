package com.brigade.rockit.fragments.music;

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

import java.util.concurrent.atomic.AtomicReference;


public class MusicFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        setFragment(new MusicPagerFragment());

        // Фрагмент с результатами поиска
        AtomicReference<SearchMusicFragment> fragment = new AtomicReference<>(new SearchMusicFragment());

        // Получение виджетов
        Button backBtn = view.findViewById(R.id.back_btn);
        backBtn.setVisibility(View.INVISIBLE);
        EditText searchEdit = view.findViewById(R.id.search_music);

        searchEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            // При изменении вводимого текста фрагмент отображает результаты поиска
            @Override
            public void afterTextChanged(Editable s) {
                fragment.get().clear();
                if (!s.toString().equals(""))
                    fragment.get().search(s.toString());
            }
        });

        // Отображение фрагмента с результатами
        searchEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                fragment.set(new SearchMusicFragment());
                setFragment(fragment.get());
                backBtn.setVisibility(View.VISIBLE);
            }

        });

        // Переключение фокуса на предыдущий фрагмент
        backBtn.setOnClickListener(v -> {
            setFragment(new MusicPagerFragment());
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