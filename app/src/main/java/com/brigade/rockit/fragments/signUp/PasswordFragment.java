package com.brigade.rockit.fragments.signUp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Patterns;
import com.brigade.rockit.R;
import com.brigade.rockit.activities.StartActivity;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.database.UserManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Ввод пароля
public class PasswordFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_password, container, false);
        StartActivity startActivity = (StartActivity)getActivity();

        // Получение виджетов
        EditText enterPassword = view.findViewById(R.id.enter_pswrd_edit);
        EditText repeatPassword = view.findViewById(R.id.repeat_pswrd_edit);
        Button nextBtn = view.findViewById(R.id.next_btn);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);

        // Ввод паролей
        nextBtn.setOnClickListener(v -> {
            String password1 = enterPassword.getText().toString();
            String password2 = repeatPassword.getText().toString();
            // Проверка совппадений паролей и правильности формы
            if (validatePassword(password1) && password1.equals(password2)) {
                // Создание пользователя
                UserManager manager = new UserManager();
                manager.createUser(Data.getNewUser(), password1, new TaskListener() {
                    @Override
                    public void onComplete() { // Вход
                        startActivity.setFragment(new VerifyEmailFragment());
                    }

                    @Override
                    public void onFailure(Exception e) { // Обработка ошибки
                        ExceptionManager.showError(e, getContext());
                    }
                });
            }
            if (!validatePassword(password1)) {
                Toast.makeText(getContext(), getString(R.string.pswrd_form_error),
                        Toast.LENGTH_LONG).show();
            }
            if (!password1.equals(password2)) {
                Toast.makeText(getContext(), getString(R.string.pswrd_match_error),
                        Toast.LENGTH_LONG).show();
            }
        });

        toolbar.setNavigationOnClickListener(v -> startActivity.previousFragment());

        return view;
    }

    // Проверка правильности формы пароля
    private boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile(Patterns.PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}