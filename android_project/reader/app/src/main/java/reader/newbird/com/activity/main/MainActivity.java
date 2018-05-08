package reader.newbird.com.activity.main;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import reader.newbird.com.R;
import reader.newbird.com.activity.bookshelf.ShelfFragment;
import reader.newbird.com.activity.personal.PersonalFragment;
import reader.newbird.com.activity.recommand.RecommendFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private TopFragmentAdapter mAdapter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mViewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    mViewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_notifications:
                    mViewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);g
        getWindowManager().getDefaultDisplay().getSize(new Point());
        setContentView(R.layout.activity_main);

        mViewPager = (ViewPager) findViewById(R.id.main_view_pager);
        mAdapter = new TopFragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        createFragment();

    }

    private void createFragment() {
        mAdapter.addFragment(RecommendFragment.newInstance());
        mAdapter.addFragment(ShelfFragment.newInstance());
        mAdapter.addFragment(PersonalFragment.newInstance());
        mAdapter.notifyDataSetChanged();
    }

}
