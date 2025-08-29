package com.example.tripbuddy.ui.trip;

import com.example.tripbuddy.R;
import com.example.tripbuddy.DatabaseHelper;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.tripbuddy.MainActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TripFragment extends Fragment {

    private TextInputEditText startEditText, endEditText, destinationEditText, notesEditText, travelExEditText, customExEditText, mealExEditText;
    private TextInputLayout startInputLayout, endInputLayout, destinationInputLayout, notesInputLayout, travelExInputLayout, customExInputLayout, mealExInputLayout;

    private Button calculateButton, saveButton;

    private TextView resultTextView;

    private CheckBox hiking, bus, sightseeing, museum;

    private String destination, notes, userEmail;

    private View scrollView;

    private double travelExpenses, customExpenses, mealExpenses, totalExpenses, activitiesExpenses;

    private SimpleDateFormat startDate, endDate;

    ArrayList<String> activities = new ArrayList<>();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        SharedPreferences loginPreferences = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, getActivity().MODE_PRIVATE);
        userEmail = loginPreferences.getString(MainActivity.KEY_EMAIL, "");

        startEditText = view.findViewById(R.id.edtDateStart);
        endEditText = view.findViewById(R.id.edtDateEnd);
        startInputLayout = view.findViewById(R.id.inputLayoutDateStart);
        endInputLayout = view.findViewById(R.id.inputLayoutDateEnd);
        scrollView = view.findViewById(R.id.scrollView);
        destinationEditText = view.findViewById(R.id.edtDestination);
        destinationInputLayout = view.findViewById(R.id.inputLayoutDestination);
        notesEditText = view.findViewById(R.id.edtNotes);
        notesInputLayout = view.findViewById(R.id.inputLayoutNotes);
        travelExEditText = view.findViewById(R.id.edtTripExpenses);
        travelExInputLayout = view.findViewById(R.id.inputLayoutTravelExpenses);
        customExEditText = view.findViewById(R.id.edtCustomExpenses);
        customExInputLayout = view.findViewById(R.id.inputLayoutCustomExpenses);
        mealExEditText = view.findViewById(R.id.edtMealExpenses);
        mealExInputLayout = view.findViewById(R.id.inputLayoutMealExpenses);
        calculateButton = view.findViewById(R.id.btnCalculate);
        saveButton = view.findViewById(R.id.btnSave);
        resultTextView = view.findViewById(R.id.textEstimates);
        hiking = view.findViewById(R.id.checkHiking);
        bus = view.findViewById(R.id.checkBus);
        sightseeing = view.findViewById(R.id.checkSightseeing);
        museum = view.findViewById(R.id.checkMuseum);



        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isValid = true;
                totalExpenses = 0;
                activitiesExpenses = 0;

                // Validate Start Date
                if (startEditText.getText() == null || startEditText.getText().toString().isEmpty()) {
                    startInputLayout.setError("Start date cannot be empty");
                    isValid = false;
                    return;
                } else {
                    startInputLayout.setError(null);
                    startInputLayout.setErrorEnabled(false);
                }

                // Validate End Date
                if (endEditText.getText() == null || endEditText.getText().toString().isEmpty()) {
                    endInputLayout.setError("End date cannot be empty");
                    isValid = false;
                    return;
                } else {
                    endInputLayout.setError(null);
                    endInputLayout.setErrorEnabled(false);
                }

                // Date validation: start date >= current date, end date > start date
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date start = sdf.parse(startEditText.getText().toString());
                    Date end = sdf.parse(endEditText.getText().toString());
                    Date now = new Date();

                    // Remove time from current date for comparison
                    String todayStr = sdf.format(now);
                    Date today = sdf.parse(todayStr);

                    if (start.before(today)) {
                        startInputLayout.setError("Start date cannot be before today");
                        isValid = false;
                        return;
                    } else {
                        startInputLayout.setError(null);
                        startInputLayout.setErrorEnabled(false);
                    }

                    if (!end.after(start)) {
                        endInputLayout.setError("End date must be after start date");
                        isValid = false;
                        return;
                    } else {
                        endInputLayout.setError(null);
                        endInputLayout.setErrorEnabled(false);
                    }
                } catch (Exception e) {
                    startInputLayout.setError("Invalid date format");
                    endInputLayout.setError("Invalid date format");
                    isValid = false;
                    return;
                }

                // Validate Destination
                if (destinationEditText.getText() == null || destinationEditText.getText().toString().isEmpty()) {
                    destinationInputLayout.setError("Destination cannot be empty");
                    isValid = false;
                } else {
                    destination = destinationEditText.getText().toString();
                    destinationInputLayout.setError(null);
                    destinationInputLayout.setErrorEnabled(false);
                }

                // Validate Notes
                if (notesEditText.getText() == null || notesEditText.getText().toString().isEmpty()) {
                    notesInputLayout.setError("Notes cannot be empty");
                    isValid = false;
                } else {
                    notes = notesEditText.getText().toString();
                    notesInputLayout.setError(null);
                    notesInputLayout.setErrorEnabled(false);
                }

                // Validate Travel Expenses
                String travelEx = travelExEditText.getText() != null ? travelExEditText.getText().toString() : "";
                if (travelEx.isEmpty()) {
                    travelExInputLayout.setError("Travel expenses cannot be empty");
                    isValid = false;
                } else {
                    try {
                        travelExpenses = Double.parseDouble(travelEx);
                        travelExInputLayout.setError(null);
                        travelExInputLayout.setErrorEnabled(false);
                    } catch (NumberFormatException e) {
                        travelExInputLayout.setError("Enter a valid number");
                        isValid = false;
                    }
                }

                // Validate Custom Expenses
                String customEx = customExEditText.getText() != null ? customExEditText.getText().toString() : "";
                if (customEx.isEmpty()) {
                    customExInputLayout.setError("Custom expenses cannot be empty");
                    isValid = false;
                } else {
                    try {
                        customExpenses = Double.parseDouble(customEx);
                        customExInputLayout.setError(null);
                        customExInputLayout.setErrorEnabled(false);
                    } catch (NumberFormatException e) {
                        customExInputLayout.setError("Enter a valid number");
                        isValid = false;
                    }
                }

                // Validate Meal Expenses
                String mealEx = mealExEditText.getText() != null ? mealExEditText.getText().toString() : "";
                if (mealEx.isEmpty()) {
                    mealExInputLayout.setError("Meal expenses cannot be empty");
                    isValid = false;
                } else {
                    try {
                        mealExpenses = Double.parseDouble(mealEx);
                        mealExInputLayout.setError(null);
                        mealExInputLayout.setErrorEnabled(false);
                    } catch (NumberFormatException e) {
                        mealExInputLayout.setError("Enter a valid number");
                        isValid = false;
                    }
                }

                // If all fields are valid, proceed with activities and final calculations
                if (isValid) {
                    totalExpenses = travelExpenses + customExpenses + mealExpenses;

                    if (hiking.isChecked()) {
                        activities.add("Hiking");
                        activitiesExpenses += 450;
                    }
                    if (bus.isChecked()) {
                        activities.add("Bus");
                        activitiesExpenses += 500;
                    }
                    if (sightseeing.isChecked()) {
                        activities.add("Sightseeing");
                        activitiesExpenses += 2500;
                    }
                    if (museum.isChecked()) {
                        activities.add("Museum");
                        activitiesExpenses += 150;
                    }

                    totalExpenses += activitiesExpenses;

                    String temp = "Travel expenses - R " + travelExpenses + "\nActivity expenses - R " + activitiesExpenses + "\nCustom expenses - R " + customExpenses + "\nMeal expenses - R " + mealExpenses + "\n --------------------- \nTotal expenses - R " + totalExpenses;

                    resultTextView.setText(temp);

                    saveButton.setEnabled(true);
                }
            }
        });

        saveButton.setOnClickListener(v -> {
            dbHelper.addTrip(
                    destination,
                    startEditText.getText().toString(),
                    endEditText.getText().toString(),
                    String.join(", ", activities),
                    travelExpenses,
                    customExpenses,
                    mealExpenses,
                    notes,
                    userEmail
            );

            Toast.makeText(getActivity(), "Trip saved", Toast.LENGTH_SHORT).show();

            startEditText.setText("");
            endEditText.setText("");
            destinationEditText.setText("");
            notesEditText.setText("");
            travelExEditText.setText("");
            customExEditText.setText("");
            mealExEditText.setText("");
            hiking.setChecked(false);
            bus.setChecked(false);
            sightseeing.setChecked(false);
            museum.setChecked(false);
            resultTextView.setText("Estimated Expenses:\n");
            activities.clear();
            totalExpenses = 0;
            travelExpenses = 0;
            customExpenses = 0;
            mealExpenses = 0;
            activitiesExpenses = 0;
            destination = "";
            notes = "";
            startDate = null;
            endDate = null;



            saveButton.setEnabled(false);
        });

        startEditText.setOnClickListener(v -> {
            MaterialDatePicker<Long> startDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select start date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();
            startDatePicker.show(getParentFragmentManager(), "START_DATE_PICKER");
            startDatePicker.addOnPositiveButtonClickListener(selection ->
                startEditText.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date((Long) selection)))
            );

            startDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
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

            endDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        });

//        ViewCompat.setOnApplyWindowInsetsListener(scrollView, (v, insets) -> {
//            Insets navBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(0, 0, 0, navBarInsets.bottom);
//            return insets;
//        });

        android.text.TextWatcher disableSaveTextWatcher = new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveButton.setEnabled(false);
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        };
        startEditText.addTextChangedListener(disableSaveTextWatcher);
        endEditText.addTextChangedListener(disableSaveTextWatcher);
        destinationEditText.addTextChangedListener(disableSaveTextWatcher);
        notesEditText.addTextChangedListener(disableSaveTextWatcher);
        travelExEditText.addTextChangedListener(disableSaveTextWatcher);
        customExEditText.addTextChangedListener(disableSaveTextWatcher);
        mealExEditText.addTextChangedListener(disableSaveTextWatcher);
    }

}