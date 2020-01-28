package ca.bcit.androidproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import static com.google.android.gms.common.api.GoogleApiClient.*;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;


public class ViewMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,
        ConnectionCallbacks, OnConnectionFailedListener {
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };
    private SupportMapFragment mapFragment;
    private Location lastLocation;
    private LocationManager locationManager;
    private Marker markerCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Button cancelButton;
    private LatLng destLatLng = null;
    private LatLng nextLatLng = null;
    private LatLng currentLatLng;
    private SupportMapFragment supportMapFragment;
    private ArrayList<Polyline> polylines;
    private Marker nextMarker;
    Context mContext;

    ArrayList<LatLng> markerPoints = new ArrayList<>();
    ArrayList<LatLng> latlngPoints = new ArrayList<>();
    ArrayList<LatLng> artList = new ArrayList<>();

    private GoogleMap mMap;
    private double currentLat;
    private double currentLng;

    @Override
    @SuppressWarnings("MissingPermission")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_maps);
        mContext = this;
        cancelButton = findViewById(R.id.cancelButton);
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        requestPermissions(INITIAL_PERMS, 1337);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                2000,
                10, locationListenerGPS);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewMapsActivity.this.cancelAdventure();
            }
        });
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Adventure adventure = (Adventure) getIntent().getSerializableExtra("adventure");
        ArrayList<Double> points = adventure.getPath();
        for(int i = 0; i < points.size(); i = i + 2){
            double lat = points.get(i);
            double lng = points.get(i + 1);
            latlngPoints.add(new LatLng(lat, lng));
        }
    }

    private void init() {
        for (int i = 0; i < latlngPoints.size(); i++) {
            MarkerOptions options = new MarkerOptions();
            options.position(latlngPoints.get(i));
            if (i == 0)
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
            else
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(options);
        }
        PolylineOptions rectOptions = new PolylineOptions();
        rectOptions.add(currentLatLng);
        rectOptions.addAll(latlngPoints);
        rectOptions.width(10);
        rectOptions.color(Color.BLUE);
        mMap.addPolyline(rectOptions);
    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        return distance;
    }

    private void createAdventure() {
    }

    private void findClosest() {
        double min = 100000.0;
        int index = 0;
        double usedLng = 0, usedLat = 0;
        for (int i = 0; i < artList.size(); i++) {
            double newLat = artList.get(i).latitude;
            double newLng = artList.get(i).longitude;
            double temp = Math.abs((newLat - currentLat) * (newLat - currentLat) + (newLng - currentLng) * (newLng - currentLng));
            if (temp < min) {
                min = temp;
                index = i;
                usedLng = newLng;
                usedLat = newLat;
            }
        }
        double dist = distance(currentLat, usedLat, currentLng, usedLng);
        if (dist > 30.0) {
            String msg = "There are no spots close by!";
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
            return;
        }

        latlngPoints.add(artList.get(index));

        destLatLng = null;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlngPoints.get(latlngPoints.size() - 1)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        PolylineOptions rectOptions = new PolylineOptions();
        rectOptions.addAll(latlngPoints);
        rectOptions.width(10);
        rectOptions.color(Color.BLUE);
        mMap.addPolyline(rectOptions);
    }

    private void cancelAdventure() {
        Intent intent = new Intent(this, RatingActivity.class);
        Adventure adventure = (Adventure) getIntent().getSerializableExtra("adventure");
        intent.putExtra("adventure", adventure);
        startActivity(intent);
    }


    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            currentLat = location.getLatitude();
            currentLng = location.getLongitude();
            LatLng latLng = new LatLng(currentLat, currentLng);
            currentLatLng = latLng;
            mMap.clear();
            init();
            double dist = distance(currentLat, latlngPoints.get(0).latitude, currentLng, latlngPoints.get(0).longitude);
            if (dist < 30.0) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.success_dialog, null);
                dialogBuilder.setView(dialogView);
                final TextView dialogUserID = dialogView.findViewById(R.id.textViewSuccess);
                dialogUserID.setText("Tou successfully made it to the next point! Good job :)");
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();



                /*String msg="You made it to the next point! Good job :)";
                Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();*/
                latlngPoints.remove(0);
                mMap.clear();
                init();
                return;
            }
            String msg = "New Latitude: " + BigDecimal.valueOf(currentLat)
                    .setScale(3, RoundingMode.HALF_UP)
                    .doubleValue() + "New Longitude: " + BigDecimal.valueOf(currentLng)
                    .setScale(3, RoundingMode.HALF_UP)
                    .doubleValue();
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
        LatLng bcit = new LatLng(49.25, -123.001);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bcit));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setMapType(MAP_TYPE_HYBRID);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        //init();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        if (markerCurrentLocation != null) {
            markerCurrentLocation.remove();
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        double dist = distance(latLng.latitude, latlngPoints.get(0).latitude, latLng.longitude, latlngPoints.get(0).longitude);
        if (dist < 30.0) {
            String msg = "You made it to the next point! Good job :)";
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
            latlngPoints.remove(latlngPoints.size() - 1);
            mMap.clear();
            init();
            return;
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        markerCurrentLocation = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}




