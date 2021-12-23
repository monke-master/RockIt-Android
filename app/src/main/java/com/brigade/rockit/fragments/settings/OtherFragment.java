package com.brigade.rockit.fragments.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.R;
import com.brigade.rockit.activities.OtherActivity;
import com.brigade.rockit.activities.StartActivity;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.fragments.main.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;

public class OtherFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other, container, false);
        OtherActivity otherActivity = (OtherActivity) getActivity();

        // Получение виджетов
        Button acntStngsBtn = view.findViewById(R.id.acnt_stngs_btn);
        Button stngsBtn = view.findViewById(R.id.stngs_btn);
        Button signOutBtn = view.findViewById(R.id.sign_out_btn);
        Button backBtn = view.findViewById(R.id.back_btn_other);

        // Настройки аккаунта
        acntStngsBtn.setOnClickListener(v -> {
            otherActivity.setFragment(new AccountFragment());
        });
        // Выход из учетной записи
        signOutBtn.setOnClickListener(v -> {
            if (Data.getMusicPlayer() != null)
                Data.getMusicPlayer().stopSong();
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(otherActivity, StartActivity.class);
            startActivity(intent);
        });
        // Предыдущий фрагмент
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(otherActivity, MainActivity.class);
            startActivity(intent);
        });
        return view;
    }
}