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
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Song;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.fragments.dialogs.SongDialog;

import java.util.ArrayList;

// Адаптер для песен
public class SongAdapter extends RecyclerView.Adapter<SongAdapter.MusicViewHolder>{


    private ArrayList<String> ids;
    private MainActivity mainActivity;

    class MusicViewHolder extends RecyclerView.ViewHolder {

        private ImageView coverImg;
        private TextView nameTxt;
        private TextView artistTxt;
        private TextView durationTxt;
        private ImageView optionButton;
        private ConstraintLayout layout;

        // Получение виджетов
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImg = itemView.findViewById(R.id.cover_img_s);
            nameTxt = itemView.findViewById(R.id.song_name_txt);
            artistTxt = itemView.findViewById(R.id.song_artist_txt);
            durationTxt = itemView.findViewById(R.id.duration_txt);
            optionButton = itemView.findViewById(R.id.option_btn);
            layout = itemView.findViewById(R.id.layout);

            nameTxt.setText("");
            artistTxt.setText("");
            durationTxt.setText("");
        }

        // Отображения элемента с песней
        public void bind(String musicId) {

            ContentManager contentManager = new ContentManager();
            // Получение информации о песне
            contentManager.getSong(musicId, new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    Song song = (Song)object;
                    nameTxt.setText(song.getName());
                    durationTxt.setText(song.getDuration());
                    artistTxt.setText(song.getAuthor().getLogin());
                    GlideApp.with(itemView.getContext()).load(song.getCoverUri()).into(coverImg);
                    Data.getMusicPlayer().getQueue().add(song);

                    layout.setOnClickListener(v -> {
                        playMusic(song);
                    });

                    optionButton.setOnClickListener(v -> {
                        SongDialog dialog = new SongDialog(itemView.getContext(), song);
                        dialog.show();
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, mainActivity);

                }
            });
        }



        // Проигрывание песни
        public void playMusic(Song song) {
            Data.getMusicPlayer().stopSong();
            Data.getMusicPlayer().playSong(song);
            Data.getMusicPlayer().setCurPosition(ids.indexOf(song.getId()));
            mainActivity.showBottomPlayer();
            song.increaseAuditions();
        }


    }

    public SongAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        ids = new ArrayList<>();
    }


    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_song_item, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        holder.bind(ids.get(position));
    }

    @Override
    public int getItemCount() {
        return ids.size();
    }

    // Добавление песни
    public void addItem(String id) {
        ids.add(id);
        notifyDataSetChanged();
    }

    // Очистка списка
    public void clear() {
        ids.clear();
        notifyDataSetChanged();
    }

    public void deleteItem(String id) {
        ids.remove(id);
        notifyDataSetChanged();
    }

    public ArrayList<String> getIds() {
        return ids;
    }

}
