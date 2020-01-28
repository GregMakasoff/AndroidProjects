package ca.bcit.androidproject;


import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Adventure implements Serializable {
    private String key;
    private String user;
    private String title;
    private String description;
    private double rating;
    private int reviews;
    private ArrayList<Double> path;

    public Adventure() {
    }

    public Adventure(String title, String description, double rating) {
        this.title = title;
        this.description = description;
        this.rating = rating;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getReviews() {
        return reviews;
    }

    public void setReviews(int reviews) {
        this.reviews = reviews;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public ArrayList<Double> getPath() {
        return path;
    }

    public void setPath(ArrayList<Double> path) {
        this.path = path;
    }
}
