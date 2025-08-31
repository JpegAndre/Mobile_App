package com.example.tripbuddy.ui.memory;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import com.example.tripbuddy.DatabaseHelper;
import com.example.tripbuddy.MainActivity;
import com.example.tripbuddy.ui.memory.MemoryViewModel;
import com.example.tripbuddy.R;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MemoryFragment extends Fragment {

    private MemoryViewModel mViewModel;

    private String title, description;

    private SimpleDateFormat date;
    private TextView txtImage, txtSong;

    private TextInputEditText dateEditText, titleEditText, descriptionEditText;

    private TextInputLayout dateInputLayout, locationInputLayout, descriptionInputLayout;

    private Button imageButton, songButton, saveButton;

    private Uri selectedImageUri, selectedSongUri;

    private ActivityResultLauncher<String> imagePickerLauncher, songPickerLauncher;

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
        descriptionInputLayout = view.findViewById(R.id.inputLayoutDescription);
        imageButton = view.findViewById(R.id.btnUploadImage);
        songButton = view.findViewById(R.id.btnUploadSong);
        saveButton = view.findViewById(R.id.btnSave);
        txtImage = view.findViewById(R.id.textUploadImage);
        txtSong = view.findViewById(R.id.textUploadSong);

        txtImage.setVisibility(View.INVISIBLE);
        txtSong.setVisibility(View.INVISIBLE);

        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, getActivity().MODE_PRIVATE);
        String userEmail = prefs.getString(MainActivity.KEY_EMAIL, "");

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


        // Register image picker launcher
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    Toast.makeText(getActivity(), "Image uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        );

        imageButton.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");

            if (selectedImageUri == null || selectedImageUri.toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please select an image file", Toast.LENGTH_SHORT).show();
                return;
            }
            txtImage.setVisibility(View.VISIBLE);
        });


        // Register song picker launcher
        songPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedSongUri = uri;
                    Toast.makeText(getActivity(), "Song uploaded", Toast.LENGTH_SHORT).show();
                }
            }
        );

        songButton.setOnClickListener(v -> {
            songPickerLauncher.launch("audio/mpeg");

            if (selectedSongUri == null || selectedSongUri.toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please select an mp3 file", Toast.LENGTH_SHORT).show();
                return;
            }
            txtSong.setVisibility(View.VISIBLE);
        });


        saveButton.setOnClickListener(v -> {
            // Validate fields
            title = titleEditText.getText().toString().trim();
            description = descriptionEditText.getText().toString().trim();
            String dateStr = dateEditText.getText().toString().trim();

            boolean valid = true;

            if (dateStr.isEmpty()) {
                dateInputLayout.setError("Date is required");
                valid = false;
                return;
            } else {
                dateInputLayout.setError(null);
            }

            if (title.isEmpty()) {
                titleEditText.setError("Title is required");
                valid = false;
                return;
            } else {
                titleEditText.setError(null);
            }

            if (description.isEmpty()) {
                descriptionEditText.setError("Description is required");
                valid = false;
                return;
            } else {
                descriptionEditText.setError(null);
            }

            if (selectedImageUri == null) {
                txtImage.setError("Image is required");
                valid = false;
                return;
            } else {
                txtImage.setError(null);
            }

            if (selectedSongUri == null) {
                txtSong.setError("Song is required");
                valid = false;
                return;
            } else {
                txtSong.setError(null);
            }

            if (valid) {
                // Copy files to internal storage
                String savedImagePath = copyFileToInternalStorage(selectedImageUri, "image_");
                String savedSongPath = copyFileToInternalStorage(selectedSongUri, "song_");

                if (savedImagePath != null && savedSongPath != null) {

                    DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
                    dbHelper.addMemory(title, description, dateStr, savedImagePath, savedSongPath, userEmail);
                    Toast.makeText(getActivity(), "Memory saved", Toast.LENGTH_SHORT).show();

                    // Clear fields
                    dateEditText.setText("");
                    titleEditText.setText("");
                    descriptionEditText.setText("");
                    txtImage.setVisibility(View.INVISIBLE);
                    txtSong.setVisibility(View.INVISIBLE);
                    selectedImageUri = null;
                    selectedSongUri = null;
                } else {
                    Toast.makeText(getActivity(), "Error saving files. Please try again.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String copyFileToInternalStorage(Uri sourceUri, String prefix) {
        try {
            // Create a unique filename
            String fileName = prefix + UUID.randomUUID().toString();

            // Get the app's internal storage directory
            File internalDir = new File(getActivity().getFilesDir(), "memories");
            if (!internalDir.exists()) {
                internalDir.mkdirs();
            }

            File destFile = new File(internalDir, fileName);

            // Copy the file
            InputStream inputStream = getActivity().getContentResolver().openInputStream(sourceUri);
            FileOutputStream outputStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            android.util.Log.d("MemoryFragment", "File saved to: " + destFile.getAbsolutePath());
            return destFile.getAbsolutePath();

        } catch (Exception e) {
            android.util.Log.e("MemoryFragment", "Error copying file: " + e.getMessage());
            return null;
        }
    }
}