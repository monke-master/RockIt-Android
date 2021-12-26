package com.brigade.rockit.fragments.signIn;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.StartActivity;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.database.UserManager;

public class SignInFragment extends Fragment {
    private StartActivity startActivity;
    private UserManager manager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        startActivity = (StartActivity)getActivity();
        manager = new UserManager();

        // Получение виджетов
        EditText emailEdit = view.findViewById(R.id.phone_edit);
        EditText passwordEdit = view.findViewById(R.id.pswrd_edit);
        Button signInBtn = view.findViewById(R.id.sign_in_btn);
        Button backBtn = view.findViewById(R.id.back_btn);

        // Вход
        signInBtn.setOnClickListener(v -> {
            String email = emailEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            if (!email.equals("") && !password.equals("")) {
                if (email.contains("@")) {
                    signInWithEmail(email, password); // Вход по почте
                } else {
                    signInWithLogin(email, password); // Вход по логину
                }
            }
            // Обработка пустых вводов
            if (email.equals(""))
                Toast.makeText(getContext(), getString(R.string.empty_email_error),
                        Toast.LENGTH_LONG).show();
            if (password.equals(""))
                Toast.makeText(getContext(), getString(R.string.empty_password_error),
                        Toast.LENGTH_LONG).show();
        });

        // Возвращение на стартовый фрагмент
        backBtn.setOnClickListener(v -> {
            startActivity.goToStart();
        });

        return view;
    }

    // Вход по почте
    private void signInWithEmail(String email, String password) {
        manager.signInWithEmail(email, password, new TaskListener() {
            @Override
            public void onComplete() { // Вход
                startActivity.signIn();
            }

            @Override
            public void onFailure(Exception e) { // Обработка ошибки
                ExceptionManager.showError(e, getContext());
            }
        });
    }

    // Вход по логину
    private void signInWithLogin(String login, String password) {
        manager.signInWithLogin(login, password, new TaskListener() {
            @Override
            public void onComplete() { // Вход
                startActivity.signIn();
            }

            @Override
            public void onFailure(Exception e) { // Обработка ошибки
                ExceptionManager.showError(e, getContext());
            }
        });
    }
}