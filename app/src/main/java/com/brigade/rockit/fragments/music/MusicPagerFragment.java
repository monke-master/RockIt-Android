package com.brigade.rockit.fragments.music;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brigade.rockit.adapter.MusicPagerAdapter;
import com.brigade.rockit.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MusicPagerFragment extends Fragment {


    // Фрагмент с вкладками музыки
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_pager, container, false);
        ViewPager2 viewPager = view.findViewById(R.id.pager);
        MusicPagerAdapter adapter = new MusicPagerAdapter(this);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(getString(R.string.my_music));
                    break;
                case 1:
                    tab.setText(getString(R.string.for_you));
            }
        });
        mediator.attach();
        return view;
    }
}