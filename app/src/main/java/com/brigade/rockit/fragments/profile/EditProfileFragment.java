package com.brigade.rockit.fragments.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.android.material.appbar.MaterialToolbar;

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
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);

        nameEdit.setText(Data.getCurUser().getName());
        surnameEdit.setText(Data.getCurUser().getSurname());
        bioEdit.setText(Data.getCurUser().getBio());

        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String name = s.toString();
                toolbar.getMenu().getItem(0).setVisible(!(name.equals("") ||
                        name.equals(Data.getCurUser().getName()) ||
                        surnameEdit.getText().toString().equals("")));

            }
        });

        surnameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String surname = s.toString();
                toolbar.getMenu().getItem(0).setVisible(!(surname.equals("") ||
                        surname.equals(Data.getCurUser().getSurname()) ||
                        nameEdit.getText().toString().equals("")));
            }
        });

        bioEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String bio = s.toString();
                toolbar.getMenu().getItem(0).setVisible(!(bio.equals(Data.getCurUser().getBio())
                        || nameEdit.getText().toString().equals("") ||
                        surnameEdit.getText().toString().equals("")));
            }
        });

        toolbar.setOnMenuItemClickListener(item -> {
            String newName = nameEdit.getText().toString();
            String newSurname = surnameEdit.getText().toString();
            String newBio = bioEdit.getText().toString();
            boolean successfully = true;
            UserManager userManager = new UserManager();
            // Если пользователь ввел новое имя
            if (!newName.equals(Data.getCurUser().getName())) {
                if (validateName(newName)) { // И оно удовлетворяет требованиям
                    userManager.changeName(newName, new TaskListener() { // Смена имени
                        @Override
                        public void onComplete() {
                            Toast.makeText(getContext(), getActivity().getString(R.string.name_changed),
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
                            Toast.makeText(getContext(), getString(R.string.surname_changed),
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
                            Toast.makeText(getContext(), getString(R.string.bio_changed),
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
            return true;
        });

        toolbar.setNavigationOnClickListener(v -> mainActivity.previousFragment());

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