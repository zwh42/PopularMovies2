package me.zhaowenhao.popularmovies2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainPageActivity extends AppCompatActivity {

    private static final String TAG = MainPageActivity.class.getSimpleName();

    private boolean isTwoPane = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.movie_detail_container) != null){
            isTwoPane = true;
            DetailPageFragment detail_page_fragment = new DetailPageFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.movie_detail_container, detail_page_fragment).commit();
        }else{
            isTwoPane = false;
        }

        MainPageFragment main_page_fragment = new MainPageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_page_fragment_container, main_page_fragment).commit();

    }



}
