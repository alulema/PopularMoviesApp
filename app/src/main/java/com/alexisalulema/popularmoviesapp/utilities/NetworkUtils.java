package com.alexisalulema.popularmoviesapp.utilities;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    final public static int SORT_BY_POPULAR = 0;
    final public static int SORT_BY_TOP_RATED = 1;

    final private static String API_KEY = "3429bc6d31c2c7a5fcbd0fa24d130fd9";
    final private static String[] QUERY = {"popular", "top_rated"};
    final private static String THE_MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/movie/%s?api_key=%s";

    /**
     * Builds the URL used to query all popular movies from TheMovieDB.
     *
     * @return The URL to use to query the TheMovieDB.
     */
    public static URL buildAllMoviesUrl(int option) {
        String urlString = String.format(THE_MOVIE_DB_BASE_URL, QUERY[option], API_KEY);
        return buildUrl(urlString);
    }

    /**
     * Builds the URL used to query a specific movie from TheMovieDB.
     *
     * @return
     */
    public static URL buildMovieUrl(int movieId) {
        String urlString = String.format(THE_MOVIE_DB_BASE_URL, Integer.toString(movieId), API_KEY);
        return buildUrl(urlString);
    }

    private static URL buildUrl(String urlString) {
        Uri builtUri = Uri.parse(urlString).buildUpon()
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}