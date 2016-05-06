package com.papramaki.papramaki.database;

import android.provider.BaseColumns;

public class UserContract {

    public UserContract(){}

    /**
     * Defines the schema of the user table in the database
     */
    public static abstract class User implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String ID = "id";
        public static final String ACCESS_TOKEN = "access_token";
        public static final String CLIENT = "client";
        public static final String UID = "uid";
        private static final String TEXT_TYPE = " TEXT";
        private static final String INT_TYPE = " INTEGER";
        private static final String COMMA_SEP = ", ";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + User.TABLE_NAME + " (" +
                        User.ID + INT_TYPE + COMMA_SEP +
                        User.UID + TEXT_TYPE + COMMA_SEP +
                        User.ACCESS_TOKEN + TEXT_TYPE + COMMA_SEP +
                        User.CLIENT + TEXT_TYPE + ");";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

}
