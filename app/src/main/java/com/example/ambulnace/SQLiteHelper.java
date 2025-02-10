package com.example.ambulnace;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "DriverDatabase.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "DriverInfo";

    // Table Columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DRIVER_NAME = "driver_name";
    public static final String COLUMN_VEHICLE_NUMBER = "vehicle_number";
    public static final String COLUMN_PHONE_NUMBER = "phone_number";
    public static final String COLUMN_LOCATION = "location";  // Added location column
    public static final String COLUMN_PASSWORD = "password";


    // SQL statement to create the table
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_DRIVER_NAME + " TEXT, "
            + COLUMN_VEHICLE_NUMBER + " TEXT, "
            + COLUMN_PHONE_NUMBER + " TEXT, "
            + COLUMN_LOCATION + " TEXT, "  // Added location column to table creation
            + COLUMN_PASSWORD + " TEXT)";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table when the database is created
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert new user data with location
    public boolean insertUser(String driverName, String vehicleNumber, String phoneNumber, String location, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DRIVER_NAME, driverName);
        contentValues.put(COLUMN_VEHICLE_NUMBER, vehicleNumber);
        contentValues.put(COLUMN_PHONE_NUMBER, phoneNumber);
        contentValues.put(COLUMN_LOCATION, location);  // Added location
        contentValues.put(COLUMN_PASSWORD, password);

        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();



        return result != -1;  // If result is -1, insertion failed
    }

    // Validate login credentials
    public boolean checkUser(String driverName, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_DRIVER_NAME + " =? AND " + COLUMN_PASSWORD + " =?";
        String[] selectionArgs = {driverName, password};

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        return cursorCount > 0;
    }

    public Cursor getAllDrivers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }
}
