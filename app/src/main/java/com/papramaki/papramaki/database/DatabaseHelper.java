package com.papramaki.papramaki.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.papramaki.papramaki.models.User;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "User.db";

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates database from the user contract
     */
    @Override
    public void onCreate(SQLiteDatabase database){
        database.execSQL(UserContract.User.SQL_CREATE_ENTRIES);

    }

    /**
     * Enables foreign key constraints for the database (not used)
     */
    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(UserContract.User.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Adds User to a local database on the phone.
     * This is used to maintain the Auth-Token, Client and Uid in order to persist the session.
     * @param user      the User to be added
     */
    public void addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserContract.User.UID, user.getUid());
        values.put(UserContract.User.ACCESS_TOKEN, user.getAccessToken());
        values.put(UserContract.User.CLIENT, user.getClient());
        values.put(UserContract.User.ID, user.getUser_id());

        if(values != null) {
            // Inserting Row
            db.insert(UserContract.User.TABLE_NAME, null, values);
        }
        db.close(); // Closing database connection
    }

    /**
     * Gets User from the local SQL database.
     * @return          the User
     */
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
            int user_id = cursor.getInt(0);
            user.setAccessToken(accessToken);
            user.setClient(client);
            user.setUid(uid);
            user.setUser_id(user_id);

        }
        db.close();
        return user;
    }

    /**
     * Deletes the User info saved above from the local database.
     */
    public void userLogout(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(UserContract.User.TABLE_NAME, null, null);
        db.close();
    }

}