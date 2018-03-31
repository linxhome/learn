package reader.newbird.com.global.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import reader.newbird.com.global.R;
import reader.newbird.com.global.bookshelf.BookFragment;
import reader.newbird.com.global.personal.PersonalFragment;
import reader.newbird.com.global.recommand.RecommandFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;

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
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.main_view_pager);
        mAdapter  = new TopFragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        createFragment();
    }

    TopFragmentAdapter mAdapter;
    private void createFragment() {
        mAdapter.addFragment(new RecommandFragment());
        mAdapter.addFragment(new BookFragment());
        mAdapter.addFragment(new PersonalFragment());
        mAdapter.notifyDataSetChanged();
    }

}
