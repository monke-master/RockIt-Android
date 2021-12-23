package com.brigade.rockit.fragments.profile;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.brigade.rockit.adapter.PostAdapter;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.GlideApp;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.R;
import com.brigade.rockit.data.User;
import com.brigade.rockit.database.AdapterManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.database.UserManager;
import com.brigade.rockit.fragments.main.NewContentFragment;
import com.brigade.rockit.fragments.settings.EditProfileFragment;
import com.brigade.rockit.fragments.users.FollowPagerFragment;

public class ProfileFragment extends Fragment {
    private TextView nameTxt, loginTxt, bioTxt;
    private ImageView profilePicImg;
    private MainActivity mainActivity;
    private User user;
    private TextView followersCount;
    private TextView followingCount;
    private UserManager manager;


    public ProfileFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mainActivity = (MainActivity)getActivity();
        manager = new UserManager();

        // Получение виджетов
        nameTxt = view.findViewById(R.id.name_txt);
        loginTxt = view.findViewById(R.id.login_txt);
        bioTxt = view.findViewById(R.id.bio_txt);
        profilePicImg = view.findViewById(R.id.profile_pic_img);
        Button editBtn = view.findViewById(R.id.edit_profile_btn);
        Button followBtn = view.findViewById(R.id.follow_btn);
        Button newCntnBtn = view.findViewById(R.id.new_cntnt_btn);
        RecyclerView postsList = view.findViewById(R.id.posts_list);
        followersCount = view.findViewById(R.id.followers_count);
        followingCount = view.findViewById(R.id.following_count);
        TextView followersTxt = view.findViewById(R.id.followers_txt);
        TextView followingTxt = view.findViewById(R.id.following_txt);

        if (user.getId().equals(Data.getCurUser().getId())) {
            followBtn.setVisibility(View.INVISIBLE);

        }
        else {
            if (Data.getCurUser().getFollowingList().contains(user.getId()))
                followBtn.setText(getString(R.string.unfollow));
            editBtn.setVisibility(View.INVISIBLE);
        }


        // Просмотр фото провиля
        profilePicImg.setOnClickListener(v -> {
            mainActivity.setFragment(new ProfilePicFragment(user));
        });

        editBtn.setOnClickListener(v -> {
            mainActivity.setFragment(new EditProfileFragment());
        });

        newCntnBtn.setOnClickListener(v -> {
            mainActivity.setFragment(new NewContentFragment());
        });

        followBtn.setOnClickListener(v -> {
            if (followBtn.getText().equals(getString(R.string.follow))) { // Подписка на пользователя
                manager.followUser(user, new TaskListener() {
                    @Override
                    public void onComplete() {
                        followBtn.setText(R.string.unfollow);
                        followersCount.setText(String.valueOf(user.getFollowersList().size() + 1));
                        user.getFollowersList().add(Data.getCurUser().getId());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ExceptionManager.showError(e, getContext());
                    }
                });

            } else {
                manager.unfollowUser(user, new TaskListener() {
                    @Override
                    public void onComplete() { // Отписка от пользователя
                        followBtn.setText(R.string.follow);
                        followersCount.setText(String.valueOf(user.getFollowersList().size() - 1));
                        user.getFollowersList().remove(Data.getCurUser().getId());
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ExceptionManager.showError(e, getContext());
                    }
                });

            }

        });

        // Переход на список подписок
        followingCount.setOnClickListener(v -> mainActivity.setFragment(
                new FollowPagerFragment(user, 0))
        );

        // Переход на список подписчиков
        followersCount.setOnClickListener(v -> mainActivity.setFragment(
                new FollowPagerFragment(user, 1))
        );

        followersTxt.setOnClickListener(v -> mainActivity.setFragment(
                new FollowPagerFragment(user, 1)));

        followingTxt.setOnClickListener(v -> mainActivity.setFragment(
                new FollowPagerFragment(user, 0)));

        // Отображение постов
        postsList.setLayoutManager(new LinearLayoutManager(getContext()));
        PostAdapter adapter = new PostAdapter(mainActivity);
        adapter.setMainActivity(mainActivity);
        postsList.setAdapter(adapter);
        AdapterManager adapterManager = new AdapterManager();
        adapterManager.showUserPosts(adapter, user, new TaskListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });

        // Вывод информации о пользователе
        showProfileInfo();
        return view;
    }

    // Вывод информации о пользователе
    private void showProfileInfo() {
        String name = user.getName() + " " + user.getSurname();
        String login = user.getLogin();
        String bio = user.getBio();
        nameTxt.setText(name);
        loginTxt.setText("@" + login);
        bioTxt.setText(bio);
        GlideApp.with(mainActivity).load(user.getPictureUri()).circleCrop().into(profilePicImg);

        // Отоюражение кол-ва подписок и подписчиков
        manager.getFollowInfo(user, new TaskListener() {
            @Override
            public void onComplete() {
                followersCount.setText(String.valueOf(user.getFollowersList().size()));
                followingCount.setText(String.valueOf(user.getFollowingList().size()));
            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });

    }
}
