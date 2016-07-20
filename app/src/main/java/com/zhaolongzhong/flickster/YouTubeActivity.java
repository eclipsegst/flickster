package com.zhaolongzhong.flickster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YouTubeActivity extends YouTubeBaseActivity {
    private static final String TAG = com.zhaolongzhong.flickster.MovieActivity.class.getSimpleName();

    private static final String VIDEO_ID = "videoId";
    private String videoId;

    @BindView(R.id.youtube_player_view_id) YouTubePlayerView youTubePlayerView;

    public static void newInstance(Context context, String videoId) {
        Intent intent = new Intent(context, YouTubeActivity.class);
        intent.putExtra(VIDEO_ID, videoId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_activity);
        ButterKnife.bind(this);

        videoId = getIntent().getStringExtra(VIDEO_ID);
        youTubePlayerView.initialize(Constants.YOUTUBE_API_KEY, onInitializedListener);
    }

    private YouTubePlayer.OnInitializedListener onInitializedListener = new YouTubePlayer.OnInitializedListener() {
        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
            youTubePlayer.loadVideo(videoId);
            youTubePlayer.setFullscreen(true);
        }
        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        }
    };
}
