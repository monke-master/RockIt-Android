package com.brigade.rockit.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;

import android.Manifest;
import android.content.ClipData;
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
import com.brigade.rockit.data.Music;
import com.brigade.rockit.database.ExceptionManager;
import com.brigade.rockit.database.TaskListener;
import com.brigade.rockit.database.UserManager;
import com.brigade.rockit.fragments.dialogs.SongDialog;
import com.brigade.rockit.fragments.main.HomeFragment;
import com.brigade.rockit.fragments.main.NewContentFragment;
import com.brigade.rockit.fragments.music.BottomPlayerFragment;
import com.brigade.rockit.fragments.music.NewMusicFragment;
import com.brigade.rockit.fragments.profile.ProfileFragment;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private int request;
    private Uri takenPhotoUri;
    private int maxPhotos;
    private MainActivity thisActivity;
    private Fragment currentFragment;
    private View playerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        thisActivity = this;

        // Отображение главной страницы
        setFragment(new HomeFragment());

        playerFragment = findViewById(R.id.player_fragment);
        playerFragment.setVisibility(View.INVISIBLE);
        showBottomPlayer();
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
                fragment).commit();
    }

    // Установка цели
    public void setRequest(int request) {
        this.request = request;
    }

    // Выбор фото из галереи
    public void pickPhotos(int maxPhotos) {
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
    public void pickAudio() {
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
    public void takePhoto() {
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
                    pickPhotos(maxPhotos);
                    return;
                case Constants.IMAGE_CAPTURE: // В случае с новым фото, запуск камеры
                    takePhoto();
                    return;
                case Constants.PICK_AUDIO: // В случае с аудио, запуск выбора аудио-файла
                    pickAudio();
            }

        }
    }

    // Получение результата
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = null;
        ArrayList<Uri> imagesUri = new ArrayList<>();
        boolean successfully = false;
        // Если данные были получены
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                // Получаем uri фото
                case Constants.PICK_PHOTOS:
                    uri = data.getData();

                    if (uri != null)
                        successfully = true;
                    if (data.getClipData() != null) {
                        if (data.getClipData().getItemCount() <= maxPhotos) {

                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                ClipData.Item item = data.getClipData().getItemAt(i);
                                Uri u = item.getUri();
                                imagesUri.add(u);
                            }
                            successfully = true;
                        } else {
                            successfully = false;
                            Toast.makeText(this, getString(R.string.pick_photo_error) +
                                    " " + maxPhotos, Toast.LENGTH_LONG).show();
                        }
                    } else
                        imagesUri.add(uri);

                    break;
                case Constants.IMAGE_CAPTURE:
                    uri = takenPhotoUri;
                    imagesUri.add(uri);
                    if (uri != null)
                        successfully = true;
                    break;
                case Constants.PICK_AUDIO:
                    uri = data.getData();
                    successfully = true;
            }

            if (successfully) {
                switch (request) {
                    case Constants.EDIT_PROFILE_PIC: // В случае цели замены аватарки
                        UserManager userManager = new UserManager();
                        userManager.changeProfilePicture(uri, new TaskListener() {
                            @Override
                            public void onComplete() {
                                Toast.makeText(thisActivity, getString(R.string.changed_picture),
                                        Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void onFailure(Exception e) {
                                ExceptionManager.showError(e, thisActivity);
                            }
                        });
                        setFragment(new ProfileFragment(Data.getCurUser())); // Отображаем фрагмент профиля
                        break;
                    case Constants.PICK_POST_IMAGES: // Выбор фото для поста
                        for (Uri uri1: imagesUri)
                            Data.getCurPost().getImagesList().add(uri1); // Добавляем выбранные фото к списку фото
                        setFragment(new NewContentFragment()); // Отображаем фрагмент редактирования поста
                        break;
                    case Constants.PICK_AUDIO: // Выбор аудио
                        Data.getCurMusic().setUri(uri);
                        setFragment(new NewMusicFragment());
                        break;
                    case Constants.PICK_COVER_IMAGE: // Выбор обложки для песни
                        Data.getCurMusic().setCover(uri);
                        setFragment(new NewMusicFragment());
                        break;
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.error_pick_file), Toast.LENGTH_LONG).show();
        }
    }


    // Отображение настроек песни
    public void showSongSettings(Music music) {
        SongDialog dialog = new SongDialog(this, music);
        dialog.show();
    }

    public Fragment getCurrentFragment() {
        return currentFragment;
    }

    public void showBottomPlayer() {
        if (Data.getMusicPlayer().isPlaying()) {
            playerFragment.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction().replace(R.id.player_fragment, new BottomPlayerFragment()).commit();
        }
    }

    public void hideBottomPlayer() {
        playerFragment.setVisibility(View.INVISIBLE);
    }


}