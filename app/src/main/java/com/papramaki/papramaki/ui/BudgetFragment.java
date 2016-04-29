package com.papramaki.papramaki.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.papramaki.papramaki.R;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A placeholder fragment containing a simple view.
 */
public class BudgetFragment extends Fragment {

    private static final String TAG = BudgetFragment.class.getSimpleName();

    protected TextView mTextView;
    public static TextView mBudgetDisplay;
    protected EditText mBudget;
    protected Spinner mSpinner;
    protected Button mButton;
    protected FloatingActionButton mFAB;
    protected DatabaseHelper mDbHelper;
    protected APIHelper mAPIHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_budget, container, false);

        mTextView = (TextView)rootView.findViewById(R.id.another_fragment_text);
        mBudgetDisplay = (TextView)rootView.findViewById(R.id.budgetDisplay);
        mButton = (Button)rootView.findViewById(R.id.button2);
        mFAB = (FloatingActionButton)rootView.findViewById(R.id.FAB);

        mBudget = (EditText) rootView.findViewById(R.id.budget);
        mAPIHelper = new APIHelper(getContext(), getActivity());

        mSpinner = (Spinner)rootView.findViewById(R.id.spinner);


        mDbHelper = new DatabaseHelper(getContext());

            //mBudgetDisplay.setText(mDbHelper.getLatestBudget().getFormattedBudget());
        mBudgetDisplay.setText(String.valueOf(LocalData.budget.getFormattedBudget()));

//        else{
//            DecimalFormat formatter = new DecimalFormat("$0.00");
//            mBudgetDisplay.setText(formatter.format(0.00));
//        }

        rootView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    hideSoftKeyboard(getActivity());
                }
                return true;
            }
        });

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DeductionActivity.class);
                startActivity(intent);
            }
        });

        // set up dropdown menu for user to select budget's duration
        Integer[] durationOptions = new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12};
        ArrayAdapter<Integer> spinAdapter = new ArrayAdapter<Integer>(getContext(), android.R.layout.simple_spinner_dropdown_item, durationOptions);
        mSpinner.setAdapter(spinAdapter);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String strAmount = mBudget.getText().toString();
                String duration = mSpinner.getSelectedItem().toString();
                if(!strAmount.equals("")) {
                    LocalData.history.getExpenditures().clear();
                    LocalData.balance = 0;
                    LocalData.categories.clear();
                    Double amount = Double.valueOf(strAmount);
                    postBudgetRequest(amount,duration);
                    resetBalanceRequest(amount);
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

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        MainActivity.getBudgetsRequest();
        updateLayout();
    }

    protected void updateLayout() {
        mBudgetDisplay.setText(String.valueOf(LocalData.budget.getFormattedBudget()));
    }

    private void postBudgetRequest(double budgetAmount, String duration) {
        String apiUrl = "https://papramakiapi.herokuapp.com/api/budgets";
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

//                            MainActivity.runOnUI(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(getContext(), String.valueOf(budget.getBudget()), Toast.LENGTH_LONG).show();
//                                    updateLayout();
//                                }
//                            });
//
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), String.valueOf(budget.getBudget()), Toast.LENGTH_LONG).show();
                                    updateLayout();
                                }
                            });

                        } else {
                            mAPIHelper.alertUserAboutError(jsonData);
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

    private void resetBalanceRequest(double expenditureAmount) {
        User user = mDbHelper.getUser();
        String apiUrl = "https://papramakiapi.herokuapp.com/api/balances/" + String.valueOf(user.getUser_id());
//        String budgetsEndpoint = "budgets";
//        String finalUrl = apiUrl + budgetsEndpoint;
        //mAPIHelper = new APIHelper(getContext(), getActivity());

        RequestBody params = new FormEncodingBuilder()
                .add("amount", String.valueOf(expenditureAmount))
                .build();
        if (mAPIHelper.isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .put(params)
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
                public void onResponse(Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            JSONObject object = new JSONObject(jsonData);
                            final double balance = object.getDouble("amount");
                            LocalData.balance = balance;
////                            final String budgetsValue = getBudgetsValue(jsonData);
//                            final Budget budget = mAPIHelper.getLatestBudget(jsonData);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Toast.makeText(getContext(), budgetsValue, Toast.LENGTH_LONG).show();

                                }
                            });
                        } else {
                            mAPIHelper.alertUserAboutError(jsonData);
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

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(activity.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

}
