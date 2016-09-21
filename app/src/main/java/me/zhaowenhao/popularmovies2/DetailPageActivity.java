package me.zhaowenhao.popularmovies2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by zhaowenhao on 16/9/12.
 */
public class DetailPageActivity extends AppCompatActivity {
    private static final String TAG = DetailPageActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        DetailPageFragment detailPageFragment = new DetailPageFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.detail_fragment_container, detailPageFragment).commit();

    }


}
