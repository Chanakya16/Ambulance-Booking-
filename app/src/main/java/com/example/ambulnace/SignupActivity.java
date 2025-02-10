package com.example.ambulnace;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText editTextDriverName, editTextVehicleNumber, editTextPhoneNumber, editTextLocation, editTextPassword, editTextConfirmPassword;
    private Button signUpButton, loginButton;
    private SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        dbHelper = new SQLiteHelper(this);  // Initialize SQLiteHelper

        // Initialize UI components
        editTextDriverName = findViewById(R.id.editTextText3);
        editTextVehicleNumber = findViewById(R.id.editTextText4);
        editTextPhoneNumber = findViewById(R.id.editTextText5);
        editTextLocation = findViewById(R.id.editTextLocation);  // Added location field
        editTextPassword = findViewById(R.id.editTextText6);
        editTextConfirmPassword = findViewById(R.id.editTextText7);
        signUpButton = findViewById(R.id.button2);

        // Sign-up button click event
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String driverName = editTextDriverName.getText().toString().trim();
                String vehicleNumber = editTextVehicleNumber.getText().toString().trim();
                String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                String location = editTextLocation.getText().toString().trim();  // Retrieve location
                String password = editTextPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();

                // Validate inputs
                if (TextUtils.isEmpty(driverName)) {
                    editTextDriverName.setError("Driver name is required");
                    editTextDriverName.requestFocus();
                    return;
                }

                String vehicleNumberPattern = "^[A-Z]{2}-\\d{2}-[A-Z]{2}-\\d{4}$";

                if (TextUtils.isEmpty(vehicleNumber)) {
                    editTextVehicleNumber.setError("Vehicle number is required");
                    editTextVehicleNumber.requestFocus();
                    return;
                }

                if (!vehicleNumber.matches(vehicleNumberPattern)) {
                    editTextVehicleNumber.setError("Invalid vehicle number. Example: KA-20-ET-2946");
                    editTextVehicleNumber.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(phoneNumber)) {
                    editTextPhoneNumber.setError("Phone number is required");
                    editTextPhoneNumber.requestFocus();
                    return;
                }

                // Validate phone number length (example: 10 digits)
                if (phoneNumber.length() != 10) {
                    editTextPhoneNumber.setError("Phone number must be 10 digits");
                    editTextPhoneNumber.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(location)) {
                    editTextLocation.setError("Location is required");
                    editTextLocation.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Password is required");
                    editTextPassword.requestFocus();
                    return;
                }

                // Validate password length (example: minimum 6 characters)
                if (password.length() < 6) {
                    editTextPassword.setError("Password must be at least 6 characters");
                    editTextPassword.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword)) {
                    editTextConfirmPassword.setError("Please confirm your password");
                    editTextConfirmPassword.requestFocus();
                    return;
                }

                // Validate password confirmation
                if (!password.equals(confirmPassword)) {
                    editTextConfirmPassword.setError("Passwords do not match");
                    editTextConfirmPassword.requestFocus();
                    return;
                }

                // Insert data into the SQLite database
                boolean isInserted = dbHelper.insertUser(driverName, vehicleNumber, phoneNumber, location, password);
                if (isInserted) {
                    Toast.makeText(SignupActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                    // Navigate back to Login screen
                    finish();
                    Intent intent = new Intent(SignupActivity.this, DriverLogin.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(SignupActivity.this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
