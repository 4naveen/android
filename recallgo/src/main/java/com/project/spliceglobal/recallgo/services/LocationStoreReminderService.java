package com.project.spliceglobal.recallgo.services;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

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
import com.project.spliceglobal.recallgo.LocateStoreActivity;
import com.project.spliceglobal.recallgo.utils.Http;
import com.project.spliceglobal.recallgo.R;
import com.project.spliceglobal.recallgo.model.Item;
import com.project.spliceglobal.recallgo.model.Place;
import com.project.spliceglobal.recallgo.utils.MyVolleySingleton;
import com.squareup.picasso.Picasso;

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
public class LocationStoreReminderService extends IntentService {
    private Location mCurrentLocation,mPreviousLocation;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    ArrayList<Item> itemArrayList,locationItemArrayList;
    private String next_url;
    public LocationStoreReminderService() {
        super("LocationStoreReminderService");
    }
    boolean isLocationCallbakRunningForTheFirstTime;
    private Boolean mRequestingLocationUpdates;
    private int PROXIMITY_RADIUS = 5000;
    private static final String GOOGLE_API_KEY = "AIzaSyDmslbRobmpTcZHQD0moN3VuMw-wiRonCg";
    private int notification_id;
    private boolean isStoreFound=false;

    @Override
    public void onCreate() {
        super.onCreate();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mPreviousLocation=new Location("");
        itemArrayList=new ArrayList<>();
        //mLastUpdateTime = "";
        createLocationCallback();
        createLocationRequest();
       // Toast.makeText(this, "onCreate runs", Toast.LENGTH_SHORT).show();
       // Log.v("my_tag", "onCreate runs");
       // System.out.println("onCreate runs");
    /*    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("PREF_NAME", true);
        editor.commit();*/
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        getItems("http://ec2-35-154-135-19.ap-south-1.compute.amazonaws.com:8001/api/reminders/"+"?date="+dateFormat.format(new Date()));

    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //System.out.println("callback runs");
                /*  SharedPreferences prefsa = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Log.v("my_tag", "pref is: "+prefsa.getBoolean("PREF_NAME", false));*/
                 mCurrentLocation = locationResult.getLastLocation();
                 System.out.println("location changed");
                if (mCurrentLocation!=null){
                   // System.out.println("mcurrentlocation:"+mCurrentLocation+"mpreviouslocation"+mPreviousLocation);
                    mRequestingLocationUpdates=true;
                      stopLocationUpdates();
                    if (itemArrayList.size()!=0){
                        for (int i = 0; i < itemArrayList.size(); i++) {
                            notification_id=i;

                            //System.out.println("today list size"+itemArrayList.size());
                         //  System.out.println("item name"+itemArrayList.get(i).getItem_name()+"item list"+itemArrayList.get(i).getList_name()+"pstore"+itemArrayList.get(i).getStore_name());
                            String reminder_name=itemArrayList.get(i).getItem_name();
                            String prefered_store=itemArrayList.get(i).getStore_name();
                          /*  SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            if (prefs.getBoolean("PREF_NAME", true)) {
                                System.out.println("send notification");
                            }*/
                           // String type = itemArrayList.get(i).getList_name();
                            //String type = "grocery_or_supermarket";
                            String type=itemArrayList.get(i).getGoogle_category();
                            if (!type.equalsIgnoreCase("null")){

                                StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                                googlePlacesUrl.append("location=" + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());
                                googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
                                googlePlacesUrl.append("&types=" + type);
                                googlePlacesUrl.append("&sensor=true");
                                googlePlacesUrl.append("&key=" + GOOGLE_API_KEY);
                                GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();

                                Object[] toPass = new Object[4];
                                toPass[0] = googlePlacesUrl.toString();
                                toPass[1]=notification_id;
                                toPass[2]=prefered_store;
                                toPass[3]=reminder_name;
                                googlePlacesReadTask.execute(toPass);
                            }


                        }
                    }
                }
              /*  isLocationCallbakRunningForTheFirstTime = false;
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("PREF_NAME", false);
                editor.commit();*/
            }
        };
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
       // Log.v("my_tag", "onDestroy called");
      //  Toast.makeText(this, "onDestroy called", Toast.LENGTH_SHORT).show();
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

    public  void getItems(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            //  System.out.println("resonse"+response);
                            JSONObject jsonObject=new JSONObject(response);
                            int final_count=jsonObject.getInt("count");
                            next_url=jsonObject.getString("next");
                            // System.out.println("next url"+next_url);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            if (jsonArray.length()!=0){

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Item item=new Item();
                                    item.setItem_name(object.getString("name"));
                                    item.setDate_time(object.getString("date"));
                                    item.setList_name(object.getString("list_name"));
                                    item.setGoogle_category(object.getString("google_category"));
                                    if (object.getString("store").equalsIgnoreCase("null"))
                                    {item.setStore_name("");}
                                    else {item.setStore_name(object.getString("store_name"));}
                                    itemArrayList.add(item);

                                }
                            }
                            if (!next_url.equalsIgnoreCase("null")){
                                getItems(next_url);
                            }
                            else {
                                System.out.println("today list size in call"+itemArrayList.size());
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
                HashMap<String,String>header=new HashMap<>();
                header.put("Content-Type", "application/json; charset=utf-8");
                // header.put("Authorization","Token fe63a7b37e04515a4cba77d2960526a84d1a56da");
                header.put("Authorization","Token aa5c12b3ebac6d122304d9b6c0713ae39863d938");
                // header.put("Content-Type", "application/x-www-form-urlencoded");
                return header;
            }
        } ;
        MyVolleySingleton.getInstance(this).getRequestQueue().add(stringRequest);
    }

    private void stopLocationUpdates() {
        if (!mRequestingLocationUpdates) {
           // Log.d("", "stopLocationUpdates: updates never requested, no-op.");
            return;
        }
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }
    public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {
        String googlePlacesData = null;
        ArrayList<Place>placeArrayList;
        int notify_id;
        String prefered_store,reminder;
        @Override
        protected String doInBackground(Object... inputObj) {
            try {
                String googlePlacesUrl = (String) inputObj[0];
                notify_id=(int)inputObj[1];
                System.out.println("url"+googlePlacesUrl);
                Http http = new Http();
                googlePlacesData = http.read(googlePlacesUrl);
                prefered_store=(String) inputObj[2];
                reminder=(String) inputObj[3];
            } catch (Exception e) {
                Log.d("Google Place Read Task", e.toString());
            }
            return googlePlacesData;
        }
        @Override
        protected void onPostExecute(String result) {
          // System.out.println("reponse"+result);
            placeArrayList=new ArrayList<>();
            try {
                JSONObject jsonObject=new JSONObject(result);
                JSONArray jsonArray=jsonObject.getJSONArray("results");
                for (int i = 0; i <5 ; i++) {
                    Place place=new Place();
                    JSONObject object=jsonArray.getJSONObject(i);
                    JSONObject geometry=object.getJSONObject("geometry");
                    JSONObject location=geometry.getJSONObject("location");
                   /* double lat=location.getDouble("lat");
                    double lng=location.getDouble("lng");
                    String icon_url=object.getString("icon");
                    String name=object.getString("name");*/
                    // System.out.println("lat"+lat+"lng"+lng+"name"+name+"url"+icon_url);
                    place.setLatitude(location.getDouble("lat"));
                    place.setLongitude(location.getDouble("lng"));
                    place.setName(object.getString("name"));
                    place.setIcon_url(object.getString("icon"));
                    placeArrayList.add(place);

                   // System.out.println("prefered store"+prefered_store);
                   // CustomNotification(placeArrayList.get(0).getLatitude(),placeArrayList.get(0).getLongitude(),placeArrayList.get(0).getName(),placeArrayList.get(0).getIcon_url(),notification_id);
                  //  CustomNotification(lat,lng,name,icon_url,notify_id);
                   // System.out.println("notify_id"+notify_id);

            /*   if (prefered_store.equalsIgnoreCase(name)){
                   CustomNotification(lat,lng,name,icon_url,notification_id);
                   isStoreFound=true;
                   System.out.println(isStoreFound);
                   break;
               }*/
                }
                boolean found=false;
                for (int i = 0; i <placeArrayList.size() ; i++) {
                  //  Log.e("placelist contains",placeArrayList.get(i).getName());
                    if (prefered_store.equalsIgnoreCase(placeArrayList.get(i).getName())){
                        Log.e("found ","true");
                          found=true;
                          CustomNotification(reminder,placeArrayList.get(i).getLatitude(),placeArrayList.get(i).getLongitude(),placeArrayList.get(i).getName(),placeArrayList.get(i).getIcon_url(),notify_id,placeArrayList);
                        break;
                    }
                    else {
                    }
                   // Log.e("found ","false");
                }
                if (!found){
                    CustomNotification(reminder,placeArrayList.get(0).getLatitude(),placeArrayList.get(0).getLongitude(),placeArrayList.get(0).getName(),placeArrayList.get(0).getIcon_url(),notify_id,placeArrayList);

                }

             /*   if (!isStoreFound){
                    System.out.println(isStoreFound);
                    CustomNotification(placeArrayList.get(0).getLatitude(),placeArrayList.get(0).getLongitude(),placeArrayList.get(0).getName(),placeArrayList.get(0).getIcon_url(),notification_id);
                }*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void CustomNotification(String reminder,double lat,double lng,String name,String icon_url,int id,ArrayList<Place>placeArrayList) {
        // Using RemoteViews to bind custom layouts into Notification
        Location mShopLocation = new Location(LocationManager.GPS_PROVIDER);
        mShopLocation.setLatitude(lat);
        mShopLocation.setLongitude(lng);
        float distance =mCurrentLocation.distanceTo(mShopLocation)/1000;
        Double truncatedDistance = BigDecimal.valueOf(distance)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        //System.out.println("distance"+distance);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.my_notification);
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f",lat, lng);
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lng);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
       /* Intent mapIntent = new Intent(this, LocateStoreActivity.class);
        mapIntent.putParcelableArrayListExtra("placeList",placeArrayList);
        mapIntent.putExtra("clat",mCurrentLocation.getLatitude());
        mapIntent.putExtra("clong",mCurrentLocation.getLongitude());*/
        PendingIntent pIntent = PendingIntent.getActivity(this, id, mapIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                // Set Icon
                .setSmallIcon(R.mipmap.ic_launcher_round)
                // Set Ticker Message
                .setTicker("Notification")
                // Dismiss Notification
                .setAutoCancel(true)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Set RemoteViews into Notification
                .setContent(remoteViews);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.title_img);
            builder.setColor(getResources().getColor(R.color.bg_screen3));
        } else {
            builder.setSmallIcon(R.drawable.notification_icon);
        }
        //Intent switchIntent1 = new Intent(this, MapActivity.class);
        remoteViews.setTextViewText(R.id.store_name,reminder+" from "+name);
        remoteViews.setTextViewText(R.id.distance,"Distance :"+String.valueOf(truncatedDistance)+" km");
        final Notification notification = builder.build();
        Picasso.with(this)
                .load(icon_url)
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(remoteViews, R.id.shop_icon,id,notification);
        Intent switchIntent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        switchIntent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingSwitchIntent1 = PendingIntent.getActivity(this, id,
                switchIntent1, PendingIntent.FLAG_ONE_SHOT);
        remoteViews.setOnClickPendingIntent(R.id.gmap, pendingSwitchIntent1);
        // Create Notification Manager
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Build Notification with Notification Manager
        notificationmanager.notify(id, builder.build());
    }
}
