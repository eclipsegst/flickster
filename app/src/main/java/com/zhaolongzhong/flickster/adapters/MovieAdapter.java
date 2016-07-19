package com.zhaolongzhong.flickster.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zhaolongzhong.flickster.R;
import com.zhaolongzhong.flickster.YouTubeActivity;
import com.zhaolongzhong.flickster.models.Movie;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MovieAdapter extends ArrayAdapter {
    public static final String TAG = MovieAdapter.class.getSimpleName();

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_POPULAR = 1;

    public static class ViewHolder {
        @BindView(R.id.movie_item_poster_image_view_id) ImageView posterImageView;
        @BindView(R.id.movie_item_title_text_view_id) TextView titleTextView;
        @BindView(R.id.movie_item_overview_text_view_id) TextView overviewTextView;
        @BindView(R.id.movie_item_play_image_view_id) ImageView playImageView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public static class ViewHolderPopular {
        @BindView(R.id.movie_item_poster_image_view_id) ImageView posterImageView;
        @BindView(R.id.movie_item_play_image_view_id) ImageView playImageView;

        public ViewHolderPopular(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public MovieAdapter(Context context, List<Movie> movies) {
        super(context, R.layout.movie_item, movies);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Movie movie = (Movie) getItem(position);
        if (isLandscape()) {
            return TYPE_NORMAL;
        }
        return movie.getVoteAverage() < 5 ? TYPE_NORMAL : TYPE_POPULAR;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Movie movie = (Movie) getItem(position);
        int viewType = this.getItemViewType(position);

        switch (viewType) {
            case TYPE_NORMAL:
                ViewHolder viewHolder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item, null);
                    viewHolder = new ViewHolder(convertView);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                viewHolder.titleTextView.setText(movie.getOriginalTitle());
                viewHolder.overviewTextView.setText(movie.getOverview());
                String imageUrl = isLandscape() ? movie.getBackdropPath() : movie.getPosterPath();
                int placeholderResId = isLandscape() ? R.drawable.backdrop_placeholder : R.drawable.poster_placeholder;
                Picasso.with(getContext()).load(imageUrl).placeholder(placeholderResId)
                        .transform(new RoundedCornersTransformation(8, 8)).into(viewHolder.posterImageView);

                if (movie.getVideos().size() > 0 && movie.getVoteAverage() >= 5) {
                    viewHolder.playImageView.setVisibility(View.VISIBLE);
                    viewHolder.playImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            YouTubeActivity.newInstance(getContext(), movie.getVideos().get(0).getKey());
                        }
                    });
                } else {
                    viewHolder.playImageView.setVisibility(View.GONE);
                }
                break;
            case TYPE_POPULAR:
                ViewHolderPopular viewHolderPopular;
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.movie_item_backdrop, null);
                    viewHolderPopular = new ViewHolderPopular(convertView);
                    convertView.setTag(viewHolderPopular);

                } else {
                    viewHolderPopular = (ViewHolderPopular) convertView.getTag();
                }

                Picasso.with(getContext()).load(movie.getBackdropPath()).placeholder(R.drawable.backdrop_placeholder)
                        .transform(new RoundedCornersTransformation(8, 8)).into(viewHolderPopular.posterImageView);

                if (movie.getVideos().size() > 0) {
                    viewHolderPopular.playImageView.setVisibility(View.VISIBLE);
                    viewHolderPopular.playImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            YouTubeActivity.newInstance(getContext(), movie.getVideos().get(0).getKey());
                        }
                    });
                }
                break;
            default:
                break;
        }

        return convertView;
    }

    private boolean isLandscape() {
        return getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
