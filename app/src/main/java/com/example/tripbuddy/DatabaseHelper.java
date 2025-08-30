package com.example.tripbuddy;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tripbuddy.db";
    private static final int DATABASE_VERSION = 6;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_AGE = "age";

    public static final String TABLE_TRIPS = "trips";
    public static final String COLUMN_TRIP_ID = "trip_id";
    public static final String COLUMN_TRIP_DESTINATION = "trip_destination";
    public static final String COLUMN_TRIP_START_DATE = "trip_start_date";
    public static final String COLUMN_TRIP_END_DATE = "trip_end_date";
    public static final String COLUMN_TRIP_ACTIVITIES = "trip_activities";
    public static final String COLUMN_TRIP_TRAVEL_EXPENSES = "trip_travel_expenses";
    public static final String COLUMN_TRIP_NOTES = "trip_notes";
    public static final String COLUMN_TRIP_CUSTOM_EXPENSES = "trip_custom_expenses";
    public static final String COLUMN_TRIP_MEAL_EXPENSES = "trip_meal_expenses";
    public static final String COLUMN_TRIP_EMAIL = "email"; // Foreign key

    public static final String TABLE_MEMORIES = "memories";
    public static final String COLUMN_MEMORY_ID = "memory_id";
    public static final String COLUMN_MEMORY_TITLE = "memory_title";
    public static final String COLUMN_MEMORY_DESCRIPTION = "memory_description";
    public static final String COLUMN_MEMORY_DATE = "memory_date";
    public static final String COLUMN_MEMORY_IMAGE_URI = "memory_image_uri";
    public static final String COLUMN_MEMORY_SONG_URI = "memory_song_uri";
    public static final String COLUMN_MEMORY_EMAIL = "email"; // Foreign key

    private static final String TABLE_CREATE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_PHONE + " TEXT, " +
                    COLUMN_AGE + " INTEGER);";

    private static final String TABLE_CREATE_TRIPS =
            "CREATE TABLE " + TABLE_TRIPS + " (" +
                    COLUMN_TRIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TRIP_DESTINATION + " TEXT, " +
                    COLUMN_TRIP_START_DATE + " TEXT, " +
                    COLUMN_TRIP_END_DATE + " TEXT, " +
                    COLUMN_TRIP_ACTIVITIES + " TEXT, " +
                    COLUMN_TRIP_TRAVEL_EXPENSES + " REAL, " +
                    COLUMN_TRIP_CUSTOM_EXPENSES + " REAL, " +
                    COLUMN_TRIP_MEAL_EXPENSES + " REAL, " +
                    COLUMN_TRIP_NOTES + " TEXT, " +
                    COLUMN_TRIP_EMAIL + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_TRIP_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_EMAIL + ") ON DELETE CASCADE);";

    private static final String TABLE_CREATE_MEMORIES =
            "CREATE TABLE " + TABLE_MEMORIES + " (" +
                    COLUMN_MEMORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MEMORY_TITLE + " TEXT, " +
                    COLUMN_MEMORY_DESCRIPTION + " TEXT, " +
                    COLUMN_MEMORY_DATE + " TEXT, " +
                    COLUMN_MEMORY_IMAGE_URI + " TEXT, " +
                    COLUMN_MEMORY_SONG_URI + " TEXT, " +
                    COLUMN_MEMORY_EMAIL + " TEXT, " +
                    "FOREIGN KEY(" + COLUMN_MEMORY_EMAIL + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_EMAIL + ") ON DELETE CASCADE);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE_USERS);
        db.execSQL(TABLE_CREATE_TRIPS);
        db.execSQL(TABLE_CREATE_MEMORIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRIPS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Add a user
    public long addUser(String email, String password, String phone, int age) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_AGE, age);
        return db.insert(TABLE_USERS, null, values);
    }

    // Add a trip
    public long addTrip(String destination, String startDate, String endDate, String activities, double travelExpenses, double customExpenses, double mealExpenses, String notes, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TRIP_DESTINATION, destination);
        values.put(COLUMN_TRIP_START_DATE, startDate);
        values.put(COLUMN_TRIP_END_DATE, endDate);
        values.put(COLUMN_TRIP_ACTIVITIES, activities);
        values.put(COLUMN_TRIP_TRAVEL_EXPENSES, travelExpenses);
        values.put(COLUMN_TRIP_CUSTOM_EXPENSES, customExpenses);
        values.put(COLUMN_TRIP_MEAL_EXPENSES, mealExpenses);
        values.put(COLUMN_TRIP_NOTES, notes);
        values.put(COLUMN_TRIP_EMAIL, email);
        return db.insert(TABLE_TRIPS, null, values);
    }

    // Add a memory
    public long addMemory(String title, String description, String date, String imageUri, String songUri, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MEMORY_TITLE, title);
        values.put(COLUMN_MEMORY_DESCRIPTION, description);
        values.put(COLUMN_MEMORY_DATE, date);
        values.put(COLUMN_MEMORY_IMAGE_URI, imageUri);
        values.put(COLUMN_MEMORY_SONG_URI, songUri);
        values.put(COLUMN_MEMORY_EMAIL, email);
        return db.insert(TABLE_MEMORIES, null, values);
    }

    // Check if user exists with email and password
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?";
        String[] selectionArgs = { email, password };
        Cursor cursor = db.query(TABLE_USERS, null, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Get trip count for a user
    public int getTripCountForUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_TRIP_EMAIL + "=?";
        String[] selectionArgs = { email };
        Cursor cursor = db.query(TABLE_TRIPS, null, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        android.util.Log.d("DatabaseHelper", "Trip count for " + email + ": " + count);
        return count;
    }

    // Debug method to get all trips from database
    public List<android.database.Cursor> getAllTripsDebug() {
        SQLiteDatabase db = this.getReadableDatabase();
        android.util.Log.d("DatabaseHelper", "=== DATABASE DEBUG INFO ===");

        // Check if trips table exists
        Cursor tableCheck = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='" + TABLE_TRIPS + "'", null);
        android.util.Log.d("DatabaseHelper", "Trips table exists: " + (tableCheck.getCount() > 0));
        tableCheck.close();

        // Get all trips with raw query for debugging
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TRIPS, null);
        android.util.Log.d("DatabaseHelper", "Total trips in database: " + cursor.getCount());

        if (cursor.moveToFirst()) {
            do {
                StringBuilder tripInfo = new StringBuilder("Trip: ");
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    tripInfo.append(cursor.getColumnName(i)).append("='")
                           .append(cursor.getString(i)).append("' ");
                }
                android.util.Log.d("DatabaseHelper", tripInfo.toString());
            } while (cursor.moveToNext());
        }
        cursor.close();

        return new ArrayList<>();
    }
}
