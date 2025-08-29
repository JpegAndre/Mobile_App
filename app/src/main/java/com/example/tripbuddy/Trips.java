package com.example.tripbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripbuddy.adapter.TripAdapter;
import com.example.tripbuddy.model.Trip;

import java.util.ArrayList;
import java.util.List;

public class Trips extends AppCompatActivity {

    private RecyclerView recyclerTrips;

    private Button btnBack;
    private TextView textNoTrips;
    private TripAdapter tripAdapter;
    private List<Trip> tripList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trips);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerTrips = findViewById(R.id.recyclerTrips);
        textNoTrips = findViewById(R.id.textNoTrips);
        btnBack = findViewById(R.id.btnBack);
        recyclerTrips.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences prefs = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
        String userEmail = prefs.getString(MainActivity.KEY_EMAIL, "");

        tripList = Trip.getTripsForUser(this, userEmail);
        tripAdapter = new TripAdapter(tripList);
        recyclerTrips.setAdapter(tripAdapter);

        if (tripList.isEmpty()) {
            textNoTrips.setVisibility(View.VISIBLE);
            recyclerTrips.setVisibility(View.GONE);
        } else {
            textNoTrips.setVisibility(View.GONE);
            recyclerTrips.setVisibility(View.VISIBLE);
        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Trips.this, HomeActivity.class));
            }
        });
    }
}