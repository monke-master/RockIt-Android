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
import com.brigade.rockit.database.AdapterManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;


public class SearchUserFragment extends Fragment {

    private UserAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_user, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();
        // Получение виджетов
        RecyclerView usersList = view.findViewById(R.id.users_list);
        usersList.setLayoutManager(new LinearLayoutManager(mainActivity));
        adapter = new UserAdapter(mainActivity);
        usersList.setAdapter(adapter);

        return view;
    }
    // Поиск пользователей
    public void search(String searching) {
        AdapterManager viewer = new AdapterManager();
        viewer.showSearchedUser(adapter, searching, new TaskListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onFailure(Exception e) {
                ExceptionManager.showError(e, getContext());
            }
        });
    }

    public void clear() {
        adapter.clear();
    }
}