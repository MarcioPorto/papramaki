package com.papramaki.papramaki.ui;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.papramaki.papramaki.R;
import com.papramaki.papramaki.database.DatabaseHelper;
import com.papramaki.papramaki.models.Expenditure;
import com.papramaki.papramaki.utils.LocalData;

import java.util.Date;

public class DeductionActivity extends AppCompatActivity {

    private static final String TAG = DeductionActivity.class.getSimpleName();

    protected EditText mAmount;
    protected EditText mCategory;
    protected Button mButton;
    protected DatabaseHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deduction);

        mAmount = (EditText)findViewById(R.id.amount_input);
        mCategory = (EditText)findViewById(R.id.category_input);
        mButton = (Button)findViewById(R.id.button);
        mDbHelper = new DatabaseHelper(this);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                double amount = Double.valueOf(mAmount.getText().toString());
                Expenditure expenditure = new Expenditure(amount, mCategory.getText().toString(), date);

                mDbHelper.addExpenditure(expenditure);
                mDbHelper.updateBalance(mDbHelper.viewLatestBalance() - expenditure.getAmount());

                Log.i(TAG, LocalData.budget.toString());

                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
