package com.brigade.rockit.data;

public class TooManyPhotoException extends Exception{

    private int maxPhotos;

    public TooManyPhotoException(int maxPhotos) {
        this.maxPhotos = maxPhotos;
    }

    @Override
    public void printStackTrace() {
        super.printStackTrace();

    }

    public int getMaxPhotos() {
        return maxPhotos;
    }
}
