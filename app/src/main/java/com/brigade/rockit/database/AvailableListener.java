package com.brigade.rockit.database;

public interface AvailableListener {
    void onComplete(boolean available);
    void onFailure(Exception e);
}
