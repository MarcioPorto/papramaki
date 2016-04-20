package com.papramaki.papramaki.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.papramaki.papramaki.R;
import com.papramaki.papramaki.database.BudgetContract;
import com.papramaki.papramaki.database.DatabaseHelper;
import com.papramaki.papramaki.models.Budget;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
            mBudgetDisplay.setText(mDbHelper.getLatestBudget().getFormattedBudget());
            mBalanceDisplay.setText(mDbHelper.getLatestBudget().getFormattedBalance());
            if (Double.valueOf(mDbHelper.getLatestBudget().getBalance()) < 0) {
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
                    mBudgetDisplay.setText(mDbHelper.getLatestBudget().getFormattedBudget());
                    mBalanceDisplay.setText(mDbHelper.getLatestBudget().getFormattedBalance());

                }
                //PREVIOUS VERSION
                //mBudgetDisplay.setText("$ " + LocalData.budget.toString());

                if (Double.valueOf(mDbHelper.getLatestBudget().getBalance()) < 0) {
                    mBalanceDisplay.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                } else {
                    mBalanceDisplay.setTextColor(Color.parseColor("black"));
                }
                mBudget.getText().clear();
            }
        });

        getHerokuInfo();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(DatabaseUtils.queryNumEntries(mDbHelper.getReadableDatabase(), BudgetContract.Budget.TABLE_NAME) > 0 ) {
            mBudgetDisplay.setText(mDbHelper.getLatestBudget().getFormattedBudget());
            mBalanceDisplay.setText(mDbHelper.getLatestBudget().getFormattedBalance());
            if (Double.valueOf(mDbHelper.getLatestBudget().getBalance()) < 0) {
                mBalanceDisplay.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
            } else {
                mBalanceDisplay.setTextColor(Color.parseColor("black"));
            }
        }
    }

    private void getHerokuInfo() {
        String apiUrl = "https://papramakiapi.herokuapp.com/budgets";
//        String budgetsEndpoint = "budgets";
//        String finalUrl = apiUrl + budgetsEndpoint;

        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(apiUrl)
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
                            final int value = getBudgetsValue(jsonData);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Toast.makeText(getContext(), budgetsValue, Toast.LENGTH_LONG).show();
                                     updateDisplay(value);
                                }
                            });
                        } else {
                            alertUserAboutError();
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

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("There was an error processing your request").setTitle("Oops!");
                builder.create();
            }
        });
    }

    private void updateDisplay(int value) {
        // TODO: We update the view here
        mBudgetDisplay.setText(String.valueOf(value));
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

}
