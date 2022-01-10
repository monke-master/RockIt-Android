package com.brigade.rockit.fragments.settings;

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
import com.brigade.rockit.data.Constants;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Patterns;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.database.UserManager;
import com.brigade.rockit.fragments.profile.ProfileFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Редактирование профиля
public class EditProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();

        // Получение виджетов
        EditText nameEdit = view.findViewById(R.id.new_name_edit);
        EditText surnameEdit = view.findViewById(R.id.new_surname_edit);
        EditText bioEdit = view.findViewById(R.id.bio_edit);
        Button backBtn = view.findViewById(R.id.back_btn_ep);
        Button changeBtn = view.findViewById(R.id.change_btn);

        nameEdit.setText(Data.getCurUser().getName());
        surnameEdit.setText(Data.getCurUser().getSurname());
        bioEdit.setText(Data.getCurUser().getBio());

        changeBtn.setOnClickListener(v -> {
            String newName = nameEdit.getText().toString();
            String newSurname = surnameEdit.getText().toString();
            String newBio = bioEdit.getText().toString();
            if (newBio == null)
                newBio = "";
            boolean successfully = true;
            UserManager userManager = new UserManager();
            // Если пользователь ввел новое имя
            if (!newName.equals(Data.getCurUser().getName())) {
                if (validateName(newName)) { // И оно удовлетворяет требованиям
                    userManager.changeName(newName, new TaskListener() { // Смена имени
                        @Override
                        public void onComplete() {
                            Toast.makeText(getContext(), getActivity().getString(R.string.successfully_edit_name),
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            ExceptionManager.showError(e, getContext());
                        }
                    });
                } else { // Уведомление об ошибке
                    Toast.makeText(getContext(), getString(R.string.name_error),
                            Toast.LENGTH_LONG).show();
                    successfully = false;
                }
            }
            // Если пользователь ввел новую фамилию
            if (!newSurname.equals(Data.getCurUser().getSurname())) {
                if (validateName(newSurname)) {// И она удовлетворяет требованиям
                    userManager.changeSurname(newSurname, new TaskListener() {  // Смена фамилии
                        @Override
                        public void onComplete() {
                            Toast.makeText(getContext(), getString(R.string.successfully_edit_surname),
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            ExceptionManager.showError(e, getContext());
                        }
                    });
                } else { // Уведомление об ошибке
                    Toast.makeText(getContext(), getString(R.string.surname_error),
                            Toast.LENGTH_LONG).show();
                    successfully = false;
                }
            }
            // Если пользователь изменил краткую информацию о себе,
            if (!newBio.equals(Data.getCurUser().getBio())) {
                if (validateBio(newBio)) {  // И она удовлетворяет требованиям
                    userManager.changeBio(newBio, new TaskListener() { // Изменение кр. инфоомации
                        @Override
                        public void onComplete() {
                            Toast.makeText(getContext(), getString(R.string.successfully_edit_bio),
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            ExceptionManager.showError(e, getContext());
                        }
                    });
                } else { // Уведомление об ошибке
                    Toast.makeText(getContext(), getString(R.string.bio_length_error),
                            Toast.LENGTH_LONG).show();
                    successfully = false;
                }
            }
            if (successfully)
                mainActivity.setFragment(new ProfileFragment(Data.getCurUser()));
        });

        backBtn.setOnClickListener(v -> {
            mainActivity.setFragment(new ProfileFragment(Data.getCurUser()));
        });

        return view;
    }

    // Проверка формы имени
    private boolean validateName(String name) {
        Pattern pattern = Pattern.compile(Patterns.NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private boolean validateBio(String bio) {
        return bio.length() <= Constants.MAX_BIO_LENGTH;
    }
}