package me.zhaowenhao.popularmovies2;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by zhaowenhao on 16/9/19.
 */
public class MovieAdapter extends CursorAdapter {
    private static final String TAG = MovieAdapter.class.getSimpleName();

    private static final String MOVIE_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private Context mContext;

    public MovieAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        mContext = context;
        Log.d(TAG, "MovieAdapter: inited!");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.movie_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        Log.d(TAG, "newView: called!");
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d(TAG, "bindView: started");
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String posterPath = MOVIE_POSTER_BASE_URL + cursor.getString(MainPageFragment.COLUMN_MOVIE_POSTER_PATH);
        Log.d(TAG, "bindView: " + posterPath);
        Picasso.with(mContext).load(R.drawable.doge).into(viewHolder.moviePosterView);
    }
/*
    @Override
    public int getItemViewType(int position){
        return 0;
    }

    @Override
    public int getViewTypeCount(){
        return 1;
    }

    @Override
    public int getCount(){
        Log.d(TAG, "getCount: called!");
        return 10;
    }
*/

    public static class ViewHolder {
        public final ImageView moviePosterView;

        public ViewHolder(View view) {
            moviePosterView = (ImageView) view.findViewById(R.id.movie_poster);
        }
    }

}
