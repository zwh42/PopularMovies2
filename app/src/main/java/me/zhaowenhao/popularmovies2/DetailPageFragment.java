package me.zhaowenhao.popularmovies2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;

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
    private ToggleButton mFavoriteButton;

    private Button mReviewButton;
    private Button mTrailerButton;

    private boolean isMarkedFavorite;

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

        mFavoriteButton = (ToggleButton) v.findViewById(R.id.favorite_button);

        mReviewButton = (Button) v.findViewById(R.id.review_button);
        mTrailerButton = (Button) v.findViewById(R.id.trailer_button);

        DecimalFormat decimalFormat = new DecimalFormat(".00");

        mCursor = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.MOVIE_CONTENT_URI,
                null,
                MovieContract.MovieEntry.MOVIE_ID + " = " + mMovieID,
                null,
                null
        );

        if (mCursor.getCount() == 0) {
            mCursor.close();
            return v;
        }

        mCursor.moveToFirst();
        posterUrl = MOVIE_POSTER_BASE_URL + mCursor.getString(MainPageFragment.COLUMN_MOVIE_POSTER_PATH);
        Log.d(TAG, "onCreate: poster url: " + posterUrl);
        Picasso.with(getActivity().getApplicationContext()).load(posterUrl).into(mPoster);

        mTitle.setText(mCursor.getString(MainPageFragment.COLUMN_MOVIE_TITLE));
        mReleaseDate.setText(mCursor.getString(MainPageFragment.COLUMN_MOVIE_RELEASE_DATE));
        mRating.setText(getString(R.string.rating) + " "
                + decimalFormat.format(Double.parseDouble(mCursor.getString(MainPageFragment.COLUMN_MOVIE_RATING))) + "/10");
        mPopularity.setText(getString(R.string.popularity) + " "
                + decimalFormat.format(Double.parseDouble(mCursor.getString(MainPageFragment.COLUMN_MOVIE_POPULARITY))));
        mOverview.setText(mCursor.getString(MainPageFragment.COLUMN_MOVIE_OVERVIEW));

        isMarkedFavorite = mCursor.getInt(MainPageFragment.COLUMN_MOVIE_FAVORITE) == 1;
        mFavoriteButton.setChecked(isMarkedFavorite);

        mFavoriteButton.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    String sqlSelection = null;
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            mFavoriteButton.setTextColor(getResources().getColor(R.color.colorMarkAsFavorite));
                        } else {
                            mFavoriteButton.setTextColor(getResources().getColor(R.color.colorMarkAsNotFavorite));
                        }

                        if (isChecked != isMarkedFavorite) {
                            ContentValues updateFavoriteStatus = new ContentValues();

                            if (isChecked) {
                                updateFavoriteStatus.put(MovieContract.MovieEntry.MOVIE_FAVORITE, 1);
                            } else {
                                updateFavoriteStatus.put(MovieContract.MovieEntry.MOVIE_FAVORITE, 0);
                            }

                            getActivity().getContentResolver().update(
                                    MovieContract.MovieEntry.MOVIE_CONTENT_URI,
                                    updateFavoriteStatus,
                                    MovieContract.MovieEntry.MOVIE_ID + " = " + mMovieID,
                                    null
                            );
                        }
                    }
                }
        );

        mCursor.close();

        mReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ReviewPageActivity.class);
                i.putExtra(ReviewPageFragment.MOVIE_ID, mMovieID);
                startActivity(i);
            }
        });

        mTrailerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TrailerPageActivity.class);
                i.putExtra(TrailerPageFragment.MOVIE_ID, mMovieID);
                startActivity(i);
            }
        });

        return v;
    }


}
