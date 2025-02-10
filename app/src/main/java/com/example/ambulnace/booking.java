package com.example.ambulnace;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class booking extends AppCompatActivity {

    public static final String ACCOUNT_SID = ""; // Replace with your actual Account SID
    public static final String AUTH_TOKEN = ""; // Replace with your actual Auth Token
    public static final String TWILIO_PHONE_NUMBER = ""; // Replace with your Twilio phone number
    private static final String ORS_API_KEY = "";


    private EditText nameEditText, phoneEditText, otpEditText, locationEditText;
    private Button generateOtpButton, verifyOtpButton;
    private String generatedOtp;
    private SQLiteHelper sqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        otpEditText = findViewById(R.id.otpEditText);
        locationEditText = findViewById(R.id.locationEditText);
        generateOtpButton = findViewById(R.id.generateOtpButton);
        verifyOtpButton = findViewById(R.id.verifyOtpButton);

        sqliteHelper = new SQLiteHelper(this);

        generateOtpButton.setOnClickListener(v -> generateOtp());
        verifyOtpButton.setOnClickListener(v -> verifyOtp());
    }

    private void generateOtp() {
        String phoneNumber = phoneEditText.getText().toString().trim();
        if (phoneNumber.isEmpty() || phoneNumber.length() != 10) {
            phoneEditText.setError("Enter a valid 10-digit phone number");
            return;
        }
        generatedOtp = String.format("%04d", new Random().nextInt(10000));
        new SendOtpTask().execute(phoneNumber, generatedOtp);
        Log.e("OTP " ,"code "+generatedOtp);
        otpEditText.setEnabled(true);
        verifyOtpButton.setEnabled(true);
    }

    private void verifyOtp() {
        String enteredOtp = otpEditText.getText().toString().trim();
        if (enteredOtp.isEmpty() || enteredOtp.length() != 4) {
            otpEditText.setError("Enter the 4-digit OTP");
            return;
        }
        if (enteredOtp.equals(generatedOtp)) {
            Toast.makeText(this, "OTP Verified!", Toast.LENGTH_SHORT).show();
            String userAddress = locationEditText.getText().toString().trim();
            if (!userAddress.isEmpty()) {
                new FetchCoordinatesTask().execute(userAddress);
            } else {
                Toast.makeText(this, "Enter a valid location", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
        }
    }


    private class SendOtpTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String phoneNumber = "+91" + params[0];
            String otp = params[1];

            try {
                String urlString = "https://api.twilio.com/2010-04-01/Accounts/" + ACCOUNT_SID + "/Messages.json";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                // Authentication
                String auth = ACCOUNT_SID + ":" + AUTH_TOKEN;
                String basicAuth = "Basic " + Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);
                conn.setRequestProperty("Authorization", basicAuth);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // Twilio Message Data
                String data = "From=" + TWILIO_PHONE_NUMBER + "&To=" + phoneNumber + "&Body=Your OTP is " + otp;

                // Sending the request
                OutputStream os = conn.getOutputStream();
                os.write(data.getBytes());
                os.flush();
                os.close();

                // Read response
                int responseCode = conn.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        (responseCode >= 200 && responseCode < 300) ? conn.getInputStream() : conn.getErrorStream()
                ));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Logging response
                Log.e("TwilioResponse", "Code: " + responseCode + " Response: " + response.toString());

                return (responseCode == HttpURLConnection.HTTP_CREATED) ? "OTP sent successfully" : "Failed to send OTP: " + response.toString();

            } catch (Exception e) {
                Log.e("TwilioError", "Error sending OTP", e);
                return "Error: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(booking.this, result, Toast.LENGTH_SHORT).show();
        }
    }


    private class FetchCoordinatesTask extends AsyncTask<String, Void, double[]> {
        @Override
        protected double[] doInBackground(String... params) {
            String address = params[0];
            try {
                String urlString = "https://api.openrouteservice.org/geocode/search?api_key=" + ORS_API_KEY + "&text=" + address;
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray features = jsonResponse.getJSONArray("features");
                if (features.length() > 0) {
                    JSONObject geometry = features.getJSONObject(0).getJSONObject("geometry");
                    JSONArray coordinates = geometry.getJSONArray("coordinates");

                    double lon = coordinates.getDouble(0);
                    double lat = coordinates.getDouble(1);

                    return new double[]{lat, lon};
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(double[] coordinates) {
            if (coordinates != null) {
                double startLat = coordinates[0];
                double startLon = coordinates[1];
                double destLat = 13.3409; // Udupi Latitude
                double destLon = 74.7421; // Udupi Longitude

                // Start new activity with route details
                Intent intent = new Intent(booking.this, RouteActivity.class);
                intent.putExtra("startLat", startLat);
                intent.putExtra("startLon", startLon);
                intent.putExtra("destLat", destLat);
                intent.putExtra("destLon", destLon);
                startActivity(intent);
            } else {
                Toast.makeText(booking.this, "Failed to get location", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
