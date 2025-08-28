package com.example.tripbuddy.ui.memory;

import androidx.lifecycle.ViewModelProvider;
import com.example.tripbuddy.ui.memory.MemoryViewModel;
import com.example.tripbuddy.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MemoryFragment extends Fragment {

    private MemoryViewModel mViewModel;

    private String title, description;

    private SimpleDateFormat date;
    private TextView txtImage, txtSong;

    private TextInputEditText dateEditText, titleEditText, descriptionEditText;

    private TextInputLayout dateInputLayout, locationInputLayout, descriptionInputLayout;

    private Button imageButton, songButton, saveButton;

    public static MemoryFragment newInstance() {
        return new MemoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_memory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dateEditText = view.findViewById(R.id.edtDate);
        titleEditText = view.findViewById(R.id.edtTitle);
        descriptionEditText = view.findViewById(R.id.edtNotes);
        dateInputLayout = view.findViewById(R.id.inputLayoutDate);
        imageButton = view.findViewById(R.id.btnUploadImage);
        songButton = view.findViewById(R.id.btnUploadSong);
        saveButton = view.findViewById(R.id.btnSave);
        txtImage = view.findViewById(R.id.textUploadImage);
        txtSong = view.findViewById(R.id.textUploadSong);

        txtImage.setVisibility(View.INVISIBLE);
        txtSong.setVisibility(View.INVISIBLE);

//        locationInputLayout = view.findViewById(R.id.inputLayoutLocation);
//        descriptionInputLayout = view.findViewById(R.id.inputLayoutDescription);

        dateEditText.setOnClickListener(v -> {
            MaterialDatePicker<Long> startDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select memory date")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();
            startDatePicker.show(getParentFragmentManager(), "START_DATE_PICKER");
            startDatePicker.addOnPositiveButtonClickListener(selection ->
                    dateEditText.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date((Long) selection)))
            );
            date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        });


        imageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*"); // TODO: Replace this deprecated method
            startActivityForResult(intent, 100);
            txtImage.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), "Image uploaded", Toast.LENGTH_SHORT).show();
                });

        saveButton.setOnClickListener(v -> {
            Toast.makeText(getActivity(), date.toString(), Toast.LENGTH_SHORT).show();
//            title = titleEditText.getText().toString().trim();
//            description = descriptionEditText.getText().toString().trim();
//            if (dateEditText.getText().toString().isEmpty()) {
//                dateInputLayout.setError("Date is required");
//            } else {
//                dateInputLayout.setError(null);
//            }
//            if (title.isEmpty()) {
//                titleEditText.setError("Title is required");
//            } else {
//                titleEditText.setError(null);
//            }
//            if (description.isEmpty()) {
//                descriptionEditText.setError("Description is required");
//            } else {
//                descriptionEditText.setError(null);
//            }
//            if (!dateEditText.getText().toString().isEmpty() && !title.isEmpty() && !description.isEmpty()) {
//                Toast.makeText(getActivity(), "Memory saved", Toast.LENGTH_SHORT).show();
//                // Save memory to database
//                mViewModel.insertMemory(dateEditText.getText().toString(), title, description);
//                // Clear fields
//                dateEditText.setText("");
//                titleEditText.setText("");
//                descriptionEditText.setText("");
//            } else {
//                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
//            }
        });


    }

}