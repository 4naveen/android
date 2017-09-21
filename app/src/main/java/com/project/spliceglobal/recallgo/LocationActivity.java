package com.project.spliceglobal.recallgo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    ListView listView;
    ArrayList<String> locationList;
    Button arriving, leaving;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private double longitude;
    private double latitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        arriving = (Button) findViewById(R.id.arriving);
        leaving = (Button) findViewById(R.id.leaving);
        leaving.setBackgroundColor(Color.rgb(0, 137, 123));
        arriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arriving.setBackgroundColor(Color.rgb(0, 137, 123));
                leaving.setBackgroundColor(Color.WHITE);
                arriving.setTextColor(Color.WHITE);
                leaving.setTextColor(Color.BLACK);

            }
        });
        leaving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaving.setBackgroundColor(Color.rgb(0, 137, 123));
                arriving.setBackgroundColor(Color.WHITE);
                leaving.setTextColor(Color.WHITE);
                arriving.setTextColor(Color.BLACK);
            }
        });
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Add Location");
        }
        listView = (ListView) findViewById(R.id.lv);
        SearchView searchView = (SearchView) findViewById(R.id.searchView);
        locationList = new ArrayList<>();
        locationList.add("Hyderabad");
        locationList.add("Mumbai");
        locationList.add("Pune");
        locationList.add("Delhi");
        locationList.add("Banglore");
        locationList.add("Noida");
        locationList.add("Chennai");
        locationList.add("NCR");
        locationList.add("Kolkatta");
        locationList.add("Ahmedabad");
        locationList.add("Gurgoan");
        ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, locationList);
        locationArrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        listView.setAdapter(locationArrayAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<String> subLocationArrayList = new ArrayList<>();
                for (int i = 0; i < locationList.size(); i++) {
                    if (locationList.get(i).contains(newText)) {
                        subLocationArrayList.add(locationList.get(i));
                    }
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                }
                ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<>(LocationActivity.this, android.R.layout.simple_list_item_1, subLocationArrayList);
                locationArrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                listView.setAdapter(locationArrayAdapter);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_ok, menu);
        return super.onCreateOptionsMenu(menu);

    }
    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
       LatLng l = new LatLng(28.644800, 77.216721);
        MarkerOptions marker = new MarkerOptions().position(new LatLng(28.644800, 77.216721)).title("delhi");
        googleMap.addMarker(marker);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(l, 10));
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
      //  getCurrentLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    getCurrentLocation();
        System.out.println("onconnected");

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void getCurrentLocation() {
        map.clear();
        //Creating a location object
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            //moving the map to location
            moveMap();
        }
    }
    private void moveMap() {
        //String to display current latitude and longitude
        String msg = latitude + ", "+longitude;

        //Creating a LatLng Object to store Coordinates
        LatLng latLng = new LatLng(latitude, longitude);

        //Adding marker to map
        map.addMarker(new MarkerOptions()
                .position(latLng) //setting position
                .draggable(true) //Making the marker draggable
                .title("Current Location")); //Adding a title

        //Moving the camera
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        //Animating the camera
        map.animateCamera(CameraUpdateFactory.zoomTo(15));

        //Displaying current coordinates in toast
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        System.out.println("message"+msg);
    }
}
