package me.zhaowenhao.popularmovies2;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by zhaowenhao on 16/9/14.
 */
public class MovieService extends IntentService {
    public static final String FETCH_TYPE = "FETCH_TYPE"; //popular or top_rated

    private static final String TAG = MovieService.class.getSimpleName();
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String VIDEO = "videos";
    private static final String REVIEW = "reviews";

    private static final String API_KEY = BuildConfig.THE_MOVIE_DB_API_KEY;

    private String FETCH_ENDPOINT = "popular"; //top_rated

    public MovieService() {
        super("Movie");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: started");
        switch (intent.getIntExtra(FETCH_TYPE, 1)) {
            case 1: {
                FETCH_ENDPOINT = POPULAR;
                break;
            }
            case 2: {
                FETCH_ENDPOINT = TOP_RATED;
                break;
            }
            default: {
                FETCH_ENDPOINT = POPULAR;
            }
        }

        fetchMovies();
        Log.d(TAG, "onHandleIntent: end");
    }

    private byte[] getUrlBytes(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            Log.d(TAG, "getUrlBytes: connection.getResponseCode() = " + connection.getResponseCode());
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    private String getUrl(String urlString) throws IOException {
        return new String(getUrlBytes(urlString));
    }

    private void fetchMovies() {
        String url = Uri.parse(BASE_URL).buildUpon().appendPath(FETCH_ENDPOINT).appendQueryParameter("api_key", API_KEY).build().toString();
        Log.d(TAG, "fetchMovies: url = " + url);

        try {
            String jsonString = getUrl(url);
            Log.d(TAG, "fetchMovies: received json string: " + jsonString);
            getMovieDataFromJSONString(jsonString);
        } catch (IOException e) {
            Log.e(TAG, "fetchMovies: failed to fetch movie json", e);
        } catch (JSONException e) {
            Log.e(TAG, "fetchMovies: failed in json processing", e);
        }
    }

    private void fetchMovieTrailerFromMovieId(String movieId) {
        Vector<ContentValues> trailerVector = new Vector<>();

        String JSON_ARRAY_RESULT = "results";
        String JSON_TRAILER_ID = "id";
        String JSON_TRAILER_KEY = "key";
        String JSON_TRAILER_NAME = "name";

        String url = Uri.parse(BASE_URL).buildUpon().appendPath(movieId).appendPath(VIDEO).appendQueryParameter("api_key", API_KEY).build().toString();


        Log.d(TAG, "fetchMovieTrailer: url = " + url);
        try {
            String jsonString = getUrl(url);
            Log.d(TAG, "fetchMovieTrailer: jsonString: " + jsonString);
            if (jsonString != null) {
                JSONArray jsonArray = (new JSONObject(jsonString)).getJSONArray(JSON_ARRAY_RESULT);
                for (int i = 0; i < jsonArray.length(); i++) {
                    ContentValues trailerValues = new ContentValues();
                    JSONObject tmpJson = jsonArray.getJSONObject(i);
                    trailerValues.put(MovieContract.MovieEntry.MOVIE_ID, movieId);
                    trailerValues.put(MovieContract.MovieEntry.TRAILER_ID, tmpJson.getString(JSON_TRAILER_ID));
                    trailerValues.put(MovieContract.MovieEntry.TRAILER_KEY, tmpJson.getString(JSON_TRAILER_KEY));
                    trailerValues.put(MovieContract.MovieEntry.TRAILER_NAME, tmpJson.getString(JSON_TRAILER_NAME));
                    Log.d(TAG, "fetchMovieTrailerFromMovieId: trailer key: " + tmpJson.getString(JSON_TRAILER_KEY));

                    trailerVector.add(trailerValues);
                }

                if (trailerVector.size() > 0) {
                    ContentValues[] trailerArray = new ContentValues[trailerVector.size()];
                    trailerVector.toArray(trailerArray);
                    this.getContentResolver().bulkInsert(MovieContract.MovieEntry.TRAILER_CONTENT_URI, trailerArray);
                }

            }

        } catch (IOException e) {
            Log.e(TAG, "fetchMovieTrailer: failed to fetch movie json", e);
        } catch (JSONException e) {
            Log.e(TAG, "fetchMovies: failed in json processing", e);
        }
        return;
    }


    private void fetchMovieReviewFromMovieId(String movieId) {
        Vector<ContentValues> reviewVector = new Vector<>();

        String JSON_ARRAY_RESULT = "results";
        String JSON_REVIEW_ID = "id";
        String JSON_REVIEW_AUTHOR = "author";
        String JSON_REVIEW_CONTENT = "content";
        String JSON_REVIEW_URL = "url";


        String url = Uri.parse(BASE_URL).buildUpon().appendPath(movieId).appendPath(REVIEW).appendQueryParameter("api_key", API_KEY).build().toString();


        Log.d(TAG, "fetchMovieReview: url = " + url);
        try {
            String jsonString = getUrl(url);
            Log.d(TAG, "fetchMovieReview: jsonString: " + jsonString);
            if (jsonString != null) {
                JSONArray jsonArray = (new JSONObject(jsonString)).getJSONArray(JSON_ARRAY_RESULT);
                for (int i = 0; i < jsonArray.length(); i++) {
                    ContentValues trailerValues = new ContentValues();
                    JSONObject tmpJson = jsonArray.getJSONObject(i);
                    trailerValues.put(MovieContract.MovieEntry.MOVIE_ID, movieId);
                    trailerValues.put(MovieContract.MovieEntry.REVIEW_ID, tmpJson.getString(JSON_REVIEW_ID));
                    trailerValues.put(MovieContract.MovieEntry.REVIEW_AUTHOR, tmpJson.getString(JSON_REVIEW_AUTHOR));
                    trailerValues.put(MovieContract.MovieEntry.REVIEW_CONTENT, tmpJson.getString(JSON_REVIEW_CONTENT));
                    trailerValues.put(MovieContract.MovieEntry.REVIEW_URL, tmpJson.getString(JSON_REVIEW_URL));

                    Log.d(TAG, "fetchMovieReview: review author: " + tmpJson.getString(JSON_REVIEW_AUTHOR));

                    reviewVector.add(trailerValues);
                }

                if (reviewVector.size() > 0) {
                    ContentValues[] reviewArray = new ContentValues[reviewVector.size()];
                    reviewVector.toArray(reviewArray);
                    this.getContentResolver().bulkInsert(MovieContract.MovieEntry.REVIEW_CONTENT_URI, reviewArray);
                    Log.d(TAG, "fetchMovieReviewFromMovieId: " + reviewVector.size() + " reviewed appended");
                }

            }

        } catch (IOException e) {
            Log.e(TAG, "fetchMovieTrailer: failed to fetch movie json", e);
        } catch (JSONException e) {
            Log.e(TAG, "fetchMovies: failed in json processing", e);
        }
        return;
    }

    private void getMovieDataFromJSONString(String jsonString) throws JSONException {
        Vector<ContentValues> movieVector = new Vector<>();

        String JSON_ARRAY_RESULT = "results";
        String JSON_ID = "id";
        String JSON_TITLE = "title";
        String JSON_ORIGINAL_TITLE = "original_title";
        String JSON_POSTER_PATH = "poster_path";
        String JSON_OVERVIEW = "overview";
        String JSON_POPULARITY = "popularity";
        String JSON_RATING = "vote_average";
        String JSON_RELEASE_STRING = "release_date";

        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray jsonArray = json.getJSONArray(JSON_ARRAY_RESULT);

            JSONObject tempJson = null;

            for (int i = 0; i < jsonArray.length(); i++) {
                ContentValues movieValues = new ContentValues();
                tempJson = jsonArray.getJSONObject(i);
                Log.d(TAG, "getMovieDataFromJSONString,  i = " + i + ", json = " + tempJson.toString());
                String movieId = tempJson.getString(JSON_ID);

                movieValues.put(MovieContract.MovieEntry.MOVIE_ID, movieId);
                movieValues.put(MovieContract.MovieEntry.MOVIE_ORIGINAL_TITLE, tempJson.getString(JSON_ORIGINAL_TITLE));
                movieValues.put(MovieContract.MovieEntry.MOVIE_TITLE, tempJson.getString(JSON_TITLE));
                movieValues.put(MovieContract.MovieEntry.MOVIE_OVERVIEW, tempJson.getString(JSON_OVERVIEW));
                movieValues.put(MovieContract.MovieEntry.MOVIE_POPULARITY, tempJson.getString(JSON_POPULARITY));
                movieValues.put(MovieContract.MovieEntry.MOVIE_POSTER_PATH, tempJson.getString(JSON_POSTER_PATH));
                movieValues.put(MovieContract.MovieEntry.MOVIE_RATING, tempJson.getString(JSON_RATING));
                movieValues.put(MovieContract.MovieEntry.MOVIE_RELEASE_DATE, tempJson.getString(JSON_RELEASE_STRING));
                movieValues.put(MovieContract.MovieEntry.MOVIE_TRAILER_PATH, "PLACE_HOLDER"); //move trailer to a separate table
                movieValues.put(MovieContract.MovieEntry.MOVIE_FAVORITE, 0); //0 as not favorite
                movieVector.add(movieValues);

                fetchMovieTrailerFromMovieId(movieId);
                fetchMovieReviewFromMovieId(movieId);

            }

            if (movieVector.size() > 0) {
                ContentValues[] movieArray = new ContentValues[movieVector.size()];
                movieVector.toArray(movieArray);
                this.getContentResolver().bulkInsert(MovieContract.MovieEntry.MOVIE_CONTENT_URI, movieArray);
            }
        } catch (JSONException e) {
            Log.d(TAG, "getMovieDataFromJSONString: failed to create JSON String" + jsonString, e);
        }
    }
}
