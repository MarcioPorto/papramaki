package com.papramaki.papramaki.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by paulchery on 3/19/16.
 */
public final class BudgetContract {


    public BudgetContract(){}


    public static abstract class Budget implements BaseColumns {
        public static final String TABLE_NAME = "budget";
        public static final String COLUMN_NAME_BUDGET_ID = "id";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_BALANCE = "balance";
        //public static final String COLUMN_NAME_CREATION_DATE = "date";
        //public static final String COLUMN_NAME_HISTORY = "history";
        public static final String COLUMN_NAME_NULLABLE = "NULLHACK";
        private static final String TEXT_TYPE = " TEXT";
        private static final String REAL_TYPE = " REAL";
        private static final String BLOB_TYPE = " BLOB";
        private static final String COMMA_SEP = ",";

        //Should these two be public?
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + Budget.TABLE_NAME + " (" +
                        Budget.COLUMN_NAME_BUDGET_ID + " INTEGER PRIMARY KEY, " +
                        //Budget.COLUMN_NAME_BUDGET_ID + TEXT_TYPE + COMMA_SEP +
                        Budget.COLUMN_NAME_AMOUNT + REAL_TYPE + COMMA_SEP +
                        Budget.COLUMN_NAME_BALANCE + REAL_TYPE +
                        //Budget.COLUMN_NAME_CREATION_DATE + TEXT_TYPE + COMMA_SEP +
                        //Budget.COLUMN_NAME_HISTORY + BLOB_TYPE +
                        ");";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + Budget.TABLE_NAME;

    }



}
