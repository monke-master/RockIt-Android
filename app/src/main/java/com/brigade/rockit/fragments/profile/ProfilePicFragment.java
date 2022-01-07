package com.brigade.rockit.fragments.profile;

import android.net.Uri;
import android.os.Bundle;


import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.brigade.rockit.data.Constants;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.GlideApp;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.R;
import com.brigade.rockit.data.User;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.database.UserManager;
import com.brigade.rockit.fragments.dialogs.PhotoDialog;

import java.util.ArrayList;

public class ProfilePicFragment extends Fragment {
    private User user;


    public ProfilePicFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_pic, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();

        // Получение виджетов
        Button editBtn = view.findViewById(R.id.edit_btn);
        Button backBtn = view.findViewById(R.id.back_btn_pp);
        ImageView profilePicImg = view.findViewById(R.id.profile_pic_img_big);
        editBtn.setVisibility(View.INVISIBLE);

        GlideApp.with(mainActivity).load(user.getPictureUri()).into(profilePicImg);

        // Возвращение к фрагменту с данными пользователя
        backBtn.setOnClickListener(v -> {
            mainActivity.setFragment(new ProfileFragment(user));
        });

        if (user.getId().equals(Data.getCurUser().getId())) {
            // Изменение фото профиля
            editBtn.setVisibility(View.VISIBLE);
            editBtn.setOnClickListener(v -> {
                // Диалог с вариантами изменения
                PhotoDialog dialog = new PhotoDialog(1, new GetObjectListener() {
                    @Override
                    public void onComplete(Object object) {
                        ArrayList<Uri> uris = (ArrayList<Uri>) object;
                        UserManager userManager = new UserManager();
                        userManager.changeProfilePicture(uris.get(0), new TaskListener() {
                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onFailure(Exception e) {
                                ExceptionManager.showError(e, getContext());
                            }
                        });
                        mainActivity.getPreviousFragment();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ExceptionManager.showError(e, getContext());
                    }
                });
                dialog.show(getParentFragmentManager(), getString(R.string.photo));
            });
        }


        return view;
    }

}
