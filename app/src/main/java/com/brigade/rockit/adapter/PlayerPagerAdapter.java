package com.brigade.rockit.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.brigade.rockit.fragments.music.PlayerFragment;
import com.brigade.rockit.fragments.music.QueueFragment;

public class PlayerPagerAdapter extends FragmentStateAdapter {
    private static int NUM_PAGES = 2;

    public PlayerPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PlayerFragment();
            case 1:
                return new QueueFragment();
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
