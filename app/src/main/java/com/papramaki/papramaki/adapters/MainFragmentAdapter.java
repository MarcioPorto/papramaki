package com.papramaki.papramaki.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.papramaki.papramaki.ui.AnalysisFragment;
import com.papramaki.papramaki.ui.BudgetFragment;
import com.papramaki.papramaki.ui.HistoryFragment;

/**
 * This is the file that handles all the fragments we
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
     * @return              the fragment to be displayed
     */
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new BudgetFragment();
            case 1:
                return new AnalysisFragment();
            case 2:
                return new HistoryFragment();
        }

        return null;
    }

    /**
     * Returns the number of fragments in this adapter.
     * @return              number of fragments
     */
    @Override
    public int getCount() {
        return 3;
    }

    /**
     * Returns an appropriate title for each fragment.
     * @param position      the position of the fragment
     * @return              the title
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

}
