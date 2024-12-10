package com.example.cyclingtracker;

public class CyclingActivity {

    private double distance;
    private float speed;
    private float duration;
    private long timestamp;

    @SuppressWarnings("unused") // Required by Firebase for deserialization
    public CyclingActivity() {
        // Default constructor required for calls to DataSnapshot.getValue(CyclingActivity.class)
    }


    // Constructor to initialize the fields
    public CyclingActivity(float speed, double distance, long timestamp) {
        this.speed = speed;
        this.distance = distance;
        this.timestamp = timestamp;
        this.duration = 0; // You can set this value elsewhere if needed
    }

    // Getters and setters for each field
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
