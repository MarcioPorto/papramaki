package com.papramaki.papramaki.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.papramaki.papramaki.models.Budget;
import com.papramaki.papramaki.models.Category;
import com.papramaki.papramaki.models.Expenditure;
import com.papramaki.papramaki.models.History;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class APIHelper {

    private Context context;
    private Activity activity;

    public APIHelper(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    /**
     * Checks if the User is connected to the internet.
     * @return      true or false
     */
    public boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    /**
     * More general errors, not related to login or signup
     * @param jsonData - the json response in String format
     * @throws JSONException
     */
    public void alertUserAboutError(String jsonData) throws JSONException{
        JSONObject response = new JSONObject(jsonData);
        final String errorText = response.getString("error");
//        MainActivity.runOnUI(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(MainActivity.getAppContext(), "There was an error", Toast.LENGTH_LONG).show();
//            }
//        });
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, errorText, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Shows errors, if they exist, when the user tries to log in
     * @param jsonData          the json response in String format
     * @throws JSONException
     */
    public void showLogInErrors(String jsonData) throws JSONException{
        JSONObject response = new JSONObject(jsonData);
        JSONArray errorType = response.getJSONArray("errors");
        final String errorText = errorType.getString(0);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, errorText, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Shows errors, if they exist, when the user tries to sign up
     * @param jsonData          the json response in String format
     * @throws JSONException
     */
    public void showSignUpErrors(String jsonData) throws JSONException{
        JSONObject response = new JSONObject(jsonData);
        JSONObject errorType = response.getJSONObject("errors");
        JSONArray fullMessage = errorType.getJSONArray("full_messages");
        final String errorText = fullMessage.getString(0);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, errorText, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Parses JSON data from the API to retrieve the most recent budget.
     * It creates a Budget object using the JSON data and returns it.
     * @param jsonData          JSON data from GET#budgets
     * @return                  Budget object from data
     * @throws JSONException
     */
    public Budget getLatestBudget(String jsonData) throws JSONException {
        Budget budget = new Budget();
        if(!jsonData.equals("[]")) {
            JSONArray response = new JSONArray(jsonData);

            JSONObject currentBudget = response.getJSONObject(0);
            double amount = currentBudget.getDouble("amount");
            int duration = currentBudget.getInt("duration");
            int budgetId = currentBudget.getInt("id");
            boolean budgetExpired = currentBudget.getBoolean("expired");
            String budgetExpirationDate = currentBudget.getString("expiration_date");
            String budgetCreationDate = currentBudget.getString("created_at");

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date expirationDate = new Date();
            try {
                expirationDate = df.parse(budgetExpirationDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Date creationDate = formatDate(budgetCreationDate);
            budget.setBudget(amount);
            budget.setId(budgetId);
            budget.setDuration(duration);
            budget.setExpired(budgetExpired);
            budget.setExpirationDate(expirationDate);
            budget.setCreationDate(creationDate);
        }
        return budget;
    }

    /**
     * Parses JSON data from the API to retrieve the most recent budget AFTER A PUT REQUEST.
     * It creates a Budget object using the JSON data and returns it.
     * @param jsonData          JSON data from PUT#budgets/:id
     * @return                  Budget object from data
     * @throws JSONException
     */
    public Budget getLatestBudgetFromPR(String jsonData) throws JSONException {

        Budget budget = new Budget();

        JSONObject currentBudget = new JSONObject(jsonData);
        double amount = currentBudget.getDouble("amount");
        int duration = currentBudget.getInt("duration");
        int budgetId = currentBudget.getInt("id");
        boolean budgetExpired = currentBudget.getBoolean("expired");
        String budgetExpirationDate = currentBudget.getString("expiration_date");
        String budgetCreationDate = currentBudget.getString("created_at");

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Date expirationDate = new Date();
        try {
            expirationDate = df.parse(budgetExpirationDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date creationDate = formatDate(budgetCreationDate);
        budget.setBudget(amount);
        budget.setId(budgetId);
        budget.setDuration(duration);
        budget.setExpired(budgetExpired);
        budget.setExpirationDate(expirationDate);
        budget.setCreationDate(creationDate);
        return budget;
    }

    /**
     * Parses JSON data from the API to retrieve the expenditures that belong to the most recent budget.
     * It creates a History object using the JSON data and returns it.
     * @param jsonData          JSON data from GET#budgets
     * @return                  History object from data
     * @throws JSONException
     */
    public History getHistory(String jsonData) throws JSONException {

        JSONArray response = new JSONArray(jsonData);
        History history = new History();
        List<Expenditure> expenditureList = new ArrayList<>();

        // TODO: If first time user, no budget exists so don't go through loop
        if(!jsonData.equals("[]")) {
            for (int i = 0; i < 1; i++) {
                JSONObject currentBudget = response.getJSONObject(i);
                JSONArray expenditures = currentBudget.getJSONArray("expenditures");
                Expenditure expenditure;

                for (int j = 0; j < expenditures.length(); j++) {
                    JSONObject currentExp = expenditures.getJSONObject(j);
                    double amount = currentExp.getDouble("amount");
                    int category_id = currentExp.getInt("category_id");
                    String dateString = currentExp.getString("created_at");
                    Date date = formatDate(dateString);
                    expenditure = new Expenditure(amount, category_id, date);
                    expenditureList.add(expenditure);

                }
                history.setExpenditureSum(currentBudget.getDouble("expenditure_sum_amount"));
                history.setExpenditures(expenditureList);
            }
        }
        return history;

    }

    /**
     * Parses JSON data from the API to retrieve the categories that belong to the current User.
     * @param jsonData          JSON data from GET#categories
     * @return                  a list of categories that have been previously entered by the User
     * @throws JSONException
     */
    public List<Category> getCategories(String jsonData) throws JSONException {

        JSONArray response = new JSONArray(jsonData);

        List<Category> categoryList = new ArrayList<>();

        //TODO: If first time user, no budget exists so don't go through loop
        if(!jsonData.equals("[]")) {
            for (int i = 0; i < response.length(); i++) {
                JSONObject currentCategory = response.getJSONObject(i);
                String name = currentCategory.getString("name");
                String color = currentCategory.getString("color");
                int id = currentCategory.getInt("id");
                JSONArray expenditures = currentCategory.getJSONArray("expenditures");
                List<Expenditure> expenditureList = new ArrayList<>();
                for (int j = 0; j < expenditures.length(); j++) {
                    JSONObject currentExpenditure = expenditures.getJSONObject(j);
                    if(currentExpenditure.getInt("budget_id") == LocalData.budget.getId()){
                        Expenditure expenditure = new Expenditure();
                        expenditure.setAmount(currentExpenditure.getDouble("amount"));
                        expenditure.setDate(formatDate(currentExpenditure.getString("created_at")));
                        expenditureList.add(expenditure);
                    }
                }
                Category category = new Category();
                category.setName(name);
                category.setColor(color);
                category.setExpenditures(expenditureList);
                category.setId(id);
                categoryList.add(category);
            }
        }
        return categoryList;

    }

    /**
     * Parses JSON data from the API to retrieve the balance that belong to the current User.
     * @param jsonData          JSON data from GET#balances
     * @return                  the balance amount as a double
     * @throws JSONException
     */
    public double getLatestBalance(String jsonData) throws JSONException {
        JSONObject object = new JSONObject(jsonData);
        return object.getDouble("amount");
    }

    /**
     * Parses JSON data from the API to retrieve info from the current User.
     * @param jsonData          JSON data
     * @return                  a JSONObject
     * @throws JSONException
     */
    public JSONObject getUserInfoFromResponse(String jsonData) throws JSONException {
        JSONObject response = new JSONObject(jsonData);
        return response.getJSONObject("data");
    }

    /**
     * Converts a String to a Date object
     * @param dateString        date as a String
     * @return                  date as Date
     */
    public Date formatDate(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        return sdf.parse(dateString, new ParsePosition(0));
    }

}
