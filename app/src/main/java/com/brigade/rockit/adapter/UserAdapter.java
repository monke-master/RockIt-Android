package com.brigade.rockit.adapter;

import android.util.Log;
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
import com.brigade.rockit.data.User;
import com.brigade.rockit.fragments.profile.ProfileFragment;

import java.util.ArrayList;

// Адаптер для пользователей
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<User> usersList;
    private MainActivity mainActivity;

    class UserViewHolder extends RecyclerView.ViewHolder {

        private ImageView profileImg;
        private TextView nameTxt;
        private TextView loginTxt;
        private ConstraintLayout layout;

        // Получение виджетов
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profile_img);
            nameTxt = itemView.findViewById(R.id.name_txt);
            loginTxt = itemView.findViewById(R.id.login_txt);
            layout = itemView.findViewById(R.id.layout);
        }

        // Отображение
        public void bind(User user) {
            GlideApp.with(mainActivity).load(user.getPictureUri()).circleCrop().into(profileImg);
            nameTxt.setText(user.getName() + " " + user.getSurname());
            loginTxt.setText("@" + user.getLogin());
            // переход на страницу пользователя
            layout.setOnClickListener(v -> {
                mainActivity.setFragment(new ProfileFragment(user));
            });
        }
    }

    public UserAdapter(MainActivity mainActivity) {
        usersList = new ArrayList<>();
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(usersList.get(position));
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public ArrayList<User> getUsersList() {
        return usersList;
    }

    public void addItem(User user) {
        usersList.add(user);
        notifyDataSetChanged();
    }

    public void clear() {
        usersList.clear();
        Log.d("SanFr", usersList.size() + "");
        notifyDataSetChanged();
    }

    public void removeItem(User user) {
        usersList.remove(user);
        notifyDataSetChanged();
    }


}
