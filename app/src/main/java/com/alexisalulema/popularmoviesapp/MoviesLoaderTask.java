package com.alexisalulema.popularmoviesapp;

import android.os.AsyncTask;

import com.alexisalulema.popularmoviesapp.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

public class MoviesLoaderTask extends AsyncTask<URL, Void, String> {

    private AsyncTaskCompleteListener<String> listener;

    public MoviesLoaderTask(AsyncTaskCompleteListener<String> listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(URL... params) {
        URL searchUrl = params[0];
        String moviesResult = null;

        try {
            moviesResult = NetworkUtils.getResponseFromHttpUrl(searchUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return moviesResult;
    }

    @Override
    protected void onPostExecute(String json) {
        super.onPostExecute(json);
        listener.onTaskComplete(json);
    }
}
