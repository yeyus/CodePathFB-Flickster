package com.ea7jmf.flickster.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ea7jmf.flickster.R;
import com.ea7jmf.flickster.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.ea7jmf.flickster.R.id.tvOverview;
import static com.ea7jmf.flickster.R.id.tvTitle;

public class MovieArrayAdapter extends ArrayAdapter<Movie> {

    private static class ViewHolder {
        TextView tvTitle;
        TextView tvOverview;
        ImageView ivImage;
    }

    public MovieArrayAdapter(Context context, List<Movie> objects) {
        super(context, R.layout.item_movie, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);

        // Check if the existing view has been reused
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_movie, parent, false);
            viewHolder.tvTitle = (TextView) convertView.findViewById(tvTitle);
            viewHolder.tvOverview = (TextView) convertView.findViewById(tvOverview);
            viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivMovieImage);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivImage.setImageResource(0);

        viewHolder.tvTitle.setText(movie.getOriginalTitle());
        viewHolder.tvOverview.setText(movie.getOverview());

        String imageUrl = movie.getPosterPath();
        int orientation = getContext().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imageUrl = movie.getBackdropPath();
        }

        Picasso.with(getContext())
                .load(imageUrl)
                .placeholder(R.mipmap.poster_placeholder)
                .into(viewHolder.ivImage);

        return convertView;
    }
}
