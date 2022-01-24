package com.brigade.rockit.fragments.profile;

import android.net.Uri;
import android.os.Bundle;


import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

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
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

// Фрагмент с фото профиля
public class ProfilePicFragment extends Fragment {
    private User user;
    private Uri newPhoto;


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
        ImageView profilePicImg = view.findViewById(R.id.profile_pic_img_big);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);

        editBtn.setVisibility(View.INVISIBLE);

        GlideApp.with(mainActivity).load(user.getPictureUri()).into(profilePicImg);


        if (user.getId().equals(Data.getCurUser().getId())) {
            // Изменение фото профиля
            editBtn.setVisibility(View.VISIBLE);
            editBtn.setOnClickListener(v -> {
                // Диалог с вариантами изменения
                PhotoDialog dialog = new PhotoDialog(1, new GetObjectListener() {
                    @Override
                    public void onComplete(Object object) {
                        ArrayList<Uri> uris = (ArrayList<Uri>) object;
                        newPhoto = uris.get(0);
                        toolbar.getMenu().getItem(0).setVisible(true);
                        GlideApp.with(mainActivity).load(newPhoto).into(profilePicImg);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ExceptionManager.showError(e, getContext());
                    }
                });
                dialog.show(getParentFragmentManager(), getString(R.string.photo));
            });
        }

        toolbar.setOnMenuItemClickListener(item -> {
            UserManager userManager = new UserManager();
            userManager.changeProfilePicture(newPhoto, new TaskListener() {
                @Override
                public void onComplete() {

                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, getContext());
                }
            });
            mainActivity.previousFragment();
            return true;
        });

        toolbar.setNavigationOnClickListener(v -> mainActivity.previousFragment());


        return view;
    }

}
