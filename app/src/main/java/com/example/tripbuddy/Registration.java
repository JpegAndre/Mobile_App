package com.example.tripbuddy;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.util.TypedValue;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class Registration extends AppCompatActivity {

    private Button btnRegister, btnLogin;

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

        TextInputEditText edtEmail = findViewById(R.id.edtEmail);
        TextInputEditText edtPassword = findViewById(R.id.edtPassword);
        TextInputEditText edtPhone = findViewById(R.id.edtPhone);
        TextInputEditText edtAge = findViewById(R.id.edtAge);

        setupEditTextAnimation(edtEmail);
        setupEditTextAnimation(edtPassword);
        setupEditTextAnimation(edtPhone);
        setupEditTextAnimation(edtAge);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Registration.this, MainActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(Registration.this, MainActivity.class));
            }
        });
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
        // Assuming the background color of the EditText itself doesn't change, only corners and stroke
        // If the solid color also needs to change, that can be added to the animator too.

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