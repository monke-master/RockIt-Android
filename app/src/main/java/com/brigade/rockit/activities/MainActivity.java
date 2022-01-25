package com.brigade.rockit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.brigade.rockit.data.Constants;
import com.brigade.rockit.R;
import com.brigade.rockit.data.Data;
import com.brigade.rockit.data.Genre;
import com.brigade.rockit.data.Song;
import com.brigade.rockit.data.TooManyPhotoException;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.GetObjectListener;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.database.UserManager;
import com.brigade.rockit.fragments.dialogs.SongDialog;
import com.brigade.rockit.fragments.main.HomeFragment;
import com.brigade.rockit.fragments.music.BottomPlayerFragment;
import com.brigade.rockit.fragments.music.GenresFragment;

import java.io.File;
import java.util.ArrayList;

// Главная активность
public class MainActivity extends AppCompatActivity {

    private Uri takenPhotoUri;
    private Fragment currentFragment;
    private View playerFragment;
    private GetObjectListener listener;
    private int maxPhotos = 0;
    private MainActivity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisActivity = this;

        setFragment(new HomeFragment());
        // Если пользователь не выбирал жанры, то
        if (Data.getCurUser().getFavouriteGenres() == null) {
            // Переходим на фрагмент выбора жанров
            selectFavouriteGenres();
        } else if (Data.getCurUser().getFavouriteGenres().size() == 0)
            selectFavouriteGenres();

        // Начальные установки для музыкального плеера
        playerFragment = findViewById(R.id.player_fragment);
        playerFragment.setVisibility(View.INVISIBLE);
        showBottomPlayer();
    }

    private void selectFavouriteGenres() {
        GenresFragment fragment = new GenresFragment(100);
        fragment.setListener(new GetObjectListener() {
            @Override
            public void onComplete(Object object) {
                ArrayList<String> genresIds = new ArrayList<>();
                for (Genre genre: (ArrayList<Genre>)object)
                    genresIds.add(genre.getId());
                Data.getCurUser().setFavouriteGenres(genresIds);
                new UserManager().setFavouriteGenres(genresIds, new TaskListener() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onFailure(Exception e) {
                        ExceptionManager.showError(e, thisActivity);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
        setFragment(fragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        showBottomPlayer();
    }

    // Отображение заданного фрагмента
    public void setFragment(Fragment fragment) {
        currentFragment = fragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frgmnt_view,
                fragment).addToBackStack(null).commit();
    }

    // Предыдущий фрагмент
    public void previousFragment() {
        getSupportFragmentManager().popBackStack();
    }


    // Выбор фото из галереи
    public void pickPhotos(int maxPhotos, GetObjectListener listener) {
        this.listener = listener;
        this.maxPhotos = maxPhotos;
        // Проверка разрешения на чтение хранилища телефона
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        // Если разрешение есть, запуск выбора фото
        if (permission == PackageManager.PERMISSION_GRANTED) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
            photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, Constants.PICK_PHOTOS);
        } else { // Иначе просьба дать нужные разрешения
            ActivityCompat.requestPermissions(this,
                    Constants.PERMISSIONS_STORAGE,
                    Constants.PICK_PHOTOS
            );
        }
    }

    // Получение аудио-файла
    public void pickAudio(GetObjectListener listener) {
        this.listener = listener;
        // Проверка разрешения на чтение хранилища телефона
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            Intent audioPickIntent = new Intent((Intent.ACTION_GET_CONTENT));
            audioPickIntent.setType("audio/*");
            startActivityForResult(audioPickIntent, Constants.PICK_AUDIO);
        } else {
            ActivityCompat.requestPermissions(this,
                    Constants.PERMISSIONS_STORAGE,
                    Constants.PICK_AUDIO
            );
        }
    }

    // Сделать новое фото
    public void takePhoto(GetObjectListener listener) {
        this.listener = listener;
        // Создание пути для сохранения фото
        File dir = new File(getCacheDir(), "/");
        File newFile = new File(dir, "new" + Math.random()*69);
        takenPhotoUri = FileProvider.getUriForFile(
                this, "com.brigade.rockit.provider", newFile);
        // Проверка разрешения на запись в хранилище телефона и доступ к камере
        int permission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) + ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // Если разрешение есть, запуск камеры
        if (permission == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, takenPhotoUri);
            startActivityForResult(takePictureIntent, Constants.IMAGE_CAPTURE);
        } else { // Иначе просьба дать нужные разрешения
            ActivityCompat.requestPermissions(this,
                    Constants.PERMISSIONS_CAMERA,
                    Constants.IMAGE_CAPTURE
            );
        }

    }

    // Получение разрешений
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Если разрешения получены
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
            switch (requestCode) {
                case Constants.PICK_PHOTOS: // В случае с галереей, запуск выбора фото
                    pickPhotos(maxPhotos, listener);
                    return;
                case Constants.IMAGE_CAPTURE: // В случае с новым фото, запуск камеры
                    takePhoto(listener);
                    return;
                case Constants.PICK_AUDIO: // В случае с аудио, запуск выбора аудио-файла
                    pickAudio(listener);
            }

        }
    }

    // Получение результата
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Если данные были получены
        if (resultCode == RESULT_OK) {
            ArrayList<Uri> uris = new ArrayList<>();
            switch (requestCode) {
                // Выбор фото из галереи
                case Constants.PICK_PHOTOS:
                    boolean successful = true;
                    if (data.getClipData() != null) {
                        if (data.getClipData().getItemCount() <= maxPhotos) {
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                Uri u = data.getClipData().getItemAt(i).getUri();
                                uris.add(u);
                            }
                        } else {
                            listener.onFailure(new TooManyPhotoException(maxPhotos));
                            successful = false;
                        }
                    } else
                        uris.add(data.getData());
                    if (successful)
                        listener.onComplete(uris);
                    break;
                // Съемка фото
                case Constants.IMAGE_CAPTURE:
                    uris.add(takenPhotoUri);
                    listener.onComplete(uris);
                    break;
                // Выбор аудио файла
                case Constants.PICK_AUDIO:
                    listener.onComplete(data.getData());
                    break;
            }
        } else {
            Toast.makeText(this, getString(R.string.pick_file_error), Toast.LENGTH_LONG).show();
        }
    }


    // Отображение настроек песни
    public void showSongSettings(Song song) {
        SongDialog dialog = new SongDialog(this, song);
        dialog.show();
    }

    // Получение текущего фрагмента
    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    // Отображение плеера
    public void showBottomPlayer() {
        if (Data.getMusicPlayer().isPlaying()) {
            playerFragment.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().replace(R.id.player_fragment, new BottomPlayerFragment()).commit();
        }
    }





}