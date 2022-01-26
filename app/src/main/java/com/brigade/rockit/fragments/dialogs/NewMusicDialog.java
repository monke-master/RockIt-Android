package com.brigade.rockit.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.fragments.music.NewAlbumFragment;
import com.brigade.rockit.fragments.music.NewSongFragment;

// Диалог добавления музыки
public class NewMusicDialog extends DialogFragment{

    public NewMusicDialog() {

    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setItems(R.array.music_dialog, (dialog, which) -> {
                    MainActivity mainActivity = (MainActivity)getActivity();
                    switch (which) {
                        case 0:
                            mainActivity.setFragment(new NewSongFragment());
                            break;
                        case 1:
                            mainActivity.setFragment(new NewAlbumFragment());
                            break;

                    }

                });
        return builder.create();
    }
}
