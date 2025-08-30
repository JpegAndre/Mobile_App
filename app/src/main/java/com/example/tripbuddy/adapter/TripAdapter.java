package com.example.tripbuddy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripbuddy.R;
import com.example.tripbuddy.model.Trip;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    private List<Trip> tripList;
    private Context context;

    public TripAdapter(List<Trip> tripList) {
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = tripList.get(position);

        holder.textDestination.setText(trip.destination);
        holder.textDates.setText(trip.startDate + " to " + trip.endDate);
        holder.textActivities.setText("Activities: " + trip.activities);

        // Calculate and display expenses
        double activityExpenses = calculateActivityExpenses(trip.activities);
        double totalExpenses = trip.travelExpenses + trip.customExpenses + trip.mealExpenses + activityExpenses;

        // Check if this trip had a discount
        boolean hadDiscount = trip.hadDiscount(context);
        double discountAmount = 0.0;
        double finalTotal = totalExpenses;

        if (hadDiscount) {
            discountAmount = totalExpenses * 0.10; // 10% discount
            finalTotal = totalExpenses - discountAmount;
        }

        // Display expenses with final total
        String expensesText = String.format("Travel: R %.2f, Activities: R %.2f, Custom: R %.2f, Meal: R %.2f\nTotal: R %.2f",
            trip.travelExpenses, activityExpenses, trip.customExpenses, trip.mealExpenses, finalTotal);
        holder.textExpenses.setText(expensesText);

        // Handle discount display
        if (hadDiscount) {
            holder.layoutDiscount.setVisibility(View.VISIBLE);
            holder.textDiscount.setText("10% Discount Applied - 4th Trip Bonus!");
            holder.textDiscountAmount.setText(String.format("R %.2f saved", discountAmount));
        } else {
            holder.layoutDiscount.setVisibility(View.GONE);
        }

        holder.textNotes.setText("Notes: " + trip.notes);
    }

    private double calculateActivityExpenses(String activities) {
        double total = 0.0;
        if (activities != null) {
            if (activities.contains("Hiking")) total += 450;
            if (activities.contains("Bus")) total += 500;
            if (activities.contains("Sightseeing")) total += 2500;
            if (activities.contains("Museum")) total += 150;
        }
        return total;
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView textDestination, textDates, textActivities, textExpenses, textNotes;
        TextView textDiscount, textDiscountAmount;
        LinearLayout layoutDiscount;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            textDestination = itemView.findViewById(R.id.textDestination);
            textDates = itemView.findViewById(R.id.textDates);
            textActivities = itemView.findViewById(R.id.textActivities);
            textExpenses = itemView.findViewById(R.id.textExpenses);
            textNotes = itemView.findViewById(R.id.textNotes);
            textDiscount = itemView.findViewById(R.id.textDiscount);
            textDiscountAmount = itemView.findViewById(R.id.textDiscountAmount);
            layoutDiscount = itemView.findViewById(R.id.layoutDiscount);
        }
    }
}
