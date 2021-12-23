package com.brigade.rockit.fragments.signUp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Patterns;
import com.brigade.rockit.R;
import com.brigade.rockit.activities.StartActivity;
import com.brigade.rockit.database.AvailableListener;
import com.brigade.rockit.database.DatabaseUser;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.UserManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EmailFragment extends Fragment {
    public EmailFragment() {
        super(R.layout.fragment_email);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StartActivity startActivity = (StartActivity)getActivity();

        // Получение виджетов
        EditText emailEdit = view.findViewById(R.id.email_edit);
        Button signUpBtn = view.findViewById(R.id.next_btn);
        Button backBtn = view.findViewById(R.id.back_btn);

        // Ввод почты
        signUpBtn.setOnClickListener(v -> {
            String email = emailEdit.getText().toString();
            // Проверка правильности формы почты
            if (validateEmail(email)) {
                // Проверка занятости почты
                UserManager manager = new UserManager();
                manager.checkEmail(email, new AvailableListener() {
                    @Override
                    public void onComplete(boolean available) {
                        if (available) {
                            DatabaseUser user = new DatabaseUser();
                            user.setEmail(email);
                            Data.setNewUser(user);
                            startActivity.nextRegFrag();
                        } else
                            Toast.makeText(getContext(), getString(R.string.email_taken),
                                    Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onFailure(Exception e) {
                        ExceptionManager.showError(e, getContext());
                    }
                });
            } else
                Toast.makeText(getContext(), getString(R.string.email_form_error),
                        Toast.LENGTH_LONG).show();
        });

        // Возвращение на стартовый фрагмент
        backBtn.setOnClickListener(v -> {
            startActivity.goToStart();
        });
    }

    // Проверка правильности формы почты
    private boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(Patterns.EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    
}