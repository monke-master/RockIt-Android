package com.brigade.rockit.fragments.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.activities.OtherActivity;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Patterns;
import com.brigade.rockit.database.AvailableListener;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.UserManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Смена логина
public class ChangeLoginFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_login, container, false);
        OtherActivity otherActivity = (OtherActivity) getActivity();

        // Полученние виджетов
        EditText newLoginEdit = view.findViewById(R.id.new_login_edit);
        TextView curLoginTxt = view.findViewById(R.id.cur_login_txt);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);

        // Отображение текущего логина
        String currentLogin = Data.getCurUser().getLogin();
        curLoginTxt.setText(currentLogin);

        newLoginEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                toolbar.getMenu().getItem(0).setVisible(!s.toString().equals(""));
            }
        });

        toolbar.setOnMenuItemClickListener(item -> {
            String newLogin = newLoginEdit.getText().toString();
            // Если форма нового логина удовлетворяет требованиям
            if (validateLogin(newLogin)) {
                UserManager manager = new UserManager();
                // Смена логина
                manager.changeLogin(newLogin, new AvailableListener() {
                    @Override
                    public void onComplete(boolean available) {
                        if (available) {
                            Toast.makeText(getContext(), getString(R.string.successfully_edit_login),
                                    Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(otherActivity, MainActivity.class);
                            startActivity(intent);
                        } else
                            Toast.makeText(getContext(), getString(R.string.login_taken_error),
                                    Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onFailure(Exception e) {
                        ExceptionManager.showError(e, getContext());
                    }
                });
            } else
                Toast.makeText(getContext(), getString(R.string.login_form_error),
                        Toast.LENGTH_LONG).show();
            return false;
        });

        toolbar.setNavigationOnClickListener(v -> {
            otherActivity.previousFragment();
        });

        return view;
    }

    // Проверка формы логина
    private boolean validateLogin(String login) {
        Pattern pattern = Pattern.compile(Patterns.LOGIN_PATTERN);
        Matcher matcher = pattern.matcher(login);
        return matcher.matches();
    }
}