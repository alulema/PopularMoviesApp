package com.alexisalulema.popularmoviesapp;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

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
                startActivity(new         Intent(getApplicationContext(),MainActivity.class));
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

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent startingActivityIntent = getIntent();

        if (startingActivityIntent.hasExtra("movie")) {
            movie = startingActivityIntent.getParcelableExtra("movie");

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
        public static TrailerAdapter mAdapter;

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

        public static void loadData(MovieTrailer[] trailers) {

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
                    mListVView = (ListView)inflater.inflate(R.layout.fragment_trailers, container, false);
                    mAdapter = new TrailerAdapter(trailers == null ? new MovieTrailer[0] : trailers, getContext());
                    mListVView.setAdapter(mAdapter);
                    rootView = mListVView;

                    break;
                case 3:
                    rootView = new LinearLayout(getContext());
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
