package com.project.spliceglobal.recallgo;

import android.*;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.project.spliceglobal.recallgo.adapters.ListStoreAdapter;
import com.project.spliceglobal.recallgo.model.Place;
import com.project.spliceglobal.recallgo.services.LocationStoreReminderService;
import com.project.spliceglobal.recallgo.utils.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class ListStoreActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private Location mCurrentLocation;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private int PROXIMITY_RADIUS = 5000;
    private static final String GOOGLE_API_KEY = "AIzaSyDmslbRobmpTcZHQD0moN3VuMw-wiRonCg";
    private Boolean mRequestingLocationUpdates;
    private LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    String type;
    private LinearLayoutManager layoutManager;
    RecyclerView rv;
    private ListStoreAdapter listAdapter;
    private ArrayList<Place> placeArrayList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_store);
        type = getIntent().getStringExtra("category");
        createLocationRequest();
        // createLocationCallback();
        placeArrayList = new ArrayList<>();
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        rv = (RecyclerView) findViewById(R.id.rv);
/*        for (int i = 0; i <10 ; i++) {
            Place place=new Place();
            place.setName("Agarwal Store");
            place.setAddress("dhbvhdbvdvbvdbvdhbvjh");
            place.setRating("4.5");
            placeArrayList.add(place);
        }*/
/*        layoutManager = new LinearLayoutManager(ListStoreActivity.this,LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        listAdapter = new ListStoreAdapter(ListStoreActivity.this,placeArrayList);
        rv.setAdapter(listAdapter);
        rv.setItemAnimator(new DefaultItemAnimator());*/
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


    }

    @Override
    public void onStart() {
        super.onStart();
        // Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        //  Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        // Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //System.out.println("callback runs");
                /*  SharedPreferences prefsa = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Log.v("my_tag", "pref is: "+prefsa.getBoolean("PREF_NAME", false));*/
                startLocationUpdates();
                mCurrentLocation = locationResult.getLastLocation();
                System.out.println("location changed");
                if (mCurrentLocation != null) {
                    // System.out.println("mcurrentlocation:"+mCurrentLocation+"mpreviouslocation"+mPreviousLocation);
                    mRequestingLocationUpdates = true;
                    stopLocationUpdates();

                }
            }
        };
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
         mCurrentLocation=location;
        if (!type.equalsIgnoreCase("null")) {
            StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            googlePlacesUrl.append("location=" + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
            googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
            googlePlacesUrl.append("&types=" + type);
            googlePlacesUrl.append("&sensor=true");
            googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);
            GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
            Object[] toPass = new Object[2];
            toPass[0] = googlePlacesUrl.toString();
            toPass[1]=mCurrentLocation;

            googlePlacesReadTask.execute(toPass);
        }

    }

    public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
        String googlePlacesData = null;
        ArrayList<Place> placeArrayList;

        Location location;

        @Override
        protected String doInBackground(Object... inputObj) {
            try {
                String googlePlacesUrl = (String) inputObj[0];
                location=(Location)inputObj[1];
               // System.out.println("url" + googlePlacesUrl);
                Http http = new Http();
                googlePlacesData = http.read(googlePlacesUrl);
            } catch (Exception e) {
                Log.d("Google Place Read Task", e.toString());
            }
            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String result) {
            // System.out.println("reponse"+result);
            placeArrayList = new ArrayList<>();
            Location mShopLocation = new Location(LocationManager.GPS_PROVIDER);
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("results");
                for (int i = 0; i < 5; i++) {
                    Place place = new Place();
                    JSONObject object = jsonArray.getJSONObject(i);
                    JSONObject geometry = object.getJSONObject("geometry");
                    JSONObject location = geometry.getJSONObject("location");
                   /* double lat=location.getDouble("lat");
                    double lng=location.getDouble("lng");
                    String icon_url=object.getString("icon");
                    String name=object.getString("name");*/
                    // System.out.println("lat"+lat+"lng"+lng+"name"+name+"url"+icon_url);
                    mShopLocation.setLatitude(location.getDouble("lat"));
                    mShopLocation.setLongitude(location.getDouble("lng"));
                    float distance =mCurrentLocation.distanceTo(mShopLocation)/1000;
                    Double truncatedDistance = BigDecimal.valueOf(distance)
                            .setScale(2, RoundingMode.HALF_UP)
                            .doubleValue();
                    place.setLatitude(location.getDouble("lat"));
                    place.setLongitude(location.getDouble("lng"));
                    place.setName(object.getString("name"));
                    place.setIcon_url(object.getString("icon"));
                    place.setRating(String.valueOf(object.getDouble("rating")));
                    place.setAddress(object.getString("vicinity"));
                    place.setDistance(String.valueOf(truncatedDistance));
                    placeArrayList.add(place);
                }

                layoutManager = new LinearLayoutManager(ListStoreActivity.this, LinearLayoutManager.VERTICAL, false);
                rv.setLayoutManager(layoutManager);
                listAdapter = new ListStoreAdapter(ListStoreActivity.this, placeArrayList);
                rv.setAdapter(listAdapter);
                rv.setItemAnimator(new DefaultItemAnimator());
                progressBar.setVisibility(View.GONE);

                boolean found = false;
                for (int i = 0; i < placeArrayList.size(); i++) {
                    Log.e("placelist contains", placeArrayList.get(i).getName());
                    // Log.e("found ","false");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
       // Log.d(TAG, "Location update started ..............: ");
    }
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
       // Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
           // Log.d(TAG, "Location update resumed .....................");
        }
    }
}
