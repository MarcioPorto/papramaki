package com.papramaki.papramaki.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.papramaki.papramaki.R;
import com.papramaki.papramaki.utils.LocalData;

/**
 * A placeholder fragment containing a simple view.
 */
public class BudgetFragment extends Fragment {

    private static final String TAG = BudgetFragment.class.getSimpleName();

    protected TextView mTextView;
    protected TextView mBudgetDisplay;
    protected EditText mBudget;
    protected Button mButton;
    protected FloatingActionButton mFAB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_budget, container, false);

        mTextView = (TextView)rootView.findViewById(R.id.another_fragment_text);
        mBudgetDisplay = (TextView) rootView.findViewById(R.id.budgetDisplay);
        mButton = (Button)rootView.findViewById(R.id.button2);
        mFAB = (FloatingActionButton)rootView.findViewById(R.id.FAB);
        mBudget = (EditText) rootView.findViewById(R.id.budget);

        mBudgetDisplay.setText("$ " + LocalData.budget.toString());
        if (LocalData.budget.getBudget() < 0.0) {
            mBudgetDisplay.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        } else {
            mBudgetDisplay.setTextColor(Color.parseColor("black"));
        }

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DeductionActivity.class);
                startActivity(intent);
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double amount = Double.valueOf(mBudget.getText().toString());
                LocalData.budget.setBudget(amount);
                mBudgetDisplay.setText("$ " + LocalData.budget.toString());
                if (LocalData.budget.getBudget() < 0.0) {
                    mBudgetDisplay.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                } else {
                    mBudgetDisplay.setTextColor(Color.parseColor("black"));
                }
                mBudget.getText().clear();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mBudgetDisplay.setText("$ " + LocalData.budget.toString());
        if (LocalData.budget.getBudget() < 0.0) {
            mBudgetDisplay.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        } else {
            mBudgetDisplay.setTextColor(Color.parseColor("black"));
        }
    }

}
