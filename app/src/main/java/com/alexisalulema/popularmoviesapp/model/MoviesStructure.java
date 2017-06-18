package com.alexisalulema.popularmoviesapp.model;

import org.json.JSONArray;
import org.json.JSONObject;

public class MoviesStructure {
    private int page;
    private int totalResults;
    private int totalPages;
    private MovieData[] results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public MovieData[] getResults() {
        return results;
    }

    public void setResults(MovieData[] results) {
        this.results = results;
    }

    public static MoviesStructure parse(String json) {
        MoviesStructure data = new MoviesStructure();

        try {
            JSONObject root = new JSONObject(json);
            JSONArray jResults = root.getJSONArray("results");

            data.setPage(root.getInt("page"));
            data.setTotalResults(root.getInt("total_results"));
            data.setTotalPages(root.getInt("total_pages"));

            MovieData[] movies = new MovieData[jResults.length()];

            for (int i = 0; i < jResults.length(); i++) {
                JSONObject jResult = jResults.getJSONObject(i);
                MovieData movie = new MovieData();

                movie.posterPath = jResult.getString("poster_path");
                movie.adult = jResult.getBoolean("adult");
                movie.overview = jResult.getString("overview");
                movie.id = jResult.getInt("id");
                movie.originalTitle = jResult.getString("original_title");
                movie.originalLanguage = jResult.getString("original_language");
                movie.title = jResult.getString("title");
                movie.releaseDate = jResult.getString("release_date");
                movie.backdropPath = jResult.getString("backdrop_path");
                movie.popularity = jResult.getDouble("popularity");
                movie.voteCount = jResult.getInt("vote_count");
                movie.video = jResult.getBoolean("video");
                movie.voteAverage = jResult.getDouble("vote_average");

                JSONArray genre_ids = jResult.getJSONArray("genre_ids");
                int[] ids = new int[genre_ids.length()];

                for (int j = 0; j < genre_ids.length(); j++)
                    ids[j] = genre_ids.getInt(j);

                movie.genreIds = ids;
                movies[i] = movie;
            }

            data.setResults(movies);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        return data;
    }
}
