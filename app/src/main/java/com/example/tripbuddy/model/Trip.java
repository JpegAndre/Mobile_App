package com.example.tripbuddy.model;

import android.content.Context;
import com.example.tripbuddy.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class Trip {
    public int id;
    public String destination;
    public String startDate;
    public String endDate;
    public String activities;
    public double travelExpenses;
    public double customExpenses;
    public double mealExpenses;
    public String notes;
    public String email;

    public Trip(int id, String destination, String startDate, String endDate, String activities, double travelExpenses, double customExpenses, double mealExpenses, String notes, String email) {
        this.id = id;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activities = activities;
        this.travelExpenses = travelExpenses;
        this.customExpenses = customExpenses;
        this.mealExpenses = mealExpenses;
        this.notes = notes;
        this.email = email;
    }

    public static List<Trip> getTripsForUser(Context context, String email) {
        List<Trip> trips = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        android.database.Cursor cursor = dbHelper.getReadableDatabase().query(
                DatabaseHelper.TABLE_TRIPS,
                null,
                DatabaseHelper.COLUMN_TRIP_EMAIL + "=?",
                new String[]{email},
                null,
                null,
                DatabaseHelper.COLUMN_TRIP_START_DATE + " DESC"
        );
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRIP_ID));
                String destination = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRIP_DESTINATION));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRIP_START_DATE));
                String endDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRIP_END_DATE));
                String activities = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRIP_ACTIVITIES));
                double travelExpenses = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRIP_TRAVEL_EXPENSES));
                double customExpenses = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRIP_CUSTOM_EXPENSES));
                double mealExpenses = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRIP_MEAL_EXPENSES));
                String notes = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRIP_NOTES));
                String tripEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TRIP_EMAIL));
                trips.add(new Trip(id, destination, startDate, endDate, activities, travelExpenses, customExpenses, mealExpenses, notes, tripEmail));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return trips;
    }
}
