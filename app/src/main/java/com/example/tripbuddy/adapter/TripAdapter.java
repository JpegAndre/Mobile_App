package com.example.tripbuddy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripbuddy.R;
import com.example.tripbuddy.model.Trip;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    private List<Trip> tripList;

    public TripAdapter(List<Trip> tripList) {
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = tripList.get(position);
        holder.textDestination.setText(trip.destination);
        holder.textDates.setText(trip.startDate + " to " + trip.endDate);
        holder.textActivities.setText("Activities: " + trip.activities);
        holder.textExpenses.setText("Travel: R " + trip.travelExpenses + ", Custom: R " + trip.customExpenses + ", Meal: R " + trip.mealExpenses);
        holder.textNotes.setText("Notes: " + trip.notes);
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView textDestination, textDates, textActivities, textExpenses, textNotes;
        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            textDestination = itemView.findViewById(R.id.textDestination);
            textDates = itemView.findViewById(R.id.textDates);
            textActivities = itemView.findViewById(R.id.textActivities);
            textExpenses = itemView.findViewById(R.id.textExpenses);
            textNotes = itemView.findViewById(R.id.textNotes);
        }
    }
}

