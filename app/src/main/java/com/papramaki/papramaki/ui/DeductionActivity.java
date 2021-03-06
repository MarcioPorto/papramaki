package com.papramaki.papramaki.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.papramaki.papramaki.R;
import com.papramaki.papramaki.database.DatabaseHelper;
import com.papramaki.papramaki.models.Budget;
import com.papramaki.papramaki.models.Category;
import com.papramaki.papramaki.models.Expenditure;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DeductionActivity extends AppCompatActivity {

    private static final String TAG = DeductionActivity.class.getSimpleName();

    protected EditText mAmountInput;
    protected Spinner mUserCategoriesSpinner;
    protected EditText mCategoryInput;
    protected Button mAddButton;
    protected int mCategoryId;
    protected User user;
    protected List<String> mCategoriesDropdownItems = new ArrayList<String>();

    protected DatabaseHelper mDbHelper;
    protected APIHelper mAPIHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deduction);

        // Displays back button in the navbar
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e(TAG, "Exception caught: ", e);
        }

        mAmountInput = (EditText)findViewById(R.id.amount_input);
        mUserCategoriesSpinner = (Spinner)findViewById(R.id.user_categories_spinner);
        mCategoryInput = (EditText)findViewById(R.id.category_input);
        mAddButton = (Button)findViewById(R.id.add_button);
        mDbHelper = new DatabaseHelper(this);
        mAPIHelper = new APIHelper(this, this);
        user = mDbHelper.getUser();

         //Populates dropdown
        for(Category category: LocalData.categories) {
            mCategoriesDropdownItems.add(category.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.category_spinner_item,
                mCategoriesDropdownItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUserCategoriesSpinner.setAdapter(adapter);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String categoryName = mCategoryInput.getText().toString();

                if (!mAmountInput.getText().toString().equals("") && categoryName.equals("") && mCategoriesDropdownItems.size() == 0) {
                    // If there is an amount, but no category from the spinner or input:
                    Toast.makeText(
                            DeductionActivity.this,
                            "Please select a category.",
                            Toast.LENGTH_LONG).show();
                } else if (!mAmountInput.getText().toString().equals("")){
                    // If there is an amount and there is a category:

                    double amount = Double.valueOf(mAmountInput.getText().toString());
                    putBalanceRequest(amount);

                    if (!categoryName.equals("")) {
                        // this will also post the new expenditure if successful
                        postCategoryRequest(categoryName, amount);
                    } else {
                        String selectedCategory = mUserCategoriesSpinner.getSelectedItem().toString();
                        for(Category category : LocalData.categories){
                            if(selectedCategory.equals(category.getName())) {
                                mCategoryId = category.getId();
                                break;
                            }
                        }
                        postExpenditureRequest(amount);
                    }
                } else {
                    Intent intent = new Intent(v.getContext(), MainActivity.class);
                    intent.putExtra("caller", "DeductionActivity");
                    startActivity(intent);
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_deduction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();

        int id = item.getItemId();

        if (id == R.id.action_log_out) {
            makeLogoutRequest();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Adds a new expenditure to the database.
     * @param expenditureAmount     the amount of the expenditure, in dollars
     */
    private void postExpenditureRequest(double expenditureAmount) {
        String apiUrl = "https://papramakiapi.herokuapp.com/api/expenditures";

        User user = mDbHelper.getUser();
        RequestBody params = new FormEncodingBuilder()
                .add("amount", String.valueOf(expenditureAmount))
                .add("budget_id", String.valueOf(LocalData.budget.getId()))
                .add("category_id", String.valueOf(mCategoryId))
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
                    Toast.makeText(DeductionActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();
                        Log.v(TAG, jsonData);

                        if (response.isSuccessful()) {
                            JSONObject object = new JSONObject(jsonData);
                            final double amount = object.getDouble("amount");
                            final String dateString = object.getString("created_at");
                            System.out.println("//////////////DATESTRING: "+ dateString);
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                            System.out.println("//////////////SIMPLEDATEFORMAT: "+ sdf.toString());
                            final Expenditure expenditure = new Expenditure();
                            expenditure.setAmount(amount);
                            try {
                                Date date = sdf.parse(dateString);
                                expenditure.setDate(date);
                                System.out.println("//////////////DATE: "+ date.toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            LocalData.history.getExpenditures().add(expenditure);

                            // Goes back to Main
                            Intent intent = new Intent(DeductionActivity.this, MainActivity.class);
                            intent.putExtra("caller", "DeductionActivity");
                            startActivity(intent);
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
            Toast.makeText(DeductionActivity.this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Subtracts the amount of the new expenditure from the balance of the current user
     * @param expenditureAmount - value to be subtracted
     */
    private void putBalanceRequest(double expenditureAmount) {
        User user = mDbHelper.getUser();
        String apiUrl = "https://papramakiapi.herokuapp.com/api/balances/" + String.valueOf(user.getUser_id());

        RequestBody params = new FormEncodingBuilder()
                .add("amount", String.valueOf(LocalData.balance - expenditureAmount))
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
                    Toast.makeText(DeductionActivity.this, "There was an error", Toast.LENGTH_LONG).show();
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
            Toast.makeText(DeductionActivity.this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Adds a new category to the User's profile
     * @param categoryName          the name of the category
     * @param expenditureAmount     the amount of the expenditure, used to call postExpenditureRequest()
     */
    private void postCategoryRequest(String categoryName, final double expenditureAmount) {
        User user = mDbHelper.getUser();
        String apiUrl = "https://papramakiapi.herokuapp.com/api/categories";

        RequestBody params = new FormEncodingBuilder()
                .add("name", categoryName)
                .add("user_id", String.valueOf(user.getUser_id()))
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
                    Toast.makeText(DeductionActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            JSONObject object = new JSONObject(jsonData);
                            // category attributes from response
                            final String categoryName = object.getString("name");
                            final String categoryColor = object.getString("color");
                            final int categoryId = object.getInt("id");

                            mCategoryId = categoryId;
                            // create new category locally
                            Category category = new Category(categoryName, categoryColor, categoryId);
                            LocalData.categories.add(category);
                            postExpenditureRequest(expenditureAmount);
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
            Toast.makeText(DeductionActivity.this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Logs the User out
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
                    Toast.makeText(DeductionActivity.this, "There was an error", Toast.LENGTH_LONG).show();
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

                            Intent intent = new Intent(DeductionActivity.this, LoginActivity.class);
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
            Toast.makeText(DeductionActivity.this, "Network is unavailable", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideSoftKeyboard(DeductionActivity.this);
        return false;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(activity.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

}
