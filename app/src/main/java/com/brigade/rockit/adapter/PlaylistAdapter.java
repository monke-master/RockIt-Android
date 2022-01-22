package com.brigade.rockit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.brigade.rockit.GlideApp;
import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.data.Album;
import com.brigade.rockit.data.Playlist;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.fragments.music.AlbumFragment;
import com.brigade.rockit.fragments.music.PlaylistFragment;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

// Адаптер для плейлиста
public class PlaylistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<String> ids;
    private MainActivity mainActivity;

    class AlbumViewHolder extends RecyclerView.ViewHolder {

        private ImageView coverImg;
        private TextView nameTxt;
        private TextView authorTxt;
        private TextView dateTxt;
        private ConstraintLayout mainLayout;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            // Получение виджетов
            coverImg = itemView.findViewById(R.id.cover_img);
            nameTxt = itemView.findViewById(R.id.name_txt);
            authorTxt = itemView.findViewById(R.id.author_txt);
            dateTxt = itemView.findViewById(R.id.date_txt);
            mainLayout = itemView.findViewById(R.id.main_layout);

        }

        public void bind(String id) {
            id = id.substring(id.indexOf("/") + 1);
            ContentManager contentManager = new ContentManager();
            contentManager.getAlbum(id, new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    Album album = (Album) object;
                    nameTxt.setText(album.getName());
                    authorTxt.setText(album.getAuthor().getLogin());
                    dateTxt.setText(album.getDate());
                    Glide.with(itemView.getContext()).load(album.getCoverUri()).into(coverImg);
                    mainLayout.setOnClickListener(v -> mainActivity.setFragment(new AlbumFragment(album)));
                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, itemView.getContext());
                }
            });
        }

    }

    class PlaylistViewHolder extends RecyclerView.ViewHolder {

        private ImageView coverImg;
        private TextView nameTxt;
        private TextView authorTxt;
        private TextView dateTxt;
        private ConstraintLayout mainLayout;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            // Получение виджетов
            coverImg = itemView.findViewById(R.id.cover_img);
            nameTxt = itemView.findViewById(R.id.name_txt);
            authorTxt = itemView.findViewById(R.id.author_txt);
            dateTxt = itemView.findViewById(R.id.date_txt);
            mainLayout = itemView.findViewById(R.id.main_layout);
        }

        public void bind(String id) {
            ContentManager contentManager = new ContentManager();
            // Получение и отображение данных о плейлисте
            id = id.substring(id.indexOf("/") + 1);
            contentManager.getPlaylist(id, new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    Playlist playlist = (Playlist)object;
                    nameTxt.setText(playlist.getName());
                    dateTxt.setText(playlist.getDate());
                    authorTxt.setText(playlist.getAuthor().getLogin());
                    GlideApp.with(itemView.getContext()).
                            load(playlist.getCoverUri()).into(coverImg);
                    mainLayout.setOnClickListener(v -> mainActivity.setFragment(
                            new PlaylistFragment(playlist)));
                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, itemView.getContext());
                }
            });
        }
    }

    public PlaylistAdapter(MainActivity mainActivity) {
        ids = new ArrayList<>();
        this.mainActivity = mainActivity;
    }

    @Override
    public int getItemViewType(int position) {
        if (ids.get(position).contains("album"))
            return 0;
        return 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_playlist, parent, false);
        switch (viewType) {
            case 0: return new AlbumViewHolder(view);
            case 1: return new PlaylistViewHolder(view);
        }
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                AlbumViewHolder albumViewHolder = (AlbumViewHolder) holder;
                albumViewHolder.bind(ids.get(position));
                break;
            case 1:
                PlaylistViewHolder playlistViewHolder = (PlaylistViewHolder) holder;
                playlistViewHolder.bind(ids.get(position));
                break;
        }

    }

    @Override
    public int getItemCount() {
        return ids.size();
    }

    public void addItem(String playlistId) {
        ids.add(playlistId);
        notifyDataSetChanged();
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void clear() {
        ids.clear();
        notifyDataSetChanged();
    }
}
