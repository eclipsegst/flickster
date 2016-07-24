package com.zhaolongzhong.flickster.models;

import android.support.annotation.Nullable;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.zhaolongzhong.flickster.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmModel;
import io.realm.RealmResults;
import io.realm.annotations.RealmClass;

@RealmClass
public class Genre implements RealmModel {
    private static final String TAG = Genre.class.getSimpleName();

    private static final String JSON_KEY_ID = "id";
    private static final String JSON_KEY_NAME = "name";

    private static final String ID_KEY = "id";

    private String id;
    private String name;

    public String getName() {
        return name;
    }

    /**
     * @param jsonObject, movie JSONObject
     * @throws JSONException
     *
     * Map a movie JSONObject to a movie object
     */
    private void map(JSONObject jsonObject) throws JSONException{
        this.id = jsonObject.getString(JSON_KEY_ID);
        this.name = jsonObject.getString(JSON_KEY_NAME);
    }

    /**
     * @param array, genre JSONArray
     * @return List of genre object
     */
    public static void mapFromJSONArray(JSONArray array) {
        RealmList<Genre> results = new RealmList<>();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        for (int i = 0; i < array.length(); i++) {
            try {
                Genre genre = new Genre();
                genre.map(array.getJSONObject(i));
                results.add(genre);
            } catch (JSONException e) {
                Log.e(TAG, "Error when parsing genre object.", e);
            }
        }
        realm.copyToRealmOrUpdate(results);
        realm.commitTransaction();
        realm.close();
    }

    /**
     * @param genreId
     * @return a movie
     */
    public static @Nullable Genre getGenreById(String genreId) {
        Realm realm = Realm.getDefaultInstance();
        Genre genre = realm.where(Genre.class).equalTo(ID_KEY, genreId).findFirst();
        realm.close();
        return genre;
    }

    /**
     * Fetch genres
     */
    public static void fetchGenresAsync() {
        // we only fetch genres once since the genres don't change often.
        if (size() > 0) {
            return;
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(Constants.GENRE_BASE_URL), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray genreJsonResult;

                try {
                    genreJsonResult = response.getJSONArray("genres");
                    Genre.mapFromJSONArray(genreJsonResult);
                    if (genreJsonResult.length() == 0) {
                        return;
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Error:", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "Fetch genre error." + errorResponse);
            }
        });
    }

    /**
     * @return the total number of genre
     */
    private static int size() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Genre> genres =  realm.where(Genre.class).findAll();
        realm.close();
        return genres.size();
    }
}
