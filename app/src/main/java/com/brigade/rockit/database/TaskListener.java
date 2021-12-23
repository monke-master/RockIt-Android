package com.brigade.rockit.database;

import android.net.Uri;

import com.brigade.rockit.data.Post;

public interface TaskListener {
    void onComplete();
    void onFailure(Exception e);
}
