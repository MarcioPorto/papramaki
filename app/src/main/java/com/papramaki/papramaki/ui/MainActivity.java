package com.papramaki.papramaki.ui;

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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.papramaki.papramaki.R;
import com.papramaki.papramaki.adapters.MainFragmentAdapter;
import com.papramaki.papramaki.database.DatabaseHelper;
import com.papramaki.papramaki.models.Budget;
import com.papramaki.papramaki.models.History;
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
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    // Creates an instance of the MainFragmentAdapter and one
    // ViewPager (which basically is what allows you to flip
    // left and right
    protected static MainFragmentAdapter mMainFragmentAdapter;
    protected static ViewPager mViewPager;

    // Making these static so they can be accessed from the fragments
    public static APIHelper mAPIHelper;
    public static User user;
    public static Handler UIHandler = new Handler(Looper.getMainLooper());
    private static Context context;
    public static RadioGroup mRadioGroup;

    private DatabaseHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainActivity.context = getApplicationContext();

        // Creates an adapter that will return a fragment for each section
        mMainFragmentAdapter = new MainFragmentAdapter(this, getSupportFragmentManager());
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mMainFragmentAdapter);

        // Makes the Analysis fragment the default view
        mViewPager.setCurrentItem(1);
        mRadioGroup.check(mRadioGroup.getChildAt(1).getId());

        // This is the part that actually changes the fragments displayed when the user flips left or right
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                mRadioGroup.check(mRadioGroup.getChildAt(position).getId());
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        mDbHelper = new DatabaseHelper(this);

        user = mDbHelper.getUser();
        mAPIHelper = new APIHelper(MainActivity.this, this);

        retrieveAllData();
    }

    @Override
    public void onResume() {
        super.onResume();
        user = mDbHelper.getUser();
        retrieveAllData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Quits the app when the user presses the back button (instead of going to LoginActivity
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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

        if (id == R.id.action_log_out) {
            makeLogoutRequest();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * GETS the the budgets that belong to the current User from the API.
     */
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
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            LocalData.budget = mAPIHelper.getLatestBudget(jsonData);
                            MainActivity.runOnUI(new Runnable() {
                                @Override
                                public void run() {
                                    BudgetFragment.updateLayout();
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

    public static void getExpendituresRequest() {
        String apiUrl = "https://papramakiapi.herokuapp.com/api/budgets";

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
                            final History history = mAPIHelper.getHistory(jsonData);
                            Collections.reverse(history.getExpenditures());
                            LocalData.history = history;

                            MainActivity.runOnUI(new Runnable() {
                                @Override
                                public void run() {
                                    HistoryFragment.updateLayout();
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
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            LocalData.balance = mAPIHelper.getLatestBalance(jsonData);
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
                            LocalData.categories = mAPIHelper.getCategories(jsonData);
                            MainActivity.runOnUI(new Runnable() {
                                @Override
                                public void run() {
                                    AnalysisFragment.updateLayout();
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
     * Destroys the current User's session.
     */
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
                    Toast.makeText(MainActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mDbHelper.userLogout();
                            LocalData.balance = 0;
                            LocalData.budget = new Budget();
                            LocalData.history.getExpenditures().clear();
                            LocalData.categories = new ArrayList<>();

                            // redirects to Login
                            Intent intent = new Intent(MainActivity.getAppContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
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

    /**
     * Enables the requests methods to run code on the UI thread.
     * @param runnable
     */
    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }

    /**
     * Gets the context of the MainActivity.
     * @return      the context
     */
    public static Context getAppContext() {
        return MainActivity.context;
    }

    /**
     * Makes all the GET requests we need to show the initial data when the User opens the app.
     */
    public static void retrieveAllData() {
        getBudgetsRequest();
        getBalancesRequest();
        getExpendituresRequest();
        getCategoriesRequest();
    }

}
