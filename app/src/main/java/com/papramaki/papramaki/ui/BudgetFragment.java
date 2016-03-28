package com.papramaki.papramaki.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.DatabaseUtils;
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
import com.papramaki.papramaki.database.BudgetContract;
import com.papramaki.papramaki.database.DatabaseHelper;
import com.papramaki.papramaki.models.Budget;
import com.papramaki.papramaki.utils.LocalData;

/**
 * A placeholder fragment containing a simple view.
 */
public class BudgetFragment extends Fragment {

    private static final String TAG = BudgetFragment.class.getSimpleName();

    protected TextView mTextView;
    protected TextView mBudgetDisplay;
    protected TextView mBalanceDisplay;
    protected EditText mBudget;
    protected Button mButton;
    protected FloatingActionButton mFAB;
    protected DatabaseHelper mDbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_budget, container, false);

        mTextView = (TextView)rootView.findViewById(R.id.another_fragment_text);
        mBudgetDisplay = (TextView) rootView.findViewById(R.id.budgetDisplay);
        mBalanceDisplay = (TextView) rootView.findViewById(R.id.balanceDisplay);
        mButton = (Button)rootView.findViewById(R.id.button2);
        mFAB = (FloatingActionButton)rootView.findViewById(R.id.FAB);
        mBudget = (EditText) rootView.findViewById(R.id.budget);

        mDbHelper = new DatabaseHelper(getContext());
        if(DatabaseUtils.queryNumEntries(mDbHelper.getReadableDatabase(), BudgetContract.Budget.TABLE_NAME) > 0 ) {
            mBudgetDisplay.setText("$ " + Double.toString(mDbHelper.viewLatestBudget()));
            mBalanceDisplay.setText("$ " + Double.toString(mDbHelper.viewLatestBalance()));
            if (Double.valueOf(mDbHelper.viewLatestBalance()) < 0) {
                System.out.print("1");
                mBalanceDisplay.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            } else {
                mBalanceDisplay.setTextColor(Color.parseColor("black"));
            }
        }
        else{
            mBudgetDisplay.setText("$ 0.0");
            mBalanceDisplay.setText("$ 0.0");
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
                String strAmount = mBudget.getText().toString();
                if(!strAmount.equals("")) {
                    Double amount = Double.valueOf(strAmount);

                    Budget budget = new Budget(amount);
                    budget.setBalance(amount);
                    mDbHelper.addBudget(budget);
                    mBudgetDisplay.setText("$ " + Double.toString(mDbHelper.viewLatestBudget()));
                    mBalanceDisplay.setText("$ " + Double.toString(mDbHelper.viewLatestBalance()));

                }
                //PREVIOUS VERSION
                //mBudgetDisplay.setText("$ " + LocalData.budget.toString());

                if (Double.valueOf(mDbHelper.viewLatestBalance()) < 0) {
                    mBalanceDisplay.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                } else {
                    mBalanceDisplay.setTextColor(Color.parseColor("black"));
                }
                mBudget.getText().clear();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(DatabaseUtils.queryNumEntries(mDbHelper.getReadableDatabase(), BudgetContract.Budget.TABLE_NAME) > 0 ) {
            mBudgetDisplay.setText("$ " + Double.toString(mDbHelper.viewLatestBudget()));
            mBalanceDisplay.setText("$ " + Double.toString(mDbHelper.viewLatestBalance()));
            if (Double.valueOf(mDbHelper.viewLatestBalance()) < 0) {
                mBalanceDisplay.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            } else {
                mBalanceDisplay.setTextColor(Color.parseColor("black"));
            }
        }

    }

}
