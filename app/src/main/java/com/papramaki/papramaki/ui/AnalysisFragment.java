package com.papramaki.papramaki.ui;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.papramaki.papramaki.R;
import com.papramaki.papramaki.database.DatabaseHelper;
import com.papramaki.papramaki.models.Category;
import com.papramaki.papramaki.utils.APIHelper;
import com.papramaki.papramaki.utils.LocalData;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class AnalysisFragment extends Fragment {

    private static final String TAG = AnalysisFragment.class.getSimpleName();

    protected Button mAnimateButton;
    protected PieGraph mPieGraph;
    protected FloatingActionButton mFAB;
    protected DatabaseHelper mDbHelper;
    protected TextView moneySpentView;
    protected TextView balanceView;
    //protected List<Category> categories;

    protected APIHelper mAPIHelper;
    protected TextView budgetView;


    private String[] mColors = { "red", "blue", "green", "black", "white", "gray", "cyan", "magenta",
            "yellow", "lightgray", "darkgray", "grey", "lightgrey", "darkgrey", "aqua", "fuchsia", "lime",
            "maroon", "navy", "olive", "purple", "silver", "teal"};

    public AnalysisFragment() {
    }

    /***
     * Helper method to properly format money values in the pop-up captions
     * @param amount
     * @return
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
        moneySpentView = (TextView) rootView.findViewById(R.id.moneySpent);
        balanceView = (TextView) rootView.findViewById(R.id.textView3);
        budgetView = (TextView) rootView.findViewById(R.id.ofBudget);

        // mAnimateButton = (Button) rootView.findViewById(R.id.animatePieButton);

        final LinkedHashMap<String, Double> expensesMap = organizeHistory();
        PieSlice slice;

        final List<Category> categories = organizeHistory1();
        Random random = new Random();
        //for (String category : expensesMap.keySet()) {
//            int color1Index = random.nextInt(mColors.length);
//            int color2Index = random.nextInt(mColors.length);
        for(Category category: categories){
            slice = new PieSlice();
            slice.setColor(Color.parseColor(category.getColor()));//mColors[color1Index]));
            slice.setSelectedColor(Color.parseColor(category.getColor()));//mColors[color2Index]));
            slice.setValue(Float.parseFloat(String.valueOf(category.getSumCategory())));//Float.parseFloat(String.valueOf(expensesMap.get(category))));
            slice.setTitle(category.getName());
            mPieGraph.addSlice(slice);
        }


        mPieGraph.setOnSliceClickedListener(new PieGraph.OnSliceClickedListener() {
            @Override
            public void onClick(int index) {
                try {
                    Toast.makeText(getActivity(),
//                            "You spent " + formatAmount((double) expensesMap.values().toArray()[index]) + " on " + String.valueOf(expensesMap.keySet().toArray()[index]),
//                            Toast.LENGTH_LONG)
//                            .show();
                            "You spent " + formatAmount(categories.get(index).getSumCategory()) + " on " + String.valueOf(categories.get(index).getName()),
                            Toast.LENGTH_LONG)
                            .show();
                } catch (ArrayIndexOutOfBoundsException exception) {
                    Log.e(TAG, "Exception caught: ", exception);
                }

            }
        });

        DecimalFormat formatter = new DecimalFormat("$0.00");

        moneySpentView.setText("You've spent " + String.valueOf(formatter.format(LocalData.budget.getBudget() - LocalData.balance)));
        //budgetView.setText(" of " + mDbHelper.getLatestBudget().getFormattedBudget());
        budgetView.setText(" of " + LocalData.budget.getFormattedBudget());
        balanceView.setText("You have " + formatter.format(LocalData.balance) + " left in your budget.");

//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
//            mAnimateButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    for (PieSlice s : mPieGraph.getSlices())
//                        s.setGoalValue((float) Math.random() * 10);
//                    mPieGraph.setDuration(1000);//default if unspecified is 300 ms
//                    mPieGraph.setInterpolator(new AccelerateDecelerateInterpolator());//default if unspecified is linear; constant speed
//                    mPieGraph.setAnimationListener(getAnimationListener());
//                    mPieGraph.animateToGoalValues();//animation will always overwrite. Pass true to call the onAnimationCancel Listener with onAnimationEnd
//                }
//            });
//        }

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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public Animator.AnimatorListener getAnimationListener(){
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
            return new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    Log.d("piefrag", "anim end");
                }

                @Override
                public void onAnimationCancel(Animator animation) {//you might want to call slice.setvalue(slice.getGoalValue)
                    Log.d("piefrag", "anim cancel");
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            };
        else return null;
    }

    public static LinkedHashMap<String, Double> organizeHistory() {
        LinkedHashMap<String, Double> expensesMap = new LinkedHashMap<>();
        //List<Expenditure> expenditures = LocalData.history.getExpenditures();
        //List<Expenditure> expenditures = mDbHelper.getLatestExpenditures();
        //List<Expenditure> expenditures = LocalData.history.getExpenditures();
        List<Category> categories = LocalData.categories;
        //List<String> categories = mDbHelper.getCategories();
        /*for(int i = 0; i < expenditures.size(); i++){
            String category = categories.get(i);
            Double amount = expenditures.get(i);

            if (!expensesMap.containsKey(category)){
                expensesMap.put(category , amount);
            } else {
                expensesMap.put(category, expensesMap.get(category) + amount);
            }
        }*/
//        for (Expenditure expenditure : expenditures){
//            if (!expensesMap.containsKey(expenditure.getCategory())) {
//                expensesMap.put(expenditure.getCategory(), expenditure.getAmount());
//            } else {
//                expensesMap.put(expenditure.getCategory(), expensesMap.get(expenditure.getCategory()) + expenditure.getAmount());
//            }
//        }
        for (Category category : categories){
            if (!expensesMap.containsKey(category.getName())) {
                expensesMap.put(category.getName(), category.getSumCategory());
            } else {
                expensesMap.put(category.getName(), expensesMap.get(category.getName()) + category.getSumCategory());
            }
        }
        return expensesMap;
    }

    public List<Category> organizeHistory1(){
        List<Category> categories = new ArrayList<Category>();
        for(Category category: LocalData.categories){
            if(!category.getExpenditures().isEmpty()){
                categories.add(category);
            }
        }
        return categories;
    }
}
