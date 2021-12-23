package com.brigade.rockit.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;


public class PhotoDialog extends DialogFragment {

    private int request;
    private int maxPhotos;

    public PhotoDialog(int maxPhotos, int request) {
        this.request = request;
        this.maxPhotos = maxPhotos;
    }

    // Диалог загрузки фото
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.photo)
                .setItems(R.array.photo_dialog, (dialog, which) -> {
                    MainActivity mainActivity = (MainActivity)getActivity();
                    mainActivity.setRequest(request);
                    switch (which) {
                        case 0:
                            mainActivity.pickPhotos(maxPhotos); // Галерея
                            break;
                        case 1:
                            mainActivity.takePhoto(); // Камера
                    }

                });

        return builder.create();
    }

}
