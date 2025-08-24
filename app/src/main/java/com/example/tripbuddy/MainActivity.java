package com.example.tripbuddy;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

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

        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
    }
}
