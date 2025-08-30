package com.example.tripbuddy.adapter;

import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tripbuddy.R;
import com.example.tripbuddy.model.Memory;

import java.io.File;
import java.util.List;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder> {
    private List<Memory> memoryList;
    private MediaPlayer currentMediaPlayer;
    private MemoryViewHolder currentPlayingHolder;
    private OnMemoryClickListener onMemoryClickListener;

    public interface OnMemoryClickListener {
        void onMemoryClick(Memory memory);
    }

    public MemoryAdapter(List<Memory> memoryList) {
        this.memoryList = memoryList;
    }

    public void setOnMemoryClickListener(OnMemoryClickListener listener) {
        this.onMemoryClickListener = listener;
    }

    @NonNull
    @Override
    public MemoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        return new MemoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoryViewHolder holder, int position) {
        Memory memory = memoryList.get(position);

        holder.textMemoryTitle.setText(memory.title);
        holder.textMemoryDate.setText(memory.date);
        holder.textMemoryDescription.setText(memory.description);

        // Set click listener for the entire card
        holder.itemView.setOnClickListener(v -> {
            if (onMemoryClickListener != null) {
                onMemoryClickListener.onMemoryClick(memory);
            }
        });

        // Load image if available
        if (memory.imageUri != null && !memory.imageUri.isEmpty()) {
            try {

                if (memory.imageUri.startsWith("/")) {

                    File imageFile = new File(memory.imageUri);
                    if (imageFile.exists()) {
                        Uri fileUri = Uri.fromFile(imageFile);
                        holder.imageMemory.setImageURI(fileUri);
                        holder.imageMemory.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    } else {

                        holder.imageMemory.setImageResource(R.color.gray_300);
                        android.util.Log.w("MemoryAdapter", "Image file not found: " + memory.imageUri);
                    }
                } else {

                    Uri imageUri = Uri.parse(memory.imageUri);
                    holder.imageMemory.setImageURI(imageUri);
                    holder.imageMemory.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            } catch (Exception e) {

                holder.imageMemory.setImageResource(R.color.gray_300);
                android.util.Log.e("MemoryAdapter", "Error loading image: " + e.getMessage());
            }
        } else {

            holder.imageMemory.setImageResource(R.color.gray_300);
        }

        // Handle audio controls
        if (memory.songUri != null && !memory.songUri.isEmpty()) {
            holder.iconMusic.setVisibility(View.VISIBLE);
            holder.textHasAudio.setVisibility(View.VISIBLE);
            holder.btnPlay.setVisibility(View.VISIBLE);

            // Reset button states
            if (currentPlayingHolder != holder) {
                holder.btnPlay.setVisibility(View.VISIBLE);
                holder.btnPause.setVisibility(View.GONE);
            }


            holder.btnPlay.setOnClickListener(v -> {
                playAudio(memory.songUri, holder);
            });


            holder.btnPause.setOnClickListener(v -> {
                pauseAudio(holder);
            });

        } else {
            holder.iconMusic.setVisibility(View.GONE);
            holder.textHasAudio.setVisibility(View.GONE);
            holder.btnPlay.setVisibility(View.GONE);
            holder.btnPause.setVisibility(View.GONE);
        }
    }

    private void playAudio(String audioUri, MemoryViewHolder holder) {
        try {
            // Stop any currently playing audio
            if (currentMediaPlayer != null) {
                currentMediaPlayer.stop();
                currentMediaPlayer.release();
                currentMediaPlayer = null;


                if (currentPlayingHolder != null) {
                    currentPlayingHolder.btnPlay.setVisibility(View.VISIBLE);
                    currentPlayingHolder.btnPause.setVisibility(View.GONE);
                }
            }


            currentMediaPlayer = new MediaPlayer();
            currentMediaPlayer.setDataSource(holder.itemView.getContext(), Uri.parse(audioUri));
            currentMediaPlayer.prepareAsync();

            currentMediaPlayer.setOnPreparedListener(mp -> {
                mp.start();
                holder.btnPlay.setVisibility(View.GONE);
                holder.btnPause.setVisibility(View.VISIBLE);
                currentPlayingHolder = holder;
            });

            currentMediaPlayer.setOnCompletionListener(mp -> {
                holder.btnPlay.setVisibility(View.VISIBLE);
                holder.btnPause.setVisibility(View.GONE);
                currentPlayingHolder = null;
                mp.release();
                currentMediaPlayer = null;
            });

            currentMediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(holder.itemView.getContext(), "Error playing audio", Toast.LENGTH_SHORT).show();
                holder.btnPlay.setVisibility(View.VISIBLE);
                holder.btnPause.setVisibility(View.GONE);
                currentPlayingHolder = null;
                mp.release();
                currentMediaPlayer = null;
                return true;
            });

        } catch (Exception e) {
            Toast.makeText(holder.itemView.getContext(), "Failed to play audio: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            android.util.Log.e("MemoryAdapter", "Error playing audio: " + e.getMessage());
        }
    }

    private void pauseAudio(MemoryViewHolder holder) {
        if (currentMediaPlayer != null && currentMediaPlayer.isPlaying()) {
            currentMediaPlayer.pause();
            holder.btnPlay.setVisibility(View.VISIBLE);
            holder.btnPause.setVisibility(View.GONE);
        }
    }

    // Clean up MediaPlayer when adapter is destroyed
    public void releaseMediaPlayer() {
        if (currentMediaPlayer != null) {
            currentMediaPlayer.release();
            currentMediaPlayer = null;
        }
    }

    @Override
    public int getItemCount() {
        return memoryList.size();
    }

    public static class MemoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageMemory, iconMusic;
        TextView textMemoryTitle, textMemoryDate, textMemoryDescription, textHasAudio;
        ImageButton btnPlay, btnPause;

        public MemoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMemory = itemView.findViewById(R.id.imageMemory);
            iconMusic = itemView.findViewById(R.id.iconMusic);
            textMemoryTitle = itemView.findViewById(R.id.textMemoryTitle);
            textMemoryDate = itemView.findViewById(R.id.textMemoryDate);
            textMemoryDescription = itemView.findViewById(R.id.textMemoryDescription);
            textHasAudio = itemView.findViewById(R.id.textHasAudio);
            btnPlay = itemView.findViewById(R.id.btnPlay);
            btnPause = itemView.findViewById(R.id.btnPause);
        }
    }
}
