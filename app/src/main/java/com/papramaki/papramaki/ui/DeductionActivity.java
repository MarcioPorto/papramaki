package com.papramaki.papramaki.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.papramaki.papramaki.R;
import com.papramaki.papramaki.database.DatabaseHelper;
import com.papramaki.papramaki.models.Expenditure;
import com.papramaki.papramaki.utils.LocalData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeductionActivity extends AppCompatActivity {

    private static final String TAG = DeductionActivity.class.getSimpleName();

    protected EditText mAmountInput;
    protected Spinner mUserCategoriesSpinner;
    protected EditText mCategoryInput;
    protected Button mAddButton;

    protected DatabaseHelper mDbHelper;
    protected List<String> mCategoriesDropdownItems = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deduction);

        // Displays back button in the navbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAmountInput = (EditText)findViewById(R.id.amount_input);
        mUserCategoriesSpinner = (Spinner)findViewById(R.id.user_categories_spinner);
        mCategoryInput = (EditText)findViewById(R.id.category_input);
        mAddButton = (Button)findViewById(R.id.add_button);
        mDbHelper = new DatabaseHelper(this);

        // Populates dropdown
        mCategoriesDropdownItems.add("Item 1");
        mCategoriesDropdownItems.add("Item 2");
        mCategoriesDropdownItems.add("Item 3");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.category_spinner_item,
                mCategoriesDropdownItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUserCategoriesSpinner.setAdapter(adapter);

        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();

                double amount = Double.valueOf(mAmountInput.getText().toString());
                Expenditure expenditure = new Expenditure(amount, mCategoryInput.getText().toString(), date);

                mDbHelper.addExpenditure(expenditure);
                mDbHelper.updateBalance(mDbHelper.getLatestBudget().getBalance() - expenditure.getAmount());

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
        onBackPressed();

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_log_out) {
            // TODO: Actually log the user out
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
