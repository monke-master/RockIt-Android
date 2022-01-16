package com.brigade.rockit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.brigade.rockit.GlideApp;
import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.data.Constants;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Song;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.TaskListener;

import java.util.ArrayList;

// Адаптер для песен
public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder>{


    private ArrayList<Song> songList;
    private ArrayList<String> musicIds;
    private MainActivity mainActivity;
    private int mode;
    private ArrayList<Song> selectedList;

    class MusicViewHolder extends RecyclerView.ViewHolder {

        private ImageView coverImg;
        private TextView nameTxt;
        private TextView artistTxt;
        private TextView durationTxt;
        private ConstraintLayout layout;
        private ConstraintLayout mainLayout;
        private ImageView optionButton;
        private Song thisSong;


        // Получение виджетов
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            coverImg = itemView.findViewById(R.id.cover_img_s);
            nameTxt = itemView.findViewById(R.id.song_name_txt);
            artistTxt = itemView.findViewById(R.id.song_artist_txt);
            durationTxt = itemView.findViewById(R.id.duration_txt);
            layout = itemView.findViewById(R.id.clickable_layout);
            mainLayout = itemView.findViewById(R.id.main_layout);
            optionButton = itemView.findViewById(R.id.option_btn);
        }

        // Отображения элемента с песней
        public void bind(MusicAdapter adapter, String musicId) {

            ContentManager contentManager = new ContentManager();
            // Получение информации о песне
            contentManager.getSong(musicId, new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    Song song = (Song)object;
                    nameTxt.setText(song.getName());
                    durationTxt.setText(song.getDuration());
                    artistTxt.setText(song.getAuthor().getLogin());
                    GlideApp.with(itemView.getContext()).load(song.getCover()).into(coverImg);

                    songList.add(song);

                    Data.getMusicPlayer().getQueue().add(song);


                    mainLayout.setOnClickListener(v -> {
                        if (mode == Constants.SELECTING_MODE)
                            makeSelected(musicId);
                    });
                    layout.setOnClickListener(v -> {
                        if (!(mode == Constants.SELECTING_MODE)) {
                            playMusic(song);
                        } else {
                            makeSelected(musicId);
                        }
                    });

                    thisSong = song;

                    switch (mode) {
                        case Constants.PLAYLIST_MODE:
                            optionButton.setImageDrawable(mainActivity.getDrawable(R.drawable.other_2));
                            optionButton.setOnClickListener(v -> mainActivity.showSongSettings(song));
                            break;
                        case Constants.POST_MODE:
                            optionButton.setImageDrawable(mainActivity.getDrawable(R.drawable.delete));
                            optionButton.setOnClickListener(v -> {
                                adapter.deleteItem(musicId);

                            });
                            break;
                        case Constants.SELECTING_MODE:
                            optionButton.setImageDrawable(mainActivity.getDrawable(R.drawable.pick));
                            optionButton.setVisibility(View.INVISIBLE);
                    }

                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, mainActivity);

                }
            });

        }
        // Выбор песни
        public void makeSelected(String musicId) {
            if (selectedList.contains(musicId)) {
                selectedList.remove(musicId);
                optionButton.setVisibility(View.INVISIBLE);
            } else {
                if (selectedList.size() + 1 > Constants.MAX_POST_SONGS)
                    Toast.makeText(mainActivity,
                            mainActivity.getString(R.string.select_music_error) + " " +
                                    Constants.MAX_POST_SONGS, Toast.LENGTH_LONG).show();
                else {
                    selectedList.add(thisSong);
                    optionButton.setVisibility(View.VISIBLE);
                }
            }
        }
        // Проигрывание песни
        public void playMusic(Song song) {
            Data.getMusicPlayer().stopSong();
            Data.getMusicPlayer().playSong(song);
            Data.getMusicPlayer().setCurPosition(songList.indexOf(song));
            mainActivity.showBottomPlayer();
            song.increaseAuditions();
        }

    }



    public MusicAdapter(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        songList = new ArrayList<>();
        selectedList = new ArrayList<>();
        musicIds = new ArrayList<>();
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_song_item, parent, false);
        return new MusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        holder.bind(this, musicIds.get(position));
    }

    @Override
    public int getItemCount() {
        return musicIds.size();
    }

    // Добавление песни
    public void addItem(String id) {
        musicIds.add(id);
        notifyDataSetChanged();
    }

    // Очистка списка
    public void clear() {
        musicIds.clear();
        songList.clear();
        notifyDataSetChanged();
    }

    public void deleteItem(String id) {
        musicIds.remove(id);
        notifyDataSetChanged();
    }

    public ArrayList<Song> getSelectedList() {
        return selectedList;
    }

    public ArrayList<Song> getMusicList() {
        return songList;
    }

    public ArrayList<String> getMusicIds() {
        return musicIds;
    }


}
