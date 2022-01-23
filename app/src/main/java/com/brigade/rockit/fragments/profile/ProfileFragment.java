package com.brigade.rockit.fragments.profile;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.brigade.rockit.activities.OtherActivity;
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
import com.google.android.material.appbar.MaterialToolbar;

// Фрагмент профиля
public class ProfileFragment extends Fragment {
    private TextView nameTxt, bioTxt;
    private ImageView profilePicImg;
    private MainActivity mainActivity;
    private User user;
    private TextView followersCount;
    private TextView followingCount;
    private UserManager manager;
    private MaterialToolbar toolbar;


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
        bioTxt = view.findViewById(R.id.bio_txt);
        profilePicImg = view.findViewById(R.id.profile_pic_img);
        Button editBtn = view.findViewById(R.id.edit_profile_btn);
        Button newCntnBtn = view.findViewById(R.id.new_post_btn);
        RecyclerView postsList = view.findViewById(R.id.posts_list);
        followersCount = view.findViewById(R.id.followers_count);
        followingCount = view.findViewById(R.id.following_count);
        toolbar = view.findViewById(R.id.toolbar);

        if (user.getId().equals(Data.getCurUser().getId())) {
            // Редактирование профиля
            editBtn.setOnClickListener(v -> {
                mainActivity.setFragment(new EditProfileFragment());
            });
        } else {
            if (Data.getCurUser().getFollowersList().contains(user.getId())) {
                editBtn.setText(R.string.unfollow);
                editBtn.setOnClickListener(v -> {
                    manager.unfollowUser(user, new TaskListener() {
                        @Override
                        public void onComplete() { // Отписка от пользователя
                            editBtn.setText(R.string.follow);
                            followersCount.setText(String.valueOf(user.getFollowersList().size() - 1));
                            user.getFollowersList().remove(Data.getCurUser().getId());
                        }

                        @Override
                        public void onFailure(Exception e) {
                            ExceptionManager.showError(e, getContext());
                        }
                    });
                });
            } else {
                editBtn.setText(getString(R.string.follow));
                // Подписка/отписка
                editBtn.setOnClickListener(v -> {
                    if (editBtn.getText().equals(getString(R.string.follow))) { // Подписка на пользователя
                        manager.followUser(user, new TaskListener() {
                            @Override
                            public void onComplete() {
                                editBtn.setText(R.string.unfollow);
                                followersCount.setText(String.valueOf(user.getFollowersList().size() + 1));
                                user.getFollowersList().add(Data.getCurUser().getId());
                            }

                            @Override
                            public void onFailure(Exception e) {
                                ExceptionManager.showError(e, getContext());
                            }
                        });

                    }
                });
            }
        }

        // Просмотр фото провиля
        profilePicImg.setOnClickListener(v -> {
            mainActivity.setFragment(new ProfilePicFragment(user));
        });



        if (!user.getId().equals(Data.getCurUser().getId()))
            newCntnBtn.setVisibility(View.GONE);

        newCntnBtn.setOnClickListener(v -> {
            mainActivity.setFragment(new NewContentFragment());
        });



        // Переход на актиность настроек
        toolbar.setOnMenuItemClickListener(item -> {
            Intent intent = new Intent(mainActivity, OtherActivity.class);
            startActivity(intent);
            return true;
        });

        // Переход на список подписок
        followingCount.setOnClickListener(v -> mainActivity.setFragment(
                new FollowPagerFragment(user, 0))
        );

        // Переход на список подписчиков
        followersCount.setOnClickListener(v -> mainActivity.setFragment(
                new FollowPagerFragment(user, 1))
        );

        // Переход на фрагмент с подписчиками
        followersCount.setOnClickListener(v -> mainActivity.setFragment(
                new FollowPagerFragment(user, 1)));
        // Переход на фрагмент с подписками
        followingCount.setOnClickListener(v -> mainActivity.setFragment(
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
        toolbar.setTitle("@" + login);
        nameTxt.setText(name);
        bioTxt.setText(bio);
        GlideApp.with(mainActivity).load(user.getPictureUri()).circleCrop().into(profilePicImg);

        // Отоюражение кол-ва подписок и подписчиков
        manager.getFollowInfo(user, new TaskListener() {
            @Override
            public void onComplete() {
                followersCount.setText(user.getFollowersList().size() + "\n" +
                        getString(R.string.followers));
                followingCount.setText(user.getFollowingList().size()  + "\n" +
                        getString(R.string.following));
            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });

    }
}
