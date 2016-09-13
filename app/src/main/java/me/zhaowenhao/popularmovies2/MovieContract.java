package me.zhaowenhao.popularmovies2;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by zhaowenhao on 16/9/12.
 */
public class MovieContract {
    public static final String CONTENT_AUTHORITY = "me.zhaowenhao.popularmovies2.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String MOVIE_PATH = "movie";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(MOVIE_PATH).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +MOVIE_PATH;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + MOVIE_PATH;

        public static final String TABLE_NAME = "movie";

        static final String MOVIE_ID = "movie_id";
        static final String MOVIE_TITLE = "title";
        static final String MOVIE_ORIGINAL_TITLE = "original_title";
        static final String MOVIE_POSTER_PATH = "poster_path";
        static final String MOVIE_OVERVIEW = "overview";
        static final String MOVIE_POPULARITY = "popularity";
        static final String MOVIE_RATING = "vote_average";
        static final String MOVIE_RELEASE_DATE = "release_date";
        static final String MOVIE_TRAILER_PATH = "trailer_path";

    }

}
