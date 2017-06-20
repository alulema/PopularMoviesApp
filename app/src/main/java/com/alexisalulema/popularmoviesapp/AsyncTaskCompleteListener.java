package com.alexisalulema.popularmoviesapp;

interface AsyncTaskCompleteListener<T> {
    void onTaskComplete(T json);
}
