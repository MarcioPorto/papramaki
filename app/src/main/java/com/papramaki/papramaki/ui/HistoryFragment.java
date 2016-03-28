package com.papramaki.papramaki.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import com.papramaki.papramaki.R;
import com.papramaki.papramaki.adapters.HistoryListAdapter;
import com.papramaki.papramaki.models.Expenditure;
import com.papramaki.papramaki.utils.LocalData;

import java.util.Date;
import java.util.List;

public class HistoryFragment extends ListFragment {

    private static final String TAG = HistoryFragment.class.getSimpleName();

    protected List<Expenditure> expenditureHistory;
    protected FloatingActionButton mFAB;

    public HistoryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        expenditureHistory = LocalData.history.getExpenditures();
        expenditureHistory.add(new Expenditure(25.0, "des", new Date(1, 2, 3)));

//        ArrayAdapter<Expenditure> histAdapter = new HistoryListAdapter(getContext(), expenditureHistory);
//        ListView myList=(ListView) rootView.findViewById(android.R.id.list);
//        myList.setAdapter(histAdapter);

        mFAB = (FloatingActionButton)rootView.findViewById(R.id.FAB);
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

        expenditureHistory = LocalData.history.getExpenditures();

        ArrayAdapter<Expenditure> histAdapter = new HistoryListAdapter(getContext(), expenditureHistory);
        setListAdapter(histAdapter);

        expenditureHistory.add(new Expenditure(25.0, "ex", new Date(1, 2, 3)));

    }

}
