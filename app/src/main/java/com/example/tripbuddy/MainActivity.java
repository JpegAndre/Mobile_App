package com.example.tripbuddy;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

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

    public static final String SHARED_PREFS = "loginPrefs";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";

    public static final String KEY_ISLOGGEDIN = "isLoggedIn";

    private CheckBox rememberMeCheckBox;

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

        rememberMeCheckBox = findViewById(R.id.checkRememberMe);

        setupEditTextAnimation(emailEditText);
        setupEditTextAnimation(passwordEditText);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
                //startActivity(new Intent(MainActivity.this, HomeActivity.class));
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

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        boolean userExists = dbHelper.checkUser(email, hashedPassword);

        if (userExists) {
            SharedPreferences loginPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = loginPreferences.edit();
            editor.putString(KEY_EMAIL, email);
            editor.putString(KEY_PASSWORD, hashedPassword);

            if (rememberMeCheckBox.isChecked()) {
                editor.putBoolean(KEY_ISLOGGEDIN, true);
            } else {
                editor.putBoolean(KEY_ISLOGGEDIN, false);
            }

            editor.apply();
            startActivity(new Intent(MainActivity.this, HomeActivity.class));
        } else {
            emailInputLayout.setError("Invalid email or password");
            passwordInputLayout.setError("Invalid email or password");
        }
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

    private void setupEditTextAnimation(TextInputEditText editText) {

        float defaultRadiusPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        int defaultStrokeWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        int defaultStrokeColor = Color.parseColor("#CCCCCC");

        float focusedRadiusPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());
        int focusedStrokeWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        int focusedStrokeColor = Color.parseColor("#4285F4");

        editText.setOnFocusChangeListener((v, hasFocus) -> {
            GradientDrawable background = (GradientDrawable) editText.getBackground().mutate();

            float startRadius = background.getCornerRadius(); // Current radius
            float endRadius = hasFocus ? focusedRadiusPx : defaultRadiusPx;

            ValueAnimator radiusAnimator = ValueAnimator.ofFloat(startRadius, endRadius);
            radiusAnimator.setDuration(200); // Animation duration in milliseconds
            radiusAnimator.addUpdateListener(animation -> {
                float animatedValue = (float) animation.getAnimatedValue();
                background.setCornerRadius(animatedValue);
            });

            // For stroke width, it's an int. If your drawable has getStrokeWidth() (API 24+ for GradientDrawable)
            // We'll animate from current to target. For simplicity, we assume we know the start based on focus state.
            int startStrokeWidth = hasFocus ? defaultStrokeWidthPx : focusedStrokeWidthPx;
            int endStrokeWidth = hasFocus ? focusedStrokeWidthPx : defaultStrokeWidthPx;

            ValueAnimator strokeWidthAnimator = ValueAnimator.ofInt(startStrokeWidth, endStrokeWidth);
            strokeWidthAnimator.setDuration(200);
            strokeWidthAnimator.addUpdateListener(animation -> {
                background.setStroke((int) animation.getAnimatedValue(), background.getColor() != null ? background.getColor().getDefaultColor() : (hasFocus ? focusedStrokeColor: defaultStrokeColor) );
            });

            // For stroke color
            // We need to get the current stroke color. GradientDrawable doesn't have a direct getter pre-API 29.
            // We'll animate from current to target, assuming we know the start based on focus state.
            int startStrokeColor = hasFocus ? defaultStrokeColor : focusedStrokeColor;
            int endStrokeColor = hasFocus ? focusedStrokeColor : defaultStrokeColor;

            ValueAnimator strokeColorAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), startStrokeColor, endStrokeColor);
            strokeColorAnimator.setDuration(200);
            strokeColorAnimator.addUpdateListener(animation -> {
                // Determine the correct stroke width based on the focus state
                int currentTargetStrokeWidth = hasFocus ? focusedStrokeWidthPx : defaultStrokeWidthPx;
                background.setStroke(currentTargetStrokeWidth, (int) animation.getAnimatedValue());
            });

            radiusAnimator.start();
            strokeWidthAnimator.start();
            strokeColorAnimator.start();
        });




    }
}
