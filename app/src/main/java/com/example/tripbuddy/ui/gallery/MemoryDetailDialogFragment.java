package com.example.tripbuddy.ui.gallery;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.tripbuddy.R;
import com.example.tripbuddy.model.Memory;

import java.io.File;

public class MemoryDetailDialogFragment extends DialogFragment {

    private Memory memory;
    private ImageView imageMemoryLarge;
    private TextView textMemoryTitleLarge, textMemoryDateLarge, textMemoryDescriptionLarge;
    private LinearLayout audioControlsSection;
    private ImageButton btnPlayLarge, btnPauseLarge, btnClose;

    private MediaPlayer mediaPlayer;

    public static MemoryDetailDialogFragment newInstance(Memory memory) {
        MemoryDetailDialogFragment fragment = new MemoryDetailDialogFragment();
        Bundle args = new Bundle();
        args.putInt("id", memory.id);
        args.putString("title", memory.title);
        args.putString("description", memory.description);
        args.putString("date", memory.date);
        args.putString("imageUri", memory.imageUri);
        args.putString("songUri", memory.songUri);
        args.putString("email", memory.email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_TripBuddy);

        // Reconstruct memory object from arguments
        Bundle args = getArguments();
        if (args != null) {
            memory = new Memory(
                args.getInt("id"),
                args.getString("title"),
                args.getString("description"),
                args.getString("date"),
                args.getString("imageUri"),
                args.getString("songUri"),
                args.getString("email")
            );
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_memory_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupData();
        setupClickListeners();
    }

    private void initViews(View view) {
        imageMemoryLarge = view.findViewById(R.id.imageMemoryLarge);
        textMemoryTitleLarge = view.findViewById(R.id.textMemoryTitleLarge);
        textMemoryDateLarge = view.findViewById(R.id.textMemoryDateLarge);
        textMemoryDescriptionLarge = view.findViewById(R.id.textMemoryDescriptionLarge);
        audioControlsSection = view.findViewById(R.id.audioControlsSection);
        btnPlayLarge = view.findViewById(R.id.btnPlayLarge);
        btnPauseLarge = view.findViewById(R.id.btnPauseLarge);
        btnClose = view.findViewById(R.id.btnClose);
    }

    private void setupData() {
        if (memory == null) return;

        // Set text data
        textMemoryTitleLarge.setText(memory.title);
        textMemoryDateLarge.setText(memory.date);
        textMemoryDescriptionLarge.setText(memory.description);

        // Load image
        loadImage();

        // Setup audio controls
        setupAudioControls();
    }

    private void loadImage() {
        if (memory.imageUri != null && !memory.imageUri.isEmpty()) {
            try {
                // Check if it's a file path (internal storage) or URI
                if (memory.imageUri.startsWith("/")) {
                    // It's a file path from internal storage
                    File imageFile = new File(memory.imageUri);
                    if (imageFile.exists()) {
                        Uri fileUri = Uri.fromFile(imageFile);
                        imageMemoryLarge.setImageURI(fileUri);
                        imageMemoryLarge.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    } else {
                        // File doesn't exist, show placeholder
                        imageMemoryLarge.setImageResource(R.color.gray_300);
                        android.util.Log.w("MemoryDetailDialog", "Image file not found: " + memory.imageUri);
                    }
                } else {
                    // It's a URI (for backward compatibility with old data)
                    Uri imageUri = Uri.parse(memory.imageUri);
                    imageMemoryLarge.setImageURI(imageUri);
                    imageMemoryLarge.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            } catch (Exception e) {
                // If image loading fails, show placeholder
                imageMemoryLarge.setImageResource(R.color.gray_300);
                android.util.Log.e("MemoryDetailDialog", "Error loading image: " + e.getMessage());
            }
        } else {
            // Show placeholder if no image
            imageMemoryLarge.setImageResource(R.color.gray_300);
        }
    }

    private void setupAudioControls() {
        if (memory.songUri != null && !memory.songUri.isEmpty()) {
            audioControlsSection.setVisibility(View.VISIBLE);
        } else {
            audioControlsSection.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        btnClose.setOnClickListener(v -> dismiss());

        btnPlayLarge.setOnClickListener(v -> playAudio());

        btnPauseLarge.setOnClickListener(v -> pauseAudio());
    }

    private void playAudio() {
        if (memory.songUri == null || memory.songUri.isEmpty()) return;

        try {
            // Stop any currently playing audio
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            // Start new audio
            mediaPlayer = new MediaPlayer();

            // Handle both file paths and URIs
            if (memory.songUri.startsWith("/")) {
                // It's a file path from internal storage
                File audioFile = new File(memory.songUri);
                if (audioFile.exists()) {
                    mediaPlayer.setDataSource(audioFile.getAbsolutePath());
                } else {
                    Toast.makeText(getContext(), "Audio file not found", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                // It's a URI
                mediaPlayer.setDataSource(getContext(), Uri.parse(memory.songUri));
            }

            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                btnPlayLarge.setVisibility(View.GONE);
                btnPauseLarge.setVisibility(View.VISIBLE);
            });

            mediaPlayer.setOnCompletionListener(mp -> {
                btnPlayLarge.setVisibility(View.VISIBLE);
                btnPauseLarge.setVisibility(View.GONE);
                mp.release();
                mediaPlayer = null;
            });

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(getContext(), "Error playing audio", Toast.LENGTH_SHORT).show();
                btnPlayLarge.setVisibility(View.VISIBLE);
                btnPauseLarge.setVisibility(View.GONE);
                mp.release();
                mediaPlayer = null;
                return true;
            });

        } catch (Exception e) {
            Toast.makeText(getContext(), "Failed to play audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            android.util.Log.e("MemoryDetailDialog", "Error playing audio: " + e.getMessage());
        }
    }

    private void pauseAudio() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            btnPlayLarge.setVisibility(View.VISIBLE);
            btnPauseLarge.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            // Make dialog nearly full screen with some margin
            dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.9),
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up MediaPlayer
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
