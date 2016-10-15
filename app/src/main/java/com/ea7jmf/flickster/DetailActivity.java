package com.ea7jmf.flickster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ea7jmf.flickster.models.Movie;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @BindView(R.id.ivMovieFrame) ImageView ivMovieFrame;
    @BindView(R.id.ivPlay) ImageView ivPlay;
    @BindView(R.id.ratingBar) RatingBar ratingBar;
    @BindView(R.id.tvRating) TextView tvRating;
    @BindView(R.id.tvOverview) TextView tvOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Movie movie = getIntent().getParcelableExtra("movie");
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        getSupportActionBar().setTitle(movie.getOriginalTitle());

        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        String rating = decimalFormat.format(movie.getVoteAverage()/2.0f);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetailActivity.this, TrailerActivity.class);
                i.putExtra("movie_id", movie.getId());
                startActivity(i);
            }
        };

        ivMovieFrame.setOnClickListener(clickListener);
        ivPlay.setOnClickListener(clickListener);

        tvRating.setText(String.format("%s/5", rating));
        tvOverview.setText(movie.getOverview());
        ratingBar.setRating(Float.parseFloat(rating));
        Picasso.with(this)
                .load(movie.getBackdropPath())
                .placeholder(R.drawable.video_placeholder)
                .into(ivMovieFrame);
    }
}
