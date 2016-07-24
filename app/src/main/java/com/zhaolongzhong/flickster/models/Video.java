package com.zhaolongzhong.flickster.models;

import android.util.Log;

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
    Example of video JSON object:

{
    "id": 316023,
    "results": [
        {
        "id": "5772dfa7c3a36826450004e1",
        "iso_639_1": "en",
        "iso_3166_1": "US",
        "key": "Z6ovonZxNk8",
        "name": "ATV Clip",
        "site": "YouTube",
        "size": 1080,
        "type": "Clip"
        }
     ]
}
 */

@RealmClass
public class Video implements RealmModel {
    private static final String TAG = Video.class.getSimpleName();

    // Keys for parsing video JSONObject
    private static String JSON_KEY_id = "id";
    private static String JSON_KEY_VIDEO_KEY = "key";
    private static String JSON_KEY_SITE= "site";

    // Property name
    private static String MOVIE_ID_KEY = "movieId";

    // Movie properties
    @PrimaryKey
    private String id;
    private String key;
    private String site;
    private String movieId;

    public String getKey() {
        return key;
    }

    /**
     * @param jsonObject, video JSONObject
     * @throws JSONException
     *
     * Map a video JSONObject to a video object
     */
    private void map(JSONObject jsonObject, String movieId) throws JSONException{
        this.id = jsonObject.getString(JSON_KEY_id);
        this.key = jsonObject.getString(JSON_KEY_VIDEO_KEY);
        this.site = jsonObject.getString(JSON_KEY_SITE);
        this.movieId = movieId;
    }

    /**
     * @param array, video JSONArray
     * @return a list of Video object
     */
    public static void mapFromJSONArray(JSONArray array, String movieId) {
        RealmList<Video> results = new RealmList<>();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (int i = 0; i < array.length(); i++) {
            try {
                Video video = new Video();
                video.map(array.getJSONObject(i), movieId);
                results.add(video);
            } catch (JSONException e) {
                Log.e(TAG, "Error when parsing video object.", e);
            }
        }
        realm.copyToRealmOrUpdate(results);
        realm.commitTransaction();
        realm.close();
    }

    /**
     * @param movieId
     * @return a list of videos
     */
    public static RealmResults<Video> getVideosByMovieId(String movieId) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Video> videos = realm.where(Video.class).equalTo(Video.MOVIE_ID_KEY, movieId).findAll();
        realm.close();
        return videos;
    }
}
