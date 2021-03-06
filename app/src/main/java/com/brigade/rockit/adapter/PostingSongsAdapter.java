package com.brigade.rockit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.brigade.rockit.R;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Song;


import java.util.ArrayList;

// Адаптер для списка публикуемых песен
public class PostingSongsAdapter extends RecyclerView.Adapter<PostingSongsAdapter.SongViewHolder> {

    private ArrayList<Song> songList;
    private PostingSongsAdapter adapter;
    private AdapterListener deleteListener;

    class SongViewHolder extends RecyclerView.ViewHolder {

        private ImageView playBtn;
        private ImageView deleteBtn;
        private TextView nameTxt;
        private TextView authorTxt;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            // Получение виджетов
            playBtn = itemView.findViewById(R.id.play_btn);
            deleteBtn = itemView.findViewById(R.id.delete_btn);
            nameTxt = itemView.findViewById(R.id.name_txt);
            authorTxt = itemView.findViewById(R.id.author_txt);

            nameTxt.setText("");
            authorTxt.setText("");
        }

        // Отображение данных и отслеживание нажатий
        public void bind(Song song) {
            nameTxt.setText(song.getName());
            authorTxt.setText(song.getAuthor().getLogin());
            playBtn.setOnClickListener(v -> {
                if (Data.getMusicPlayer().isPlaying()) {
                    playBtn.setImageDrawable(itemView.getContext().getDrawable(R.drawable.play));
                    Data.getMusicPlayer().stopSong();
                }
                else {
                    playBtn.setImageDrawable(itemView.getContext().getDrawable(R.drawable.pause));
                    if (Data.getMusicPlayer().getMusic() != null)
                        Data.getMusicPlayer().continueSong();
                    else
                        Data.getMusicPlayer().playSong(song);
                }
            });
            deleteBtn.setOnClickListener(v -> {
                adapter.deleteItem(song);
                Data.getMusicPlayer().stopSong();
                Data.getMusicPlayer().setMusic(null);
            });
        }
    }

    public PostingSongsAdapter() {
        songList = new ArrayList<>();
        adapter = this;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_posting_song,
                parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        holder.bind(songList.get(position));
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void addItem(Song item) {
        songList.add(item);
        notifyDataSetChanged();
    }

    public void deleteItem(Song item) {
        songList.remove(item);
        deleteListener.onDelete(item);
        notifyDataSetChanged();
    }

    public void setOnItemDeleteListener(AdapterListener listener) {
        this.deleteListener = listener;
    }


}
