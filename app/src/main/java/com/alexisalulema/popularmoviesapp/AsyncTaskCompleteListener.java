package com.alexisalulema.popularmoviesapp;

public interface AsyncTaskCompleteListener<T> {
    void onTaskComplete(T json);
}
