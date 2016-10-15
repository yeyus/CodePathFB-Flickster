package com.ea7jmf.flickster;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static android.content.ContentValues.TAG;

public class TrailerActivity extends YouTubeBaseActivity {

    AsyncHttpClient client;
    int movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);

        String youtubeApiKey;
        client = new AsyncHttpClient();
        movieId = getIntent().getIntExtra("movie_id", -1);


        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            youtubeApiKey = bundle.getString("YOUTUBE_API_KEY");

            YouTubePlayerView youTubePlayerView =
                    (YouTubePlayerView) findViewById(R.id.player);

            youTubePlayerView.initialize(youtubeApiKey,
                    new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                            YouTubePlayer youTubePlayer, boolean b) {
                            String url = String.format("https://api.themoviedb.org/3/movie/%d/trailers?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed", movieId);
                            client.get(url, getTrailerDataHandler(youTubePlayer));
                        }

                        @Override
                        public void onInitializationFailure(YouTubePlayer.Provider provider,
                                                            YouTubeInitializationResult youTubeInitializationResult) {
                            // TODO failure initializing youtube
                            Log.e("TrailerActivity", "Youtube player initialization failure");
                            throw new RuntimeException("Youtube player initialization failure");
                        }
                    });
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
        }

    }

    private JsonHttpResponseHandler getTrailerDataHandler(final YouTubePlayer youTubePlayer) {

        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray youtubeTrailerArray = null;
                try {
                    youtubeTrailerArray = response.getJSONArray("youtube");
                    if (youtubeTrailerArray.length() > 0) {
                        JSONObject trailerJson = (JSONObject) youtubeTrailerArray.get(0);
                        youTubePlayer.cueVideo(trailerJson.getString("source"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        };
    }
}
