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

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MovieArrayAdapter extends ArrayAdapter<Movie> {

    public enum ItemType {
        POSTER_ITEM,
        FULL_BACKDROP_ITEM
    }

    static class PosterItemViewHolder {
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.tvOverview) TextView tvOverview;
        @BindView(R.id.ivMovieImage) ImageView ivPoster;

        public PosterItemViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class FullBackdropItemViewHolder {
        @BindView(R.id.tvMovieTitle) TextView tvTitle;
        @BindView(R.id.ivMovieFrame) ImageView ivBackdrop;

        public FullBackdropItemViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public MovieArrayAdapter(Context context, List<Movie> objects) {
        super(context, R.layout.item_movie, objects);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).isOverRating(5.0d) ?
                ItemType.FULL_BACKDROP_ITEM.ordinal() : ItemType.POSTER_ITEM.ordinal();
    }

    @Override
    public int getViewTypeCount() {
        return ItemType.values().length;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String imageUrl;
        Movie movie = getItem(position);
        ItemType type = ItemType.values()[getItemViewType(position)];

        // Check if the existing view has been reused
        switch (type) {
            case POSTER_ITEM:
                PosterItemViewHolder piViewHolder;
                if (convertView == null) {
                    convertView = getInflatedLayoutForType(type);
                    piViewHolder = new PosterItemViewHolder(convertView);
                    convertView.setTag(piViewHolder);
                } else {
                    piViewHolder = (PosterItemViewHolder) convertView.getTag();
                }

                piViewHolder.ivPoster.setImageResource(0);

                piViewHolder.tvTitle.setText(movie.getOriginalTitle());
                piViewHolder.tvOverview.setText(movie.getOverview());

                imageUrl = movie.getPosterPath();
                int orientation = getContext().getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    imageUrl = movie.getBackdropPath();
                }

                Picasso.with(getContext())
                        .load(imageUrl)
                        .placeholder(R.mipmap.poster_placeholder)
                        .transform(new RoundedCornersTransformation(10, 10))
                        .into(piViewHolder.ivPoster);
                break;
            case FULL_BACKDROP_ITEM:
                FullBackdropItemViewHolder fbViewHolder;
                if (convertView == null) {
                    convertView = getInflatedLayoutForType(type);
                    fbViewHolder = new FullBackdropItemViewHolder(convertView);
                    convertView.setTag(fbViewHolder);
                } else {
                    fbViewHolder = (FullBackdropItemViewHolder) convertView.getTag();
                }

                fbViewHolder.ivBackdrop.setImageResource(0);

                fbViewHolder.tvTitle.setText(movie.getOriginalTitle());

                imageUrl = movie.getBackdropPath();
                Picasso.with(getContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.video_placeholder)
                        .transform(new RoundedCornersTransformation(10, 10))
                        .into(fbViewHolder.ivBackdrop);
                break;
        }

        return convertView;
    }

    private View getInflatedLayoutForType(ItemType type) {
        if (type == ItemType.FULL_BACKDROP_ITEM) {
            return LayoutInflater.from(getContext()).inflate(R.layout.item_popular_movie, null);
        } else if (type == ItemType.POSTER_ITEM) {
            return LayoutInflater.from(getContext()).inflate(R.layout.item_movie, null);
        } else {
            return null;
        }
    }
}
