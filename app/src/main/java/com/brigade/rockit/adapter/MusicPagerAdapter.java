package com.brigade.rockit.adapter;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.brigade.rockit.fragments.music.ForYouFragment;
import com.brigade.rockit.fragments.music.MyMusicFragment;

// Адаптер для вкладок с музыкой
public class MusicPagerAdapter extends FragmentStateAdapter {
    private static int NUM_PAGES = 2;

    public MusicPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MyMusicFragment();
            case 1:
                return new ForYouFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }


}
