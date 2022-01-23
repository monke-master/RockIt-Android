package com.brigade.rockit.fragments.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.R;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.fragments.music.MusicFragment;
import com.brigade.rockit.fragments.profile.ProfileFragment;
import com.brigade.rockit.fragments.users.UsersFragment;


public class FooterFragment extends Fragment {

    private ImageView homeBtn;
    private ImageView profileBtn;
    private ImageView musicBtn;
    private ImageView searchBtn;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_footer, container, false);
        mainActivity = (MainActivity)getActivity();

        // Получение виджетов
        homeBtn = view.findViewById(R.id.home_btn);
        searchBtn = view.findViewById(R.id.search_btn);
        profileBtn = view.findViewById(R.id.profile_btn);
        musicBtn = view.findViewById(R.id.music_btn);

        homeBtn.setImageDrawable(getResources().getDrawable(R.drawable.home,
                getContext().getTheme()));

        // Переключение фрагментов
        homeBtn.setOnClickListener(v -> {
            mainActivity.setFragment(new HomeFragment());
            setIcons();
        });
        searchBtn.setOnClickListener(v -> {
            mainActivity.setFragment(new UsersFragment());
            setIcons();
        });
        profileBtn.setOnClickListener(v -> {
            mainActivity.setFragment(new ProfileFragment(Data.getCurUser()));
            setIcons();
        });
        musicBtn.setOnClickListener(v -> {
            mainActivity.setFragment(new MusicFragment());
            setIcons();
        });
        return view;
    }

    public void setIcons() {
        homeBtn.setImageDrawable(getResources().getDrawable(R.drawable.home_gray,
                getContext().getTheme()));
        profileBtn.setImageDrawable(getResources().getDrawable(R.drawable.profile_gray,
                getContext().getTheme()));
        musicBtn.setImageDrawable(getResources().getDrawable(R.drawable.music_gray,
                getContext().getTheme()));
        searchBtn.setImageDrawable(getResources().getDrawable(R.drawable.search_gray,
                getContext().getTheme()));
        if (mainActivity.getCurrentFragment() instanceof HomeFragment)
            homeBtn.setImageDrawable(getResources().getDrawable(R.drawable.home,
                    getContext().getTheme()));
        else if (mainActivity.getCurrentFragment() instanceof ProfileFragment)
            profileBtn.setImageDrawable(getResources().getDrawable(R.drawable.profile,
                    getContext().getTheme()));
        else if (mainActivity.getCurrentFragment() instanceof MusicFragment)
            musicBtn.setImageDrawable(getResources().getDrawable(R.drawable.music,
                    getContext().getTheme()));
        else if (mainActivity.getCurrentFragment() instanceof UsersFragment)
            searchBtn.setImageDrawable(getResources().getDrawable(R.drawable.search,
                    getContext().getTheme()));


    }
}