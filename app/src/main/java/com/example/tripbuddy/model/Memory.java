package com.example.tripbuddy.model;

import android.content.Context;
import com.example.tripbuddy.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class Memory {
    public int id;
    public String title;
    public String description;
    public String date;
    public String imageUri;
    public String songUri;
    public String email;

    public Memory(int id, String title, String description, String date, String imageUri, String songUri, String email) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.imageUri = imageUri;
        this.songUri = songUri;
        this.email = email;
    }

    public static List<Memory> getMemoriesForUser(Context context, String email) {
        List<Memory> memories = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        android.database.Cursor cursor = null;

        // Normalize email for comparison (trim and lowercase)
        String normalizedEmail = email != null ? email.trim().toLowerCase() : "";

        // Debug logging
        android.util.Log.d("MemoryModel", "Querying memories for email: '" + email + "' (normalized: '" + normalizedEmail + "')");

        try {
            cursor = dbHelper.getReadableDatabase().query(
                    DatabaseHelper.TABLE_MEMORIES,
                    null,
                    "LOWER(TRIM(" + DatabaseHelper.COLUMN_MEMORY_EMAIL + "))=?",
                    new String[]{normalizedEmail},
                    null,
                    null,
                    DatabaseHelper.COLUMN_MEMORY_DATE + " DESC"
            );

            android.util.Log.d("MemoryModel", "Cursor count: " + (cursor != null ? cursor.getCount() : "null"));

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMORY_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMORY_TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMORY_DESCRIPTION));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMORY_DATE));
                    String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMORY_IMAGE_URI));
                    String songUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMORY_SONG_URI));
                    String memoryEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMORY_EMAIL));

                    android.util.Log.d("MemoryModel", "Found memory: " + title + " for email: '" + memoryEmail + "'");

                    memories.add(new Memory(id, title, description, date, imageUri, songUri, memoryEmail));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        android.util.Log.d("MemoryModel", "Returning " + memories.size() + " memories");
        return memories;
    }

    // Debug method to get all memories
    public static List<Memory> getAllMemories(Context context) {
        List<Memory> memories = new ArrayList<>();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        android.database.Cursor cursor = null;

        android.util.Log.d("MemoryModel", "Getting ALL memories from database");

        try {
            cursor = dbHelper.getReadableDatabase().query(
                    DatabaseHelper.TABLE_MEMORIES,
                    null,
                    null,
                    null,
                    null,
                    null,
                    DatabaseHelper.COLUMN_MEMORY_DATE + " DESC"
            );

            android.util.Log.d("MemoryModel", "Total memories in database: " + (cursor != null ? cursor.getCount() : "null"));

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMORY_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMORY_TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMORY_DESCRIPTION));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMORY_DATE));
                    String imageUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMORY_IMAGE_URI));
                    String songUri = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMORY_SONG_URI));
                    String memoryEmail = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MEMORY_EMAIL));

                    android.util.Log.d("MemoryModel", "DB Memory: " + title + " | Email: '" + memoryEmail + "'");

                    memories.add(new Memory(id, title, description, date, imageUri, songUri, memoryEmail));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return memories;
    }
}
