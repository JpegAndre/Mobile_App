package com.example.tripbuddy.ui.trip;

import com.example.tripbuddy.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TripFragment extends Fragment {

    private TextInputEditText startEditText, endEditText;
    TextInputLayout startInputLayout, endInputLayout;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        startEditText = view.findViewById(R.id.edtDateStart);
        endEditText = view.findViewById(R.id.edtDateEnd);
        startInputLayout = view.findViewById(R.id.inputLayoutDateStart);
        endInputLayout = view.findViewById(R.id.inputLayoutDateEnd);


//        edtStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MaterialDatePicker<Long> startDatePicker = MaterialDatePicker.Builder.datePicker()
//                        .setTitleText("Select start date")
//                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
//                        .build();
//                startDatePicker.show(getParentFragmentManager(), "START_DATE_PICKER");
//                startDatePicker.addOnPositiveButtonClickListener(selection ->
//                        edtStart.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date((Long) selection)))
//                );
//            }
//        });

        startEditText.setOnClickListener(v -> {
            MaterialDatePicker<Long> startDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select start date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();
            startDatePicker.show(getParentFragmentManager(), "START_DATE_PICKER");
            startDatePicker.addOnPositiveButtonClickListener(selection ->
                startEditText.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date((Long) selection)))
            );
        });

        endEditText.setOnClickListener(v -> {
            MaterialDatePicker<Long> endDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select end date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();
            endDatePicker.show(getParentFragmentManager(), "END_DATE_PICKER");
            endDatePicker.addOnPositiveButtonClickListener(selection ->
                endEditText.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date((Long) selection)))
            );
        });
    }

}