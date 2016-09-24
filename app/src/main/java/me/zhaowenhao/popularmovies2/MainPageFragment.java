package me.zhaowenhao.popularmovies2;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;


/**
 * Created by zhaowenhao on 16/9/10.
 */

public class MainPageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = MainPageFragment.class.getSimpleName();

    GridView mGridView;
    MovieAdapter mMovieAdapter;

    private static final int MOVIE_LOADER = 1;

    private int mSortOrderKey = 1; //1: by popularity, 2: by rating

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.MOVIE_ID,
            MovieContract.MovieEntry.MOVIE_TITLE,
            MovieContract.MovieEntry.MOVIE_ORIGINAL_TITLE,
            MovieContract.MovieEntry.MOVIE_POSTER_PATH,
            MovieContract.MovieEntry.MOVIE_OVERVIEW,
            MovieContract.MovieEntry.MOVIE_POPULARITY,
            MovieContract.MovieEntry.MOVIE_RATING,
            MovieContract.MovieEntry.MOVIE_RELEASE_DATE,
            //MovieContract.MovieEntry.MOVIE_TRAILER_PATH
    };

    static final int COLUMN_MOVIE_DB_ID = 0;
    static final int COLUMN_MOVIE_ORIGINAL_ID = 1;
    static final int COLUMN_MOVIE_TITLE = 2;
    static final int COLUMN_MOVIE_ORIGINAL_TITLE = 3;
    static final int COLUMN_MOVIE_POSTER_PATH = 4;
    static final int COLUMN_MOVIE_OVERVIEW = 5;
    static final int COLUMN_MOVIE_POPULARITY = 6;
    static final int COLUMN_MOVIE_RATING = 7;
    static final int COLUMN_MOVIE_RELEASE_DATE = 8;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "start updating movie info...");
        updateMovie();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);

        View v = inflater.inflate(R.layout.fragment_main_page, container, false);
        mGridView = (GridView) v.findViewById(R.id.gridView);
        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);
        setGridViewGeometry();
        mGridView.setAdapter(mMovieAdapter);


        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: position " + position + " clicked");
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    String movieID = cursor.getString(COLUMN_MOVIE_ORIGINAL_ID);
                    Log.d(TAG, "onItemClick: movie name at " + position + " is " + cursor.getString(COLUMN_MOVIE_TITLE) + ", ID is: " + movieID);
                    Intent intent = new Intent(getActivity(), DetailPageActivity.class);
                    intent.putExtra(DetailPageFragment.MOVIE_ID, movieID);
                    startActivity(intent);
                }
            }
        });

        return v;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main_page, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_refresh:
            {
                Log.d(TAG, "onOptionsItemSelected: refresh!");
                updateMovie();
                break;
            }
            case R.id.menu_most_popular:
            {
                mSortOrderKey = 1;
                getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
                Log.d(TAG, "onOptionsItemSelected: sort by popularity");
                break;
            }
            case R.id.menu_top_rated:
            {
                mSortOrderKey = 2;
                getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
                Log.d(TAG, "onOptionsItemSelected: sort by rating");
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void updateMovie(){
        Intent intent = new Intent(getActivity(), MovieService.class);
        getActivity().startService(intent);
    }

    private void setGridViewGeometry() {
        final int POSTER_WIDHT_PX = 185; // hard code for now
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        float xdpi = displayMetrics.xdpi;
        float ydpi = displayMetrics.ydpi;
        Log.d(TAG, "setGridViewGeometry: screenWidth = " + screenWidth + ", screenHeight = " + screenHeight);
        Log.d(TAG, "setGridViewGeometry: xdpi = " + xdpi + ", ydpi = " + ydpi);
        mGridView.setNumColumns(screenWidth / POSTER_WIDHT_PX + 1);

    }

    /*
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        Log.d(TAG, "onActivityCreated: movie loader inited");
        super.onActivityCreated(savedInstanceState);
    }
    */


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = "";
        switch (mSortOrderKey) {
            case 1: {
                sortOrder = MovieContract.MovieEntry.MOVIE_POPULARITY + " DESC";
                break;
            }
            case 2: {
                sortOrder = MovieContract.MovieEntry.MOVIE_RATING + " DESC";
                break;
            }
            default:
                break;
        }

        Log.d(TAG, "onCreateLoader: sort order: " + sortOrder);

        return new CursorLoader(
                getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "onLoadFinished: started!");
        Log.d(TAG, "onLoadFinished: data count" + data.getCount());
        mMovieAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: started!");
        mMovieAdapter.swapCursor(null);
    }

}
