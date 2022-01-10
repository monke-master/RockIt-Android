package com.brigade.rockit.fragments.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.Button;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.StartActivity;
import com.brigade.rockit.fragments.signIn.SignInFragment;
import com.brigade.rockit.fragments.signUp.EmailFragment;


public class StartFragment extends Fragment {


    public StartFragment() {
        super(R.layout.fragment_start);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Получение виджетов
        Button signInBtn = view.findViewById(R.id.sign_in_btn);
        Button signUpBtn = view.findViewById(R.id.sign_up_btn);
        StartActivity startActivity = (StartActivity)getActivity();
        // Вход
        signInBtn.setOnClickListener(v -> {
            startActivity.setFragment(new SignInFragment());
        });
        // Регистрация
        signUpBtn.setOnClickListener(v -> {
            startActivity.setFragment(new EmailFragment());
        });



    }
}