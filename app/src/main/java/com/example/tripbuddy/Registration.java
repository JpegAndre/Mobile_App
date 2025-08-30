package com.example.tripbuddy;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.tripbuddy.MainActivity;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Registration extends AppCompatActivity {

    private Button btnRegister, btnLogin;

    private CheckBox rememberMeCheckBox;

    private TextInputLayout emailInputLayout, passwordInputLayout, phoneInputLayout, ageInputLayout;

    private TextInputEditText emailEditText, passwordEditText, phoneEditText, ageEditText;

    private SharedPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        loginPreferences = getSharedPreferences(MainActivity.SHARED_PREFS, MODE_PRIVATE);
//        SharedPreferences.Editor editor = loginPreferences.edit();
//        editor.putString(KEY_EMAIL, email);
//        editor.putString(KEY_PASSWORD, hashedPassword);
//        editor.putBoolean(KEY_ISLOGGEDIN, true);
//        editor.apply();
//        startActivity(new Intent(MainActivity.this, HomeActivity.class));

        emailEditText = findViewById(R.id.edtEmail);
        passwordEditText= findViewById(R.id.edtPassword);
        phoneEditText= findViewById(R.id.edtPhone);
        ageEditText= findViewById(R.id.edtAge);
        emailInputLayout = findViewById(R.id.inputLayoutEmail);
        passwordInputLayout= findViewById(R.id.inputLayoutPassword);
        phoneInputLayout= findViewById(R.id.inputLayoutPhone);
        ageInputLayout= findViewById(R.id.inputLayoutAge);

        rememberMeCheckBox = findViewById(R.id.checkRememberMe);

        setupEditTextAnimation(emailEditText);
        setupEditTextAnimation(passwordEditText);
        setupEditTextAnimation(phoneEditText);
        setupEditTextAnimation(ageEditText);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        Intent receiveLogin = getIntent();
        String email = receiveLogin.getStringExtra("email");
        String password = receiveLogin.getStringExtra("password");

        if (email != null) {
            emailEditText.setText(email);
        }

        if (password != null) {
            passwordEditText.setText(password);
        }


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleRegistration();
//                startActivity(new Intent(Registration.this, MainActivity.class));

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(Registration.this, MainActivity.class));
            }
        });
    }

    private void handleRegistration() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        int age;

        try {
            age = Integer.parseInt(ageEditText.getText().toString().trim());
        } catch (NumberFormatException e) {
            ageInputLayout.setError("Enter a valid age");
            return;
        }

        if (email.isEmpty()) {
            emailInputLayout.setError("Email cannot be empty");
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.setError("Enter a valid email address");
            return;
        } else {
            emailInputLayout.setError(null);
            emailInputLayout.setErrorEnabled(false);
        }

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

        if (phone.isEmpty()) {
            phoneInputLayout.setError("Phone number cannot be empty");
            return;
        } else if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
            phoneInputLayout.setError("Enter a valid phone number");
            return;
        } else {
            phoneInputLayout.setError(null);
            phoneInputLayout.setErrorEnabled(false);
        }

        if (age <= 0) {
            ageInputLayout.setError("Age must be a positive number");
            return;
        } else if (age > 120) {
            ageInputLayout.setError("Age cannot be greater than 120");
            return;
        } else {
            ageInputLayout.setError(null);
            ageInputLayout.setErrorEnabled(false);
        }

        String hashedPassword = hashValue(password);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        boolean userExists = dbHelper.checkUser(email, hashedPassword);

        if (!userExists){
            dbHelper.addUser(email, hashedPassword, phone, age);


            SharedPreferences.Editor editor = loginPreferences.edit();
            editor.putString(MainActivity.KEY_EMAIL, email);
            editor.putString(MainActivity.KEY_PASSWORD, hashedPassword);

                if (rememberMeCheckBox.isChecked()) {
                    editor.putBoolean(MainActivity.KEY_ISLOGGEDIN, true);
                } else {
                    editor.putBoolean(MainActivity.KEY_ISLOGGEDIN, false);
                }

            editor.apply();
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            emailInputLayout.setError("User already exists with this email");
            return;
        }

        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
        System.out.println("Phone: " + phone);
        System.out.println("Age: " + age);
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

    private void setupEditTextAnimation(TextInputEditText editText) {
        // Initial state from the simplified drawable
        float defaultRadiusPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        int defaultStrokeWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        int defaultStrokeColor = Color.parseColor("#CCCCCC");

        // Focused state values
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


            int startStrokeWidth = hasFocus ? defaultStrokeWidthPx : focusedStrokeWidthPx; 
            int endStrokeWidth = hasFocus ? focusedStrokeWidthPx : defaultStrokeWidthPx;
            
            ValueAnimator strokeWidthAnimator = ValueAnimator.ofInt(startStrokeWidth, endStrokeWidth);
            strokeWidthAnimator.setDuration(200);
            strokeWidthAnimator.addUpdateListener(animation -> {
                background.setStroke((int) animation.getAnimatedValue(), background.getColor() != null ? background.getColor().getDefaultColor() : (hasFocus ? focusedStrokeColor: defaultStrokeColor) );
            });


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