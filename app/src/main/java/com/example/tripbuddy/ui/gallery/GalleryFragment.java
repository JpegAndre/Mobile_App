package com.example.tripbuddy.ui.gallery;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripbuddy.MainActivity;
import com.example.tripbuddy.adapter.MemoryAdapter;
import com.example.tripbuddy.model.Memory;
import com.example.tripbuddy.ui.gallery.GalleryViewModel;
import com.example.tripbuddy.R;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment implements MemoryAdapter.OnMemoryClickListener {

    private GalleryViewModel mViewModel;
    private RecyclerView galleryRecyclerView;
    private TextView noMemoriesText;
    private MemoryAdapter memoryAdapter;
    private List<Memory> memoryList = new ArrayList<>();

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        galleryRecyclerView = view.findViewById(R.id.galleryRecyclerView);
        noMemoriesText = view.findViewById(R.id.textNoMemories);

        // Set up GridLayoutManager with 2 columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        galleryRecyclerView.setLayoutManager(gridLayoutManager);

        // Get logged-in user's email
        SharedPreferences prefs = getActivity().getSharedPreferences(MainActivity.SHARED_PREFS, getActivity().MODE_PRIVATE);
        String userEmail = prefs.getString(MainActivity.KEY_EMAIL, "");

        // Debug logging
        android.util.Log.d("GalleryFragment", "=== LOADING MEMORIES ===");
        android.util.Log.d("GalleryFragment", "User email: " + userEmail);

        // Get memories for the logged-in user
        memoryList = Memory.getMemoriesForUser(getContext(), userEmail);

        android.util.Log.d("GalleryFragment", "Number of memories found: " + memoryList.size());

        // Set up adapter with click listener
        memoryAdapter = new MemoryAdapter(memoryList);
        memoryAdapter.setOnMemoryClickListener(this);
        galleryRecyclerView.setAdapter(memoryAdapter);

        // Handle empty state
        if (memoryList.isEmpty()) {
            noMemoriesText.setVisibility(View.VISIBLE);
            noMemoriesText.setText("No memories found for: " + userEmail);
            galleryRecyclerView.setVisibility(View.GONE);
            android.util.Log.d("GalleryFragment", "No memories found for user: " + userEmail);
        } else {
            noMemoriesText.setVisibility(View.GONE);
            galleryRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMemoryClick(Memory memory) {
        // Create and show the detail dialog
        MemoryDetailDialogFragment dialog = MemoryDetailDialogFragment.newInstance(memory);
        dialog.show(getParentFragmentManager(), "MemoryDetailDialog");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Clean up any playing audio when fragment is destroyed
        if (memoryAdapter != null) {
            memoryAdapter.releaseMediaPlayer();
        }
    }
}