package com.example.tripbuddy.ui.settings;

import androidx.lifecycle.ViewModelProvider;
import com.example.tripbuddy.ui.settings.SettingsViewModel;
import com.example.tripbuddy.R;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class SettingsFragment extends Fragment {

    private SettingsViewModel mViewModel;

    private Button logoutButton;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logoutButton = view.findViewById(R.id.btnLogout);

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences loginPreferences = getActivity().getSharedPreferences("loginPrefs", getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = loginPreferences.edit();
                editor.putBoolean("isLoggedIn", false);
                editor.putString("email", "");
                editor.putString("password", "");
                editor.apply();
                getActivity().finish();
            }
        });
    }


}