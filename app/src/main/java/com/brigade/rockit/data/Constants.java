package com.brigade.rockit.data;

import android.Manifest;

public class Constants {
    public static final int PICK_PHOTOS = 1;
    public static final int IMAGE_CAPTURE = 2;
    public static final int PICK_AUDIO = 3;
    public static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,

    };
    public static String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static final int EDIT_PROFILE_PIC = 1;
    public static final int MAX_BIO_LENGTH = 300;
    public static final int PICK_POST_IMAGES = 2;
    public static final int MAX_POST_IMAGES = 5;
    public static final int PICK_COVER_IMAGE = 4;
    public static final int MAX_POST_SONGS = 5;
    public static final int PLAYLIST_MODE = 0;
    public static final int SELECTING_MODE = 1;
    public static final int POST_MODE = 2;
    public static int NEWS_FEED_SIZE = 100000;
    public static String STORAGE_PATH = "gs://rockit-e8345.appspot.com";
}
