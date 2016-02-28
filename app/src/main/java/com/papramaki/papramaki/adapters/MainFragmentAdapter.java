package com.papramaki.papramaki.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.papramaki.papramaki.ui.AnotherFragment;
import com.papramaki.papramaki.ui.MainActivityFragment;

/**
 * This is the file that handles all the fragments we will
 * display in the main arch of the app.
 */
public class MainFragmentAdapter extends FragmentPagerAdapter {

    protected Context mContext;

    /**
     * Simple constructor to be used in the onCreate method in MainActivity
     * @param context       the context to the activity
     * @param fm            the fragment manager instance
     */
    public MainFragmentAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    /**
     * Makes sure to display the correct fragment on the screen.
     * @param position      the position of the fragment (index swiping left or right)
     * @return              the correct fragment
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MainActivityFragment();
            case 1:
                return new AnotherFragment();
        }

        return null;
    }

    /**
     * Returns the number of fragments in this adapter.
     * @return              number of fragments
     */
    @Override
    public int getCount() {
        return 2;
    }

    /**
     * Returns an appropriate title for each fragment.
     * We will change this method later once we actually have titles
     * @param position      the position of the fragment
     * @return              the title
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

}
