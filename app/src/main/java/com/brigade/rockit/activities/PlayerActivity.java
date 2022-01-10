package com.brigade.rockit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.brigade.rockit.R;
import com.brigade.rockit.fragments.music.PlayerPagerFragment;

// Активность полноэкранного плеера
public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        setFragment(new PlayerPagerFragment());
    }

    // Установка фрагмента
    public void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frgmnt_view,
                fragment).commit();
    }
}