package com.alexisalulema.popularmoviesapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alexisalulema.popularmoviesapp.data.FavoriteContract;
import com.alexisalulema.popularmoviesapp.model.MovieData;
import com.alexisalulema.popularmoviesapp.model.MovieReview;
import com.alexisalulema.popularmoviesapp.model.MovieTrailer;
import com.alexisalulema.popularmoviesapp.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.text.DecimalFormat;

public class MovieActivity extends AppCompatActivity {

    private static MovieData movie;
    private static MovieTrailer[] trailers;
    private static MovieReview[] reviews;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PlaceholderFragment.mAdapter = null;
        trailers = null;

        setContentView(R.layout.activity_movie);
        setTitle(R.string.details_title);
        Toolbar tb = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        Intent startingActivityIntent = getIntent();

        if (startingActivityIntent.hasExtra("movie")) {
            movie = startingActivityIntent.getParcelableExtra("movie");

            isFavorite = false;
            final Cursor cursor = getContentResolver().query(
                    FavoriteContract.FavoriteEntry.CONTENT_URI,
                    null,
                    FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID + " = " + movie.id,
                    null,
                    null);

            try {
                isFavorite = cursor.moveToNext();
            } finally {
                cursor.close();
            }

            final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

            if (isFavorite)
                fab.setImageResource(R.drawable.remove_favorite);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isFavorite) {
                        String stringId = Integer.toString(movie.id);
                        Uri uri = FavoriteContract.FavoriteEntry.CONTENT_URI;
                        uri = uri.buildUpon().appendPath(stringId).build();

                        getContentResolver().delete(uri, null, null);

                        isFavorite = false;
                        fab.setImageResource(R.drawable.add_favorite);

                        Snackbar.make(view, "Removed from Favorites!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        ContentValues contentValues = new ContentValues();

                        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID, movie.id);
                        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_TITLE, movie.title);
                        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_POSTER_PATH, movie.posterPath);
                        // Insert the content values via a ContentResolver
                        Uri uri = getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI, contentValues);

                        if (uri != null) {
                            isFavorite = true;
                            fab.setImageResource(R.drawable.remove_favorite);

                            Snackbar.make(view, "Added to Favorites!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                }
            });

            URL trailersUrl = NetworkUtils.buildGetTrailersUrl(movie.id);
            new MoviesLoaderTask(new AsyncTaskCompleteListener<String>() {
                @Override
                public void onTaskComplete(String json) {
                    try {
                        if (json != null && !json.equals("")) {
                            trailers = MovieTrailer.parse(json, MovieTrailer.TYPE_TRAILER);

                            if (PlaceholderFragment.mAdapter != null) {
                                PlaceholderFragment.mAdapter.trailers = trailers;
                                PlaceholderFragment.mListVView.setAdapter(PlaceholderFragment.mAdapter);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }).execute(trailersUrl);

            URL reviewsUrl = NetworkUtils.buildGetReviewsUrl(movie.id);
            new MoviesLoaderTask(new AsyncTaskCompleteListener<String>() {
                @Override
                public void onTaskComplete(String json) {
                    try {
                        if (json != null && !json.equals("")) {
                            reviews = MovieReview.parse(json);

                            if (PlaceholderFragment.rAdapter != null) {
                                PlaceholderFragment.rAdapter.reviews = reviews;
                                PlaceholderFragment.rListVView.setAdapter(PlaceholderFragment.rAdapter);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }).execute(reviewsUrl);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        return true;
        //}

        //return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        public static ListView mListVView;
        public static ListView rListVView;
        public static TrailerAdapter mAdapter;
        public static ReviewAdapter rAdapter;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            int section = getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView = null;

            switch (section) {
                case 1:
                    rootView = inflater.inflate(R.layout.activity_details, container, false);
                    DecimalFormat df = new DecimalFormat("#.00");

                    ImageView ivDetailsPoster = (ImageView) rootView.findViewById(R.id.iv_details_poster);
                    TextView tvDetailsTitle = (TextView) rootView.findViewById(R.id.tv_details_title);
                    TextView tvDetailsDate = (TextView) rootView.findViewById(R.id.tv_details_date);
                    TextView tvDetailsRating = (TextView) rootView.findViewById(R.id.tv_details_rating);
                    TextView tvDetailsOverview = (TextView) rootView.findViewById(R.id.tv_details_overview);

                    tvDetailsTitle.setText(movie.title);
                    tvDetailsDate.setText(movie.releaseDate);
                    tvDetailsRating.setText(df.format(movie.popularity));
                    tvDetailsOverview.setText(movie.overview);
                    String imgUrl = "http://image.tmdb.org/t/p/w342" + movie.posterPath;
                    Picasso.with(rootView.getContext()).load(imgUrl).into(ivDetailsPoster);
                    break;
                case 2:
                    mListVView = (ListView) inflater.inflate(R.layout.fragment_list, container, false);
                    mAdapter = new TrailerAdapter(trailers == null ? new MovieTrailer[0] : trailers, getContext());
                    mListVView.setAdapter(mAdapter);

                    mListVView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            MovieTrailer t = mAdapter.trailers[position];
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/" + t.key)));
                        }
                    });
                    rootView = mListVView;

                    break;
                case 3:
                    rListVView = (ListView) inflater.inflate(R.layout.fragment_list, container, false);
                    rAdapter = new ReviewAdapter(reviews == null ? new MovieReview[0] : reviews, getContext());
                    rListVView.setAdapter(rAdapter);
                    rootView = rListVView;
                    break;
            }

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.tab_details);
                case 1:
                    return getResources().getString(R.string.tab_trailers);
                case 2:
                    return getResources().getString(R.string.tab_reviews);
            }
            return null;
        }
    }
}
