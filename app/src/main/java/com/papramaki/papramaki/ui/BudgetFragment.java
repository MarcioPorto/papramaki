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
import com.papramaki.papramaki.models.User;
import com.papramaki.papramaki.utils.APIHelper;
import com.papramaki.papramaki.utils.LocalData;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;

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
    protected DatabaseHelper mDbHelper;
    protected APIHelper mAPIHelper;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_budget, container, false);

        mTextView = (TextView)rootView.findViewById(R.id.another_fragment_text);
        mBudgetDisplay = (TextView) rootView.findViewById(R.id.budgetDisplay);
        mButton = (Button)rootView.findViewById(R.id.button2);
        mFAB = (FloatingActionButton)rootView.findViewById(R.id.FAB);
        mBudget = (EditText) rootView.findViewById(R.id.budget);
        mAPIHelper = new APIHelper(getContext(), getActivity());

        mDbHelper = new DatabaseHelper(getContext());
        if(DatabaseUtils.queryNumEntries(mDbHelper.getReadableDatabase(), BudgetContract.Budget.TABLE_NAME) > 0 ) {
            //mBudgetDisplay.setText(mDbHelper.getLatestBudget().getFormattedBudget());
            mBudgetDisplay.setText(String.valueOf(LocalData.budget.getFormattedBudget()));
        }
        else{
            DecimalFormat formatter = new DecimalFormat("$0.00");
            mBudgetDisplay.setText(formatter.format(0.00));
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
                LocalData.history.getExpenditures().clear();
                LocalData.balance = 0;
                String strAmount = mBudget.getText().toString();
                if(!strAmount.equals("")) {
                    Double amount = Double.valueOf(strAmount);
                    postBudgetRequest(amount, 5);
//                    Budget budget = new Budget(amount);
//                    budget.setBalance(amount);
//                    mDbHelper.addBudget(budget);

//                    mBudgetDisplay.setText(mDbHelper.getLatestBudget().getFormattedBudget());
//                    mBalanceDisplay.setText(mDbHelper.getLatestBudget().getFormattedBalance());
                    //mBudgetDisplay.setText(mDbHelper.getLatestBudget().getFormattedBudget());

                }
                //PREVIOUS VERSION
                //mBudgetDisplay.setText("$ " + LocalData.budget.toString());

                mBudget.getText().clear();
            }
        });

//        getHerokuInfo();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

            //mBudgetDisplay.setText(mDbHelper.getLatestBudget().getFormattedBudget());
            //mBalanceDisplay.setText(mDbHelper.getLatestBudget().getFormattedBalance());
            mBudgetDisplay.setText(String.valueOf(LocalData.budget.getFormattedBudget()));

    }


    private void postBudgetRequest(double budgetAmount, int duration) {
        String apiUrl = "https://papramakiapi.herokuapp.com/api/budgets";
//        String budgetsEndpoint = "budgets";
//        String finalUrl = apiUrl + budgetsEndpoint;
        //mAPIHelper = new APIHelper(getContext(), getActivity());
        User user = mDbHelper.getUser();
        RequestBody params = new FormEncodingBuilder()
                .add("amount", String.valueOf(budgetAmount))
                .add("duration", String.valueOf(duration))
                .build();
        if (mAPIHelper.isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(params)
                    .addHeader("Access-Token", user.getAccessToken())
                    .addHeader("Uid", user.getUid())
                    .addHeader("Client", user.getClient())
                    .addHeader("Accept", "application/json")
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    // TODO: Handle this later

                    //put in getActivity.runUiThread()
                    Toast.makeText(getContext(), "There was an error", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(final Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();

                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            JSONObject object = new JSONObject(jsonData);
                            final double amount = object.getDouble("amount");
                            final int duration = object.getInt("duration");
                            final int id = object.getInt("id");
                            final Budget budget = new Budget(amount, id);
                            budget.setDuration(duration);
                            LocalData.budget = budget;
//                            final String budgetsValue = getBudgetsValue(jsonData);
//                            final Budget budget = mAPIHelper.getLatestBudget(jsonData);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), String.valueOf(budget.getBudget()), Toast.LENGTH_LONG).show();
                                    mBudgetDisplay.setText(budget.getFormattedBudget());
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

}
