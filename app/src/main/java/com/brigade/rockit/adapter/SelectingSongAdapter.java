package com.brigade.rockit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.brigade.rockit.R;
import com.brigade.rockit.data.Song;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SelectingSongAdapter extends RecyclerView.Adapter<SelectingSongAdapter.SelectingSongViewHolder> {

    private ArrayList<String> ids;
    private ArrayList<Song> selectedSongs;

    class SelectingSongViewHolder extends RecyclerView.ViewHolder  {

        private TextView nameTxt;
        private TextView authorTxt;
        private ImageView coverImg;
        private ImageView pickImg;
        private ConstraintLayout layout;

        public SelectingSongViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.name_txt);
            authorTxt = itemView.findViewById(R.id.author_txt);
            coverImg = itemView.findViewById(R.id.cover_img);
            pickImg = itemView.findViewById(R.id.pick_img);
            layout = itemView.findViewById(R.id.layout);
            pickImg.setVisibility(View.INVISIBLE);

            authorTxt.setText("");
            nameTxt.setText("");
        }

        public void bind(String id) {
            ContentManager contentManager = new ContentManager();
            contentManager.getSong(id, new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    Song song = (Song) object;
                    nameTxt.setText(song.getName());
                    authorTxt.setText(song.getAuthor().getLogin());
                    Glide.with(coverImg).load(song.getCoverUri()).into(coverImg);
                    layout.setOnClickListener(v -> select(song));
                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, itemView.getContext());
                }
            });
        }

        public void select(Song song) {
            if (selectedSongs.contains(song)) {
                selectedSongs.remove(song);
                pickImg.setVisibility(View.INVISIBLE);
                layout.setBackgroundColor(itemView.getContext().getColor(R.color.white_1));
            } else {
                selectedSongs.add(song);
                pickImg.setVisibility(View.VISIBLE);
                layout.setBackgroundColor(itemView.getContext().getColor(R.color.grey_2));
            }
        }

    }

    public SelectingSongAdapter() {
        ids = new ArrayList<>();
        selectedSongs = new ArrayList<>();
    }

    @NonNull
    @Override
    public SelectingSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_selecting_song, parent,
                false);
        return new SelectingSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectingSongViewHolder holder, int position) {
        holder.bind(ids.get(position));
    }

    @Override
    public int getItemCount() {
        return ids.size();
    }

    public void addItem(String item) {
        ids.add(item);
        notifyDataSetChanged();
    }

    public ArrayList<Song> getSelectedSongs() {
        return selectedSongs;
    }
}
