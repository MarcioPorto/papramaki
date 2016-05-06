package com.papramaki.papramaki.ui;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.papramaki.papramaki.R;
import com.papramaki.papramaki.database.DatabaseHelper;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BudgetFragment extends Fragment {

    private static final String TAG = BudgetFragment.class.getSimpleName();
    protected static Calendar myCalendar;
    public static BudgetFragment mCurrentBudgetFragmentInstance;

    protected TextView mTextView;
    public static TextView mBudgetDisplay;
    protected static EditText mBudget;
    protected static Spinner mSpinner;
    protected static Button mButton;
    protected static EditText mEditDate;
    protected static FloatingActionButton mFAB;
    protected static DatabaseHelper mDbHelper;
    protected static APIHelper mAPIHelper;
    protected static TextView mDurationLabel;
    protected static TextView mWeeksLabel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_budget, container, false);

        mCurrentBudgetFragmentInstance = this;

        mTextView = (TextView)rootView.findViewById(R.id.another_fragment_text);
        mBudgetDisplay = (TextView)rootView.findViewById(R.id.budgetDisplay);
        mButton = (Button)rootView.findViewById(R.id.button2);
        mEditDate = (EditText)rootView.findViewById(R.id.edit_date);
        mFAB = (FloatingActionButton)rootView.findViewById(R.id.FAB);

        mBudget = (EditText) rootView.findViewById(R.id.budget);
        mAPIHelper = new APIHelper(getContext(), getActivity());

        // these all handle picking a budget's expiration date
        mSpinner = (Spinner)rootView.findViewById(R.id.spinner);
        myCalendar = Calendar.getInstance();
        mDurationLabel = (TextView)rootView.findViewById(R.id.textView5);
        mWeeksLabel = (TextView)rootView.findViewById(R.id.textView6);

        mDbHelper = new DatabaseHelper(getContext());

        rootView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    hideSoftKeyboard(getActivity());
                }
                return true;
            }
        });

        // set up dropdown menu for user to select budget's duration
        Integer[] durationOptions = new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12};
        ArrayAdapter<Integer> spinAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, durationOptions);
        mSpinner.setAdapter(spinAdapter);

        mBudgetDisplay.setText(String.valueOf(LocalData.budget.getFormattedBudget()));
        mButton.setText("SAVE");
        mEditDate.setText("");
        mEditDate.setVisibility(View.GONE);

        mEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate();
            }
        });

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
                }

                mBudget.getText().clear();
            }
        });

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

        MainActivity.getBudgetsRequest();
    }

    /**
     * Updates the layout based on the data returned from the API
     */
    public static void updateLayout() {
        Calendar today = Calendar.getInstance();
        Date expirationDate = LocalData.budget.getExpirationDate();
        try {
            if (expirationDate.compareTo(today.getTime()) > 0) {
                // If the budget is not expired yet
                mBudgetDisplay.setText(String.valueOf(LocalData.budget.getFormattedBudget()));
                mBudget.setText(String.valueOf(LocalData.budget.getBudget()) + "0");
                mEditDate.setVisibility(View.VISIBLE);
                Date d = LocalData.budget.getExpirationDate();
                DateFormat df = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
                mEditDate.setText(df.format(d));

                mSpinner.setVisibility(View.GONE);
                mDurationLabel.setVisibility(View.GONE);
                mWeeksLabel.setVisibility(View.GONE);
                mButton.setText("UPDATE");

                myCalendar.setTime(LocalData.budget.getExpirationDate());

                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String calendarDate = mEditDate.getText().toString();
                        DateFormat format = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
                        Date date = Calendar.getInstance().getTime();
                        try {
                            date = format.parse(calendarDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        String newDate = df.format(date);
                        Log.d(TAG, newDate);

                        putBudgetRequest(Double.parseDouble(mBudget.getText().toString()), newDate);
                        resetBalanceRequest(Double.parseDouble(mBudget.getText().toString()) - LocalData.history.getExpenditureSum());
                    }
                });

                mCurrentBudgetFragmentInstance.resetFAB();
            } else {
                layoutForNewBudget();
            }
        } catch (NullPointerException e) {
            // Catches the exception throw the first the time the user signs up.
            layoutForNewBudget();
        }
    }

    /**
     * Sets the layout for when the current budget is not expired
     * or when there isn't a current budget.
     */
    public static void layoutForNewBudget() {
        // Takes the user to the BudgetFragment so they can add a new budget.
        MainActivity.mViewPager.setCurrentItem(0);
        MainActivity.mRadioGroup.check(MainActivity.mRadioGroup.getChildAt(0).getId());
        mBudgetDisplay.setText("$--.--");
        Toast.makeText(MainActivity.getAppContext(), "Please enter a new budget", Toast.LENGTH_LONG).show();

        // Updates the layout.
        mEditDate.setVisibility(View.GONE);

        mSpinner.setVisibility(View.VISIBLE);
        mDurationLabel.setVisibility(View.VISIBLE);
        mWeeksLabel.setVisibility(View.VISIBLE);
        mButton.setText("SAVE");
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String strAmount = mBudget.getText().toString();
//                String duration = mSpinner.getSelectedItem().toString();
//                if(!strAmount.equals("")) {
//                    LocalData.history.getExpenditures().clear();
//                    LocalData.balance = 0;
//                    LocalData.categories.clear();
//                    Double amount = Double.valueOf(strAmount);
//                    postBudgetRequest(amount,duration);
//                    resetBalanceRequest(amount);
//                }
//
//                mBudget.getText().clear();
//            }
//        });

        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(
                        MainActivity.getAppContext(),
                        "Please enter a budget before trying to add an expenditure.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Allows the user to go to the DeductionActivity if there's a current budget.
     */
    public void resetFAB() {
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.getAppContext(), DeductionActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Adds a new budget to the database.
     * @param budgetAmount      the amount of the budget, in dollars
     * @param duration          the duration of the budget, in weeks
     */
    public static void postBudgetRequest(double budgetAmount, String duration) {
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
                    Toast.makeText(MainActivity.getAppContext(), "There was an error", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(final Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();

                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            LocalData.budget = mAPIHelper.getLatestBudget(jsonData);

                            MainActivity.runOnUI(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.getBudgetsRequest();
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
            Toast.makeText(MainActivity.getAppContext(), "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Updates the User's balance on the database
     * @param budgetAmount      the amount of the budget, in dollars, which will become the current balance
     */
    public static void resetBalanceRequest(double budgetAmount) {
        User user = mDbHelper.getUser();
        String apiUrl = "https://papramakiapi.herokuapp.com/api/balances/" + String.valueOf(user.getUser_id());

        RequestBody params = new FormEncodingBuilder()
                .add("amount", String.valueOf(budgetAmount))
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
                    Toast.makeText(MainActivity.getAppContext(), "There was an error", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            JSONObject object = new JSONObject(jsonData);
                            LocalData.balance = object.getDouble("amount");
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
            Toast.makeText(MainActivity.getAppContext(), "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(activity.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Date calendarDate = myCalendar.getTime();
            DateFormat df = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            String newDate = df.format(calendarDate);
            mEditDate.setText(newDate);
        }
    };

    private void pickDate() {
        new DatePickerDialog(getActivity(), date,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * Edits the current budget, if there is one.
     * @param budgetAmount      the amount, in dollars
     * @param date              the expiration date, in YYYY-MM-DD format
     */
    public static void putBudgetRequest(double budgetAmount, String date) {
        String budgetId = String.valueOf(LocalData.budget.getId());
        String apiUrl = "https://papramakiapi.herokuapp.com/api/budgets/" + budgetId;
        User user = mDbHelper.getUser();

        Log.d(TAG, user.getAccessToken());
        Log.d(TAG, user.getClient());
        Log.d(TAG, user.getUid());
        RequestBody params = new FormEncodingBuilder()
                .add("amount", String.valueOf(budgetAmount))
                .add("expiration_date", String.valueOf(date))
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
                    Toast.makeText(MainActivity.getAppContext(), "There was an error", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(final Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();

                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            LocalData.budget = mAPIHelper.getLatestBudgetFromPR(jsonData);
                            MainActivity.runOnUI(new Runnable() {
                                @Override
                                public void run() {
                                    updateLayout();
                                    AnalysisFragment.updateLayout();
                                    Toast.makeText(MainActivity.getAppContext(), "Budget updated!", Toast.LENGTH_LONG).show();
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
            Toast.makeText(MainActivity.getAppContext(), "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    }

}
