package com.alexisalulema.popularmoviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexisalulema.popularmoviesapp.model.MovieTrailer;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private static final String TAG = MoviesAdapter.class.getSimpleName();
    private final TrailerAdapter.ListItemClickListener mOnClickListener;
    public MovieTrailer[] trailers;

    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    public TrailerAdapter(MovieTrailer[] trailers, TrailerAdapter.ListItemClickListener listener) {
        mOnClickListener = listener;
        this.trailers = trailers;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.trailer_item, viewGroup, false);
        TrailerViewHolder viewHolder = new TrailerViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        Log.d(TAG, "Trailer #" + position);
        //MovieTrailer t = trailers[position];
        //holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return trailers.length;
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvTrailerTitle;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            tvTrailerTitle = (TextView) itemView.findViewById(R.id.tv_trailer_title);
            itemView.setOnClickListener(this);
        }

        void bind(int listIndex) {
            MovieTrailer trailer = trailers[listIndex];
            tvTrailerTitle.setText(trailer.name);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
