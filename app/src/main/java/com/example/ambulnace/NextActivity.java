package com.example.ambulnace;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

public class NextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        TextView nameTextView = findViewById(R.id.nameTextView);
        TextView vehicleNumberTextView = findViewById(R.id.vehicleNumberTextView);
        TextView phoneNumberTextView = findViewById(R.id.phoneNumberTextView);
        TextView addressTextView = findViewById(R.id.addressTextView);

        // Retrieve data from intent
        String driverName = getIntent().getStringExtra("driverName");
        String vehicleNumber = getIntent().getStringExtra("vehicleNumber");
        String phoneNumber = getIntent().getStringExtra("phoneNumber");
        String driverAddress = getIntent().getStringExtra("driverAddress");

        // Set driver details to TextViews
        nameTextView.setText(driverName);
        vehicleNumberTextView.setText(vehicleNumber);
        phoneNumberTextView.setText(phoneNumber);
        addressTextView.setText(driverAddress);
    }
}