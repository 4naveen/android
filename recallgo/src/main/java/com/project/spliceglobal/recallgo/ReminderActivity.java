package com.project.spliceglobal.recallgo;

import android.*;
import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.spliceglobal.recallgo.adapters.DrawerListAdapter;
import com.project.spliceglobal.recallgo.fragments.AllFragment;
import com.project.spliceglobal.recallgo.fragments.SharedFragment;
import com.project.spliceglobal.recallgo.fragments.TodayFragment;
import com.project.spliceglobal.recallgo.model.NavItem;
import com.project.spliceglobal.recallgo.model.Site;
import com.project.spliceglobal.recallgo.services.LocationStoreReminderService;
import com.project.spliceglobal.recallgo.utils.AppConstants;
import com.project.spliceglobal.recallgo.utils.AppUrl;
import com.project.spliceglobal.recallgo.utils.MyVolleySingleton;
import com.project.spliceglobal.recallgo.utils.ObjectSerializer;
import com.project.spliceglobal.recallgo.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.INTERNET;

public class ReminderActivity extends AppCompatActivity {
    LinearLayout shared_layout, today_layout, all_layout;
    BottomNavigationView bottomNavigationView;
    ImageView arrow1, arrow2, arrow3;
    TextView status,shared_qty,today_qty,all_qty,date;
    CircleImageView profile_image,menu_header_image;
    TextView hello,textname,last_visited;
    byte[] images_bytes;
    private DrawerLayout mDrawerLayout;
    private static final int PERMISSION_REQUEST_CODE = 200;
    RelativeLayout mDrawerPane;
    private ActionBarDrawerToggle mDrawerToggle;
    android.support.v7.app.ActionBar actionBar;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    GridView mDrawerGrid;
    String email,name,mobile,encoded_string_image;
    SessionManager sessionManager;
    AlarmManager am;
    PendingIntent pi;
    SharedPreferences sharedPreferences,sharedPreferences1;
    SharedPreferences.Editor editor,editor1;
    int PRIVATE_MODE = 0;
    public static final String KEY_SITE = "site_name";
    public static final String PREF_NAME = "SitePref";
    public static final String PREF_NAME1 = "UncategorisedId";
    String month[]={"","Jan","Feb","March","April","May","June","July","August","Sept","Oct","Nov","Dec"};
    Set<String>siteList;
    ArrayList<Site> supported_site_List;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        supported_site_List=new ArrayList<>();
       // actionBar.setDisplayHomeAsUpEnabled(true);
        siteList=new HashSet<>();
        sharedPreferences = getApplicationContext().getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        sharedPreferences1=getApplicationContext().getSharedPreferences(PREF_NAME1,PRIVATE_MODE);
        sessionManager=new SessionManager(getApplicationContext());
        editor = sharedPreferences.edit();
        editor1 = sharedPreferences1.edit();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("BeaconId"));
       /* editor.clear();
        editor.apply();*/
        getUserSites(AppUrl.SITES_URL);
        getUserDetail(AppUrl.GET_USER_PROFILE_URL);
        getCategoryId(AppUrl.ALL_CATEGORY_URL);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        textname = (TextView) findViewById(R.id.name);
        last_visited = (TextView) findViewById(R.id.last_visited);
        hello = (TextView) findViewById(R.id.hello);
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "fonts/roboto-thin.ttf");
        hello.setTypeface(tf1);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        profile_image = (CircleImageView) findViewById(R.id.contact_image);
        menu_header_image=(CircleImageView) findViewById(R.id.menu_header_profile);
        shared_layout = (LinearLayout) findViewById(R.id.shared_layout);
        today_layout = (LinearLayout) findViewById(R.id.today_layout);
        all_layout = (LinearLayout) findViewById(R.id.all_layout);
        mNavItems.add(new NavItem("Reminder", R.drawable.reminder));
        mNavItems.add(new NavItem("Price Chaser", R.drawable.price_chaser));
        mNavItems.add(new NavItem("Tutorial", R.drawable.tutorial));
        mNavItems.add(new NavItem("Feedback", R.drawable.feedback));
        mNavItems.add(new NavItem("View Profile", R.drawable.profile));
        mNavItems.add(new NavItem("Log Out",R.drawable.logout));
        arrow1 = (ImageView) findViewById(R.id.arrow1);
        arrow2 = (ImageView) findViewById(R.id.arrow2);
        arrow3 = (ImageView) findViewById(R.id.arrow3);
        status = (TextView) findViewById(R.id.status);
        date = (TextView) findViewById(R.id.date);
        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("REFRESH_THIS");
        pi = PendingIntent.getBroadcast(this, 123456789, intent, 0);
        PendingIntent pendingIntent = PendingIntent.getService(this, 0,intent,0);
        long interval = 1000 * 50;
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),interval,pi);
        DateFormat dateFormat = new SimpleDateFormat("d MMM, yyyy");
        String formated_date = dateFormat.format(new Date());
        //System.out.println("today formated date "+formated_date);
        date.setText(dateFormat.format(new Date()));
        getCategoryId(AppUrl.ALL_CATEGORY_URL);
        //images_bytes = getIntent().getByteArrayExtra("image_bytes");
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        shared_qty=(TextView) findViewById(R.id.shared_qty);
        today_qty=(TextView) findViewById(R.id.today_qty);
        all_qty=(TextView) findViewById(R.id.all_qty);
        Bundle bundle = new Bundle();
        bundle.putString("called_from", "main_body");
        Fragment fragment1 = new TodayFragment();
        fragment1.setArguments(bundle);
        FragmentTransaction trans1 = getSupportFragmentManager().beginTransaction();
        trans1.replace(R.id.frame, fragment1);
        trans1.addToBackStack(null);
        trans1.commit();
        shared_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrow1.setVisibility(View.VISIBLE);
                arrow2.setVisibility(View.INVISIBLE);
                arrow3.setVisibility(View.INVISIBLE);
                status.setText("Shared");
                Fragment fragment1 = new SharedFragment();
                FragmentTransaction trans1 = getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame, fragment1);
                trans1.addToBackStack(null);
                trans1.commit();
            }
        });
        today_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrow2.setVisibility(View.VISIBLE);
                arrow3.setVisibility(View.INVISIBLE);
                arrow1.setVisibility(View.INVISIBLE);
                Bundle bundle = new Bundle();
                bundle.putString("called_from", "today_layout");
                status.setText("Today");
                Fragment fragment1 = new TodayFragment();
                FragmentTransaction trans1 = getSupportFragmentManager().beginTransaction();
                fragment1.setArguments(bundle);
                trans1.replace(R.id.frame, fragment1);
                trans1.addToBackStack(null);
                trans1.commit();
            }
        });
        all_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrow3.setVisibility(View.VISIBLE);
                arrow2.setVisibility(View.INVISIBLE);
                arrow1.setVisibility(View.INVISIBLE);
                status.setText("All Categories");
                Fragment fragment1 = new AllFragment();
                FragmentTransaction trans1 = getSupportFragmentManager().beginTransaction();
                trans1.replace(R.id.frame, fragment1);
                trans1.addToBackStack(null);
                trans1.commit();
            }
        });
  /*      shared_qty.setText(String.valueOf(getIntent().getIntExtra("shared",0)));
        today_qty.setText(String.valueOf(getIntent().getIntExtra("today",0)));
        all_qty.setText(String.valueOf(getIntent().getIntExtra("all",0)));*/
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.search: {
                               // bottomNavigationView.setVisibility(View.GONE);
                                arrow2.setVisibility(View.VISIBLE);
                                arrow3.setVisibility(View.INVISIBLE);
                                arrow1.setVisibility(View.INVISIBLE);
                                Bundle bundle = new Bundle();
                                bundle.putString("called_from", "search");
                                status.setText("Today");
                                Fragment fragment1 = new TodayFragment();
                                fragment1.setArguments(bundle);
                                FragmentTransaction trans1 = getSupportFragmentManager().beginTransaction();
                                trans1.replace(R.id.frame, fragment1);
                                trans1.addToBackStack(null);
                                trans1.commit();
                                break;
                            }
                            case R.id.reminder: {
                                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(bottomNavigationView.getWindowToken(),0);
                                arrow2.setVisibility(View.VISIBLE);
                                arrow3.setVisibility(View.INVISIBLE);
                                arrow1.setVisibility(View.INVISIBLE);
                                Bundle bundle = new Bundle();
                                bundle.putString("called_from", "reminder");
                                status.setText("Today");
                                Fragment fragment1 = new TodayFragment();
                                fragment1.setArguments(bundle);
                                FragmentTransaction trans1 = getSupportFragmentManager().beginTransaction();
                                trans1.replace(R.id.frame, fragment1);
                                trans1.addToBackStack(null);
                                trans1.commit();
                                break;
                            }
                            case R.id.price_chaser: {
                                startActivity(new Intent(ReminderActivity.this, PriceChaserActivity.class).putExtra("flag", false));
                                break;
                            }
                        }
                        return true;
                    }
                });
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
            checkLocationPermission();
          //  ActivityCompat.requestPermissions(this, new String[]{ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
        else {
            startService(new Intent(ReminderActivity.this, LocationStoreReminderService.class));
        }
   /*     if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
        }
        else {
            startService(new Intent(ReminderActivity.this, LocationStoreReminderService.class));
        }*/
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        // mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerGrid=(GridView)findViewById(R.id.grid);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        // mDrawerList.setAdapter(adapter);
        mDrawerGrid.setAdapter(adapter);
        // Drawer Item click listeners
      /*  mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });*/
        mDrawerGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Toast.makeText(HomeActivity.this, "You Clicked at " +position, Toast.LENGTH_SHORT).show();

                selectItemFromDrawer(position);

            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mDrawerLayout.getWindowToken(),0);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                Log.d("drawer", "onDrawerClosed: " + getTitle());
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public static Bitmap getImage(byte[] image) {
        if (image!=null){
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }
        return null;
    }

    public  void getCategoryId(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                String name=object.getString("name");
                                int id=object.getInt("id");
                                 if (name.equalsIgnoreCase("UnCategorized")){
                                     AppConstants.UncategorisedId=String.valueOf(id);
                                    // System.out.println("uncategorisedId"+AppConstants.UncategorisedId);
                                     editor1.putInt("id", id);
                                     editor1.apply();
                                     editor1.commit();
                                 }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("activity","on Activity result");

    }
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
                        startService(new Intent(ReminderActivity.this, LocationStoreReminderService.class));

                        //Request location updates:
                        //locationManager.requestLocationUpdates(provider, 400, 1, this);

                      //  getCurrentLocation();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }
    private void selectItemFromDrawer(int position) {
        //Toast.makeText(getApplicationContext(),"you clicked"+position,Toast.LENGTH_SHORT).show();
        mDrawerGrid.setItemChecked(position, true);
        setTitle(mNavItems.get(position).mTitle);
        if (position==0){

        }
        else if (position==1){
            Intent intent=new Intent(this,PriceChaserActivity.class);
            intent.putExtra("flag", false);
            if (supported_site_List!=null){
               intent.putParcelableArrayListExtra("site_list",supported_site_List);
            }
            startActivity(intent);

        }
        else if (position==2){
            startActivity(new Intent(this, TutorialActivity.class));
        }
        else if (position==3){
            startActivity(new Intent(this, FeedbackActivity.class));
        }
        else if (position==4){
            Intent i = new Intent(this, ProfileActivity.class);
            if (name!=null&&email!=null&&mobile!=null&&encoded_string_image!=null){
                i.putExtra("name",name);
                i.putExtra("email",email);
                i.putExtra("mobile",mobile);
                if (!encoded_string_image.isEmpty()){
                    byte[] decodedString = Base64.decode(encoded_string_image, Base64.DEFAULT);
                    i.putExtra("image_bytes",decodedString);
                }
                else {
                    byte[] decodedString = Base64.decode(encoded_string_image, Base64.DEFAULT);
                    i.putExtra("image_bytes",decodedString);
                }
            }
            startActivity(i);
        }
        else {
            sessionManager.logoutUser();
            finish();
        }
        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled
        // the nav drawer indicator touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerPane);
//        menu.findItem(R.id.homeAsUp).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener)
    {
        new AlertDialog.Builder(ReminderActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public  void getUserDetail(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("user detail in home--"+response);
                            JSONObject jsonObject=new JSONObject(response);
                            name=jsonObject.getString("first_name")+"  "+jsonObject.getString("last_name");
                            email=jsonObject.getString("email");
                            System.out.println(name+""+email);
                            JSONArray jsonArray = jsonObject.getJSONArray("profile");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                mobile=object.getString("mobile");
                                if (object.getString("picture").equalsIgnoreCase("null")){
                                    // Log.e("picture","false");
                                    profile_image.setImageResource(R.drawable.user);
                                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.user);
                                    encoded_string_image=getStringImage(bitmap);
                                }
                                else {
                                    JSONObject picture=object.getJSONObject("picture");
                                    encoded_string_image=picture.getString("img");
                                    byte[] decodedString = Base64.decode(encoded_string_image, Base64.DEFAULT);
                                   // profile_image.setImageBitmap(getImage(images_bytes));

                                    profile_image.setImageBitmap(getImage(decodedString));
                                    menu_header_image.setImageBitmap(getImage(decodedString));
                                }
                                shared_qty.setText(String.valueOf(object.getInt("shared")));
                                all_qty.setText(String.valueOf(object.getInt("all")));
                                today_qty.setText(String.valueOf(object.getInt("today")));

                                // System.out.println(encoded_string_image);
                            }
                            textname.setText(jsonObject.getString("first_name"));
                            String []dates=jsonObject.getString("last_login").split("T");
                            String dt_arr[]=dates[0].split("-");
                            int mon=Integer.parseInt(dt_arr[1]);
                            String conv_date=dt_arr[2]+" "+month[mon]+", "+dt_arr[0];
                            last_visited.setText("Last Visited : "+conv_date);
                            //last_visited.setText("Last Visited : "+jsonObject.getString("last_login").substring(0,10));

                            // System.out.println(jsonObject.toString());
                        } catch (JSONException e) {
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
                header.put("Authorization","Token "+AppUrl.TOKEN);

                // header.put("Content-Type", "application/x-www-form-urlencoded");

                return header;
            }
        } ;
        MyVolleySingleton.getInstance(getApplicationContext()).getRequestQueue().add(stringRequest);
    }
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        // Log.i("encodedImageString", encodedImage);
        return encodedImage;
    }
    public  void getUserSites(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("site list "+response);
                            JSONObject jsonObject=new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Site site=new Site();
                                JSONObject object = jsonArray.getJSONObject(i);
                                siteList.add(object.getString("name"));
                                site.setSite_id(object.getInt("id"));
                                site.setSite_name(object.getString("name"));
                                site.setImage_url(object.getString("logo"));
                                supported_site_List.add(site);
                            }
                            editor.putStringSet(KEY_SITE,siteList);
                            editor.apply();
                            editor.commit();
                            Log.e("inserted","success with array size"+supported_site_List.size());
                        } catch (JSONException e) {
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
                header.put("Authorization","Token "+AppUrl.TOKEN);
                // header.put("Content-Type", "application/x-www-form-urlencoded");
                return header;
            }
        } ;
        MyVolleySingleton.getInstance(getApplicationContext()).getRequestQueue().add(stringRequest);
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("id");
           // System.out.println("message from serevice"+message);
            if (!message.equalsIgnoreCase(""))
            {
                getUserDetail(AppUrl.GET_USER_PROFILE_URL);

            }

            // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

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
                                ActivityCompat.requestPermissions(ReminderActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
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
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserDetail(AppUrl.GET_USER_PROFILE_URL);
        arrow2.setVisibility(View.VISIBLE);
        arrow3.setVisibility(View.INVISIBLE);
        arrow1.setVisibility(View.INVISIBLE);
        Bundle bundle = new Bundle();
        bundle.putString("called_from", "today_layout");
        status.setText("Today");
        Fragment fragment1 = new TodayFragment();
        FragmentTransaction trans1 = getSupportFragmentManager().beginTransaction();
        fragment1.setArguments(bundle);
        trans1.replace(R.id.frame, fragment1);
        trans1.addToBackStack(null);
        trans1.commit();
    }
}
