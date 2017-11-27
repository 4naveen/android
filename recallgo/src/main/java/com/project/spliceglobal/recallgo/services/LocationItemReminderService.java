package com.project.spliceglobal.recallgo.services;

import android.Manifest;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.project.spliceglobal.recallgo.R;
import com.project.spliceglobal.recallgo.ReminderActivity;
import com.project.spliceglobal.recallgo.model.Item;
import com.project.spliceglobal.recallgo.utils.AppUrl;
import com.project.spliceglobal.recallgo.utils.MyVolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Personal on 11/10/2017.
 */

public class LocationItemReminderService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,com.google.android.gms.location.LocationListener{
    ArrayList<Item> itemArrayList,locationItemArrayList;
    int final_count;
    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;
    private double longitude;
    private double latitude;
    LatLng mLocation;
    String next_url;
    SharedPreferences pref;
    int PRIVATE_MODE = 0;
    SharedPreferences.Editor editor;
    public static LocationManager locationManager;

    public static final String PREF_NAME = "RecallPref";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public LocationItemReminderService() {
        super("");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        itemArrayList=new ArrayList<>();
        locationItemArrayList=new ArrayList<>();
        pref = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        editor = pref.edit();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        getItems(AppUrl.ITEM_LIST_URL);
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, ReminderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(com.project.spliceglobal.recallgo.R.mipmap.ic_launcher)
                .setContentTitle("Today Reminder")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.title_img);
            notificationBuilder.setColor(getResources().getColor(R.color.bg_screen3));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.notification_icon);
        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }

    public  void getItems(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("resonse"+response);
                            JSONObject jsonObject=new JSONObject(response);
                            next_url=jsonObject.getString("next");
                            final_count=jsonObject.getInt("count");
                            System.out.println("next in location"+next_url);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            if (jsonArray.length()!=0){
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Item item = new Item();
                                    if (!(object.getString("lat").equalsIgnoreCase("null")&&object.getString("long").equalsIgnoreCase("null")))
                                    {
                                        item.setLati(object.getString("lat"));
                                        item.setLongi(object.getString("long"));
                                        // System.out.println("date"+dates[0].substring(0,10));
                                        itemArrayList.add(item);
                                    }
                                }
                            }
                            if (!next_url.equalsIgnoreCase("null")){
                                getItems(next_url);
                            }
                            else {
                                System.out.println("item array size"+itemArrayList.size());
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.i("response--", String.valueOf(error));
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String>header=new HashMap<>();
                header.put("Content-Type", "application/json; charset=utf-8");
                // header.put("Authorization","Token fe63a7b37e04515a4cba77d2960526a84d1a56da");
                header.put("Authorization","Token "+ AppUrl.TOKEN);
                // header.put("Content-Type", "application/x-www-form-urlencoded");
                return header;
            }
        } ;
        MyVolleySingleton.getInstance(this).getRequestQueue().add(stringRequest);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        new Handler().post(new Runnable(){
            @Override
            public void run() {
      //  getCurrentLocation();




            }
        });
        UpdateLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            //Getting longitude and latitude
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            System.out.println("current loc"+longitude+""+latitude);
            Toast.makeText(getApplicationContext(),"current loc"+longitude+""+latitude,Toast.LENGTH_LONG).show();
            //moving the map to location
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Double truncatedlatitude = BigDecimal.valueOf(location.getLatitude())
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue();
        Double truncatedlongitude = BigDecimal.valueOf(location.getLongitude())
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue();
        mLocation = new LatLng(truncatedlatitude,truncatedlongitude);
        Location locationA = new Location(LocationManager.GPS_PROVIDER);
        locationA.setLatitude(truncatedlatitude);
        locationA.setLongitude(truncatedlongitude);

        editor.clear();
       /* editor.putString("latitude",String.valueOf(truncatedlatitude));
        editor.putString("longitude",String.valueOf(truncatedlongitude));
        editor.commit();*/

        Log.v("pref saved loc ", "location is: "+Math.round(truncatedlatitude)+""+Math.round(truncatedlongitude));

        if (itemArrayList.size()!=0){
            //System.out.println("total item size in location"+itemArrayList.size());
                for (int i = 0; i < itemArrayList.size(); i++) {
                // System.out.println("today"+dateFormat.format(new Date()));
               // System.out.println("item location"+itemArrayList.get(i).getLongi()+""+itemArrayList.get(i).getLati());
                LatLng iLocation = new LatLng(Double.parseDouble(itemArrayList.get(i).getLati()),Double.parseDouble(itemArrayList.get(i).getLongi()));
                //System.out.println("mloc"+mLocation+"i loc"+iLocation);

                Location locationB = new Location(LocationManager.GPS_PROVIDER);
                locationB.setLatitude(Double.parseDouble(itemArrayList.get(i).getLati()));
                locationB.setLongitude(Double.parseDouble(itemArrayList.get(i).getLongi()));
               // System.out.println("locationA"+locationA+"locationB"+locationB);

                float distance = locationA.distanceTo(locationB);
               /* if (mLocation.equals(iLocation)){
                        System.out.println("yes");
                        locationItemArrayList.add(itemArrayList.get(i));

                }*/
                if (distance<200.0);
                {
                    locationItemArrayList.add(itemArrayList.get(i));
                    //Toast.makeText(getApplicationContext(),"you are reaching to your fav store",Toast.LENGTH_SHORT).show();
                }
            }

            //System.out.println("loc array size"+locationItemArrayList.size());
            // sendNotification("Today you have "+todayItemArrayList.size()+"item  in reminder");
          //  Toast.makeText(getApplicationContext(),"you are reaching to your fav store",Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(this, "location is: " + location.getLatitude()+", "+location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    private void UpdateLocation() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
    }
}
