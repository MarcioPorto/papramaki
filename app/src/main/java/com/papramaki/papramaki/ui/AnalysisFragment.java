package com.papramaki.papramaki.ui;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.TextView;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;
import com.papramaki.papramaki.R;
import com.papramaki.papramaki.database.DatabaseHelper;
import com.papramaki.papramaki.models.Budget;
import com.papramaki.papramaki.models.Expenditure;
import com.papramaki.papramaki.utils.APIHelper;
import com.papramaki.papramaki.utils.LocalData;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
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
    protected APIHelper mAPIHelper;

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

        // mAnimateButton = (Button) rootView.findViewById(R.id.animatePieButton);

        final LinkedHashMap<String, Double> expensesMap = organizeHistory();
        PieSlice slice;

        Random random = new Random();
        for (String category : expensesMap.keySet()) {
            int color1Index = random.nextInt(mColors.length);
            int color2Index = random.nextInt(mColors.length);
            slice = new PieSlice();
            slice.setColor(Color.parseColor(mColors[color1Index]));
            slice.setSelectedColor(Color.parseColor(mColors[color2Index]));
            slice.setValue(Float.parseFloat(String.valueOf(expensesMap.get(category))));
            slice.setTitle(category);
            mPieGraph.addSlice(slice);
        }


        mPieGraph.setOnSliceClickedListener(new PieGraph.OnSliceClickedListener() {
            @Override
            public void onClick(int index) {
                Toast.makeText(getActivity(),
                        "You spent " + formatAmount((double) expensesMap.values().toArray()[index]) + " on " + String.valueOf(expensesMap.keySet().toArray()[index]),
                        Toast.LENGTH_LONG)
                        .show();
            }
        });


        moneySpentView.setText("You've spent " + mDbHelper.getLatestBudget().getFormattedMoneySpent());
        balanceView.setText("You have " + mDbHelper.getLatestBudget().getFormattedBalance() + " left in this month's budget.");

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

    public LinkedHashMap<String, Double> organizeHistory() {
        LinkedHashMap<String, Double> expensesMap = new LinkedHashMap<>();
        //List<Expenditure> expenditures = LocalData.history.getExpenditures();
        List<Expenditure> expenditures = mDbHelper.getLatestExpenditures();
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
        for (Expenditure expenditure : expenditures){
            if (!expensesMap.containsKey(expenditure.getCategory())) {
                expensesMap.put(expenditure.getCategory(), expenditure.getAmount());
            } else {
                expensesMap.put(expenditure.getCategory(), expensesMap.get(expenditure.getCategory()) + expenditure.getAmount());
            }
        }
        return expensesMap;
    }


    private void getHerokuInfo() {
        String apiBalanceUrl = "https://papramakiapi.herokuapp.com/api/balances";
        String apiBudgetUrl = "https://papramakiapi.herokuapp.com/api/balances";
//        String budgetsEndpoint = "budgets";
//        String finalUrl = apiUrl + budgetsEndpoint;
        mAPIHelper = new APIHelper(getContext(), getActivity());

        if (mAPIHelper.isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(apiBalanceUrl)
                    //.post("")
                    .addHeader("Accept", "application/json")
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    // TODO: Handle this later
                    Toast.makeText(getContext(), "There was an error", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
//                            final String budgetsValue = getBudgetsValue(jsonData);
                            final int spending = getBudgetsValue(jsonData);
                            final int balance = getBalanceValue(jsonData);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Toast.makeText(getContext(), budgetsValue, Toast.LENGTH_LONG).show();
                                    updateDisplay(spending, balance);
                                }
                            });
                        } else {
                            mAPIHelper.alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(getContext(), "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    }


    private void updateDisplay(int spending, int balance) {
        // TODO: We update the view here
        moneySpentView.setText(String.valueOf(spending));
        balanceView.setText(String.valueOf(balance));
    }


    private int getBudgetsValue(String jsonData) throws JSONException {

        JSONArray response = new JSONArray(jsonData);
        String returnString = "";

        int value = 0;

        for (int i = 0; i < 1; i++) {
            JSONObject currentBudget = response.getJSONObject(i);
            int budgetAmount = currentBudget.getInt("amount");
            value = budgetAmount;
            int duration = currentBudget.getInt("duration");
            JSONArray expenditures = currentBudget.getJSONArray("expenditures");

            for (int j = 0; j < expenditures.length(); j++) {
                JSONObject currentExp = expenditures.getJSONObject(j);
                Double amount = currentExp.getDouble("amount");
                returnString += Double.toString(amount) + " ";
            }
        }

        return value;
    }

    private int getBalanceValue(String jsonData) throws JSONException {

        JSONArray response = new JSONArray(jsonData);
        String returnString = "";

        int value = 0;

        for (int i = 0; i < 1; i++) {
            JSONObject currentBalance = response.getJSONObject(i);
            int balanceAmount = currentBalance.getInt("amount");
            value = balanceAmount;
        }

        return value;
    }

}
