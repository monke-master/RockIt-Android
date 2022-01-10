package com.brigade.rockit.fragments.settings;

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
import com.brigade.rockit.activities.OtherActivity;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Patterns;
import com.brigade.rockit.database.AvailableListener;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.UserManager;
import com.google.android.material.appbar.MaterialToolbar;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Смена почты
public class ChangeEmailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_email, container, false);
        OtherActivity otherActivity = (OtherActivity) getActivity();

        // Получение виджетов
        TextView curEmailTxt = view.findViewById(R.id.cur_email_txt);
        EditText newEmailEdit = view.findViewById(R.id.new_email_edit);
        EditText passwordEdit = view.findViewById(R.id.pswrd_edit_ce);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);

        String curEmail = Data.getCurUser().getEmail();
        curEmailTxt.setText(curEmail);

        newEmailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                toolbar.getMenu().getItem(0).setVisible(!(s.toString().equals("") ||
                        passwordEdit.getText().toString().equals("")));
            }
        });

        passwordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                toolbar.getMenu().getItem(0).setVisible(!(s.toString().equals("") ||
                        newEmailEdit.getText().toString().equals("")));
            }
        });

        toolbar.setOnMenuItemClickListener(item -> {
            String newEmail = newEmailEdit.getText().toString();
            String password = passwordEdit.getText().toString();
            // Если формат новой почты удовлетворяет требованиям, то
            if (validateEmail(newEmail)) {
                UserManager manager = new UserManager();
                // Смена почты пользователя
                manager.changeEmail(curEmail, newEmail, password, new AvailableListener() {
                    @Override
                    public void onComplete(boolean available) {
                        if (available) {
                            Toast.makeText(getContext(), getString(R.string.successfully_edit_email),
                                    Toast.LENGTH_LONG).show();
                            otherActivity.finish();
                        } else
                            Toast.makeText(getContext(), getString(R.string.email_taken),
                                    Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ExceptionManager.showError(e, getContext());
                    }
                });
            } else // Уведомление об ошибке
                Toast.makeText(getContext(), getString(R.string.email_form_error),
                        Toast.LENGTH_LONG).show();
            return true;
        });

        toolbar.setNavigationOnClickListener(v -> otherActivity.previousFragment());
        return view;
    }

    // Проверка правильности формы почты
    private boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(Patterns.EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}