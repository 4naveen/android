package com.project.spliceglobal.recallgo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.spliceglobal.recallgo.adapters.AllCategoryAdapter;
import com.project.spliceglobal.recallgo.fragments.AllFragment;
import com.project.spliceglobal.recallgo.fragments.ShaedFragment;
import com.project.spliceglobal.recallgo.fragments.TodayFragment;
import com.project.spliceglobal.recallgo.model.AllCategory;
import com.project.spliceglobal.recallgo.services.LocationStoreReminderService;
import com.project.spliceglobal.recallgo.utils.AppConstants;
import com.project.spliceglobal.recallgo.utils.AppUrl;
import com.project.spliceglobal.recallgo.utils.MyVolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReminderActivity extends AppCompatActivity {
    LinearLayout shared_layout, today_layout, all_layout;
    BottomNavigationView bottomNavigationView;
    ImageView arrow1, arrow2, arrow3;
    TextView status,shared_qty,today_qty,all_qty;
    CircleImageView profile_image;
    byte[] images_bytes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        profile_image = (CircleImageView) findViewById(R.id.contact_image);
        shared_layout = (LinearLayout) findViewById(R.id.shared_layout);
        today_layout = (LinearLayout) findViewById(R.id.today_layout);
        all_layout = (LinearLayout) findViewById(R.id.all_layout);
        arrow1 = (ImageView) findViewById(R.id.arrow1);
        arrow2 = (ImageView) findViewById(R.id.arrow2);
        arrow3 = (ImageView) findViewById(R.id.arrow3);
        status = (TextView) findViewById(R.id.status);
        getCategoryId(AppUrl.ALL_CATEGORY_URL);
        images_bytes = getIntent().getByteArrayExtra("image_bytes");

        profile_image.setImageBitmap(getImage(images_bytes));
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
                Fragment fragment1 = new ShaedFragment();
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
                status.setText("today_layout");
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

        shared_qty.setText(String.valueOf(getIntent().getIntExtra("shared",0)));
        today_qty.setText(String.valueOf(getIntent().getIntExtra("today",0)));
        all_qty.setText(String.valueOf(getIntent().getIntExtra("all",0)));

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.search: {

                                arrow2.setVisibility(View.VISIBLE);
                                arrow3.setVisibility(View.INVISIBLE);
                                arrow1.setVisibility(View.INVISIBLE);
                                Bundle bundle = new Bundle();
                                bundle.putString("called_from", "search");
                                status.setText("Item");
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
                                status.setText("Item");
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

        startService(new Intent(ReminderActivity.this, LocationStoreReminderService.class));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
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
                                     System.out.println("uncategorisedId"+AppConstants.UncategorisedId);
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
}
