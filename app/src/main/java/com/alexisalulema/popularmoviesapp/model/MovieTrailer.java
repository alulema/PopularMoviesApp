package com.alexisalulema.popularmoviesapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieTrailer implements Parcelable {
    final public static String TYPE_TRAILER = "Trailer";
    final public static String TYPE_TEASER = "Teaser";

    public String id;
    public String iso_639_1;
    public String iso_3166_1;
    public String key;
    public String name;
    public String site;
    public int size;
    public String type;

    public MovieTrailer() {
    }

    protected MovieTrailer(Parcel in) {
        id = in.readString();
        iso_639_1 = in.readString();
        iso_3166_1 = in.readString();
        key = in.readString();
        name = in.readString();
        site = in.readString();
        size = in.readInt();
        type = in.readString();
    }

    public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel in) {
            return new MovieTrailer(in);
        }

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };

    public static final MovieTrailer[] parse(String json, String filterBy) {
        try {
            JSONObject root = new JSONObject(json);
            JSONArray jResults = root.getJSONArray("results");

            ArrayList<MovieTrailer> trailers = new ArrayList<>();

            for (int i = 0; i < jResults.length(); i++) {
                JSONObject jResult = jResults.getJSONObject(i);
                MovieTrailer trailer = new MovieTrailer();

                trailer.type = jResult.getString("type");

                if (filterBy != null && !trailer.type.equals(filterBy))
                    continue;

                trailer.id = jResult.getString("id");
                trailer.iso_639_1 = jResult.getString("iso_639_1");
                trailer.iso_3166_1 = jResult.getString("iso_3166_1");
                trailer.key = jResult.getString("key");
                trailer.name = jResult.getString("name");
                trailer.site = jResult.getString("site");
                trailer.size = jResult.getInt("size");

                trailers.add(trailer);
            }

            MovieTrailer[] finalTrailers = new MovieTrailer[trailers.size()];
            finalTrailers = trailers.toArray(finalTrailers);

            return finalTrailers;

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
        dest.writeString(iso_639_1);
        dest.writeString(iso_3166_1);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(site);
        dest.writeInt(size);
        dest.writeString(type);
    }
}
