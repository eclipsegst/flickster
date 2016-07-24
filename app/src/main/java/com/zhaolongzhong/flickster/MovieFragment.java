package com.zhaolongzhong.flickster;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class MovieFragment extends Fragment {
    private static final String TAG = MovieFragment.class.getSimpleName();

    private static final String MOVIE_TYPE_NAME = "movieTypeName";

    private ArrayList<Movie> movies;
    private MovieAdapter movieAdapter;
    private MovieType movieType;

    @BindView(R.id.movie_activity_swipe_refresh_layout_id) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.movie_activity_list_view_id) ListView listView;

    public static MovieFragment newInstance(String movieTypeName) {
        MovieFragment movieFragment = new MovieFragment();
        Bundle args = new Bundle();
        args.putString(MOVIE_TYPE_NAME, movieTypeName);
        movieFragment.setArguments(args);
        return movieFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.movie_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);

        movieType = MovieType.instanceFromName(getArguments().getString(MOVIE_TYPE_NAME));

        movies = new ArrayList<>();
        movies.addAll(Movie.getAllMovies());
        movieAdapter = new MovieAdapter(getActivity(), movies);
        listView.setAdapter(movieAdapter);
        listView.setOnItemClickListener(onItemClickListener);
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        invalidateViews();
        fetchMovieAsync(false);
    }

    private void invalidateViews() {
        movies.clear();
        movies.addAll(Movie.getMoviesByMovieType(movieType));
        movieAdapter.notifyDataSetChanged();
    }

    private ListView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MovieDetailActivity.newInstance(getActivity(), movies.get(position).getId());
        }
    };

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
        client.get(String.format(Constants.MOVIE_URL, movieType.getValue(), params), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray movieJsonResult;

                try {
                    movieJsonResult = response.getJSONArray("results");
                    Movie.mapFromJSONArray(movieJsonResult, movieType);
                    invalidateViews();

                    //todo: any better way to handle this?
                    for (Movie movie : movies) {
                        //we only try to fetch video for movies that don't have a video
                        if (movie.getVideos().size() == 0) {
                            fetchVideosAsync(movie.getId());
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error:", e);
                }

                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "Fetch movies error." + errorResponse);
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

                    invalidateViews();
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
