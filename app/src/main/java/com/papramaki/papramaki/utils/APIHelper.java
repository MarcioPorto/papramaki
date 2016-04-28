package com.papramaki.papramaki.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.papramaki.papramaki.models.Budget;
import com.papramaki.papramaki.models.Category;
import com.papramaki.papramaki.models.Expenditure;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    public void alertUserAboutError() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("There was an error processing your request").setTitle("Oops!");
                builder.create();
            }
        });
    }

    public String errorIdentifier(String jsonData) throws JSONException{
        JSONObject response = new JSONObject(jsonData);
        JSONArray errorType = response.getJSONArray("errors");
        final String errorText = errorType.getString(0);
        return errorText;
    }
    //Methods to retrieve data from JSON Objects
    public String signUpErrorIdentifier(String jsonData) throws JSONException{
        JSONObject response = new JSONObject(jsonData);
        JSONObject errorType = response.getJSONObject("errors");
        JSONArray fullMessage = errorType.getJSONArray("full_messages");
        final String errorText = fullMessage.getString(0);
        return errorText;
    }
    public Budget getLatestBudget(String jsonData) throws JSONException {

        Budget budget = new Budget();
        if(!jsonData.equals("[]")) {
            JSONArray response = new JSONArray(jsonData);

            JSONObject currentBudget = response.getJSONObject(0);
            int amount = currentBudget.getInt("amount");
            int duration = currentBudget.getInt("duration");
            int budgetId = currentBudget.getInt("id");

            budget.setBudget(amount);
            budget.setId(budgetId);
            budget.setDuration(duration);
        }
        return budget;
    }


    public List<Expenditure> getExpenditures(String jsonData) throws JSONException {

        JSONArray response = new JSONArray(jsonData);
        String returnString = "";
        List<Expenditure> history = new ArrayList<Expenditure>();
        int value = 0;

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
                    history.add(expenditure);

                }
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
