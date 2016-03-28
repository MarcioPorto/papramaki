package com.papramaki.papramaki.database;

import android.provider.BaseColumns;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



/**
 * Created by paulchery on 3/23/16.
 */
public class ExpenditureContract {


    public ExpenditureContract(){}


    public static abstract class Expenditure implements BaseColumns {
        public static final String TABLE_NAME = "expenditures";
        public static final String COLUMN_NAME_EXPENDITURE_ID = "id";
        public static final String COLUMN_NAME_BUDGET_ID = "budget";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_DATE = "date";
        //public static final String COLUMN_NAME_CREATION_DATE = "date";
        //public static final String COLUMN_NAME_HISTORY = "history";
        public static final String COLUMN_NAME_NULLABLE = "NULLHACK";
        private static final String TEXT_TYPE = " TEXT";
        private static final String REAL_TYPE = " REAL";
        private static final String INT_PRIMARY_KEY = " INTEGER PRIMARY KEY";
        private static final String INT_FOREIGN_KEY = " INTEGER FOREIGN KEY";
        private static final String INT = " INTEGER";
        private static final String BLOB_TYPE = " BLOB";
        private static final String COMMA_SEP = ", ";

        //Should these two be public?
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Expenditure.TABLE_NAME + " (" +
                        Expenditure.COLUMN_NAME_EXPENDITURE_ID + INT_PRIMARY_KEY + COMMA_SEP +
                        Expenditure.COLUMN_NAME_BUDGET_ID + INT + COMMA_SEP +
                        Expenditure.COLUMN_NAME_AMOUNT + REAL_TYPE + COMMA_SEP +
                        Expenditure.COLUMN_NAME_CATEGORY + TEXT_TYPE + COMMA_SEP +
                        Expenditure.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                        "FOREIGN KEY (" + Expenditure.COLUMN_NAME_BUDGET_ID + ") REFERENCES " + BudgetContract.Budget.TABLE_NAME + "(" +
                        BudgetContract.Budget.COLUMN_NAME_BUDGET_ID + ")"+
                        ");";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Expenditure.TABLE_NAME;

    }



}


