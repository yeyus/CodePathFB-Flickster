package com.ea7jmf.flickster;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class TrailerActivity extends YouTubeBaseActivity {

    OkHttpClient client;
    int movieId;
    boolean autoplay;

    @BindView(R.id.player) YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);
        ButterKnife.bind(this);

        String youtubeApiKey;
        client = new OkHttpClient();
        movieId = getIntent().getIntExtra("movie_id", -1);
        autoplay = getIntent().getBooleanExtra("autoplay", false);

        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            youtubeApiKey = bundle.getString("YOUTUBE_API_KEY");

            youTubePlayerView.initialize(youtubeApiKey,
                    new YouTubePlayer.OnInitializedListener() {
                        @Override
                        public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                            YouTubePlayer youTubePlayer, boolean b) {
                            String url = String.format("https://api.themoviedb.org/3/movie/%d/trailers?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed", movieId);
                            Request request = new Request.Builder()
                                    .url(url)
                                    .build();

                            client.newCall(request).enqueue(getTrailerDataHandler(youTubePlayer));
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

    private Callback getTrailerDataHandler(final YouTubePlayer youTubePlayer) {

        return new Callback() {
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                JSONArray youtubeTrailerArray = null;

                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                try {
                    String responseData = response.body().string();
                    JSONObject json = new JSONObject(responseData);
                    youtubeTrailerArray = json.getJSONArray("youtube");
                    if (youtubeTrailerArray.length() > 0) {
                        JSONObject trailerJson = (JSONObject) youtubeTrailerArray.get(0);
                        if (autoplay) {
                            youTubePlayer.loadVideo(trailerJson.getString("source"));
                        } else {
                            youTubePlayer.cueVideo(trailerJson.getString("source"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        };
    }
}
