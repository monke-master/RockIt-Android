package com.brigade.rockit.fragments.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.brigade.rockit.fragments.main.HomeFragment;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        Button changeBtn = view.findViewById(R.id.change_btn_e);
        Button backBtn = view.findViewById(R.id.back_btn_ce);

        String curEmail = Data.getCurUser().getEmail();
        curEmailTxt.setText(curEmail);

        // Смена почты
        changeBtn.setOnClickListener(v -> {
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
                            Intent intent = new Intent(otherActivity, MainActivity.class);
                            startActivity(intent);
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
        });

        // Возвращение на предыдущий фрагмент
        backBtn.setOnClickListener(v -> {
            otherActivity.setFragment(new AccountFragment());
        });
        return view;
    }

    // Проверка правильности формы почты
    private boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(Patterns.EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}