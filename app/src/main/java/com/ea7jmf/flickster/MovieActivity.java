package com.ea7jmf.flickster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ea7jmf.flickster.adapters.MovieArrayAdapter;
import com.ea7jmf.flickster.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MovieActivity extends AppCompatActivity {

    private static String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    AsyncHttpClient client;
    ArrayList<Movie> movies;
    MovieArrayAdapter movieAdapter;

    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.lvMovies) ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);

        movies = new ArrayList<>();
        movieAdapter = new MovieArrayAdapter(this, movies);
        lvItems.setAdapter(movieAdapter);

        client = new AsyncHttpClient();
        fetchNowPlaying();

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int typeOrdinal = parent.getAdapter().getItemViewType(position);
                MovieArrayAdapter.ItemType type = MovieArrayAdapter.ItemType.values()[typeOrdinal];

                Intent i;
                switch(type) {
                    case FULL_BACKDROP_ITEM:
                        i = new Intent(MovieActivity.this, TrailerActivity.class);
                        i.putExtra("movie_id", movies.get(position).getId());
                        startActivity(i);
                        break;
                    case POSTER_ITEM:
                        i = new Intent(MovieActivity.this, DetailActivity.class);
                        i.putExtra("movie", movies.get(position));
                        startActivity(i);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown list item type");
                }
            }
        });

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchNowPlaying();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    private void fetchNowPlaying() {
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray moviesJsonResults = null;
                try {
                    moviesJsonResults = response.getJSONArray("results");
                    movies.clear();
                    movies.addAll(Movie.fromJsonArray(moviesJsonResults));
                    movieAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                swipeContainer.setRefreshing(false);

                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }
}
