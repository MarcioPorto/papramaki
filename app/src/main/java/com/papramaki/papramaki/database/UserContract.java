package com.papramaki.papramaki.database;

import android.provider.BaseColumns;

/**
 * Created by paulchery on 4/21/16.
 */
public class UserContract {


    public UserContract(){

    }
    public static abstract class User implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String ID = "id";
        public static final String ACCESS_TOKEN = "access_token";
        public static final String CLIENT = "client";
        public static final String UID = "uid";
        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ", ";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY, " +
                        UID + TEXT_TYPE + COMMA_SEP +
                        ACCESS_TOKEN + TEXT_TYPE + COMMA_SEP +
                        CLIENT + TEXT_TYPE + ");";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

    }
}
