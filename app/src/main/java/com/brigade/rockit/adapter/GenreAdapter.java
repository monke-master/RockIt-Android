package com.brigade.rockit.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brigade.rockit.R;
import com.brigade.rockit.data.Constants;
import com.brigade.rockit.data.Genre;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.TaskListener;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {
    private ArrayList<String> ids;
    private Context context;
    private ArrayList<Genre> selectedGenres;
    private int mode;
    private GenreViewHolder selectedHolder;
    private GenreAdapter adapter;
    private TaskListener listener;


    class GenreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView pictureImg;
        private ImageView pickImg;
        private TextView nameTxt;
        private boolean selected;
        private Genre thisGenre;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            pictureImg = itemView.findViewById(R.id.genre_img);
            pickImg = itemView.findViewById(R.id.pick_image);
            nameTxt = itemView.findViewById(R.id.name_txt);
            pickImg.setVisibility(View.INVISIBLE);
            selected = false;

            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            new ContentManager().getGenre(ids.get(position), new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    Genre genre = (Genre) object;
                    nameTxt.setText(genre.getName());
                    Glide.with(context).load(genre.getPicture()).circleCrop().into(pictureImg);
                    thisGenre = genre;
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        }

        @Override
        public void onClick(View v) {
            if (!selected) {
                if ((mode == Constants.SONG_GENRE) && (selectedHolder != null))
                    selectedHolder.unselect();
                select();
            } else
                unselect();
            if (thisGenre.getSubgenres().size() > 0) {
                if (!ids.contains(thisGenre.getSubgenres().get(0))) {
                    for (String id : thisGenre.getSubgenres())
                        adapter.insertItemAfter(id, ids.indexOf(thisGenre.getId()));
                }
            }
            listener.onComplete();
        }

        public void select() {
            selected = true;
            pickImg.setVisibility(View.VISIBLE);
            selectedGenres.add(thisGenre);
            selectedHolder = this;
        }

        public void unselect() {
            selected = false;
            pickImg.setVisibility(View.INVISIBLE);
            selectedGenres.remove(thisGenre);
        }
    }

    public GenreAdapter(Context context, int mode) {
        ids = new ArrayList<>();
        selectedGenres = new ArrayList<>();
        adapter = this;
        this.context = context;
        this.mode = mode;
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.view_genre, parent,
                false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return ids.size();
    }

    public void addItem(String id) {
        ids.add(id);
        notifyDataSetChanged();
    }

    public void insertItemAfter(String id, int pos) {
        ids.add(pos + 1, id);
        notifyDataSetChanged();
    }

    public ArrayList<Genre> getSelectedGenres() {
        return selectedGenres;
    }

    public void setOnClickListener(TaskListener listener) {
        this.listener = listener;
    }
}
