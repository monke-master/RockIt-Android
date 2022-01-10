package com.brigade.rockit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;

import com.brigade.rockit.R;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.MusicPlayer;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.database.UserManager;
import com.brigade.rockit.fragments.main.StartFragment;
import com.brigade.rockit.fragments.signUp.VerifyEmailFragment;
import com.google.firebase.auth.FirebaseAuth;

// начальная активность
public class StartActivity extends AppCompatActivity {

    private StartActivity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisActivity = this;
        setContentView(R.layout.activity_start);
        Data.setMusicPlayer(new MusicPlayer(this));
        if (FirebaseAuth.getInstance().getCurrentUser() != null) { // Если пользователь не вышел, то
            signIn(); // Выполняется вход
        } else { // Иначе отображается стартовый фрагмент
            goToStart();
        }
    }

    // Отображение стартового фрагмента
    public void goToStart() {
        setFragment(new StartFragment());
    }

    // Отображение заданного фрагмента
    public void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frgmnt_view,
               fragment).addToBackStack(null).commit();
    }

    // Предыдущий фрагмент
    public void previousFragment() {
        getSupportFragmentManager().popBackStack();
    }


    // Вход пользователя
    public void signIn() {
        UserManager manager = new UserManager();
        manager.getUserData(new TaskListener() {
            @Override
            public void onComplete() {
                if (manager.emailVerified()) {
                    // Запуск главной активности
                    Intent intent = new Intent(thisActivity, MainActivity.class);
                    startActivity(intent);
                } else
                    setFragment(new VerifyEmailFragment());
            }

            @Override
            public void onFailure(Exception e) {
                // Вывод ошибки
                ExceptionManager.showError(e, thisActivity);
                goToStart();
            }
        });

    }

}