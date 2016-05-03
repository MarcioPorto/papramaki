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

/**
 * Created by paulchery on 4/21/16.
 */
public class APIHelper {

    private Context context;
    private Activity activity;

    public APIHelper(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }


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
     * @param jsonData - the json response in String format
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
     * @param jsonData - the json response in String format
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

    public Budget getLatestBudget(String jsonData) throws JSONException {

        Budget budget = new Budget();
        if(!jsonData.equals("[]")) {
            JSONArray response = new JSONArray(jsonData);

            JSONObject currentBudget = response.getJSONObject(0);
            int amount = currentBudget.getInt("amount");
            int duration = currentBudget.getInt("duration");
            int budgetId = currentBudget.getInt("id");
            boolean budgetExpired = currentBudget.getBoolean("expired");
            String budgetExpirationDate = currentBudget.getString("expiration_date");
            String budgetCreationDate = currentBudget.getString("created_at");
//            int year = Integer.parseInt(budgetExpirationDate.substring(0, 4));
//            Log.d(TAG, String.valueOf(year));
//            int month = Integer.parseInt(budgetExpirationDate.substring(5, 7));
//            Log.d(TAG, String.valueOf(month));
//            int day = Integer.parseInt(budgetExpirationDate.substring(8, 10));
//            Log.d(TAG, String.valueOf(day));
//            Date date = new Date(year, month, day);

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

    public Budget getLatestBudgetFromPR(String jsonData) throws JSONException {

        Budget budget = new Budget();

        JSONObject currentBudget = new JSONObject(jsonData);
        int amount = currentBudget.getInt("amount");
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

    public History getHistory(String jsonData) throws JSONException {

        JSONArray response = new JSONArray(jsonData);
        History history = new History();
        List<Expenditure> expenditureList = new ArrayList<Expenditure>();

        //TODO: If first time user, no budget exists so don't go through loop
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

    public List<Category> getCategories(String jsonData) throws JSONException {

        JSONArray response = new JSONArray(jsonData);

        List<Category> categoryList = new ArrayList<Category>();

        //TODO: If first time user, no budget exists so don't go through loop
        if(!jsonData.equals("[]")) {
            for (int i = 0; i < response.length(); i++) {
                JSONObject currentCategory = response.getJSONObject(i);
                String name = currentCategory.getString("name");
                String color = currentCategory.getString("color");
                int id = currentCategory.getInt("id");
                JSONArray expenditures = currentCategory.getJSONArray("expenditures");
                List<Expenditure> expenditureList = new ArrayList<Expenditure>();
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


    public double getLatestBalance(String jsonData) throws JSONException {

        JSONObject object = new JSONObject(jsonData);
        return object.getDouble("amount");

    }

    public JSONObject getUserInfoFromResponse(String jsonData) throws JSONException {

        JSONObject response = new JSONObject(jsonData);
        JSONObject data = response.getJSONObject("data");

        return data;
    }

    public Date formatDate(String dateString){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = sdf.parse(dateString, new ParsePosition(0));
        return date;
    }


}
