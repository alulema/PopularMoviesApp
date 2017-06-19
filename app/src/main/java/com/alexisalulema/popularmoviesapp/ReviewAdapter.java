package com.alexisalulema.popularmoviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alexisalulema.popularmoviesapp.model.MovieReview;

class ReviewAdapter extends BaseAdapter {

    private Context context;
    MovieReview[] reviews;

    @Override
    public int getCount() {
        return reviews.length;
    }

    @Override
    public Object getItem(int position) {
        return reviews[position];
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
            rowView = inflater.inflate(R.layout.review_item, parent, false);
        }

        TextView tvAuthor = (TextView) rowView.findViewById(R.id.tv_author);
        tvAuthor.setText(reviews[position].author);

        TextView tvContent = (TextView) rowView.findViewById(R.id.tv_content);
        tvContent.setText(reviews[position].content);

        return rowView;
    }

    public ReviewAdapter(MovieReview[] reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }
}
