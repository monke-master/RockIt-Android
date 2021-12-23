package com.brigade.rockit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.brigade.rockit.R;
import com.brigade.rockit.fragments.main.HomeFragment;
import com.brigade.rockit.fragments.settings.OtherFragment;

public class OtherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);

        if (savedInstanceState == null) {
            setFragment(new OtherFragment());
        }
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frgmnt_view,
                fragment).commit();
    }
}