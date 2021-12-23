package com.brigade.rockit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brigade.rockit.GlideApp;
import com.brigade.rockit.R;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;

import java.util.ArrayList;

// Адаптер для фотографий
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private ArrayList<String> imageIds;
    private Context context;

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }

        public void bind(String id) {
            ContentManager manager = new ContentManager();
            // Получение uri фото и отображение в виджете
            manager.getUri(id, new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    GlideApp.with(context).load((Object) object).fitCenter().into(imageView);
                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, context);
                }
            });
        }
    }

    public ImageAdapter(Context context) {
        imageIds = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.view_image, parent,
                false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bind(imageIds.get(position));
    }

    @Override
    public int getItemCount() {
        return imageIds.size();
    }

    public void addItem(String id) {
        imageIds.add(id);
        notifyDataSetChanged();
    }


}
