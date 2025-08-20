package com.example.tripbuddy.ui.trip;

import androidx.lifecycle.ViewModelProvider;
import com.example.tripbuddy.ui.trip.TripViewModel;
import com.example.tripbuddy.R;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TripFragment extends Fragment {

    private TripViewModel mViewModel;

    public static TripFragment newInstance() {
        return new TripFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TripViewModel.class);
        // TODO: Use the ViewModel
    }

}