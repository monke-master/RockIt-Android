package com.brigade.rockit.fragments.users;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.adapter.UserAdapter;
import com.brigade.rockit.data.User;
import com.brigade.rockit.database.AdapterManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;


public class FollowingFragment extends Fragment {
    private User user;

    public FollowingFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();
        // Получение виджетов
        RecyclerView followingList = view.findViewById(R.id.following_list);
        UserAdapter adapter = new UserAdapter(mainActivity);
        followingList.setAdapter(adapter);
        followingList.setLayoutManager(new LinearLayoutManager(getContext()));
        // Отображение списка подписок
        AdapterManager viewer = new AdapterManager();
        viewer.showUsers(adapter, user.getFollowingList(), new TaskListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });
        return view;
    }
}