package com.project.spliceglobal.recallgo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    Button arriving, leaving;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private double longitude;
    private double latitude;
    private String entry="Arriving";
    private Circle mCircle;
    private double radiusInMeters = 200.0;
    //red outline
    private int strokeColor = 0xffff0000;
    //opaque red fill
    private int shadeColor = 0x44ff0000;
   // SearchView searchView;
    LinearLayout search_layout;
    TextView found_place;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            //ActivityCompat.requestPermissions(this, new String[]{ ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

     /*   if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }*/
        search_layout=(LinearLayout)findViewById(R.id.search_layout);
        found_place=(TextView)findViewById(R.id.found_place);
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
        arriving.setBackgroundColor(Color.rgb(5, 112, 130));
        arriving.setTextColor(Color.WHITE);
        //leaving.setBackgroundColor(Color.rgb(0, 137, 123));
        arriving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arriving.setBackgroundColor(Color.rgb(5, 112, 130));
                leaving.setBackground(getResources().getDrawable(R.drawable.cell_shape_button));
                arriving.setTextColor(Color.WHITE);
                leaving.setTextColor(getResources().getColor(R.color.colorPrimary));
                entry="Arriving";
            }
        });
        leaving.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaving.setBackgroundColor(Color.rgb(5, 112, 130));
                arriving.setBackground(getResources().getDrawable(R.drawable.cell_shape_button));
                leaving.setTextColor(Color.WHITE);
                arriving.setTextColor(getResources().getColor(R.color.colorPrimary));
                entry="leaving";
            }
        });
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Add Location");
        }
  /*      searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setQueryHint("current location");
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete
                                    .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(LocationActivity.this);
                    startActivityForResult(intent, 1);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return false;
            }
        });*/
        search_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete
                                    .IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(LocationActivity.this);
                    startActivityForResult(intent, 1);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_ok, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent myIntent = new Intent();
                myIntent.putExtra("latitude",0.00);
                myIntent.putExtra("longitude",0.00);
                myIntent.putExtra("radius",0.00);
                myIntent.putExtra("entry",entry);
                setResult(Activity.RESULT_OK, myIntent);
                finish();
                break;
            }
            case R.id.ok: {
                Intent myIntent = new Intent();
                Double truncatedlatitude = BigDecimal.valueOf(latitude)
                        .setScale(4, RoundingMode.HALF_UP)
                        .doubleValue();
                Double truncatedlongitude = BigDecimal.valueOf(longitude)
                        .setScale(4, RoundingMode.HALF_UP)
                        .doubleValue();
                Double truncatedradius = BigDecimal.valueOf(radiusInMeters)
                        .setScale(4, RoundingMode.HALF_UP)
                        .doubleValue();
                myIntent.putExtra("latitude",truncatedlatitude);
                myIntent.putExtra("longitude",truncatedlongitude);
                myIntent.putExtra("radius",truncatedradius);
                myIntent.putExtra("entry",entry);
                setResult(Activity.RESULT_OK, myIntent);
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
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
        System.out.println("current loc--"+msg);
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

        CircleOptions circleOptions = new CircleOptions().center(latLng).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(2);
        mCircle = map.addCircle(circleOptions);
        //Displaying current coordinates in toast
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        System.out.println("message"+msg);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        //locationManager.requestLocationUpdates(provider, 400, 1, this);
                        String provider = android.provider.Settings.Secure.getString(getContentResolver(),android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                        if (!provider.contains("gps"))
                        {
                            buildAlertMessageNoGps();
                        }
                        else {
                            getCurrentLocation();
                        }

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // retrive the data by using getPlace() method.
                Place place = PlaceAutocomplete.getPlace(this, data);
                latitude=place.getLatLng().latitude;
                longitude=place.getLatLng().longitude;
                Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());
                System.out.println("address"+place.getAddress()+"latlong"+place.getLatLng());
                //searchView.setQueryHint(place.getAddress());
                 found_place.setText(place.getAddress());
                map.clear();
                map.addMarker(new MarkerOptions()
                        .position(place.getLatLng()) //setting position
                        .draggable(true) //Making the marker draggable
                        .title("Current Location")); //Adding a title
                map.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                map.animateCamera(CameraUpdateFactory.zoomTo(15));

                CircleOptions circleOptions = new CircleOptions().center(place.getLatLng()).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(2);
                mCircle = map.addCircle(circleOptions);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.e("Tag", status.getStatusMessage());

            }
            else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        if (requestCode==101){
             getCurrentLocation();
        }
    }
    private void buildAlertMessageNoGps() {

                new MaterialDialog.Builder(LocationActivity.this)
                .content("Your GPS seems to be disabled, do you want to enable it")
                .titleGravity(GravityEnum.CENTER)
               // .customView(R.layout.forget_pwd_info, true)
                .positiveText("Yes")
                .negativeText("No")
                .autoDismiss(false)
                .positiveColorRes(R.color.colorPrimary)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),101);
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent myIntent = new Intent();
        myIntent.putExtra("latitude",0.00);
        myIntent.putExtra("longitude",0.00);
        myIntent.putExtra("radius",0.00);
        myIntent.putExtra("entry",entry);
        setResult(Activity.RESULT_OK, myIntent);
        finish();
    }
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("")
                        .setMessage("We will access your location to enable this feature !")
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(LocationActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSION_REQUEST_CODE);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE);
            }
            return false;
        } else {
            String provider = android.provider.Settings.Secure.getString(getContentResolver(),android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            if (!provider.contains("gps"))
            {
                buildAlertMessageNoGps();
            }
            return true;
        }
    }
}
