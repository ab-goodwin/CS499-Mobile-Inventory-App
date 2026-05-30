// LoginActivity.java
package com.example.cs360project2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameField, passwordField;

    // DatabaseHelper replaces SQLite usage in this class
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        Button loginButton = findViewById(R.id.loginButton);
        Button createAccountButton = findViewById(R.id.createAccountButton);

        // initializes DatabaseHelper
        // LoginActivity uses helper instead
        // of opening database directly
        databaseHelper = new DatabaseHelper(this);

        // connects screen buttons to the
        // login / account creation methods
        loginButton.setOnClickListener(view -> loginUser());
        createAccountButton.setOnClickListener(view -> createAccount());
    }

    private void loginUser() {
        // Reads username and password entered by user
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // login check is now handled by
        // database helper and keeps
        // SQLite logic out of this class
        boolean loginSuccessful = databaseHelper.checkUser(username, password);

        if (loginSuccessful) {
            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
    }

    private void createAccount() {
        // reads username and password entered by user
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // adds stronger validation for passwords under 6 characters
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // attempts to create a new user through DatabaseHelper
        // DatabaseHelper inserts the user and hashes the password
        boolean accountCreated = databaseHelper.createUser(username, password);

        if (accountCreated) {
            Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Account creation failed. Username might already exist.", Toast.LENGTH_SHORT).show();
        }
    }
}