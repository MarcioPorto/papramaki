package com.papramaki.papramaki.ui;

import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Button;
import android.os.Bundle;

import com.papramaki.papramaki.R;
import com.papramaki.papramaki.database.DatabaseHelper;
import com.papramaki.papramaki.utils.LocalData;

import com.papramaki.papramaki.database.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    protected EditText mUsername;
    protected EditText mPassword;
    protected Button mLogIn;
    protected Button mSignUp;
    protected DatabaseHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUsername = (EditText)findViewById(R.id.emailInput);
        mPassword = (EditText)findViewById(R.id.password);
        mLogIn = (Button)findViewById(R.id.login);
        mSignUp = (Button)findViewById(R.id.signUp);
        mDbHelper = new DatabaseHelper(this);

    }
}
