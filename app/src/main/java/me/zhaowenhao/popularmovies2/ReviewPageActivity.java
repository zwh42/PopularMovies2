package me.zhaowenhao.popularmovies2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by zhaowenhao on 16/9/26.
 */

public class ReviewPageActivity extends AppCompatActivity {
    private static final String TAG = ReviewPageActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ReviewPageFragment reviewPageFragment = new ReviewPageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.review_fragment_container, reviewPageFragment).commit();


    }
}
