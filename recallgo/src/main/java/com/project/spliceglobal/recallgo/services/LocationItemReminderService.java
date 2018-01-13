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
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.project.spliceglobal.recallgo.R;
import com.project.spliceglobal.recallgo.ReminderActivity;
import com.project.spliceglobal.recallgo.model.Item;
import com.project.spliceglobal.recallgo.utils.AppConstants;
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
import java.util.Locale;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class LocationItemReminderService extends IntentService {

    private Location mCurrentLocation,mPreviousLocation;
    private LocationCallback mLocationCallback;
    private Boolean mRequestingLocationUpdates;
    private String mLastUpdateTime;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private SettingsClient mSettingsClient;
    private static final String TAG = ReminderActivity.class.getSimpleName();
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private LocationSettingsRequest mLocationSettingsRequest;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";
    private DateFormat dateFormat;
    ArrayList<Item> itemArrayList,locationItemArrayList;
    private String next_url,repeat_alarm;
    private int repeat_type;
    public LocationItemReminderService() {
        super("MyLocationService");
    }
    boolean isLocationCallbakRunningForTheFirstTime;
    @Override
    public void onCreate() {
        super.onCreate();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);
        mPreviousLocation=new Location("");
        locationItemArrayList=new ArrayList<>();
        mLastUpdateTime = "";
        createLocationCallback();
        createLocationRequest();
       // Toast.makeText(this, "onCreate runs", Toast.LENGTH_SHORT).show();
        Log.v("my_tag", "onCreate runs");
        System.out.println("onCreate runs");
        isLocationCallbakRunningForTheFirstTime = true;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("PREF_NAME", true);
        editor.commit();
        Log.v("my_tag", "isLocationCallbakRunningForTheFirstTime inside onCreate is "+isLocationCallbakRunningForTheFirstTime);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        itemArrayList=new ArrayList<>();
        getItems(AppUrl.ITEM_LIST_URL);
    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //Toast.makeText(getApplicationContext(), "callback runs", Toast.LENGTH_SHORT).show();
                //System.out.println("callback runs");
               // Log.v("my_tag", "callback runs");
               // Log.v("my_tag", "isLocationCallbakRunningForTheFirstTime inside callback is "+isLocationCallbakRunningForTheFirstTime);
                SharedPreferences prefsa = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                //Log.v("my_tag", "pref is: "+prefsa.getBoolean("PREF_NAME", false));
                mCurrentLocation = locationResult.getLastLocation();
                Double truncatedlatitude = BigDecimal.valueOf(locationResult.getLastLocation().getLatitude())
                        .setScale(4, RoundingMode.HALF_UP)
                        .doubleValue();
                Double truncatedlongitude = BigDecimal.valueOf(locationResult.getLastLocation().getLongitude())
                        .setScale(4, RoundingMode.HALF_UP)
                        .doubleValue();
                mCurrentLocation.setLatitude(truncatedlatitude);
                mCurrentLocation.setLongitude(truncatedlongitude);
                //System.out.println("mcurrentlocation:"+mCurrentLocation+"mpreviouslocation"+mPreviousLocation);
                if (mCurrentLocation!=mPreviousLocation)
                {
                    mRequestingLocationUpdates=true;
                    stopLocationUpdates();
                    //System.out.println("location changed");
                    mPreviousLocation=mCurrentLocation;
                   // System.out.println("stored size"+AppConstant.list_size+"item array size"+itemArrayList.size());
                    if (itemArrayList.size()!=0){
                        if (AppConstants.list_size!=itemArrayList.size()) {
                            AppConstants.list_size = itemArrayList.size();
                            for (int i = 0; i < itemArrayList.size(); i++) {
                                Location locationB = new Location(LocationManager.GPS_PROVIDER);
                                locationB.setLatitude(Double.parseDouble(itemArrayList.get(i).getLati()));
                                locationB.setLongitude(Double.parseDouble(itemArrayList.get(i).getLongi()));
                                String entry = itemArrayList.get(i).getEntry();
                                String [] dates=itemArrayList.get(i).getNext_reminder_date().split("T");
                                String reminder_date=dates[0];
                                String current_date=dateFormat.format(new Date());
                                Log.e("reminder_date",reminder_date+"current date"+current_date);
                                if (itemArrayList.get(i).getEntry().equalsIgnoreCase("null")) {
                                    entry = "Arriving";
                                }
                                // System.out.println("locationB"+locationB);
                                // System.out.println("entry"+entry);
                                float distance = mCurrentLocation.distanceTo(locationB);
                                //System.out.println("distance"+distance);
                                if (entry.equalsIgnoreCase("Arriving")) {
                                    if (distance < (float) 200.0) {
                                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        if (prefs.getBoolean("PREF_NAME", true)) {
                                            //locationItemArrayList.add(itemArrayList.get(i));
                                            //locationManager.removeUpdates();
                                            // Toast.makeText(getApplicationContext(),itemArrayList.get(i).getItem_name(), Toast.LENGTH_SHORT).show();
                                              if (current_date.equalsIgnoreCase(reminder_date)){
                                                  sendNotification(itemArrayList.get(i).getItem_name(), i);
                                                  System.out.println("send notification");
                                              }
                                        }
                                    }
                                }
                                if (entry.equalsIgnoreCase("leaving")) {
                                    if (distance >= (float) 10.0 ) {
                                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                        if (prefs.getBoolean("PREF_NAME", true)) {
                                            //locationItemArrayList.add(itemArrayList.get(i));
                                            //locationManager.removeUpdates();
                                            // Toast.makeText(getApplicationContext(),itemArrayList.get(i).getItem_name(), Toast.LENGTH_SHORT).show();
                                            if (current_date.equalsIgnoreCase(reminder_date)){
                                                sendNotification(itemArrayList.get(i).getItem_name(), i);
                                                System.out.println("send notification");
                                            }
                                        }
                                    }
                                }
                            }
                            isLocationCallbakRunningForTheFirstTime = false;
                        }
                        // AppConstant.list_size=itemArrayList.size();
                        //System.out.println("stored size"+AppConstant.list_size+"item array size"+itemArrayList.size());
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("PREF_NAME", false);
                        editor.commit();
                    }
                }
                //System.out.println("mcurrentlocation"+mCurrentLocation+"mpreviouslocation:"+mPreviousLocation);
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
               // System.out.println("latitude:"+mCurrentLocation.getLatitude()+"longitude:"+mCurrentLocation.getLongitude());
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("my_tag", "onDestroy called");
       // Toast.makeText(this, "onDestroy called", Toast.LENGTH_SHORT).show();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());
    }

    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            Log.d("", "stopLocationUpdates: updates never requested, no-op.");
            return;
        }
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
    private void sendNotification(String messageBody, int id) {
        Intent intent = new Intent(this, ReminderActivity.class);
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Reminder")
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
        notificationManager.notify(id, notificationBuilder.build());
    }

    public  void getItems(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("response"+response);
                            JSONObject jsonObject=new JSONObject(response);
                            next_url=jsonObject.getString("next");
                            int final_count=jsonObject.getInt("count");
                            System.out.println("next in location"+next_url);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                          /*  if (itemArrayList.size()!=0){
                                itemArrayList.clear();
                            }*/
                            if (jsonArray.length()!=0){
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Item item = new Item();
                                    if (!(object.getString("lat").equalsIgnoreCase("null")&&object.getString("long").equalsIgnoreCase("null")))
                                    {
                                        item.setLati(object.getString("lat"));
                                        item.setLongi(object.getString("long"));
                                        item.setItem_name(object.getString("name"));
                                        item.setDate_created(object.getString("date_created"));
                                        item.setRepeat_type(String.valueOf(object.getInt("type")));
                                        item.setEntry(object.getString("entry"));
                                        item.setNext_reminder_date(object.getString("next_reminder_date"));
                                        // item.setEntry(object.getString("entry"));
                                        // System.out.println("date"+dates[0].substring(0,10));
                                        itemArrayList.add(item);

                                    }
                                }
                            }
                            if (!next_url.equalsIgnoreCase("null")){
                                getItems(next_url);
                            }
                            else {
                                System.out.println("item array size in call"+itemArrayList.size());
                                startLocationUpdates();
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
                HashMap<String,String> header=new HashMap<>();
                header.put("Content-Type", "application/json; charset=utf-8");
                header.put("Authorization","Token aa5c12b3ebac6d122304d9b6c0713ae39863d938");
                // header.put("Content-Type", "application/x-www-form-urlencoded");
                return header;
            }
        } ;
        MyVolleySingleton.getInstance(this).getRequestQueue().add(stringRequest);
    }
}
