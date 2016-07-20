package com.zhaolongzhong.flickster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.zhaolongzhong.flickster.models.Movie;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailActivity extends YouTubeBaseActivity {
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    private static final String MOVIE_ID = "movieId";
    private Movie movie;

    @BindView(R.id.movie_detail_activity_player_view_id) YouTubePlayerView youTubePlayerView;
    @BindView(R.id.movie_detail_activity_poster_image_view_id) ImageView posterImageView;
    @BindView(R.id.movie_detail_activity_title_text_view_id) TextView titleTextView;
    @BindView(R.id.movie_detail_activity_overview_text_view_id) TextView overviewTextView;
    @BindView(R.id.movie_detail_activity_release_date_text_view_id) TextView releaseDateTextView;
    @BindView(R.id.movie_detail_activity_rating_bar_id) RatingBar ratingBar;

    public static void newInstance(Context context, String movieId) {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(MOVIE_ID, movieId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail_activity);
        ButterKnife.bind(this);

        String movieId = getIntent().getStringExtra(MOVIE_ID);
        movie = Movie.getMovieById(movieId);
        ratingBar.setMax(10);

        invalidateViews();
        // todo: any better way to handle none video?
        if (movie.getVideos().size() > 0) {
            youTubePlayerView.initialize(Constants.YOUTUBE_API_KEY, onInitializedListener);
            posterImageView.setVisibility(View.GONE);
        } else {
            youTubePlayerView.setVisibility(View.INVISIBLE);
            posterImageView.setVisibility(View.VISIBLE);
        }
    }

    private void invalidateViews() {
        titleTextView.setText(movie.getOriginalTitle());
        overviewTextView.setText(movie.getOverview());
        releaseDateTextView.setText(movie.getReleaseDate());

        ratingBar.setNumStars(10);
        ratingBar.setRating((float)movie.getVoteAverage());
        ratingBar.setIsIndicator(true);

        Picasso.with(this).load(movie.getBackdropPath()).placeholder(R.drawable.backdrop_placeholder)
                .into(posterImageView);

    }

    private YouTubePlayer.OnInitializedListener onInitializedListener = new YouTubePlayer.OnInitializedListener() {
        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
            if (movie.getVideos().size() > 0) {
                youTubePlayer.cueVideo(movie.getVideos().get(0).getKey());
            }
        }
        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        }
    };
}
