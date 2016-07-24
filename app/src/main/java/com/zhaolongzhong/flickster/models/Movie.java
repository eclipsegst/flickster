package com.zhaolongzhong.flickster.models;

import android.support.annotation.Nullable;
import android.util.Log;

import com.zhaolongzhong.flickster.Constants;
import com.zhaolongzhong.flickster.MovieType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;
/*
    Example of movie JSON object:

    {
    "poster_path": "/6FxOPJ9Ysilpq0IgkrMJ7PubFhq.jpg",
    "adult": false,
    "overview": "Tarzan, having acclimated to life in London, is called back to his former home in the jungle to investigate the activities at a mining encampment.",
    "release_date": "2016-06-29",
    "genre_ids": [
    28,
    12
    ],
    "id": 258489,
    "original_title": "The Legend of Tarzan",
    "original_language": "en",
    "title": "The Legend of Tarzan",
    "backdrop_path": "/75GFqrnHMKqkcNZ2wWefWXfqtMV.jpg",
    "popularity": 34.490694,
    "vote_count": 431,
    "video": false,
    "vote_average": 4.48
    },
 */

@RealmClass
public class Movie implements RealmModel {
    private static final String TAG = Movie.class.getSimpleName();

    // Keys for parsing movie JSONobject
    private static String JSON_KEY_POSTER_PATH = "poster_path";
    private static String JSON_KEY_ADULT = "adult";
    private static String JSON_KEY_OVERVIEW = "overview";
    private static String JSON_KEY_RELEASE_DATE = "release_date";
    private static String JSON_KEY_GENRE_IDS = "genre_ids";
    private static String JSON_KEY_id = "id";
    private static String JSON_KEY_ORIGINAL_TITLE = "original_title";
    private static String JSON_KEY_ORIGINAL_LANGUAGE = "original_language";
    private static String JSON_KEY_TITLE = "title";
    private static String JSON_KEY_BACKDROP_PATH = "backdrop_path";
    private static String JSON_KEY_POPULARITY = "popularity";
    private static String JSON_KEY_VOTE_COUNT = "vote_count";
    private static String JSON_KEY_VIDEO = "video";
    private static String JSON_KEY_VOTE_AVERAGE = "vote_average";

    // Property name
    private static String ID_KEY = "id";
    private static String MOVIE_TYPE_KEY = "movieType";

    // Movie properties
    @PrimaryKey
    private String id;
    private String posterPath;
    private Boolean adult;
    private String overview;
    private String releaseDate;
    private String genreIds;
    private String originalTitle;
    private String originalLanguage;
    private String title;
    private String backdropPath;
    private double popularity;
    private int voteCount;
    private boolean video;
    private double voteAverage;

    private String movieType;

    public String getPosterPath() {
        return String.format(Constants.IMAGE_W342_BASE_URL, posterPath);
    }

    public Boolean getAdult() {
        return adult;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getGenreIds() {
        return genreIds;
    }

    public String getId() {
        return id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public String getBackdropPath() {
        return String.format(Constants.IMAGE_W500_BASE_URL, backdropPath);
    }

    public double getPopularity() {
        return popularity;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public boolean isVideo() {
        return video;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getMovieType() {
        return movieType;
    }

    public void setMovieType(String movieType) {
        this.movieType = movieType;
    }

    /**
     * @param jsonObject, movie JSONObject
     * @throws JSONException
     *
     * Map a movie JSONObject to a movie object
     */
    private void map(JSONObject jsonObject) throws JSONException{
        this.posterPath = jsonObject.getString(JSON_KEY_POSTER_PATH);
        this.adult = jsonObject.getBoolean(JSON_KEY_ADULT);
        this.overview = jsonObject.getString(JSON_KEY_OVERVIEW);
        this.releaseDate = jsonObject.getString(JSON_KEY_RELEASE_DATE);
        this.genreIds = jsonObject.getString(JSON_KEY_GENRE_IDS);
        this.id = jsonObject.getString(JSON_KEY_id);
        this.originalTitle = jsonObject.getString(JSON_KEY_TITLE);
        this.originalLanguage = jsonObject.getString(JSON_KEY_ORIGINAL_LANGUAGE);
        this.title = jsonObject.getString(JSON_KEY_TITLE);
        this.backdropPath = jsonObject.getString(JSON_KEY_BACKDROP_PATH);
        this.popularity = jsonObject.getDouble(JSON_KEY_POPULARITY);
        this.voteCount = jsonObject.getInt(JSON_KEY_VOTE_COUNT);
        this.video = jsonObject.getBoolean(JSON_KEY_VIDEO);
        this.voteAverage = jsonObject.getDouble(JSON_KEY_VOTE_AVERAGE);
    }

    /**
     * @param array, movie JSONArray
     * @return List of Movie object
     */
    public static void mapFromJSONArray(JSONArray array, MovieType movieType) {
        RealmList<Movie> results = new RealmList<>();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (int i = 0; i < array.length(); i++) {
            try {
                Movie movie = new Movie();
                movie.map(array.getJSONObject(i));
                movie.setMovieType(movieType.getName());
                results.add(movie);
            } catch (JSONException e) {
                Log.e(TAG, "Error when parsing movie object.", e);
            }
        }
        realm.copyToRealmOrUpdate(results);
        realm.commitTransaction();
        realm.close();
    }

    /**
     * @return a list of movies
     */
    public static RealmResults<Movie> getAllMovies() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Movie> movies =  realm.where(Movie.class).findAll();
        realm.close();
        return movies;
    }

    /**
     * @param movieType
     * @return a list of movies by movie type
     */
    public static RealmResults<Movie> getMoviesByMovieType(MovieType movieType) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Movie> movies =  realm.where(Movie.class).equalTo(MOVIE_TYPE_KEY, movieType.getName()).findAll();
        realm.close();
        return movies;
    }

    /**
     * @param movieId
     * @return a movie
     */
    public static @Nullable Movie getMovieById(String movieId) {
        Realm realm = Realm.getDefaultInstance();
        Movie movie = realm.where(Movie.class).equalTo(ID_KEY, movieId).findFirst();
        realm.close();
        return movie;
    }

    /**
     * @return videos
     */
    public RealmResults<Video> getVideos() {
        return Video.getVideosByMovieId(this.id);
    }
}
