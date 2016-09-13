package me.zhaowenhao.popularmovies2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by zhaowenhao on 16/9/12.
 */
public class MovieProvider extends ContentProvider {
    public static final int MOVIE_DIR = 0;
    public static final int MOVIE_ITEM = 1;

    private static UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MOVIE_PATH, MOVIE_DIR);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MOVIE_PATH + "/#", MOVIE_ITEM);

    }

    private MovieDBHelper mMovieDBHelper;

    @Override
    public boolean onCreate(){
        mMovieDBHelper = new MovieDBHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        SQLiteDatabase db = mMovieDBHelper.getReadableDatabase();
        Cursor retCursor = null;
        switch (sUriMatcher.match(uri)){
            case MOVIE_DIR:
            {
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                        );
                break;
            }

            case MOVIE_ITEM:
            {
                String movie_id = uri.getPathSegments().get(1);
                retCursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        "id = ?",
                        new String[] {movie_id},
                        null,
                        null,
                        sortOrder
                        );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues){
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        return 0;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs){
        return 0;
    }


    @Override
    public String getType(Uri uri){
        switch (sUriMatcher.match(uri)){
            case MOVIE_DIR:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ITEM:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
    }

}
