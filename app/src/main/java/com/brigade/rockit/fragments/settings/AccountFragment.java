package com.brigade.rockit.fragments.settings;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.activities.OtherActivity;
import com.google.android.material.appbar.MaterialToolbar;

// Настройки аккаунта
public class AccountFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        OtherActivity otherActivity = (OtherActivity)getActivity();

        Button changePswrdBtn = view.findViewById(R.id.change_pswrd_btn);
        Button changeLoginBtn = view.findViewById(R.id.change_login_btn);
        Button changeEmailBtn = view.findViewById(R.id.change_email_btn);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);

        changePswrdBtn.setOnClickListener(v -> {
            otherActivity.setFragment(new ChangePswrdFragment());
        });

        changeLoginBtn.setOnClickListener(v -> {
            otherActivity.setFragment(new ChangeLoginFragment());
        });

        changeEmailBtn.setOnClickListener(v -> {
            otherActivity.setFragment(new ChangeEmailFragment());
        });

        toolbar.setNavigationOnClickListener(v -> {
            otherActivity.previousFragment();
        });
        return view;
    }
}