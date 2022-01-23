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
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Song;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.fragments.dialogs.SongDialog;

import java.util.ArrayList;

// Адаптер для песен в альбоме
public class AlbumSongsAdapter extends RecyclerView.Adapter<AlbumSongsAdapter.AlbumSongViewHolder> {

    private ArrayList<String> songsIds;
    private MainActivity mainActivity;
    private AdapterListener adapterListener;



    class AlbumSongViewHolder extends RecyclerView.ViewHolder{

        private TextView nameTxt;
        private TextView durationTxt;
        private ConstraintLayout layout;
        private ImageView optionButton;
        private TextView positionTxt;

        public AlbumSongViewHolder(@NonNull View itemView) {
            super(itemView);
            // Получение виджетов
            nameTxt = itemView.findViewById(R.id.name_txt);
            durationTxt = itemView.findViewById(R.id.duration_txt);
            layout = itemView.findViewById(R.id.layout);
            optionButton = itemView.findViewById(R.id.option_btn);
            positionTxt = itemView.findViewById(R.id.pos_txt);
        }

        public void bind(String id) {
            ContentManager contentManager = new ContentManager();
            // Получение информации о песне
            contentManager.getSong(id, new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    Song song = (Song)object;
                    nameTxt.setText(song.getName());
                    durationTxt.setText(song.getDuration());
                    Data.getMusicPlayer().getQueue().add(song);
                    positionTxt.setText(String.valueOf(songsIds.indexOf(id) + 1));

                    layout.setOnClickListener(v -> {
                        playMusic(song);
                    });

                    optionButton.setOnClickListener(v -> {
                        SongDialog dialog = new SongDialog(itemView.getContext(), song);
                        dialog.show();
                    });

                    adapterListener.onAdded(song);
                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, itemView.getContext());

                }
            });
        }


        // Проигрывание песни
        public void playMusic(Song song) {
            Data.getMusicPlayer().stopSong();
            Data.getMusicPlayer().playSong(song);
            Data.getMusicPlayer().setCurPosition(songsIds.indexOf(song.getId()));
            mainActivity.showBottomPlayer();
            song.increaseAuditions();
        }

    }

    public AlbumSongsAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        songsIds = new ArrayList<>();
    }

    @NonNull
    @Override
    public AlbumSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_album_song, parent, false);
        return new AlbumSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumSongViewHolder holder, int position) {
        holder.bind(songsIds.get(position));
    }

    @Override
    public int getItemCount() {
        return songsIds.size();
    }

    public void addItem(String item) {
        songsIds.add(item);
        notifyDataSetChanged();
    }

    public void setAdapterListener(AdapterListener adapterListener) {
        this.adapterListener = adapterListener;
    }
}
