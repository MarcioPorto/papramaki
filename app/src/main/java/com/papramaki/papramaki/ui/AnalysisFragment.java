package com.papramaki.papramaki.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.papramaki.papramaki.R;
import com.papramaki.papramaki.database.DatabaseHelper;
import com.papramaki.papramaki.models.Category;
import com.papramaki.papramaki.utils.LocalData;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AnalysisFragment extends Fragment {

    private static final String TAG = AnalysisFragment.class.getSimpleName();

    protected FloatingActionButton mFAB;
    protected DatabaseHelper mDbHelper;

    public static TextView titleView;
    public static TextView moneySpentView;
    public static TextView balanceView;
    public static TextView budgetView;
    public static TextView durationView;

    public static List<Category> mCategories;
    public static PieGraph mPieGraph;

    public AnalysisFragment() {
    }

    /***
     * Helper method to properly format money values in the pop-up captions
     * @param amount - the amount to be formatted
     * @return - the formatted string
     */
    public String formatAmount(double amount) {
        DecimalFormat formatter = new DecimalFormat("$0.00");
        return formatter.format(amount);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_analysis, container, false);

        mPieGraph = (PieGraph) rootView.findViewById(R.id.piegraph);
        mPieGraph.setInnerCircleRatio(180);
        mDbHelper = new DatabaseHelper(getContext());
        titleView = (TextView) rootView.findViewById(R.id.analysisTitle);
        moneySpentView = (TextView) rootView.findViewById(R.id.moneySpent);
        balanceView = (TextView) rootView.findViewById(R.id.textView3);
        budgetView = (TextView) rootView.findViewById(R.id.ofBudget);
        durationView = (TextView) rootView.findViewById(R.id.duration);

        renderDefaultLayout();

        mPieGraph.setOnSliceClickedListener(new PieGraph.OnSliceClickedListener() {
            @Override
            public void onClick(int index) {
                try {
                    Toast.makeText(getActivity(),
                            "You spent " + formatAmount(mCategories.get(index).getSumCategory()) + " on " + String.valueOf(mCategories.get(index).getName()),
                            Toast.LENGTH_LONG)
                            .show();
                } catch (ArrayIndexOutOfBoundsException exception) {
                    Log.e(TAG, "Exception caught: ", exception);
                }

            }
        });

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

        MainActivity.retrieveAllData();
    }

    /**
     * Renders the default layout when the data is not available yet.
     */
    public static void renderDefaultLayout() {
        mPieGraph.removeSlices();
        DecimalFormat formatter = new DecimalFormat("$0.00");

        if (!LocalData.budget.isExpired()) {
            PieSlice slice;
            mCategories = retrieveCurrentCategories();

            for(Category category : mCategories){
                slice = new PieSlice();
                slice.setColor(Color.parseColor(category.getColor()));//mColors[color1Index]));
                slice.setSelectedColor(Color.parseColor(category.getColor()));//mColors[color2Index]));
                slice.setValue(Float.parseFloat(String.valueOf(category.getSumCategory())));//Float.parseFloat(String.valueOf(expensesMap.get(category))));
                slice.setTitle(category.getName());
                mPieGraph.addSlice(slice);
            }

            moneySpentView.setText("You've spent " + String.valueOf(formatter.format(LocalData.history.getExpenditureSum())));
            budgetView.setText(" of " + LocalData.budget.getFormattedBudget());

            if(LocalData.balance >= 0) {
                balanceView.setText("You have " + formatter.format(LocalData.balance) + " left in your budget.");
                balanceView.setTextColor(Color.BLACK);
            } else {
                balanceView.setText("You have exceeded your budget by " + formatter.format(-1 * LocalData.balance));
                balanceView.setTextColor(Color.RED);
            }
        } else {
            moneySpentView.setText("You've spent $0.00");
            budgetView.setText(" of $0.00");
            balanceView.setText("You have exceeded your budget by $0.00");
        }

        durationView.setText("(from ___ to ___)");

        titleView.setText("Your Spending Analysis");

    }

    /**
     * Updates the layout based on the data retrieved from the API.
     */
    public static void updateLayout() {
        if (!LocalData.budget.isExpired()) {
            mPieGraph.removeSlices();
            PieSlice slice;
            mCategories = retrieveCurrentCategories();

            for(Category category : mCategories){
                slice = new PieSlice();
                slice.setColor(Color.parseColor(category.getColor()));//mColors[color1Index]));
                slice.setSelectedColor(Color.parseColor(category.getColor()));//mColors[color2Index]));
                slice.setValue(Float.parseFloat(String.valueOf(category.getSumCategory())));//Float.parseFloat(String.valueOf(expensesMap.get(category))));
                slice.setTitle(category.getName());
                mPieGraph.addSlice(slice);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            String creationDate = sdf.format(LocalData.budget.getCreationDate());
            String expirationDate = sdf.format(LocalData.budget.getExpirationDate());
            durationView.setText("(from " + creationDate + " to " + expirationDate + ")");

            titleView.setText("Your Spending Analysis");
            DecimalFormat formatter = new DecimalFormat("$0.00");
            moneySpentView.setText("You've spent " + String.valueOf(formatter.format(LocalData.history.getExpenditureSum())));
            budgetView.setText(" of " + LocalData.budget.getFormattedBudget());

            if(LocalData.balance >= 0) {
                balanceView.setText("You have " + formatter.format(LocalData.balance) + " left in your budget.");
                balanceView.setTextColor(Color.BLACK);
            } else {
                balanceView.setText("You have exceeded your budget by " + formatter.format(-1 * LocalData.balance));
                balanceView.setTextColor(Color.RED);
            }
        } else {
            renderDefaultLayout();
        }
    }

    /**
     * Retrieves the categories that the User has added before
     * @return      the categories
     */
    public static List<Category> retrieveCurrentCategories(){
        List<Category> categories = new ArrayList<>();
        for(Category category : LocalData.categories){
            if(!category.getExpenditures().isEmpty()){
                categories.add(category);
            }
        }
        return categories;
    }
}
