package com.example.tripbuddy;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tripbuddy.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    public static final String TABLE_TRIPS = "trips";
    public static final String COLUMN_TRIP_ID = "trip_id";
    public static final String COLUMN_TRIP_NAME = "trip_name";
    public static final String COLUMN_TRIP_DESTINATION = "trip_destination";
    public static final String COLUMN_TRIP_START_DATE = "trip_start_date";
    public static final String COLUMN_TRIP_END_DATE = "trip_end_date";
    public static final String COLUMN_TRIP_ACTIVITIES = "trip_activities";
    public static final String COLUMN_TRIP_TRAVEL_EXPENSES = "trip_travel_expenses";
    public static final String COLUMN_TRIP_NOTES = "trip_notes";
    public static final String COLUMN_TRIP_CUSTOM_EXPENSES = "trip_custom_expenses";
    public static final String COLUMN_TRIP_MEAL_EXPENSES = "trip_meal_expenses";
    public static final String COLUMN_TRIP_DATE = "trip_date";
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
                    COLUMN_PASSWORD + " TEXT);";

    private static final String TABLE_CREATE_TRIPS =
            "CREATE TABLE " + TABLE_TRIPS + " (" +
                    COLUMN_TRIP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TRIP_NAME + " TEXT, " +
                    COLUMN_TRIP_DESTINATION + " TEXT, " +
                    COLUMN_TRIP_START_DATE + " TEXT, " +
                    COLUMN_TRIP_END_DATE + " TEXT, " +
                    COLUMN_TRIP_ACTIVITIES + " TEXT, " +
                    COLUMN_TRIP_TRAVEL_EXPENSES + " REAL, " +
                    COLUMN_TRIP_NOTES + " TEXT, " +
                    COLUMN_TRIP_CUSTOM_EXPENSES + " REAL, " +
                    COLUMN_TRIP_MEAL_EXPENSES + " REAL, " +
                    COLUMN_TRIP_DATE + " TEXT, " +
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
}
