package me.zhaowenhao.popularmovies2;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zhaowenhao on 16/9/14.
 */
public class MovieService extends IntentService{
    private static final String TAG = MovieService.class.getSimpleName();
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String POPULAR = "popular";
    private static final String API_KEY = BuildConfig.THE_MOVIE_DB_API_KEY;


    public MovieService(){
        super("Movie");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        Log.d(TAG, "onHandleIntent: started");
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

    private void fetchMovies(){
        String url = Uri.parse(BASE_URL).buildUpon().appendPath(POPULAR).appendQueryParameter("api_key", API_KEY).build().toString();
        Log.d(TAG, "fetchMovies: url = " + url);

        try{
            String jsonString = getUrl(url);
            Log.d(TAG, "fetchMovies: received json string: " + jsonString);
        }catch (IOException e){
            Log.e(TAG, "fetchMovies: failed to fetch movie json", e);
        }
    }
}
