package com.brigade.rockit.fragments.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.R;
import com.brigade.rockit.activities.OtherActivity;
import com.brigade.rockit.adapter.PostAdapter;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.database.AdapterManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;

// Фрагмент с лентой новостей
public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();

        // Получение виджетов
        ImageView otherBtn = view.findViewById(R.id.option_btn);
        RecyclerView newsFeed = view.findViewById(R.id.news_feed);
        ImageView logoImg = view.findViewById(R.id.logo_img);

        // Отображение ленты новостей
        PostAdapter adapter = new PostAdapter(mainActivity);
        newsFeed.setLayoutManager(new LinearLayoutManager(mainActivity));
        newsFeed.setAdapter(adapter);
        AdapterManager adapterManager = new AdapterManager();
        adapterManager.showNewPosts(adapter, Data.getCurUser(), new TaskListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });

        // Пасхалка)
        logoImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/dQw4w9WgXcQ"));
            startActivity(intent);
        });

        // Переход к активности настроек
        otherBtn.setOnClickListener(v -> {
            Intent intent = new Intent(mainActivity, OtherActivity.class);
            startActivity(intent);
        });

        return view;
    }
}