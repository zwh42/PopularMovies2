package me.zhaowenhao.popularmovies2;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by zhaowenhao on 16/9/12.
 */
public class DetailPageFragment extends Fragment {
    private static final String TAG = DetailPageFragment.class.getSimpleName();
    private static final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private static final String MOVIE_TRAILER_BASE_URL = "https://www.youtube.com"; // eg. https://www.youtube.com/watch?v=SUXWAEX2jlg;

    public static final String MOVIE_ID = "MOVIE_ID";

    private String mMovieID;
    private Cursor mCursor;
    private String posterUrl;

    private TextView mTitle;
    private ImageView mPoster;
    private TextView mReleaseDate;
    private TextView mRating;
    private TextView mPopularity;
    private TextView mOverview;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovieID = getActivity().getIntent().getStringExtra(MOVIE_ID);
        Log.d(TAG, "onCreate: passed movie id is: " + mMovieID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detail_page, parent, false);
        mTitle = (TextView) v.findViewById(R.id.title);
        mPoster = (ImageView) v.findViewById(R.id.poster);
        mReleaseDate = (TextView) v.findViewById(R.id.release_date);
        mRating = (TextView) v.findViewById(R.id.rating);
        mPopularity = (TextView) v.findViewById(R.id.popularity);
        mOverview = (TextView) v.findViewById(R.id.overview);

        mCursor = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.MOVIE_ID + " = " + mMovieID,
                null,
                null
        );
        mCursor.moveToFirst();
        posterUrl = MOVIE_POSTER_BASE_URL + mCursor.getString(MainPageFragment.COLUMN_MOVIE_POSTER_PATH);
        Log.d(TAG, "onCreate: poster url: " + posterUrl);
        Picasso.with(getActivity().getApplicationContext()).load(posterUrl).into(mPoster);

        mTitle.setText(mCursor.getString(MainPageFragment.COLUMN_MOVIE_TITLE));
        mReleaseDate.setText(mCursor.getString(MainPageFragment.COLUMN_MOVIE_RELEASE_DATE));
        mRating.setText(mCursor.getString(MainPageFragment.COLUMN_MOVIE_RATING));
        mPopularity.setText(mCursor.getString(MainPageFragment.COLUMN_MOVIE_POPULARITY));
        mOverview.setText(mCursor.getString(MainPageFragment.COLUMN_MOVIE_OVERVIEW));

        
        mCursor.close();


        return v;
    }


}
