package com.brigade.rockit.database;


public interface GetObjectListener {
    void onComplete(Object object);
    void onFailure(Exception e);
}
