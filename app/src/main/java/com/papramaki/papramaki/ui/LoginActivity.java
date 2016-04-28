package com.papramaki.papramaki.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLogInButton;
    protected Button mSignUpButton;
    protected DatabaseHelper mDbHelper;
    protected APIHelper mAPIHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (EditText)findViewById(R.id.emailInput);
        mPassword = (EditText)findViewById(R.id.password);
        mLogInButton = (Button)findViewById(R.id.login);
        mSignUpButton = (Button)findViewById(R.id.signUp);
        mAPIHelper = new APIHelper(this, this);
        mDbHelper = new DatabaseHelper(this);

        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUser(mUsername.getText().toString(), mPassword.getText().toString());
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
    //Get request sign user
    private void getUser(String email, String password) {
        String apiUrl = "https://papramakiapi.herokuapp.com/api/auth/sign_in";
        RequestBody params = new FormEncodingBuilder()
                .add("email", email)
                .add("password", password)
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
                            Toast.makeText(LoginActivity.this, "There was an error.", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        final String jsonData = response.body().string();

                        //get user id and save to database to use to get balances
                        // TODO: HTTP header to store locally
                        final String AccessToken = response.header("Access-Token");
                        final String Client = response.header("Client");
                        final String Uid = response.header("Uid");


                        User user = new User(Uid, Client, AccessToken);
                        mDbHelper.addUser(user);

                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            final JSONObject resp = mAPIHelper.getUserInfoFromResponse(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Toast.makeText(SignUpActivity.this, resp.toString() + AccessToken + " " + Client + " " + Uid, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("caller", "LoginActivity");
//                                    intent.putExtra("Access-Token", AccessToken);
//                                    intent.putExtra("Client", Client);
//                                    intent.putExtra("Uid", Uid);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            //mAPIHelper.alertUserAboutError();
                            final String errorMessage = mAPIHelper.errorIdentifier(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, "Network is unavailable.", Toast.LENGTH_LONG).show();
        }
    }

}
