package com.ea7jmf.flickster;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ea7jmf.flickster.models.Movie;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

public class DetailActivity extends AppCompatActivity {

    ImageView ivBackdrop;
    RatingBar ratingBar;
    TextView tvRating;
    TextView tvOverview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Movie movie = getIntent().getParcelableExtra("movie");
        setContentView(R.layout.activity_detail);

        ivBackdrop = (ImageView) findViewById(R.id.ivBackdrop);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        tvRating = (TextView) findViewById(R.id.tvRating);
        tvOverview = (TextView) findViewById(R.id.tvOverview);

        getSupportActionBar().setTitle(movie.getOriginalTitle());

        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        String rating = decimalFormat.format(movie.getVoteAverage()/2.0f);

        tvRating.setText(String.format("%s/5", rating));
        tvOverview.setText(movie.getOverview());
        ratingBar.setRating(Float.parseFloat(rating));
        Picasso.with(this)
                .load(movie.getBackdropPath())
                .placeholder(R.drawable.video_placeholder)
                .into(ivBackdrop);
    }
}
