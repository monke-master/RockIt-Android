package com.brigade.rockit.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.database.GetObjectListener;

// Диалог выбора пути загрузки фото
public class PhotoDialog extends DialogFragment {

    public GetObjectListener listener;
    public int maxPhotos;

    public PhotoDialog(int maxPhotos, GetObjectListener listener) {
        this.listener = listener;
        this.maxPhotos = maxPhotos;
    }

    // Диалог загрузки фото
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.photo)
                .setItems(R.array.photo_dialog, (dialog, which) -> {
                    MainActivity mainActivity = (MainActivity)getActivity();
                    switch (which) {
                        case 0:
                            mainActivity.pickPhotos(maxPhotos, listener); // Галерея
                            break;
                        case 1:
                            mainActivity.takePhoto(listener); // Камера
                    }

                });

        return builder.create();
    }


}
