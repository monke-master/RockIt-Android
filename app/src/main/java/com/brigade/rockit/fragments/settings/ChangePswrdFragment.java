package com.brigade.rockit.fragments.settings;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.activities.OtherActivity;
import com.brigade.rockit.data.Patterns;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.database.UserManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePswrdFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_pswrd, container, false);
        OtherActivity otherActivity = (OtherActivity) getActivity();

        // Получение виджетов
        EditText curPswrdEdit = view.findViewById(R.id.cur_pswrd_edit);
        EditText newPswrdEdit = view.findViewById(R.id.new_pswrd_edit);
        Button backBtn = view.findViewById(R.id.back_btn_cp);
        Button changeBtn = view.findViewById(R.id.change_btn_p);

        changeBtn.setOnClickListener(v -> {
            String curPswrd = curPswrdEdit.getText().toString();
            String newPswrd = newPswrdEdit.getText().toString();
            // Если форма текущего пароля правильная
            if (validatePassword(newPswrd)) {
                UserManager manager = new UserManager();
                // Смена пароля
                manager.changePassword(curPswrd, newPswrd, new TaskListener() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(getContext(), getString(R.string.successfully_edit_pswrd),
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(otherActivity, MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ExceptionManager.showError(e, getContext());
                    }
                });
            } else // Неверная форма нового пароля
                Toast.makeText(getContext(), getString(R.string.pswrd_form_error),
                        Toast.LENGTH_LONG).show();
        });

        backBtn.setOnClickListener(v -> {
            otherActivity.setFragment(new AccountFragment());
        });
        return view;
    }

    // Проверка правильности формы пароля
    private boolean validatePassword(String password) {
        Pattern pattern = Pattern.compile(Patterns.PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}