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
    public static final int REVIEW_DIR = 2;
    public static final int TRAILER_DIR = 3;

    private static UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MOVIE_PATH, MOVIE_DIR);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.MOVIE_PATH + "/#", MOVIE_ITEM);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.REVIEW_PATH, REVIEW_DIR);
        sUriMatcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.TRAILER_PATH, TRAILER_DIR);
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
                retCursor = db.query(MovieContract.MovieEntry.MOVIE_TABLE_NAME,
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
                retCursor = db.query(MovieContract.MovieEntry.MOVIE_TABLE_NAME,
                        projection,
                        "id = ?",
                        new String[] {movie_id},
                        null,
                        null,
                        sortOrder
                        );
                break;
            }

            case REVIEW_DIR: {
                retCursor = db.query(MovieContract.MovieEntry.REVIEW_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case TRAILER_DIR: {
                retCursor = db.query(MovieContract.MovieEntry.TRAILER_TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
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
                //long _id = db.insert(MovieContract.MovieEntry.MOVIE_TABLE_NAME, null, contentValues);
                //long _id = db.insertWithOnConflict(MovieContract.MovieEntry.MOVIE_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

                long _id;
                Cursor cursor = db.query(
                        MovieContract.MovieEntry.MOVIE_TABLE_NAME,
                        null,
                        MovieContract.MovieEntry.MOVIE_ID + " = " + contentValues.getAsString(MovieContract.MovieEntry.MOVIE_ID),
                        null,
                        null,
                        null,
                        null
                );

                if (cursor.getCount() == 0) {
                    _id = db.insert(MovieContract.MovieEntry.MOVIE_TABLE_NAME, null, contentValues);
                    Log.d(TAG, "bulkInsert: inserted: " + contentValues.toString());
                } else {
                    _id = -1;
                }


                if (_id > 0) {
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                    Log.d(TAG, "insert: " + returnUri.toString() + " , id = " + _id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }

            case REVIEW_DIR: {
                long _id;
                Cursor cursor = db.query(
                        MovieContract.MovieEntry.REVIEW_TABLE_NAME,
                        null,
                        MovieContract.MovieEntry.REVIEW_ID + " = " + contentValues.getAsString(MovieContract.MovieEntry.REVIEW_ID),
                        null,
                        null,
                        null,
                        null
                );

                if (cursor.getCount() == 0) {
                    _id = db.insert(MovieContract.MovieEntry.REVIEW_TABLE_NAME, null, contentValues);
                    Log.d(TAG, "bulkInsert: inserted: " + contentValues.toString());
                } else {
                    _id = -1;
                }
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
        int updatedRows = 0;
        final SQLiteDatabase db = mMovieDBHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE_DIR: {
                updatedRows = db.update(MovieContract.MovieEntry.MOVIE_TABLE_NAME, contentValues, selection, selectionArgs);
                Log.d(TAG, "update: updated rows: " + updatedRows);
                break;
            }
            default: {
            }
        }

        return updatedRows;
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

                        long _id;
                        Log.d(TAG, "bulkInsert: content value movie ID: " + value.getAsString(MovieContract.MovieEntry.MOVIE_ID));
                        //long _id = db.insert(MovieContract.MovieEntry.MOVIE_TABLE_NAME, null, value);
                        //_id = db.insertWithOnConflict(MovieContract.MovieEntry.MOVIE_TABLE_NAME, MovieContract.MovieEntry.MOVIE_ID, value, SQLiteDatabase.CONFLICT_REPLACE);


                        Cursor cursor = db.query(
                                MovieContract.MovieEntry.MOVIE_TABLE_NAME,
                                null,
                                MovieContract.MovieEntry.MOVIE_ID + " = " + value.getAsString(MovieContract.MovieEntry.MOVIE_ID),
                                null,
                                null,
                                null,
                                null
                        );

                        if (cursor.getCount() == 0) {
                            _id = db.insert(MovieContract.MovieEntry.MOVIE_TABLE_NAME, null, value);
                            Log.d(TAG, "bulkInsert: inserted: " + value.toString());
                        } else {
                            _id = -1;
                        }


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

            case REVIEW_DIR: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id;
                        Log.d(TAG, "bulkInsert: content value movie ID: " + value.getAsString(MovieContract.MovieEntry.MOVIE_ID));


                        Cursor cursor = db.query(
                                MovieContract.MovieEntry.REVIEW_TABLE_NAME,
                                null,
                                MovieContract.MovieEntry.REVIEW_ID + " = " + "\"" + value.getAsString(MovieContract.MovieEntry.REVIEW_ID) + "\"",
                                null,
                                null,
                                null,
                                null
                        );

                        if (cursor.getCount() == 0) {
                            _id = db.insert(MovieContract.MovieEntry.REVIEW_TABLE_NAME, null, value);
                            Log.d(TAG, "bulkInsert: inserted: " + value.toString());
                        } else {
                            _id = -1;
                        }


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

            case TRAILER_DIR: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id;
                        Log.d(TAG, "bulkInsert: content value movie ID: " + value.getAsString(MovieContract.MovieEntry.MOVIE_ID));
                        //long _id = db.insert(MovieContract.MovieEntry.MOVIE_TABLE_NAME, null, value);
                        //_id = db.insertWithOnConflict(MovieContract.MovieEntry.MOVIE_TABLE_NAME, MovieContract.MovieEntry.MOVIE_ID, value, SQLiteDatabase.CONFLICT_REPLACE);


                        Cursor cursor = db.query(
                                MovieContract.MovieEntry.TRAILER_TABLE_NAME,
                                null,
                                MovieContract.MovieEntry.TRAILER_KEY + " = " + "\"" + value.getAsString(MovieContract.MovieEntry.TRAILER_KEY) + "\"",
                                null,
                                null,
                                null,
                                null
                        );

                        if (cursor.getCount() == 0) {
                            _id = db.insert(MovieContract.MovieEntry.TRAILER_TABLE_NAME, null, value);
                            Log.d(TAG, "bulkInsert: inserted: " + value.toString());
                        } else {
                            _id = -1;
                        }


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
                return MovieContract.MovieEntry.MOVIE_CONTENT_TYPE;
            case MOVIE_ITEM:
                return MovieContract.MovieEntry.MOVIE_CONTENT_ITEM_TYPE;
            case TRAILER_DIR:
                return MovieContract.MovieEntry.TRAILER_CONTENT_TYPE;
            case REVIEW_DIR:
                return MovieContract.MovieEntry.REVIEW_CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri" + uri);
        }
    }

}
