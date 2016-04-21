package com.papramaki.papramaki.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.papramaki.papramaki.R;
import com.papramaki.papramaki.database.DatabaseHelper;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = SignUpActivity.class.getSimpleName();

    protected EditText mUsername;
    protected EditText mPassword;
    protected EditText mPasswordConfirmation;
    protected Button mSignUpButton;
    protected DatabaseHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUsername = (EditText)findViewById(R.id.emailInput);
        mPassword = (EditText)findViewById(R.id.password);
        mPasswordConfirmation = (EditText)findViewById(R.id.password_confirmation);
        mSignUpButton = (Button)findViewById(R.id.sign_up_button);
        mDbHelper = new DatabaseHelper(this);

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

}
