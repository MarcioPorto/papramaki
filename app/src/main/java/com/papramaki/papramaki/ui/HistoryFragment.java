package com.papramaki.papramaki.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.papramaki.papramaki.R;
import com.papramaki.papramaki.models.Expenditure;
import com.papramaki.papramaki.models.History;

import java.util.List;

public class HistoryFragment extends ListFragment {

    private static final String TAG = HistoryFragment.class.getSimpleName();

    protected List<Expenditure> expenditureHistory;
    protected String[] expenditureArray;
    protected FloatingActionButton mFAB;

    public HistoryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);


        expenditureHistory = MainActivity.mHistory.getExpenditures();
        expenditureArray = new String[expenditureHistory.size()];
        for (int i=0; i < expenditureHistory.size(); i++) {
            String expenditureString = expenditureHistory.get(i).toString();
            expenditureArray[i] = expenditureString;
        }


        ArrayAdapter<String> histAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, expenditureArray);
        ListView myList=(ListView) rootView.findViewById(R.id.list);
        myList.setAdapter(histAdapter);

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

}
