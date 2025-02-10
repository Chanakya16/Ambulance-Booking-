package com.example.ambulnace;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity {

    private MapView mapView;
    private IMapController mapController;
    private Marker movingMarker;
    private static final String ORS_API_KEY = "5b3ce3597851110001cf62483f94d74cbe394ae785fe2b37b43e159f"; // Replace with your ORS API Key

    private double startLat, startLon, destLat, destLon;
    private List<GeoPoint> routePoints = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(getApplicationContext(), getPreferences(MODE_PRIVATE));
        setContentView(R.layout.activity_route);

        // Initialize Map
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Get coordinates from intent
        Intent intent = getIntent();
        startLat = intent.getDoubleExtra("startLat", 0);
        startLon = intent.getDoubleExtra("startLon", 0);
        destLat = intent.getDoubleExtra("destLat", 0);
        destLon = intent.getDoubleExtra("destLon", 0);

        Log.d("RouteRequest", "Start: " + startLon + "," + startLat + " | End: " + destLon + "," + destLat);

        // Set initial map view
        mapController = mapView.getController();
        mapController.setZoom(12);
        mapController.setCenter(new GeoPoint(startLat, startLon));

        // Add moving marker (default OSMDroid marker)
        movingMarker = new Marker(mapView);
        movingMarker.setPosition(new GeoPoint(startLat, startLon));
        movingMarker.setTitle("Moving Vehicle");
        mapView.getOverlays().add(movingMarker);

        // Fetch route
        new FetchRouteTask().execute();
    }

    private class FetchRouteTask extends AsyncTask<Void, Void, List<GeoPoint>> {
        @Override
        protected List<GeoPoint> doInBackground(Void... voids) {
            try {
                // Request route from ORS AP
                String urlString = "https://api.openrouteservice.org/v2/directions/driving-car?api_key="
                        + ORS_API_KEY + "&start=" + destLon + "," +  destLat
                        + "&end=" + startLon + "," +  startLat
                        + "&geometry_format=geojson";

                Log.d("RouteAPI", "Request URL: " + urlString);

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // Read response (including errors)
                int responseCode = conn.getResponseCode();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        responseCode >= 200 && responseCode < 300 ? conn.getInputStream() : conn.getErrorStream()
                ));

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Log.d("RouteAPI", "Response Code: " + responseCode);
                Log.d("RouteAPI", "Response: " + response.toString());

                if (responseCode != 200) {
                    return null; // Invalid response
                }

                // Parse JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray features = jsonResponse.getJSONArray("features");

                if (features.length() == 0) {
                    Log.e("RouteError", "No route found");
                    return null;
                }

                JSONArray coordinates = features.getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONArray("coordinates");

                List<GeoPoint> points = new ArrayList<>();
                for (int i = 0; i < coordinates.length(); i++) {
                    JSONArray point = coordinates.getJSONArray(i);
                    double lon = point.getDouble(0);
                    double lat = point.getDouble(1);
                    points.add(new GeoPoint(lat, lon));
                }
                return points;
            } catch (Exception e) {
                Log.e("RouteError", "Error fetching route", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<GeoPoint> points) {
            if (points != null) {
                routePoints = points;
                startMoving();
            } else {
                Toast.makeText(RouteActivity.this, "Failed to fetch route", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startMoving() {
        new Thread(() -> {
            try {
                for (GeoPoint point : routePoints) {
                    runOnUiThread(() -> {
                        movingMarker.setPosition(point);
                        mapController.setCenter(point);
                        mapView.invalidate();
                    });
                    Thread.sleep(100); // Adjust speed (100ms per move)
                }

                runOnUiThread(() -> {
                    // Show a success dialog first
                    AlertDialog.Builder builder = new AlertDialog.Builder(RouteActivity.this);
                    builder.setTitle("Success")
                            .setMessage("Destination Reached!")
                            .setIcon(R.drawable.done)
                            .setPositiveButton("OK", (dialog, which) -> {
                                // After clicking OK, navigate to HomeActivity
                                Intent intent = new Intent(RouteActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .setCancelable(false)
                            .show();
                });


            } catch (InterruptedException e) {
                Log.e("RouteError", "Error animating marker", e);
            }
        }).start();
    }


}
