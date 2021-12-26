package com.brigade.rockit.fragments.signUp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.StartActivity;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.database.UserManager;
import com.brigade.rockit.fragments.signIn.SignInFragment;

public class VerifyEmailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify_email, container, false);
        StartActivity startActivity = (StartActivity)getActivity();

        Button backBtn = view.findViewById(R.id.back_btn);
        Button nextBtn = view.findViewById(R.id.next_btn);

        UserManager manager = new UserManager();
        manager.verifyEmail(new TaskListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });

        nextBtn.setOnClickListener(v -> startActivity.setFragment(new SignInFragment()));

        // Возвращение на стартовый фрагмент
        backBtn.setOnClickListener(v -> {
            startActivity.goToStart();
        });

        return view;
    }
}