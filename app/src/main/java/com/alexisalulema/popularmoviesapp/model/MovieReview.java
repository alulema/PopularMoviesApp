package com.alexisalulema.popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieReview implements Parcelable {
    public String id;
    public String author;
    public String content;
    public String url;

    public MovieReview () {
    }

    protected MovieReview(Parcel in) {
        id = in.readString();
        author = in.readString();
        content = in.readString();
        url = in.readString();
    }

    public static final Creator<MovieReview> CREATOR = new Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel in) {
            return new MovieReview(in);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };

    public static MovieReview[] parse(String json) {
        try {
            JSONObject root = new JSONObject(json);
            JSONArray jResults = root.getJSONArray("results");

            ArrayList<MovieReview> reviews = new ArrayList<>();

            for (int i = 0; i < jResults.length(); i++) {
                JSONObject jResult = jResults.getJSONObject(i);
                MovieReview trailer = new MovieReview();

                trailer.id = jResult.getString("id");
                trailer.author = jResult.getString("author");
                trailer.content = jResult.getString("content");
                trailer.url = jResult.getString("url");

                reviews.add(trailer);
            }

            MovieReview[] finalReviews = new MovieReview[reviews.size()];
            finalReviews = reviews.toArray(finalReviews);

            return finalReviews;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(author);
        dest.writeString(content);
        dest.writeString(url);
    }
}
