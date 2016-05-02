package com.papramaki.papramaki.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.papramaki.papramaki.R;
import com.papramaki.papramaki.adapters.HistoryListAdapter;
import com.papramaki.papramaki.database.DatabaseHelper;
import com.papramaki.papramaki.models.Expenditure;
import com.papramaki.papramaki.utils.LocalData;

import java.util.List;

public class HistoryFragment extends ListFragment {

    public static final String TAG = HistoryFragment.class.getSimpleName();

    protected static List<Expenditure> expenditureHistory;
    protected FloatingActionButton mFAB;
    protected DatabaseHelper mDbHelper;
    public static HistoryFragment mCurrentHistoryFragmentInstance;

    public HistoryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        mCurrentHistoryFragmentInstance = this;
        updateLayout();

        mFAB = (FloatingActionButton)rootView.findViewById(R.id.FAB);
        mDbHelper = new DatabaseHelper(getContext());
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DeductionActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.retrieveAllData();
    }

    /**
     * Updates the layout based on the data returned by the getExpendituresRequest()
     * method in MainActivity.
     */
    public static void updateLayout() {
        expenditureHistory = LocalData.history.getExpenditures();
        ArrayAdapter<Expenditure> histAdapter = new HistoryListAdapter(MainActivity.getAppContext(), expenditureHistory);
        new android.app.ListFragment().setListAdapter(histAdapter);
        mCurrentHistoryFragmentInstance.setListAdapter(histAdapter);
        histAdapter.notifyDataSetChanged();
    }

}
