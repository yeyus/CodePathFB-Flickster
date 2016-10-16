package com.ea7jmf.flickster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ea7jmf.flickster.adapters.MovieArrayAdapter;
import com.ea7jmf.flickster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MovieActivity extends AppCompatActivity {

    private static String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    OkHttpClient client;
    ArrayList<Movie> movies;
    MovieArrayAdapter movieAdapter;

    @BindView(R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    @BindView(R.id.lvMovies) ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_movie);
        ButterKnife.bind(this);

        movies = new ArrayList<>();
        movieAdapter = new MovieArrayAdapter(this, movies);
        lvItems.setAdapter(movieAdapter);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        lvItems.setOnScrollListener(new AbsListView.OnScrollListener() {
            int mLastFirstVisibleItem = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {   }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (view.getId() == lvItems.getId()) {
                    final int currentFirstVisibleItem = lvItems.getFirstVisiblePosition();

                    if (currentFirstVisibleItem > mLastFirstVisibleItem) {
                        // getSherlockActivity().getSupportActionBar().hide();
                        getSupportActionBar().hide();
                    } else if (currentFirstVisibleItem < mLastFirstVisibleItem) {
                        // getSherlockActivity().getSupportActionBar().show();
                        getSupportActionBar().show();
                    }

                    mLastFirstVisibleItem = currentFirstVisibleItem;
                }
            }
        });

        client = new OkHttpClient();
        fetchNowPlaying();

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
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MovieActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeContainer.setRefreshing(false);
                    }
                });
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                JSONArray moviesJsonResults = null;
                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    moviesJsonResults = json.getJSONArray("results");
                    movies.clear();
                    movies.addAll(Movie.fromJsonArray(moviesJsonResults));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                MovieActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        movieAdapter.notifyDataSetChanged();
                        swipeContainer.setRefreshing(false);
                    }
                });

            }
        });
    }

    @OnItemClick(R.id.lvMovies)
    public void listItemClick(AdapterView<?> parent, View view, int position, long id) {
        int typeOrdinal = parent.getAdapter().getItemViewType(position);
        MovieArrayAdapter.ItemType type = MovieArrayAdapter.ItemType.values()[typeOrdinal];

        Intent i;
        switch(type) {
            case FULL_BACKDROP_ITEM:
                i = new Intent(MovieActivity.this, TrailerActivity.class);
                i.putExtra("movie_id", movies.get(position).getId());
                i.putExtra("autoplay", true);
                startActivity(i);
                break;
            case POSTER_ITEM:
                i = new Intent(MovieActivity.this, DetailActivity.class);
                i.putExtra("movie", movies.get(position));
                startActivity(i);
                this.overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
                break;
            default:
                throw new IllegalArgumentException("Unknown list item type");
        }
    }
}
