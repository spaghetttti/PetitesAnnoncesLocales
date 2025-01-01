package com.example.petitesannonceslocales;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Spinner profileTypeSpinner;
    private Button registerButton;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        profileTypeSpinner = findViewById(R.id.profileTypeSpinner);
        registerButton = findViewById(R.id.registerButton);

        // Populate spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.profile_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        profileTypeSpinner.setAdapter(adapter);

        // Set register button click listener
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String profileType = profileTypeSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is required.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required.");
            return;
        }

        if (password.length() < 6) {
            passwordInput.setError("Password must be at least 6 characters.");
            return;
        }

        // Create user with Firebase Auth
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Save user profile to Firestore
                        saveUserProfile(email, profileType);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Registration failed: "
                                + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveUserProfile(String email, String profileType) {
        String userId = auth.getCurrentUser().getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("userType", profileType);

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                    // Navigate to login or main activity

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtra("destination", R.id.navigation_account);
                    startActivity(intent);
                    finish();

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this, "Failed to save user profile: "
                            + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}