package me.zhaowenhao.popularmovies2;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by zhaowenhao on 16/9/12.
 */
public class MovieProvider extends ContentProvider {
    public static final String TAG = MovieProvider.class.getSimpleName();

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
        final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri = null;

        switch (match) {
            case MOVIE_DIR: {
                //long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                long _id = db.insertWithOnConflict(MovieContract.MovieEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                    Log.d(TAG, "insert: " + returnUri.toString() + " , id = " + _id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                //throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
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
    public int bulkInsert(Uri uri, ContentValues[] values) {
        Log.d(TAG, "bulkInsert: value size: " + values.length);
        final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "bulkInsert: started! match = " + match);
        switch (match) {
            case MOVIE_DIR: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        Log.d(TAG, "bulkInsert: content value: " + value.toString());
                        //long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        long _id = db.insertWithOnConflict(MovieContract.MovieEntry.TABLE_NAME, MovieContract.MovieEntry.MOVIE_ID, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                Log.d(TAG, "bulkInsert: total " + returnCount + " movie inserted to database");
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);

        }
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
