package com.alexisalulema.popularmoviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alexisalulema.popularmoviesapp.model.MovieTrailer;

class TrailerAdapter extends BaseAdapter {

    private Context context;
    MovieTrailer[] trailers;

    @Override
    public int getCount() {
        return trailers.length;
    }

    @Override
    public Object getItem(int position) {
        return trailers[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.trailer_item, parent, false);
        }

        TextView tvTrailerTitle = (TextView) rowView.findViewById(R.id.tv_trailer_title);
        tvTrailerTitle.setText(trailers[position].name);

        return rowView;
    }

    public TrailerAdapter(MovieTrailer[] trailers, Context context) {
        this.trailers = trailers;
        this.context = context;
    }
}
