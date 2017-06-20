package com.alexisalulema.popularmoviesapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.PersistableBundle;
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

import com.alexisalulema.popularmoviesapp.data.FavoriteContract;
import com.alexisalulema.popularmoviesapp.model.MovieData;
import com.alexisalulema.popularmoviesapp.model.MoviesStructure;
import com.alexisalulema.popularmoviesapp.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ListItemClickListener,
        AsyncTaskCompleteListener<String>, AdapterView.OnItemSelectedListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private Spinner mSpinner;
    private MoviesStructure mStructure;
    private MoviesAdapter mAdapter;
    private RecyclerView rvMoviesList;
    private MovieData[] shownMovies;
    private boolean fromRestoreInstance;

    ArrayAdapter<CharSequence> arrayAdapter;

    private int sortingOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fromRestoreInstance = false;
        setContentView(R.layout.activity_main);
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns());

        rvMoviesList = (RecyclerView) findViewById(R.id.rv_movies);
        rvMoviesList.setLayoutManager(layoutManager);
        rvMoviesList.setHasFixedSize(true);

        setupSharedPreferences();
    }

    private void setupSharedPreferences() {
        // Get all of the values from shared preferences to set it up
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showFavorites = sharedPreferences.getBoolean(
                getString(R.string.pref_show_favorites_key),
                getResources().getBoolean(R.bool.pref_show_favorites_default));

        arrayAdapter = ArrayAdapter.createFromResource(
                this,
                showFavorites ? R.array.sort_options_extended : R.array.sort_options,
                android.R.layout.simple_spinner_item);

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
        if (option != NetworkUtils.SORT_BY_FAVORITES) {
            URL moviesUrl = NetworkUtils.buildAllMoviesUrl(option);
            new MoviesLoaderTask(this).execute(moviesUrl);
        } else {
            Cursor cursor = getContentResolver().query(
                    FavoriteContract.FavoriteEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

            ArrayList<MovieData> movies = new ArrayList<>();

            while (cursor.moveToNext()) {
                MovieData movie = new MovieData();
                movie.id = cursor.getInt(1);
                movie.title = cursor.getString(2);
                movie.posterPath = cursor.getString(3);

                movies.add(movie);
            }

            cursor.close();

            shownMovies = new MovieData[movies.size()];
            movies.toArray(shownMovies);

            mAdapter = new MoviesAdapter(shownMovies, MainActivity.this, MainActivity.this);
            rvMoviesList.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.action_sort_options);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);

        if (arrayAdapter == null)
            arrayAdapter = ArrayAdapter.createFromResource(this, R.array.sort_options, android.R.layout.simple_spinner_item);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setGravity(Gravity.END);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(sortingOption);

        mSpinner = spinner;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
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
                shownMovies = mStructure.getResults();
                mAdapter = new MoviesAdapter(shownMovies, MainActivity.this, MainActivity.this);
                rvMoviesList.setAdapter(mAdapter);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (fromRestoreInstance)
        {
            fromRestoreInstance = false;
            return;
        }

        AppCompatTextView tvItem = (AppCompatTextView) view;
        String cs = tvItem.getText().toString();

        if (cs.equals(getResources().getString(R.string.sort_by_top_rated))) {
            sortingOption = NetworkUtils.SORT_BY_TOP_RATED;
        } else if (cs.equals(getResources().getString(R.string.sort_by_popular))) {
            sortingOption = NetworkUtils.SORT_BY_POPULAR;
        } else if (cs.equals(getResources().getString(R.string.sort_favorites))) {
            sortingOption = NetworkUtils.SORT_BY_FAVORITES;
        }

        loadData(sortingOption);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        boolean showFavorites = sharedPreferences.getBoolean(
                getString(R.string.pref_show_favorites_key),
                getResources().getBoolean(R.bool.pref_show_favorites_default));

        arrayAdapter = ArrayAdapter.createFromResource(
                this,
                showFavorites ? R.array.sort_options_extended : R.array.sort_options,
                android.R.layout.simple_spinner_item);

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(arrayAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray("shownMovies", shownMovies);
        outState.putInt("sortingOption", sortingOption);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        shownMovies = (MovieData[]) savedInstanceState.getParcelableArray("shownMovies");
        sortingOption = savedInstanceState.getInt("sortingOption");

        if (shownMovies != null) {
            shownMovies = (MovieData[]) savedInstanceState.getParcelableArray("shownMovies");
            mAdapter = new MoviesAdapter(shownMovies, MainActivity.this, MainActivity.this);
            rvMoviesList.setAdapter(mAdapter);

            fromRestoreInstance = true;
        }
    }
}