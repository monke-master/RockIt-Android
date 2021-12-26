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
import com.brigade.rockit.fragments.signUp.EmailFragment;
import com.brigade.rockit.fragments.signUp.LoginFragment;
import com.brigade.rockit.fragments.signUp.PasswordFragment;
import com.brigade.rockit.fragments.signIn.SignInFragment;
import com.brigade.rockit.fragments.main.StartFragment;
import com.brigade.rockit.fragments.signUp.VerifyEmailFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Iterator;

public class StartActivity extends AppCompatActivity {
    private Iterator<Fragment> signUpIter;
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
        regFragInit();
        getSupportFragmentManager().beginTransaction().replace(R.id.frgmnt_view_music,
                new StartFragment()).commit();
    }

    public void setFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frgmnt_view_music,
               fragment).commit();
    }

    // Инициализация фрагментов регистрации
    private void regFragInit() {
        ArrayList<Fragment> signUpFragments = new ArrayList<>();
        signUpFragments.add(new EmailFragment());
        signUpFragments.add(new LoginFragment());
        signUpFragments.add(new PasswordFragment());
        signUpFragments.add(new VerifyEmailFragment());
        signUpIter = signUpFragments.iterator();
    }


    // Следующий фрагмент регистрации
    public void nextRegFrag() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frgmnt_view_music,
                signUpIter.next()).commit();
    }

    // Вывод фрагмента входа
    public void signInFrag() {
        getSupportFragmentManager().beginTransaction().replace(R.id.frgmnt_view_music,
                new SignInFragment()).commit();
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