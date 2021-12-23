package com.brigade.rockit.fragments.users;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.adapter.FollowPagerAdapter;
import com.brigade.rockit.adapter.MusicPagerAdapter;
import com.brigade.rockit.data.User;
import com.brigade.rockit.fragments.profile.ProfileFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class FollowPagerFragment extends Fragment {
    private int position;
    private User user;

    public FollowPagerFragment(User user, int position) {
        this.position = position;
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow_pager, container, false);
        MainActivity mainActivity = (MainActivity)getActivity();

        ViewPager2 viewPager = view.findViewById(R.id.pager);
        Button backBtn = view.findViewById(R.id.back_btn);

        FollowPagerAdapter adapter = new FollowPagerAdapter(this, user);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getString(R.string.following));
                    break;
                case 1:
                    tab.setText(getString(R.string.followers));
            }
        });
        mediator.attach();

        backBtn.setOnClickListener(v -> {
            mainActivity.setFragment(new ProfileFragment(user));
        });

        return view;
    }
}