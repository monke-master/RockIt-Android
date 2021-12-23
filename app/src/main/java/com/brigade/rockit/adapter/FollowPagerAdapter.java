package com.brigade.rockit.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.brigade.rockit.data.User;
import com.brigade.rockit.fragments.users.FollowersFragment;
import com.brigade.rockit.fragments.users.FollowingFragment;

public class FollowPagerAdapter extends FragmentStateAdapter {

    private static int NUM_PAGES = 2;
    private User user;

    public FollowPagerAdapter(@NonNull Fragment fragment, User user) {
        super(fragment);
        this.user = user;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FollowingFragment(user);
            case 1:
                return new FollowersFragment(user);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
