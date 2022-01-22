package com.brigade.rockit.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.database.GetObjectListener;

public class NewSongDialog extends DialogFragment {

    private GetObjectListener listener;

    // Диалог загрузки фото
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog);
        builder.setTitle(R.string.photo);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_song, null);

        EditText nameEdit = view.findViewById(R.id.name_edit);
        builder.setView(view);
        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {

                }
            }
        });

        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            listener.onComplete(nameEdit.getText().toString());
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> {

        });

        return builder.create();
    }

    public void setListener(GetObjectListener listener) {
        this.listener = listener;
    }
}
