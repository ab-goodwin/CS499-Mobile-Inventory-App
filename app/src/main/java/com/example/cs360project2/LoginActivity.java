// LoginActivity.java
package com.example.cs360project2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameField, passwordField;
    private Button loginButton, createAccountButton;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);

        // Initialize database
        database = openOrCreateDatabase("AppDB", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS Users(username TEXT PRIMARY KEY, password TEXT);");

        loginButton.setOnClickListener(view -> loginUser());
        createAccountButton.setOnClickListener(view -> createAccount());
    }

    private void loginUser() {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = database.rawQuery("SELECT * FROM Users WHERE username = ? AND password = ?", new String[]{username, password});
        if (cursor.moveToFirst()) {
            Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private void createAccount() {
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            database.execSQL("INSERT INTO Users(username, password) VALUES(?, ?);", new String[]{username, password});
            Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Account creation failed. Username might already exist.", Toast.LENGTH_SHORT).show();
        }
    }
}
