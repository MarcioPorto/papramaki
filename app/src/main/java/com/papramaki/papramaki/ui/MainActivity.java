package com.papramaki.papramaki.ui;


import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;

import android.content.Intent;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.papramaki.papramaki.R;
import com.papramaki.papramaki.adapters.MainFragmentAdapter;
import com.papramaki.papramaki.database.DatabaseHelper;

import com.papramaki.papramaki.models.Budget;
import com.papramaki.papramaki.models.Expenditure;
import com.papramaki.papramaki.models.History;
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

import java.io.IOException;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    // Creates an instance of the MainFragmentAdapter and one
    // ViewPager (which basically is what allows you to flip
    // left and right
    MainFragmentAdapter mMainFragmentAdapter;
    ViewPager mViewPager;
    DatabaseHelper mDbHelper;
    APIHelper mAPIHelper;
    protected User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Creates an adapter that will return a fragment for each section
        mMainFragmentAdapter = new MainFragmentAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mMainFragmentAdapter);

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
        mAPIHelper = new APIHelper(mViewPager.getContext(), this);

        getBudgetsRequest();
        getBalancesRequest();
        getExpendituresRequest();

    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        user = mDbHelper.getUser();
//    }

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
            mDbHelper.userLogout();
            LocalData.balance = 0;
            LocalData.budget = null;
            LocalData.history.getExpenditures().clear();
            LocalData.category = null;
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //HTTP Requests
    private void getBudgetsRequest() {
        String apiUrl = "https://papramakiapi.herokuapp.com/api/budgets";
//        String budgetsEndpoint = "budgets";
//        String finalUrl = apiUrl + budgetsEndpoint;
        //mAPIHelper = new APIHelper(getContext(), getActivity());
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
//                            final String budgetsValue = getBudgetsValue(jsonData);
                            final Budget budget = mAPIHelper.getLatestBudget(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Toast.makeText(getContext(), budgetsValue, Toast.LENGTH_LONG).show();
                                    //mBudgetDisplay.setText(String.valueOf(budget.getBudget()));
                                    LocalData.budget = budget;

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
            Toast.makeText(MainActivity.this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    }

    private void getExpendituresRequest() {
        String apiUrl = "https://papramakiapi.herokuapp.com/api/budgets";
//        String budgetsEndpoint = "budgets";
//        String finalUrl = apiUrl + budgetsEndpoint;
        //mAPIHelper = new APIHelper(getContext(), getActivity());

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
//                            final String budgetsValue = getBudgetsValue(jsonData);
                            final List<Expenditure> history = mAPIHelper.getExpenditures(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Toast.makeText(getContext(), budgetsValue, Toast.LENGTH_LONG).show();
                                    LocalData.history.setExpenditures(history);
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
            Toast.makeText(MainActivity.this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    }


    private void getBalancesRequest() {
        String apiUrl = "https://papramakiapi.herokuapp.com/api/balances";
//        String budgetsEndpoint = "budgets";
//        String finalUrl = apiUrl + budgetsEndpoint;
        //mAPIHelper = new APIHelper(getContext(), getActivity());
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
//                            final String budgetsValue = getBudgetsValue(jsonData);
                            final double currentBalance = mAPIHelper.getLatestBalance(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Toast.makeText(getContext(), budgetsValue, Toast.LENGTH_LONG).show();
                                    LocalData.balance = currentBalance;
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
            Toast.makeText(MainActivity.this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    }

}
