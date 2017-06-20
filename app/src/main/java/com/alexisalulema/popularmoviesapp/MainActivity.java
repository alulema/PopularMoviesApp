package com.alexisalulema.popularmoviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.alexisalulema.popularmoviesapp.model.MovieData;
import com.alexisalulema.popularmoviesapp.model.MoviesStructure;
import com.alexisalulema.popularmoviesapp.utilities.NetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ListItemClickListener,
        AsyncTaskCompleteListener<String>, AdapterView.OnItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private MoviesStructure mStructure;
    private MoviesAdapter mAdapter;
    private RecyclerView rvMoviesList;

    private int sortingOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvMoviesList = (RecyclerView) findViewById(R.id.rv_movies);

        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns());
        rvMoviesList.setLayoutManager(layoutManager);
        rvMoviesList.setHasFixedSize(true);

        sortingOption = NetworkUtils.SORT_BY_POPULAR;
        loadData(sortingOption);

        setupSharedPreferences();
    }


    private void setupSharedPreferences() {
        // Get all of the values from shared preferences to set it up
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        mVisualizerView.setShowBass(sharedPreferences.getBoolean(getString(R.string.pref_show_favorites_key),
//                getResources().getBoolean(R.bool.pref_show_favorites_default)));

        // Register the listener
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 350;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }

    private void loadData(int option) {
        URL moviesUrl = NetworkUtils.buildAllMoviesUrl(option);
        new MoviesLoaderTask(this).execute(moviesUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_sort_options);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sort_options, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setGravity(Gravity.END);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        } else {
            CharSequence cs;

            if (sortingOption == NetworkUtils.SORT_BY_POPULAR) {
                cs = getResources().getString(R.string.sort_by_popular);
                sortingOption = NetworkUtils.SORT_BY_TOP_RATED;
            } else {
                cs = getResources().getString(R.string.sort_by_top_rated);
                sortingOption = NetworkUtils.SORT_BY_POPULAR;
            }

            item.setTitle(cs);
            loadData(sortingOption);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        MovieData movie = mStructure.getResults()[clickedItemIndex];
        Intent detailsIntent = new Intent(this, MovieActivity.class);

        detailsIntent.putExtra("movie", movie);
        startActivity(detailsIntent);
    }

    @Override
    public void onTaskComplete(String json) {
        try {
            if (json != null && !json.equals("")) {
                mStructure = MoviesStructure.parse(json);
                mAdapter = new MoviesAdapter(mStructure.getResults(), MainActivity.this, MainActivity.this);
                rvMoviesList.setAdapter(mAdapter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        AppCompatTextView tvItem = (AppCompatTextView) view;
        String cs = tvItem.getText().toString();

        if (cs.equals(getResources().getString(R.string.sort_by_top_rated))) {
            sortingOption = NetworkUtils.SORT_BY_TOP_RATED;
        } else if (cs.equals(getResources().getString(R.string.sort_by_popular))) {
            sortingOption = NetworkUtils.SORT_BY_POPULAR;
        }

        loadData(sortingOption);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        System.out.print("1");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}
