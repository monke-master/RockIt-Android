package com.brigade.rockit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.brigade.rockit.R;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.database.UserManager;
import com.brigade.rockit.fragments.signUp.CodeFragment;
import com.brigade.rockit.fragments.signUp.EmailFragment;
import com.brigade.rockit.fragments.signUp.LoginFragment;
import com.brigade.rockit.fragments.signUp.PasswordFragment;
import com.brigade.rockit.fragments.signIn.SignInFragment;
import com.brigade.rockit.fragments.main.StartFragment;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_start);
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

    // Инициализация фрагментов регистрации
    private void regFragInit() {
        ArrayList<Fragment> signUpFragments = new ArrayList<>();
        signUpFragments.add(new EmailFragment());
        signUpFragments.add(new CodeFragment());
        signUpFragments.add(new LoginFragment());
        signUpFragments.add(new PasswordFragment());
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
                // Запуск главной активности
                Intent intent = new Intent(thisActivity, MainActivity.class);
                startActivity(intent);
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