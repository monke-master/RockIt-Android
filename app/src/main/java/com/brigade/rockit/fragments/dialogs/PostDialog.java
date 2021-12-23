package com.brigade.rockit.fragments.dialogs;

import android.content.Context;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.brigade.rockit.R;
import com.brigade.rockit.data.Post;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class PostDialog extends BottomSheetDialog {

    // Настройки поста
    public PostDialog(@NonNull Context context, Post post) {
        super(context);
        setContentView(R.layout.dialog_post);
        // Удаление поста
        Button deletePost = findViewById(R.id.delete_btn);
        deletePost.setOnClickListener(v -> {
            ContentManager manager = new ContentManager();
            manager.deletePost(post, new TaskListener() {
                @Override
                public void onComplete() {
                    Toast.makeText(getContext(), getContext().getString(R.string.deleted_post),
                            Toast.LENGTH_LONG).show();
                }
                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, getContext());
                }
            });
            hide();
        });
    }

}
