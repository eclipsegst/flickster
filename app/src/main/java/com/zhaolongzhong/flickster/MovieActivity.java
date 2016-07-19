package com.zhaolongzhong.flickster;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zhaolongzhong.flickster.adapters.MovieAdapter;
import com.zhaolongzhong.flickster.models.Movie;
import com.zhaolongzhong.flickster.models.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MovieActivity extends AppCompatActivity {
    private static final String TAG = MovieActivity.class.getSimpleName();

    private ArrayList<Movie> movies;
    private MovieAdapter movieAdapter;

    @BindView(R.id.movie_activity_swipe_refresh_layout_id) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.movie_activity_list_view_id) ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_activity);
        ButterKnife.bind(this);

        movies = new ArrayList<>();
        movies.addAll(Movie.getAllMovies());
        movieAdapter = new MovieAdapter(this, movies);
        listView.setAdapter(movieAdapter);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fetchMovieAsync(false);

        for (Movie movie : movies) {
            fetchVideosAsync(movie.getId());
        }
    }

    private SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            fetchMovieAsync(true);
        }
    };

    private void fetchMovieAsync(boolean isPullToRefresh) {
        AsyncHttpClient client = new AsyncHttpClient();
        // when user pull to refresh, we load second page movies
        String params = isPullToRefresh ? "&page=2" : "";
        client.get( String.format(Constants.MOVIE_URL, params), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray movieJsonResult;

                try {
                    movieJsonResult = response.getJSONArray("results");
                    Movie.mapFromJSONArray(movieJsonResult);
                    movies.clear();
                    movies.addAll(Movie.getAllMovies());
                    movieAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Error:", e);
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                swipeRefreshLayout.setRefreshing(false);
                Log.d(TAG, "Fetch movies error." + errorResponse);
            }
        });
    }

    private void fetchVideosAsync(final String movieId) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(Constants.VIDEO_BASE_URL, movieId), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray videoJsonResult;

                try {
                    videoJsonResult = response.getJSONArray("results");
                    Video.mapFromJSONArray(videoJsonResult, movieId);
                    if (videoJsonResult.length() == 0) {
                        return;
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error:", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "Fetch movies error." + errorResponse);
            }
        });
    }
}
