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


public class AccountFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        OtherActivity otherActivity = (OtherActivity)getActivity();

        Button changePswrdBtn = view.findViewById(R.id.change_pswrd_btn);
        Button changeLoginBtn = view.findViewById(R.id.change_login_btn);
        Button changeEmailBtn = view.findViewById(R.id.change_email_btn);
        Button backBtn = view.findViewById(R.id.back_btn_acnt);

        changePswrdBtn.setOnClickListener(v -> {
            otherActivity.setFragment(new ChangePswrdFragment());
        });

        changeLoginBtn.setOnClickListener(v -> {
            otherActivity.setFragment(new ChangeLoginFragment());
        });

        changeEmailBtn.setOnClickListener(v -> {
            otherActivity.setFragment(new ChangeEmailFragment());
        });

        backBtn.setOnClickListener(v -> {
            otherActivity.setFragment(new OtherFragment());
        });
        return view;
    }
}