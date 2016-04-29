package com.papramaki.papramaki.ui;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.papramaki.papramaki.R;
import com.papramaki.papramaki.adapters.MainFragmentAdapter;
import com.papramaki.papramaki.database.DatabaseHelper;
import com.papramaki.papramaki.models.Budget;
import com.papramaki.papramaki.models.Category;
import com.papramaki.papramaki.models.Expenditure;
import com.papramaki.papramaki.models.User;
import com.papramaki.papramaki.utils.APIHelper;
import com.papramaki.papramaki.utils.LocalData;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    // Creates an instance of the MainFragmentAdapter and one
    // ViewPager (which basically is what allows you to flip
    // left and right
    MainFragmentAdapter mMainFragmentAdapter;
    ViewPager mViewPager;
    DatabaseHelper mDbHelper;

    // Making these static so they can be accessed from the fragments
    public static APIHelper mAPIHelper;
    public static User user;
    public static Handler UIHandler = new Handler(Looper.getMainLooper());
    private static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.context = getApplicationContext();

        // Creates an adapter that will return a fragment for each section
        mMainFragmentAdapter = new MainFragmentAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mMainFragmentAdapter);

        // Makes the Analysis fragment the default view
        // TODO: Lead to BydgetFragment if the most recent budget has expired
        mViewPager.setCurrentItem(1);

        // This is the part that actually changes the fragments displayed when the user flips left or right
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        mDbHelper = new DatabaseHelper(this);

        Intent intentGetter = getIntent();
        final String Caller = intentGetter.getStringExtra("caller");
//        final String AccessToken = intentGetter.getStringExtra("Access-Token");
//        final String Client = intentGetter.getStringExtra("Client");
//        final String Uid = intentGetter.getStringExtra("Uid");

//        if(Caller.equals("LoginActivity") || Caller.equals("SignUpActivity")) {
//            user = new User(Uid, Client, AccessToken);
//            mDbHelper.addUser(user);
//            Toast.makeText(this, AccessToken + " " + Client + " " + Uid, Toast.LENGTH_LONG).show();
//            //mDatabase = mDbHelper.getWritableDatabase();
//        }else{
        user = mDbHelper.getUser();
//        }
        mAPIHelper = new APIHelper(MainActivity.this, this);

        retrieveAllData();

    }

    @Override
    public void onResume() {
        super.onResume();
        user = mDbHelper.getUser();
        retrieveAllData();
    }

    /**
     * Makes all the get requests we need to show the initial data.
     */
    public static void retrieveAllData() {
        getBudgetsRequest();
        getBalancesRequest();
        getExpendituresRequest();
        getCategoriesRequest();
    }

    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log_out) {
            // TODO: Actually log the user out
            //remove user from database
            makeLogoutRequest();
            mDbHelper.userLogout();
            LocalData.balance = 0;
            LocalData.budget = new Budget();
            LocalData.history.getExpenditures().clear();
            LocalData.categories = new ArrayList<Category>();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //HTTP Requests
    public static void getBudgetsRequest() {
        String apiUrl = "https://papramakiapi.herokuapp.com/api/budgets";

        if (mAPIHelper.isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Accept", "application/json")
                    .addHeader("Access-Token", user.getAccessToken())
                    .addHeader("Uid", user.getUid())
                    .addHeader("Client", user.getClient())
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    MainActivity.runOnUI(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.getAppContext(), "There was an error", Toast.LENGTH_LONG).show();
                        }
                    });
//                    Toast.makeText(MainActivity.getAppContext(), "There was an error", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            final Budget budget = mAPIHelper.getLatestBudget(jsonData);
                            MainActivity.runOnUI(new Runnable() {
                                @Override
                                public void run() {
                                    LocalData.budget = budget;
                                    BudgetFragment.mBudgetDisplay.setText(budget.getFormattedBudget());
                                }
                            });
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    LocalData.budget = budget;
//                                    BudgetFragment.mBudgetDisplay.setText(budget.getFormattedBudget());
//
//                                }
//                            });
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

    public static void getExpendituresRequest() {
        String apiUrl = "https://papramakiapi.herokuapp.com/api/budgets";

        if (mAPIHelper.isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(apiUrl)
                    //.post("")
                    .addHeader("Access-Token", user.getAccessToken())
                    .addHeader("Uid", user.getUid())
                    .addHeader("Client", user.getClient())
                    .addHeader("Accept", "application/json")
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    MainActivity.runOnUI(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.getAppContext(), "There was an error", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            final List<Expenditure> history = mAPIHelper.getExpenditures(jsonData);
                            LocalData.history.setExpenditures(history);

//                            MainActivity.runOnUI(new Runnable() {
//                                @Override
//                                public void run() {
//                                    HistoryFragment.updateDisplay();
//                                }
//                            });
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


    public static void getBalancesRequest() {
        String apiUrl = "https://papramakiapi.herokuapp.com/api/balances";

        if (mAPIHelper.isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Access-Token", user.getAccessToken())
                    .addHeader("Uid", user.getUid())
                    .addHeader("Client", user.getClient())
                    .addHeader("Accept", "application/json")
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    MainActivity.runOnUI(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.getAppContext(), "There was an error", Toast.LENGTH_LONG).show();
                        }
                    });
//                    Toast.makeText(MainActivity.getAppContext(), "There was an error", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
//                            final String budgetsValue = getBudgetsValue(jsonData);
                            final double currentBalance = mAPIHelper.getLatestBalance(jsonData);
                            LocalData.balance = currentBalance;

//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    // Toast.makeText(getContext(), budgetsValue, Toast.LENGTH_LONG).show();
//                                    LocalData.balance = currentBalance;
//                                }
//                            });
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

    public static void getCategoriesRequest(){
        String apiUrl = "https://papramakiapi.herokuapp.com/api/categories";

        if (mAPIHelper.isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Access-Token", user.getAccessToken())
                    .addHeader("Uid", user.getUid())
                    .addHeader("Client", user.getClient())
                    .addHeader("Accept", "application/json")
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    MainActivity.runOnUI(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.getAppContext(), "There was an error", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            final List<Category> categories = mAPIHelper.getCategories(jsonData);
                            LocalData.categories = categories;

                            // Updates the graph and text in the AnalysisFragment
                            MainActivity.runOnUI(new Runnable() {
                                @Override
                                public void run() {
                                    AnalysisFragment.updateLayout();
                                }
                            });
//                            AnalysisFragment.updateLayout();

//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    // Toast.makeText(getContext(), budgetsValue, Toast.LENGTH_LONG).show();
//                                    LocalData.categories = categories;
//                                }
//                            });
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

    private void makeLogoutRequest(){
        String apiUrl = "https://papramakiapi.herokuapp.com/api/auth/sign_out";
        if (mAPIHelper.isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Access-Token", user.getAccessToken())
                    .addHeader("Uid", user.getUid())
                    .addHeader("Client", user.getClient())
                    .addHeader("Accept", "application/json")
                    .delete()
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    // TODO: Handle this later

                    //put in getActivity.runUiThread()
                    Toast.makeText(MainActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, jsonData, Toast.LENGTH_LONG).show();

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
            Toast.makeText(MainActivity.this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    }

}
