package reader.newbird.com.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TopFragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> mList= new ArrayList<Fragment>();

    public TopFragmentAdapter(FragmentManager fm) {
        super(fm);
    }


    public void addFragment(Fragment fragment) {
        mList.add(fragment);
    }
    @Override
    public Fragment getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getCount() {
        return mList.size();
    }
}
