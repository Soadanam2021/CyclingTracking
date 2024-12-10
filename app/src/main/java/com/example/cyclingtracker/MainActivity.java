package com.example.cyclingtracker;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private boolean tracking = false;
    private Location lastLocation = null;
    private TextView tvDistance, tvSpeed;
    private Button btnStartStop;
    private DatabaseReference cyclingRef;
    private LocationRequest locationRequest;
    private CyclingActivity cyclingActivity;
    private long startTime; // To track the start time of the activity
    private double totalDistance = 0; // Track the total distance covered

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Ensure the layout is set

        // Initialize UI components
        tvDistance = findViewById(R.id.tvDistance);
        tvSpeed = findViewById(R.id.tvSpeed);
        btnStartStop = findViewById(R.id.btnStartStop);
        Button viewActivitiesButton = findViewById(R.id.viewActivitiesButton);
        Button btnOpenMap = findViewById(R.id.btnOpenMap);  // Button to open the map

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        cyclingRef = database.getReference("cyclingActivities");

        // Initialize the CyclingActivity object with initial values
        cyclingActivity = new CyclingActivity(0.0f, 0.0, System.currentTimeMillis());

        // Set up location services
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Update to remove 'Priority' and use 'LocationRequest.PRIORITY_HIGH_ACCURACY'
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds interval
        locationRequest.setFastestInterval(5000); // 5 seconds fastest interval

        // Define location callback to update speed and distance
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult.getLastLocation() != null) {
                    Location currentLocation = locationResult.getLastLocation();
                    if (lastLocation != null) {
                        // Calculate distance
                        float distance = lastLocation.distanceTo(currentLocation) / 1000; // meters to kilometers
                        totalDistance += distance; // Accumulate total distance
                        cyclingActivity.setDistance(totalDistance); // Set distance in CyclingActivity
                        tvDistance.setText(String.format(Locale.getDefault(), getString(R.string.distance_text), totalDistance));

                        // Calculate speed
                        float speed = currentLocation.getSpeed() * 3.6f; // m/s to km/h
                        cyclingActivity.setSpeed(speed); // Set speed in CyclingActivity
                        tvSpeed.setText(String.format(Locale.getDefault(), getString(R.string.speed_text), speed));
                    }
                    lastLocation = currentLocation;
                }
            }
        };

        // Set up the Start/Stop tracking button listener
        btnStartStop.setOnClickListener(v -> {
            if (tracking) {
                stopTracking();
            } else {
                startTracking();
            }
        });

        // Set up the View Activities button listener to launch ActivityListActivity
        viewActivitiesButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActivityListActivity.class);
            startActivity(intent);
        });

        // Set up the Open Map button listener to launch MapActivity
        btnOpenMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
        });
    }

    private void startTracking() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        tracking = true;
        btnStartStop.setText(getString(R.string.stop_tracking));

        // Set the timestamp when tracking starts
        startTime = System.currentTimeMillis(); // Record the start time
        cyclingActivity.setTimestamp(startTime); // Initial timestamp

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopTracking() {
        tracking = false;
        btnStartStop.setText(getString(R.string.start_tracking));
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        // Calculate the duration in hours
        long endTime = System.currentTimeMillis();
        float duration = (endTime - startTime) / (1000 * 60 * 60.0f); // Convert milliseconds to hours
        cyclingActivity.setDuration(duration);  // Set the duration

        // Set the end timestamp for the cycling activity
        cyclingActivity.setTimestamp(endTime);

        // Save the cycling activity to Firebase
        saveCyclingActivity();
        totalDistance = 0;  // Reset for the next activity
    }

    private void saveCyclingActivity() {
        cyclingRef.push().setValue(cyclingActivity).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(MainActivity.this, "Activity saved to Firebase!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Failed to save activity to Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startTracking();
            } else {
                Toast.makeText(this, "Location permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
