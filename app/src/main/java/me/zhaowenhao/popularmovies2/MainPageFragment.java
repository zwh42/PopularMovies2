package me.zhaowenhao.popularmovies2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;



/**
 * Created by zhaowenhao on 16/9/10.
 */

public class MainPageFragment extends Fragment {
    private static final String TAG = MainPageFragment.class.getSimpleName();


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_main_page, container, false);
        //mMovieRecyclerView = (RecyclerView) v.findViewById(R.id.main_page_fragment_recycler_view);
        //mMovieRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

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
                break;
            }
            case R.id.menu_top_rated:
            {
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

}
