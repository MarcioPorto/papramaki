package com.papramaki.papramaki.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.papramaki.papramaki.models.Budget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paulchery on 3/20/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Budget.db";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        database.execSQL(BudgetContract.Budget.SQL_CREATE_ENTRIES);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(BudgetContract.Budget.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void addBudget(Budget budget){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BudgetContract.Budget.COLUMN_NAME_AMOUNT, budget.getBudget());// budget amount
        values.put(BudgetContract.Budget.COLUMN_NAME_BALANCE, budget.getBalance()); // budget balance


        if(values != null) {
            // Inserting Row
            db.insert(BudgetContract.Budget.TABLE_NAME, null, values);
        }
        db.close(); // Closing database connection
    }

    public double viewLatestBudget(){
        String LATEST_BUDGET =
            "SELECT " + BudgetContract.Budget.COLUMN_NAME_AMOUNT +
                    " FROM " + BudgetContract.Budget.TABLE_NAME +
                    " WHERE id = (SELECT MAX(id) FROM " +
                    BudgetContract.Budget.TABLE_NAME + ");";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(LATEST_BUDGET, null);
        cursor.moveToLast();
        //why does second index work? what is in index 1?
        return cursor.getDouble(0);
    }

    public List<Double> viewBudgets() {
        List<Double> budgetList = new ArrayList<Double>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + BudgetContract.Budget.TABLE_NAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                double amount = cursor.getDouble(2);
                // Adding contact to list
                budgetList.add(amount);
            } while (cursor.moveToNext());
        }

        // return contact list
        return budgetList;
    }

  /*  public void addBalance(double balance){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BudgetContract.Budget.COLUMN_NAME_BALANCE, balance); // budget amount

        if(values != null) {
            db.insert(BudgetContract.Budget.TABLE_NAME, null, values);
        }
        db.close(); // Closing database connection
    }*/

    public double viewLatestBalance(){
        String LATEST_BALANCE =
                "SELECT " + BudgetContract.Budget.COLUMN_NAME_BALANCE +
                        " FROM " + BudgetContract.Budget.TABLE_NAME +
                        " WHERE id = (SELECT MAX(id) FROM " +
                        BudgetContract.Budget.TABLE_NAME + ");";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(LATEST_BALANCE, null);
        cursor.moveToLast();
        //why does second index work? what is in index 1?
        return cursor.getDouble(0);
    }

    public void updateBalance(double balance){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BudgetContract.Budget.COLUMN_NAME_BALANCE, balance);
        String selection = "id = (SELECT MAX(id) FROM " + BudgetContract.Budget.TABLE_NAME + ")";
        db.update(BudgetContract.Budget.TABLE_NAME, values, selection , null);
    }
}