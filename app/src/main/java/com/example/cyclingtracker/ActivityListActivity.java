package com.example.cyclingtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityListActivity extends AppCompatActivity {

    private ListView listView;
    private DatabaseReference cyclingRef;
    private List<String> activityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Initialize listView and Firebase reference
        listView = findViewById(R.id.listView);
        cyclingRef = FirebaseDatabase.getInstance().getReference("cyclingActivities");

        // Initialize the activityList
        activityList = new ArrayList<>();

        // Load activities from Firebase
        loadActivitiesFromFirebase();
    }

    private void loadActivitiesFromFirebase() {
        cyclingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("FirebaseData", dataSnapshot.toString());  // Log the entire DataSnapshot

                activityList.clear(); // Clear the list before adding new data
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Convert each snapshot into a CyclingActivity object
                        CyclingActivity activity = snapshot.getValue(CyclingActivity.class);
                        if (activity != null) {
                            // Format the data and add it to the list
                            String distance = activity.getDistance() + " km";
                            String speed = activity.getSpeed() + " km/h";
                            String duration = activity.getDuration() + " hours";
                            String timestamp;

                            // Format timestamp as readable date
                            if (activity.getTimestamp() > 0) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                timestamp = sdf.format(new Date(activity.getTimestamp()));
                            } else {
                                timestamp = "N/A";
                            }

                            String activityDetails = "Distance: " + distance + "\n"
                                    + "Speed: " + speed + "\n"
                                    + "Duration: " + duration + "\n"
                                    + "Timestamp: " + timestamp;
                            activityList.add(activityDetails);
                        }
                    }
                } else {
                    Toast.makeText(ActivityListActivity.this, "No activities found in Firebase.", Toast.LENGTH_SHORT).show();
                }

                // Set up the adapter to display the list
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ActivityListActivity.this, android.R.layout.simple_list_item_1, activityList);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.getMessage());
                Toast.makeText(ActivityListActivity.this, "Failed to load data from Firebase.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
