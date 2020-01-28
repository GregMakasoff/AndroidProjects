package ca.bcit.androidproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String[] INITIAL_PERMS={
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS
    };
    private SupportMapFragment mapFragment;
    private Location lastLocation;
    private LocationManager locationManager;
    private Marker markerCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Button startButton;
    private Button cancelButton;
    private Button saveButton;
    private Button addButton;
    private LatLng destLatLng = null;
    private LatLng nextLatLng = null;
    private SupportMapFragment supportMapFragment;
    private ArrayList<Polyline> polylines;
    private Marker nextMarker;
    Context mContext;

    ArrayList<LatLng> markerPoints = new ArrayList<>();
    ArrayList<LatLng> latlngPoints = new ArrayList<>();
    ArrayList<LatLng> pointList = new ArrayList<>();

    private GoogleMap mMap;
    private double currentLat;
    private double currentLng;

    @Override
    @SuppressWarnings("MissingPermission")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mContext = this;
        cancelButton = findViewById(R.id.cancelButton);
        startButton = findViewById(R.id.startButton);
        saveButton = findViewById(R.id.saveButton);
        addButton = findViewById(R.id.addButton);
        locationManager=(LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        requestPermissions(INITIAL_PERMS, 1337);
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                2000,
                10, locationListenerGPS);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsActivity.this.findClosest();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsActivity.this.cancelAdventure();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsActivity.this.createAdventure();

            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsActivity.this.findClosest();
            }
        });
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
        Intent intent = new Intent(this, SaveAdventure.class);
        Adventure adventure = new Adventure();
        ArrayList<Double> points = new ArrayList<>();
        for(int i = 0; i < latlngPoints.size(); i++) {
            double lat = latlngPoints.get(i).latitude;
            double lng = latlngPoints.get(i).longitude;
            points.add(lat);
            points.add(lng);
        }
        adventure.setPath(points);
        intent.putExtra("adventure", adventure);
        startActivity(intent);
    }

    private void findClosest() {
        double min = 100000.0;
        int index = 0;
        double usedLng = 0, usedLat = 0;
        for(int i = 0; i < pointList.size(); i++) {
            double newLat = pointList.get(i).latitude;
            double newLng = pointList.get(i).longitude;
            double temp = Math.abs((newLat - currentLat)*(newLat - currentLat) + (newLng - currentLng)*(newLng - currentLng));
            if(temp < min) {
                min = temp;
                index = i;
                usedLng = newLng;
                usedLat = newLat;
            }
        }
        double dist = distance(currentLat, usedLat, currentLng, usedLng);
        if(dist > 30.0) {
            String msg="There are no spots close by!";
            Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
            return;
        }
        startButton.setVisibility(View.INVISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        addButton.setVisibility(View.VISIBLE);

        latlngPoints.add(pointList.get(index));

        destLatLng = null;
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlngPoints.get(latlngPoints.size() - 1)));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        PolylineOptions rectOptions = new PolylineOptions();
        rectOptions.addAll(latlngPoints);
        rectOptions.width(10);
        rectOptions.color(Color.BLUE);
        mMap.addPolyline(rectOptions);
    }

    private void addLayers() throws IOException, JSONException {
        InputStream assetInStream=getResources().openRawResource(R.raw.jsonformatter);
        createLayer(assetInStream);
        assetInStream=getResources().openRawResource(R.raw.jsongardens);
        createLayer(assetInStream);
        assetInStream=getResources().openRawResource(R.raw.jsonheritage);
        createLayer(assetInStream);
        assetInStream=getResources().openRawResource(R.raw.jsongardens);
        createLayer(assetInStream);
    }

    private void createLayer(InputStream assetInStream) throws IOException, JSONException  {
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(assetInStream, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            assetInStream.close();
        }

        String jsonString = writer.toString();
        double lat = 0.0, lng = 0.0;
        JSONArray jsonObject = new JSONArray(jsonString);
        for(int i = 0; i < jsonObject.length(); i++) {
            JSONObject firstObject = jsonObject.getJSONObject(i);
            JSONObject fields = firstObject.getJSONObject("fields");

            JSONObject geom = fields.getJSONObject("geom");
            if(geom.has("coordinates")) {
                JSONArray coordinates = geom.getJSONArray("coordinates");
                lat = Double.parseDouble(coordinates.get(1).toString());
                lng = Double.parseDouble(coordinates.get(0).toString());
            }
            MarkerOptions options = new MarkerOptions();
            LatLng newSpot = new LatLng(lat, lng);
            options.position(newSpot);
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(options);
            pointList.add(newSpot);
        }
    }

    private void cancelAdventure() {
        startActivity(new Intent(MapsActivity.this, HomeActivity.class));
    }


    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            currentLat=location.getLatitude();
            currentLng=location.getLongitude();
            String msg="New Latitude: " + currentLat + "New Longitude: " + currentLng;
            Toast.makeText(mContext,msg,Toast.LENGTH_LONG).show();
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
        try {
            addLayers();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException g) {
            g.printStackTrace();
        }
    }

    private void printList(ArrayList<LatLng> list) {
        for(int i =0; i < list.size(); i++) {
            MarkerOptions options = new MarkerOptions();
            options.position(list.get(i));
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            mMap.addMarker(options);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
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
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        markerCurrentLocation =  mMap.addMarker(markerOptions);

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



