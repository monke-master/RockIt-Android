package com.brigade.rockit.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brigade.rockit.GlideApp;
import com.brigade.rockit.R;
import com.brigade.rockit.data.Data;

import java.util.ArrayList;

// Адаптер для прикрепленных к посту фотографий
public class PostImagesAdapter extends RecyclerView.Adapter<PostImagesAdapter.PostImageViewHolder> {

    private ArrayList<Uri> images;
    private Context context;

    class PostImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private ImageView deleteBtn;

        // Получение виджетов
        public PostImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
        }

        // Отображение
        public void bind(PostImagesAdapter adapter, Uri uri) {
            GlideApp.with(context).load(uri).into(imageView);
            // Удаление фото
            deleteBtn.setOnClickListener(v -> {
                Data.getCurPost().getImagesList().remove(uri);
                adapter.deleteItem(uri);
            });
        }
    }

    public PostImagesAdapter(Context context) {
        this.context = context;
        images = new ArrayList<>();
    }


    @NonNull
    @Override
    public PostImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post_image, parent,
                false);
        return new PostImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostImageViewHolder holder, int position) {
        holder.bind(this, images.get(position));
    }


    @Override
    public int getItemCount() {
        return images.size();
    }

    public void addItem(Uri uri) {
        images.add(uri);
        notifyDataSetChanged();
    }

    public void deleteItem(Uri uri) {
        images.remove(uri);
        notifyDataSetChanged();
    }
}
