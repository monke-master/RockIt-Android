package com.brigade.rockit.adapter;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.brigade.rockit.GlideApp;
import com.brigade.rockit.R;
import com.brigade.rockit.activities.MainActivity;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Post;
import com.brigade.rockit.data.User;
import com.brigade.rockit.database.ContentManager;
import com.brigade.rockit.data.TimeManager;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.fragments.dialogs.PostDialog;
import com.brigade.rockit.fragments.profile.ProfileFragment;

import java.util.ArrayList;

// Адаптер для постов
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder>{

    private ArrayList<String> postIds;
    private MainActivity mainActivity;

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTxt;
        private TextView loginTxt;
        private TextView dateTxt;
        private TextView textTxt;
        private RecyclerView imagesList;
        private RecyclerView songsList;
        private ImageView profileImg;
        private ImageView otherBtn;

        // Получение виджетов
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.name_txt);
            loginTxt = itemView.findViewById(R.id.login_txt);
            dateTxt = itemView.findViewById(R.id.date_txt);
            textTxt = itemView.findViewById(R.id.text_txt);
            imagesList = itemView.findViewById(R.id.images_list);
            songsList = itemView.findViewById(R.id.songs_list);
            profileImg = itemView.findViewById(R.id.profile_img);
            otherBtn = itemView.findViewById(R.id.option_btn);
            otherBtn.setVisibility(View.GONE);
            textTxt.setVisibility(View.GONE);
            imagesList.setVisibility(View.GONE);
            songsList.setVisibility(View.GONE);
            nameTxt.setText("");
            loginTxt.setText("");
            dateTxt.setText("");
        }

        // При отображении поста
        public void bind(String postId) {
            ContentManager manager = new ContentManager();
            manager.getPost(postId, new GetObjectListener() {
                @Override
                public void onComplete(Object object) {
                    Post post = (Post)object;
                    User author = post.getAuthor();
                    // Отображение даты поста, имени и логина автора
                    TimeManager timeManager = new TimeManager();
                    dateTxt.setText(timeManager.formatDate(post.getDate(), mainActivity));
                    nameTxt.setText(author.getName() + " " + author.getSurname());
                    loginTxt.setText("@" + author.getLogin());

                    if (!textTxt.equals("")) {
                        textTxt.setText(post.getText());
                        textTxt.setVisibility(View.VISIBLE);
                    }
                    // Картинка профиля автора
                    GlideApp.with(mainActivity).load(author.getPictureUri()).circleCrop().into(profileImg);

                    ImageAdapter imagesAdapter = new ImageAdapter(mainActivity);
                    imagesList.setAdapter(imagesAdapter);
                    imagesList.setLayoutManager(new LinearLayoutManager(mainActivity,
                            LinearLayoutManager.HORIZONTAL, false));
                    for (String id: post.getImagesIds()) {
                        imagesList.setVisibility(View.VISIBLE);
                        imagesAdapter.addItem(id);
                    }

                    SongAdapter songAdapter = new SongAdapter(mainActivity);
                    songsList.setLayoutManager(new LinearLayoutManager(mainActivity));
                    songsList.setAdapter(songAdapter);
                    for (String id: post.getSongsIds()) {
                        songsList.setVisibility(View.VISIBLE);
                        songAdapter.addItem(id);
                    }

                    // Настройки поста
                    if (post.getAuthor().getLogin().equals(Data.getCurUser().getLogin())) {
                        otherBtn.setVisibility(View.VISIBLE);
                        otherBtn.setOnClickListener(v -> {
                            PostDialog dialog = new PostDialog(new ContextThemeWrapper(mainActivity,
                                    R.style.MainTheme), post);
                            dialog.show();
                        });
                    }
                    // Переход на страницу автора
                    profileImg.setOnClickListener(v -> mainActivity.setFragment(
                            new ProfileFragment(post.getAuthor())));
                }

                @Override
                public void onFailure(Exception e) {
                    ExceptionManager.showError(e, itemView.getContext());
                }
            });

        }

    }

    public PostAdapter(MainActivity mainActivity) {
        postIds = new ArrayList<>();
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(postIds.get(position));
    }

    @Override
    public int getItemCount() {
        return postIds.size();
    }

    public void addItem(String postId) {
        postIds.add(postId);
        notifyItemChanged(postIds.size() - 1);
    }

    public ArrayList<String> getPostIds() {
        return postIds;
    }
}
