package com.example.ambulnace;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnNext = findViewById(R.id.btnNext);
        Button btnDriverLogin = findViewById(R.id.btnDriverLogin);

        // Set OnClickListener for the booking button
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the booking activity
                Intent intent = new Intent(home.this, booking.class);
                startActivity(intent);
            }
        });

        // Set OnClickListener for the driver login button
        btnDriverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the DriverLogin activity
                Intent intent = new Intent(home.this, DriverLogin.class);
                startActivity(intent);
            }
        });
    }
}
