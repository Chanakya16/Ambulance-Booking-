package com.example.ambulnace;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DriverLogin extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button loginButton, signUpButton;
    private SQLiteHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);

        dbHelper = new SQLiteHelper(this);  // Initialize SQLiteHelper

        // Initialize UI components
        editTextUsername = findViewById(R.id.editTextText);
        editTextPassword = findViewById(R.id.editTextText2);
        loginButton = findViewById(R.id.button);
        signUpButton = findViewById(R.id.button3);

        // Login button click event
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                // Validate credentials
                boolean isValid = dbHelper.checkUser(username, password);
                if (isValid) {
                    Toast.makeText(DriverLogin.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    // Proceed to next activity (dashboard, etc.)
                } else {
                    Toast.makeText(DriverLogin.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sign-up button click event
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DriverLogin.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}
