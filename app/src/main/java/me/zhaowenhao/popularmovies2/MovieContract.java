package me.zhaowenhao.popularmovies2;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by zhaowenhao on 16/9/12.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "me.zhaowenhao.popularmovies2.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String MOVIE_PATH = "movie";
    public static final String REVIEW_PATH = "review";
    public static final String TRAILER_PATH = "trailer";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri MOVIE_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MOVIE_PATH).build();
        public static final String MOVIE_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;
        public static final String MOVIE_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;


        public static final Uri TRAILER_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TRAILER_PATH).build();
        public static final String TRAILER_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TRAILER_PATH;
        public static final String TRAILER_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TRAILER_PATH;

        public static final Uri REVIEW_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(REVIEW_PATH).build();
        public static final String REVIEW_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + REVIEW_PATH;
        public static final String REVIEW_CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + REVIEW_PATH;

        public static final String MOVIE_TABLE_NAME = "movie";
        public static final String MOVIE_ID = "movie_id";
        public static final String MOVIE_TITLE = "title";
        public static final String MOVIE_ORIGINAL_TITLE = "original_title";
        public static final String MOVIE_POSTER_PATH = "poster_path";
        public static final String MOVIE_OVERVIEW = "overview";
        public static final String MOVIE_POPULARITY = "popularity";
        public static final String MOVIE_RATING = "vote_average";
        public static final String MOVIE_RELEASE_DATE = "release_date";
        public static final String MOVIE_TRAILER_PATH = "trailer_path";
        public static final String MOVIE_FAVORITE = "mark_as_favorite";


        public static final String REVIEW_TABLE_NAME = "review";
        public static final String REVIEW_AUTHOR = "review_author";
        public static final String REVIEW_CONTENT = "review_content";
        public static final String REVIEW_URL = "review_url";
        public static final String REVIEW_ID = "review_id";

        public static final String TRAILER_TABLE_NAME = "trailer";
        public static final String TRAILER_NAME = "trailer_name";
        public static final String TRAILER_KEY = "trailer_key";
        public static final String TRAILER_ID = "trailer_id";



        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(MOVIE_CONTENT_URI, id);
        }

    }

}
