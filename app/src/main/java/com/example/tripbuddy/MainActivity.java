package com.example.tripbuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private static final String SHARED_PREFS = "loginPrefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    private static final String KEY_ISLOGGEDIN = "isLoggedIn";

    private TextInputLayout emailInputLayout;
    private TextInputEditText emailEditText;
    private TextInputLayout passwordInputLayout;
    private TextInputEditText passwordEditText;
    private Button loginButton, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailInputLayout = findViewById(R.id.inputLayoutEmail); 
        emailEditText = findViewById(R.id.edtEmail);

        passwordInputLayout = findViewById(R.id.inputLayoutPassword); 
        passwordEditText = findViewById(R.id.edtPassword);

        loginButton = findViewById(R.id.btnLogin);
        registerButton = findViewById(R.id.btnRegister);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                //handleLogin();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegister = new Intent(MainActivity.this, Registration.class);
                intentRegister.putExtra("email", emailEditText.getText().toString().trim());
                intentRegister.putExtra("password", passwordEditText.getText().toString().trim());
                startActivity(intentRegister);
            }
        });
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();


        // Validate Email
        if (email.isEmpty()) {
            emailInputLayout.setError("Email cannot be empty"); 
            return; 
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError("Enter a valid email address");
            return;
        } else {
            emailInputLayout.setError(null); 
            emailInputLayout.setErrorEnabled(false); 
        }

        // Validate Password
        if (password.isEmpty()) {
            passwordInputLayout.setError("Password cannot be empty");
            return;
        } else if (password.length() < 6) {
            passwordInputLayout.setError("Password must be at least 6 characters");
            return;
        } else {
            passwordInputLayout.setError(null);
            passwordInputLayout.setErrorEnabled(false);
        }

        String hashedPassword = hashValue(password);

        // TODO: Validate there exists such login and then login else send to registration

        // Shared preferences for the currently logged in user
        SharedPreferences loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = loginPreferences.edit();
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, hashedPassword);
        editor.putBoolean(KEY_ISLOGGEDIN, true);
        editor.apply();


        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
    }

    public static String hashValue(String input) {
        try {
            // Create MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Apply hash function to the input bytes
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert bytes to hexadecimal format
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: SHA-256 algorithm not found", e);
        }
    }

    private void clearData() {
        SharedPreferences loginPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = loginPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
