package me.zhaowenhao.popularmovies2;

/**
 * Created by zhaowenhao on 16/9/26.
 */

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


public class ReviewPageFragment extends Fragment {
    private static final String TAG = ReviewPageFragment.class.getSimpleName();
    private ListView mListView;
    private ReviewAdapter mReviewAdapter;
    private String mMovieID;

    public static final String MOVIE_ID = "MOVIE_ID";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_review_page, container, false);
        mListView = (ListView) v.findViewById(R.id.review_list_view);
        mMovieID = getActivity().getIntent().getStringExtra(MOVIE_ID);
        Cursor reviewCursor = getActivity().getContentResolver()
                .query(MovieContract.MovieEntry.REVIEW_CONTENT_URI,
                        null,
                        MovieContract.MovieEntry.MOVIE_ID + " = " + mMovieID,
                        null,
                        null);
        Log.d(TAG, "onCreateView: review cursor count = " + reviewCursor.getCount());
        mReviewAdapter = new ReviewAdapter(getActivity(), reviewCursor, 0);
        mListView.setAdapter(mReviewAdapter);


        return v;
    }


    class ReviewAdapter extends CursorAdapter {
        Context mContext;

        public ReviewAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
            mContext = context;
            Log.d(TAG, "ReviewAdapter: inited");
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            viewHolder.reviewContentView.setText(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.REVIEW_CONTENT)));
            viewHolder.reviewAuthorView.setText("By: " + cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.REVIEW_AUTHOR)));
        }

    }

    static class ViewHolder {
        public final TextView reviewContentView;
        public final TextView reviewAuthorView;

        public ViewHolder(View view) {
            reviewContentView = (TextView) view.findViewById(R.id.review_content);
            reviewAuthorView = (TextView) view.findViewById(R.id.review_author);
        }
    }
}
