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

    // Calculate if this trip had a discount (based on trip position for this user)
    public boolean hadDiscount(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        // Count trips before this one for the same user
        android.database.Cursor cursor = null;
        try {
            cursor = dbHelper.getReadableDatabase().query(
                DatabaseHelper.TABLE_TRIPS,
                new String[]{"COUNT(*)"},
                DatabaseHelper.COLUMN_TRIP_EMAIL + "=? AND " + DatabaseHelper.COLUMN_TRIP_ID + "<?",
                new String[]{this.email, String.valueOf(this.id)},
                null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                int previousTrips = cursor.getInt(0);
                return previousTrips >= 3; // Had discount if 3+ previous trips
            }
        } finally {
            if (cursor != null) cursor.close();
        }

        return false;
    }

    // Calculate discount amount if applicable
    public double getDiscountAmount(Context context) {
        if (hadDiscount(context)) {
            double totalExpenses = travelExpenses + customExpenses + mealExpenses + getActivityExpenses();
            return totalExpenses * 0.10; // 10% discount
        }
        return 0.0;
    }

    // Calculate activity expenses based on activities string
    private double getActivityExpenses() {
        double activityTotal = 0.0;
        if (activities != null) {
            if (activities.contains("Hiking")) activityTotal += 450;
            if (activities.contains("Bus")) activityTotal += 500;
            if (activities.contains("Sightseeing")) activityTotal += 2500;
            if (activities.contains("Museum")) activityTotal += 150;
        }
        return activityTotal;
    }

    public double getTotalExpenses() {
        return travelExpenses + customExpenses + mealExpenses + getActivityExpenses();
    }

    public double getFinalTotal(Context context) {
        return getTotalExpenses() - getDiscountAmount(context);
    }

    public static List<Trip> getTripsForUser(Context context, String email) {
        List<Trip> trips = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        android.database.Cursor cursor = null;

        // Normalize email for comparison (trim and lowercase)
        String normalizedEmail = email != null ? email.trim().toLowerCase() : "";

        // Debug logging
        android.util.Log.d("TripModel", "Querying trips for email: '" + email + "' (normalized: '" + normalizedEmail + "')");

        try {
            cursor = dbHelper.getReadableDatabase().query(
                    DatabaseHelper.TABLE_TRIPS,
                    null,
                    "LOWER(TRIM(" + DatabaseHelper.COLUMN_TRIP_EMAIL + "))=?",
                    new String[]{normalizedEmail},
                    null,
                    null,
                    DatabaseHelper.COLUMN_TRIP_START_DATE + " DESC"
            );

            android.util.Log.d("TripModel", "Cursor count: " + (cursor != null ? cursor.getCount() : "null"));

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

                    android.util.Log.d("TripModel", "Found trip: " + destination + " for email: '" + tripEmail + "'");

                    trips.add(new Trip(id, destination, startDate, endDate, activities, travelExpenses, customExpenses, mealExpenses, notes, tripEmail));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        android.util.Log.d("TripModel", "Returning " + trips.size() + " trips");
        return trips;
    }

    // Debug method to get all trips
    public static List<Trip> getAllTrips(Context context) {
        List<Trip> trips = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        android.database.Cursor cursor = null;

        android.util.Log.d("TripModel", "Getting ALL trips from database");

        try {
            cursor = dbHelper.getReadableDatabase().query(
                    DatabaseHelper.TABLE_TRIPS,
                    null,
                    null,
                    null,
                    null,
                    null,
                    DatabaseHelper.COLUMN_TRIP_START_DATE + " DESC"
            );

            android.util.Log.d("TripModel", "Total trips in database: " + (cursor != null ? cursor.getCount() : "null"));

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

                    android.util.Log.d("TripModel", "DB Trip: " + destination + " | Email: '" + tripEmail + "'");

                    trips.add(new Trip(id, destination, startDate, endDate, activities, travelExpenses, customExpenses, mealExpenses, notes, tripEmail));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return trips;
    }
}
