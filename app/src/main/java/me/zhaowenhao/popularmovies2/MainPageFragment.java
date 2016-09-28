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
import android.widget.ProgressBar;


/**
 * Created by zhaowenhao on 16/9/10.
 */

public class MainPageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = MainPageFragment.class.getSimpleName();


    GridView mGridView;
    MovieAdapter mMovieAdapter;

    private String sqlSelection = null;

    private static final int MOVIE_LOADER = 1;

    private int mSortOrderKey = 1; //1: by popularity, 2: by rating, 3: my favorite

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.MOVIE_TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.MOVIE_ID,
            MovieContract.MovieEntry.MOVIE_TITLE,
            MovieContract.MovieEntry.MOVIE_ORIGINAL_TITLE,
            MovieContract.MovieEntry.MOVIE_POSTER_PATH,
            MovieContract.MovieEntry.MOVIE_OVERVIEW,
            MovieContract.MovieEntry.MOVIE_POPULARITY,
            MovieContract.MovieEntry.MOVIE_RATING,
            MovieContract.MovieEntry.MOVIE_RELEASE_DATE,
            MovieContract.MovieEntry.MOVIE_TRAILER_PATH,
            MovieContract.MovieEntry.MOVIE_FAVORITE,
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
    static final int COLUMN_MOVIE_TRAILER_PATH = 9;
    static final int COLUMN_MOVIE_FAVORITE = 10;

    private ProgressBar mLoadingProgressBar;

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


        mLoadingProgressBar = (ProgressBar) v.findViewById(R.id.loading_progress_bar);
        //mLoadingProgressBar.setVisibility(View.VISIBLE);


        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: position " + position + " clicked");
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    String movieID = cursor.getString(COLUMN_MOVIE_ORIGINAL_ID);
                    Log.d(TAG, "onItemClick: movie name at " + position + " is " + cursor.getString(COLUMN_MOVIE_TITLE) + ", ID is: " + movieID);

                    if (MainPageActivity.isTwoPane == false) {
                        Intent intent = new Intent(getActivity(), DetailPageActivity.class);
                        intent.putExtra(DetailPageFragment.MOVIE_ID, movieID);
                        startActivity(intent);
                    } else {
                        Bundle args = new Bundle();
                        args.putString(DetailPageFragment.MOVIE_ID, movieID);
                        DetailPageFragment fragment = new DetailPageFragment();
                        fragment.setArguments(args);
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, fragment, DetailPageFragment.DETAIL_PAGE_FRAGMENT_TAG).commit();
                    }
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
                updateMovie();
                getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
                break;
            }
            case R.id.menu_top_rated:
            {
                mSortOrderKey = 2;
                updateMovie();
                getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
                break;
            }
            case R.id.menu_my_favorite: {
                mSortOrderKey = 3;
                updateMovie();
                getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void updateMovie(){
        Intent intent = new Intent(getActivity(), MovieService.class);
        intent.putExtra(MovieService.FETCH_TYPE, mSortOrderKey);
        getActivity().startService(intent);
    }

    private void setGridViewGeometry() {
        int POSTER_WIDTH_PX = 185; // hard code for now
        if (MainPageActivity.isTwoPane) {
            POSTER_WIDTH_PX = POSTER_WIDTH_PX * 2;  // for land and tablet
        }

        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        float xdpi = displayMetrics.xdpi;
        float ydpi = displayMetrics.ydpi;
        Log.d(TAG, "setGridViewGeometry: screenWidth = " + screenWidth + ", screenHeight = " + screenHeight);
        Log.d(TAG, "setGridViewGeometry: xdpi = " + xdpi + ", ydpi = " + ydpi);
        mGridView.setNumColumns(screenWidth / POSTER_WIDTH_PX + 1);

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
                sqlSelection = null;
                break;
            }
            case 2: {
                sortOrder = MovieContract.MovieEntry.MOVIE_RATING + " DESC";
                sqlSelection = null;
                break;
            }
            case 3: {
                sortOrder = MovieContract.MovieEntry.MOVIE_POPULARITY + " DESC";
                sqlSelection = MovieContract.MovieEntry.MOVIE_FAVORITE + " = " + "1";
                break;
            }
            default:
                break;
        }


        Log.d(TAG, "onCreateLoader: sort order: " + sortOrder);

        return new CursorLoader(
                getActivity(),
                MovieContract.MovieEntry.MOVIE_CONTENT_URI,
                MOVIE_COLUMNS,
                sqlSelection,
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
