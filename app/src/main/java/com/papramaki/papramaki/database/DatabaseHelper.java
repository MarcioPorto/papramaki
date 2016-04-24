package com.papramaki.papramaki.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.papramaki.papramaki.models.Budget;
import com.papramaki.papramaki.models.Expenditure;
import com.papramaki.papramaki.models.User;

import java.util.ArrayList;
import java.util.Date;
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
        database.execSQL(ExpenditureContract.Expenditure.SQL_CREATE_ENTRIES);

    }
    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(BudgetContract.Budget.SQL_DELETE_ENTRIES);
        db.execSQL(ExpenditureContract.Expenditure.SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public int getLatestBudgetId(){
        String LATEST_ID =
                "SELECT " + BudgetContract.Budget.COLUMN_NAME_BUDGET_ID +
                        " FROM " + BudgetContract.Budget.TABLE_NAME +
                        " WHERE " + BudgetContract.Budget.COLUMN_NAME_BUDGET_ID +
                        " = (SELECT MAX(" + BudgetContract.Budget.COLUMN_NAME_BUDGET_ID + ") FROM " +
                        BudgetContract.Budget.TABLE_NAME + ");";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(LATEST_ID, null);
        int id = -1;
        if(cursor != null && cursor.moveToLast()){
            id = cursor.getInt(0);
        }
        return id;
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


    public void updateBalance(double balance){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BudgetContract.Budget.COLUMN_NAME_BALANCE, balance);
        String selection = "id = (SELECT MAX(id) FROM " + BudgetContract.Budget.TABLE_NAME + ")";
        db.update(BudgetContract.Budget.TABLE_NAME, values, selection, null);
    }

    public void addExpenditure(Expenditure expenditure){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ExpenditureContract.Expenditure.COLUMN_NAME_AMOUNT, expenditure.getAmount());// expenditure amount
        values.put(ExpenditureContract.Expenditure.COLUMN_NAME_CATEGORY, expenditure.getCategory());
        values.put(ExpenditureContract.Expenditure.COLUMN_NAME_DATE, expenditure.getDate().getTime());
        values.put(ExpenditureContract.Expenditure.COLUMN_NAME_BUDGET_ID, this.getLatestBudgetId());


        if(values != null) {
            // Inserting Row
            db.insert(ExpenditureContract.Expenditure.TABLE_NAME, null, values);
        }
        db.close(); // Closing database connection

    }
    public int count(String table){
        SQLiteDatabase db = this.getReadableDatabase();
        String count =  "SELECT COUNT(*) FROM " + table + ";";
        Cursor mCursor = db.rawQuery(count, null);
        mCursor.moveToFirst();
        int iCount = mCursor.getInt(0);
        return iCount;
    }

    public List<Expenditure> getLatestExpenditures(){
        List<Expenditure> history = new ArrayList<Expenditure>();
        if(count(ExpenditureContract.Expenditure.TABLE_NAME) > 0 ) {
            String selectQuery = "SELECT * FROM " + ExpenditureContract.Expenditure.TABLE_NAME +
                    " WHERE " + ExpenditureContract.Expenditure.COLUMN_NAME_BUDGET_ID +
                    "=" + getLatestBudgetId() +
                    ";";

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            Expenditure expenditure;
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    double amount = cursor.getDouble(2);
                    String category = cursor.getString(3);
                    long dateLong = cursor.getLong(4);

                    Date date = new Date();
                    date.setTime(dateLong);
                    expenditure = new Expenditure(amount,category, date);

                    history.add(expenditure);
                } while (cursor.moveToNext());
            }

            // return contact list
            return history;
         //TODO: remove else
        }
        else{
            return history;
        }


    }
    public Budget getLatestBudget(){
        String latestBudget =
                "SELECT * FROM " + BudgetContract.Budget.TABLE_NAME +
                        " WHERE id = (SELECT MAX(id) FROM " +
                        BudgetContract.Budget.TABLE_NAME + ");";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(latestBudget, null);
        Budget budget = new Budget(0);
        if(cursor != null && cursor.moveToLast()) {
            double amount = cursor.getDouble(1);
            double balance = cursor.getDouble(2);
            budget.setBudget(amount);
            budget.setBalance(balance);
        }
        return budget;
    }


    public void addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserContract.User.UID, user.getUid());
        values.put(UserContract.User.ACCESS_TOKEN, user.getAccessToken());
        values.put(UserContract.User.CLIENT, user.getClient());

        if(values != null) {
            // Inserting Row
            db.insert(UserContract.User.TABLE_NAME, null, values);
        }
        db.close(); // Closing database connection

    }
    public User getUser(){
        String query =
                "SELECT * FROM " + UserContract.User.TABLE_NAME +
                        " WHERE id = (SELECT MAX(id) FROM " +
                        UserContract.User.TABLE_NAME + ");";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        User user = new User();
        if(cursor != null && cursor.moveToLast()){
            String uid = cursor.getString(1);
            String accessToken = cursor.getString(2);
            String client = cursor.getString(3);
            user.setAccessToken(accessToken);
            user.setClient(client);
            user.setUid(uid);

        }
        return user;

    }


}