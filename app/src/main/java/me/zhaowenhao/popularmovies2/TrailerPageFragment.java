package me.zhaowenhao.popularmovies2;

/**
 * Created by zhaowenhao on 16/9/26.
 */

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


public class TrailerPageFragment extends Fragment {
    private static final String TAG = TrailerPageFragment.class.getSimpleName();
    private ListView mListView;
    private TrailerAdapter mTrailerAdapter;
    private String mMovieID;

    public static final String MOVIE_ID = "MOVIE_ID";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_trailer_page, container, false);
        mListView = (ListView) v.findViewById(R.id.trailer_list_view);
        mMovieID = getActivity().getIntent().getStringExtra(MOVIE_ID);
        Cursor trailerCursor = getActivity().getContentResolver()
                .query(MovieContract.MovieEntry.TRAILER_CONTENT_URI,
                        null,
                        MovieContract.MovieEntry.MOVIE_ID + " = " + mMovieID,
                        null,
                        null);
        Log.d(TAG, "onCreateView: trailerCursor count = " + trailerCursor.getCount());
        mTrailerAdapter = new TrailerAdapter(getActivity(), trailerCursor, 0);
        mListView.setAdapter(mTrailerAdapter);


        return v;
    }


    class TrailerAdapter extends CursorAdapter {
        Context mContext;

        public TrailerAdapter(Context context, Cursor cursor, int flags) {
            super(context, cursor, flags);
            mContext = context;
            Log.d(TAG, "TrailerAdapter: inited");
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = LayoutInflater.from(context).inflate(R.layout.trailer_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            viewHolder.trailerTextView.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.TRAILER_NAME))));


        }

    }

    static class ViewHolder {
        public final TextView trailerTextView;


        public ViewHolder(View view) {
            trailerTextView = (TextView) view.findViewById(R.id.trailer_name);

        }
    }
}
