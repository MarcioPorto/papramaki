package com.papramaki.papramaki.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.papramaki.papramaki.R;
import com.papramaki.papramaki.database.DatabaseHelper;
import com.papramaki.papramaki.models.User;
import com.papramaki.papramaki.utils.APIHelper;
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

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = SignUpActivity.class.getSimpleName();

    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mPasswordConfirmation;
    protected Button mSignUpButton;

    protected DatabaseHelper mDbHelper;
    protected APIHelper mAPIHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mDbHelper = new DatabaseHelper(this);
        mAPIHelper = new APIHelper(this, this);

        mUsername = (EditText)findViewById(R.id.emailInput);
        mPassword = (EditText)findViewById(R.id.password);
        mPasswordConfirmation = (EditText)findViewById(R.id.password_confirmation);
        mSignUpButton = (Button)findViewById(R.id.sign_up_button);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewUser(mUsername.getText().toString(), mPassword.getText().toString(), mPasswordConfirmation.getText().toString());
            }
        });
    }

    /**
     * Makes a POST request to create a new User
     * @param email                     the email of the User to be created
     * @param password                  the password
     * @param passwordConfirmation      the password confirmation
     */
    private void createNewUser(String email, String password, String passwordConfirmation) {
        String apiUrl = "https://papramakiapi.herokuapp.com/api/auth";
        RequestBody params = new FormEncodingBuilder()
                .add("email", email)
                .add("password", password)
                .add("password_confirmation", passwordConfirmation)
                .build();

        if (mAPIHelper.isNetworkAvailable()) {
            final OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .post(params)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .build();
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignUpActivity.this, "There was an error.", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();
                        final String AccessToken = response.header("Access-Token");
                        final String Client = response.header("Client");
                        final String Uid = response.header("Uid");

                        JSONObject jsonUser = mAPIHelper.getUserInfoFromResponse(jsonData);
                        int User_id = jsonUser.getInt("id");

                        User user = new User(Uid, Client, AccessToken, User_id);
                        mDbHelper.addUser(user);

                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            final JSONObject resp = mAPIHelper.getUserInfoFromResponse(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                    intent.putExtra("caller", "SignUpActivity");
                                    startActivity(intent);
                                }
                            });
                        } else {
                            mAPIHelper.showSignUpErrors(jsonData);
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(SignUpActivity.this, "Network is unavailable.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        hideSoftKeyboard(SignUpActivity.this);
        return false;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

}
