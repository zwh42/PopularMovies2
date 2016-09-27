package me.zhaowenhao.popularmovies2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by zhaowenhao on 16/9/28.
 */

public class TrailerPageActivity extends AppCompatActivity {
    private static final String TAG = TrailerPageActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailer);

        TrailerPageFragment trailerPageFragment = new TrailerPageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.trailer_fragment_container, trailerPageFragment).commit();


    }
}