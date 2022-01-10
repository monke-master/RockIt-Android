package com.brigade.rockit.adapter;

import android.net.Uri;
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
import com.brigade.rockit.data.Playlist;
import com.brigade.rockit.data.User;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.DatabasePlaylist;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.UserManager;
import com.brigade.rockit.fragments.music.PlaylistFragment;

import java.util.ArrayList;

// Адаптер для плейлиста
public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private ArrayList<String> playlistIds;
    private MainActivity mainActivity;

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
            UserManager userManager = new UserManager();
            // Получение и отображение данных о плейлисте
            contentManager.getPlaylist(id, new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    DatabasePlaylist dbPlaylist = (DatabasePlaylist) object;
                    Playlist playlist = new Playlist(dbPlaylist);
                    playlist.setId(id);
                    nameTxt.setText(playlist.getName());
                    dateTxt.setText(playlist.getDate());
                    userManager.getUser(dbPlaylist.getAuthor(), new GetObjectListener() {
                        @Override
                        public void onComplete(Object object) {
                            playlist.setAuthor((User) object);
                            authorTxt.setText(playlist.getAuthor().getLogin());
                            contentManager.getUri(dbPlaylist.getCover(), new GetObjectListener() {
                                @Override
                                public void onComplete(Object object) {
                                    playlist.setCoverUri((Uri) object);
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

                        @Override
                        public void onFailure(Exception e) {
                            ExceptionManager.showError(e, itemView.getContext());
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, itemView.getContext());
                }
            });
        }
    }

    public PlaylistAdapter(MainActivity mainActivity) {
        playlistIds = new ArrayList<>();
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_playlist, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        holder.bind(playlistIds.get(position));
    }

    @Override
    public int getItemCount() {
        return playlistIds.size();
    }

    public void addItem(String playlistId) {
        playlistIds.add(playlistId);
        notifyDataSetChanged();
    }

    public ArrayList<String> getPlaylistIds() {
        return playlistIds;
    }

    public void clear() {
        playlistIds.clear();
        notifyDataSetChanged();
    }
}
