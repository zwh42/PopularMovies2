package me.zhaowenhao.popularmovies2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhaowenhao on 16/9/12.
 */
public class MovieDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private  static final String DATABASE_NAME = "movie.db";

    public MovieDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_MOVIE = "CREATE TABLE " + MovieContract.MovieEntry.MOVIE_TABLE_NAME + " ("
                + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY, "
                + MovieContract.MovieEntry.MOVIE_ID + " INTEGER UNIQUE NOT NULL, "  // UNIQUE to make db.insertWithOnConflict() work
                + MovieContract.MovieEntry.MOVIE_TITLE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.MOVIE_ORIGINAL_TITLE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.MOVIE_POSTER_PATH + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.MOVIE_OVERVIEW + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.MOVIE_POPULARITY + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.MOVIE_RATING + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.MOVIE_RELEASE_DATE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.MOVIE_TRAILER_PATH + " TEXT, "
                + MovieContract.MovieEntry.MOVIE_FAVORITE + " INTEGER "
                + " ); " ;

        final String SQL_CREATE_REVIEW = "CREATE TABLE " + MovieContract.MovieEntry.REVIEW_TABLE_NAME + " ("
                + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY, "
                + MovieContract.MovieEntry.MOVIE_ID + " INTEGER NOT NULL, "  // UNIQUE to make db.insertWithOnConflict() work
                + MovieContract.MovieEntry.REVIEW_ID + " INTEGER, "
                + MovieContract.MovieEntry.REVIEW_AUTHOR + " TEXT, "
                + MovieContract.MovieEntry.REVIEW_CONTENT + " TEXT, "
                + MovieContract.MovieEntry.REVIEW_URL + " TEXT "
                + " ); ";

        final String SQL_CREATE_TRAILER = "CREATE TABLE " + MovieContract.MovieEntry.TRAILER_TABLE_NAME + " ("
                + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY, "
                + MovieContract.MovieEntry.MOVIE_ID + " INTEGER NOT NULL, "  // UNIQUE to make db.insertWithOnConflict() work
                + MovieContract.MovieEntry.TRAILER_ID + " TEXT, "
                + MovieContract.MovieEntry.TRAILER_KEY + " TEXT, "
                + MovieContract.MovieEntry.TRAILER_NAME + " TEXT "
                + " ); ";


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion){
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.MOVIE_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TRAILER_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.REVIEW_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
