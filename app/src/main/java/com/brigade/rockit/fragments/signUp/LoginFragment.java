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
import com.brigade.rockit.database.AvailableListener;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.UserManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Ввод данных о пользователе
public class LoginFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        StartActivity startActivity = (StartActivity)getActivity();

        // Получение виджетов
        EditText nameEdit = view.findViewById(R.id.name_edit);
        EditText surnameEdit = view.findViewById(R.id.surname_edit);
        EditText loginEdit = view.findViewById(R.id.login_edit);
        Button nextBtn = view.findViewById(R.id.next_btn);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);

        // Ввод данных
        nextBtn.setOnClickListener(v -> {
            String name = nameEdit.getText().toString();
            String surname = surnameEdit.getText().toString();
            String login = loginEdit.getText().toString();
            // Проверка правильности формы введённых данных
            if (validateName(name) && validateName(surname) && validateLogin(login)) {
                // Проверка занятости логина
                UserManager manager = new UserManager();
                manager.checkLogin(login, new AvailableListener() {
                    @Override
                    public void onComplete(boolean available) {
                        if (available) { // Логин свободен
                            Data.getNewUser().setName(name);
                            Data.getNewUser().setSurname(surname);
                            Data.getNewUser().setLogin(login);
                            startActivity.setFragment(new PasswordFragment());
                        } else // Логин занят
                            Toast.makeText(getContext(), getString(R.string.login_taken_error),
                                    Toast.LENGTH_LONG).show();

                    }
                    @Override // Вывод ошибки
                    public void onFailure(Exception e) {
                        ExceptionManager.showError(e, getContext());
                    }
                });
            }
            if (!validateName(name)) {
                Toast.makeText(getContext(), getString(R.string.name_error),
                        Toast.LENGTH_LONG).show();
            }
            if (!validateName(surname)) {
                Toast.makeText(getContext(), getString(R.string.surname_error),
                        Toast.LENGTH_LONG).show();
            }
            if (!validateLogin(login)) {
                Toast.makeText(getContext(), getString(R.string.login_form_error),
                        Toast.LENGTH_LONG).show();
            }
        });

        toolbar.setNavigationOnClickListener(v -> startActivity.previousFragment());

        return view;
    }

    // Проверка формы имени
    private boolean validateName(String name) {
        Pattern pattern = Pattern.compile(Patterns.NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    // Проверка формы логина
    private boolean validateLogin(String login) {
        Pattern pattern = Pattern.compile(Patterns.LOGIN_PATTERN);
        Matcher matcher = pattern.matcher(login);
        return matcher.matches();
    }

}