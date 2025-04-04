package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.entity.User;
import com.example.myapplication.entity.UserManager;

import java.util.ArrayList;
import java.util.List;

public class UserDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "app.db";
    private static final String TABLE_NAME = "users";
    private static final int DB_VERSION = 1;
    private static UserDBHelper mHelper = null;
    private SQLiteDatabase mRDB = null;
    private SQLiteDatabase mWDB = null;

    private UserDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static UserDBHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new UserDBHelper(context);
        }

        return mHelper;
    }

    public SQLiteDatabase openReadLink() {
        if (mRDB == null || !mRDB.isOpen()) {
            mRDB = mHelper.getReadableDatabase();
        }

        return mRDB;
    }

    public SQLiteDatabase openWriteLink() {
        if (mWDB == null || !mWDB.isOpen()) {
            mWDB = mHelper.getWritableDatabase();
        }

        return mWDB;
    }

    public void closeLink() {
        if (mRDB != null && mRDB.isOpen()) {
            mRDB.close();
            mRDB = null;
        }
        if (mWDB != null && mWDB.isOpen()) {
            mWDB.close();
            mWDB = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT," +
                "phone TEXT," +
                "password TEXT," +
                "interests TEXT" +
                ")";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public long insert(User user) {
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("phone", user.getPhone());
        values.put("password", user.getPassword());
        values.put("interests", user.getInterests());

        return mWDB.insert(TABLE_NAME, null, values);
    }

    public long delete(int id) {
        return mWDB.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
    }

    public long update(User user) {
        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("phone", user.getPhone());
        values.put("password", user.getPassword());
        values.put("interests", user.getInterests());

        return mWDB.update(TABLE_NAME, values, "id=?", new String[]{String.valueOf(user.getId())});
    }

    public long update(UserManager userManager) {
        ContentValues values = new ContentValues();
        values.put("name", userManager.getName());
        values.put("phone", userManager.getPhone());
        values.put("password", userManager.getPassword());
        values.put("interests", userManager.getInterests());

        return mWDB.update(TABLE_NAME, values, "phone=?", new String[]{userManager.getPhone()});
    }

    public User queryByPhoneNum(String phone) {
        User user = null;

        Cursor cursor = mRDB.query(TABLE_NAME, null, "phone=?", new String[]{phone}, null, null, null);
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(0));
            user.setName(cursor.getString(1));
            user.setPhone(cursor.getString(2));
            user.setPassword(cursor.getString(3));
            user.setInterests(cursor.getString(4));
        }

        return user;
    }

    public List<User> queryAll() {
        List<User> list = new ArrayList<>();
        User user;

        Cursor cursor = mRDB.query(TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            user = new User();
            user.setId(cursor.getInt(0));
            user.setName(cursor.getString(1));
            user.setPhone(cursor.getString(2));
            user.setPassword(cursor.getString(3));
            user.setInterests(cursor.getString(4));
            list.add(user);
        }

        return list;
    }

}
