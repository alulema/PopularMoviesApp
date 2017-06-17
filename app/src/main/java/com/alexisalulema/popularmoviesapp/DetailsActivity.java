package com.alexisalulema.popularmoviesapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexisalulema.popularmoviesapp.model.MovieData;
import com.alexisalulema.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.DecimalFormat;

public class DetailsActivity extends AppCompatActivity {

    private ImageView ivDetailsPoster;
    private TextView tvDetailsTitle;
    private TextView tvDetailsDate;
    private TextView tvDetailsRating;
    private TextView tvDetailsOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        setTitle(R.string.details_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivDetailsPoster = (ImageView) findViewById(R.id.iv_details_poster);
        tvDetailsTitle = (TextView) findViewById(R.id.tv_details_title);
        tvDetailsDate = (TextView) findViewById(R.id.tv_details_date);
        tvDetailsRating = (TextView) findViewById(R.id.tv_details_rating);
        tvDetailsOverview = (TextView) findViewById(R.id.tv_details_overview);

        DecimalFormat df = new DecimalFormat("#.00");
        Intent startingActivityIntent = getIntent();

        if (startingActivityIntent.hasExtra("movie")) {
            MovieData movie = startingActivityIntent.getParcelableExtra("movie");
            tvDetailsTitle.setText(movie.title);
            tvDetailsDate.setText(movie.releaseDate);
            tvDetailsRating.setText(df.format(movie.popularity));
            tvDetailsOverview.setText(movie.overview);
            String imgUrl = "http://image.tmdb.org/t/p/w342" + movie.posterPath;
            Picasso.with(this).load(imgUrl).into(ivDetailsPoster);

            URL trailersUrl = NetworkUtils.buildGetTrailersUrl(movie.id);
            new MoviesLoaderTask(new AsyncTaskCompleteListener<String>() {
                @Override
                public void onTaskComplete(String json) {

                }
            }).execute(trailersUrl);

            URL reviewsUrl = NetworkUtils.buildGetReviewsUrl(movie.id);
            new MoviesLoaderTask(new AsyncTaskCompleteListener<String>() {
                @Override
                public void onTaskComplete(String json) {

                }
            }).execute(reviewsUrl);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
