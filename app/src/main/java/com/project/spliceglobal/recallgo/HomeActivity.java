package com.project.spliceglobal.recallgo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.spliceglobal.recallgo.utils.AppUrl;
import com.project.spliceglobal.recallgo.utils.MyVolleySingleton;
import com.project.spliceglobal.recallgo.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    TextView hello, reminder_text,textname,last_visited,feedback_text, price_chaser_text;
    LinearLayout remainder_layout, price_chaser_layout, tutorial_layout, feedback_layout;
    ImageView reminder, feedback, price_chaser, tutorial_icon;
    SessionManager sessionManager;
    String email,name,mobile,encoded_string_image;
    CircleImageView profile_image;
    int shared,all,today;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        sessionManager=new SessionManager(getApplicationContext());
        remainder_layout = (LinearLayout) findViewById(R.id.reminder_layout);
        reminder = (ImageView) findViewById(R.id.reminder);
        feedback = (ImageView) findViewById(R.id.feedback);
        tutorial_icon = (ImageView) findViewById(R.id.tutorialIcon);
        textname = (TextView) findViewById(R.id.name);
        last_visited = (TextView) findViewById(R.id.last_visited);
        System.out.println("token:--"+AppUrl.TOKEN);
        price_chaser = (ImageView) findViewById(R.id.price_chaser);
        reminder_text = (TextView) findViewById(R.id.reminder_text);
        price_chaser_layout = (LinearLayout) findViewById(R.id.price_chaser_layout);
        tutorial_layout = (LinearLayout) findViewById(R.id.tutorial_layout);
        feedback_layout = (LinearLayout) findViewById(R.id.feedback_layout);
        getUserDetail(AppUrl.GET_USER_PROFILE_URL);
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            // actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowHomeEnabled(false);
            Toolbar.LayoutParams lp1 = new Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);
            View customNav = LayoutInflater.from(this).inflate(R.layout.custom_actionbar, null); // layout which contains your button.
            actionBar.setCustomView(customNav);
            //toolbar.setLogo(R.drawable.web_hi_res_512);

        }
        hello = (TextView) findViewById(R.id.hello);
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "fonts/roboto-thin.ttf");
        hello.setTypeface(tf1);

        remainder_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(HomeActivity.this, ReminderActivity.class);
                byte[] decodedString = Base64.decode(encoded_string_image, Base64.DEFAULT);
                i.putExtra("image_bytes",decodedString);
                i.putExtra("all",all);
                i.putExtra("today",today);
                i.putExtra("shared",shared);
                startActivity(i);

            }
        });

        reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(HomeActivity.this, ReminderActivity.class);
                byte[] decodedString = Base64.decode(encoded_string_image, Base64.DEFAULT);
                i.putExtra("image_bytes",decodedString);
                i.putExtra("all",all);
                i.putExtra("today",today);
                i.putExtra("shared",shared);
                startActivity(i);

            }
        });

        reminder_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, ReminderActivity.class));
            }
        });
        feedback_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, FeedbackActivity.class));

            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, FeedbackActivity.class));
            }
        });
        price_chaser_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, PriceChaserActivity.class).putExtra("flag", false));

            }
        });

        //tutorial Layout
        tutorial_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, TutorialActivity.class));

            }
        });

        tutorial_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, TutorialActivity.class));

            }
        });

        price_chaser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, PriceChaserActivity.class).putExtra("flag", false));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_actionbar, menu);
        // this.menu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.profile) {
            Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
            if (name!=null&&email!=null&&mobile!=null&&encoded_string_image!=null){
                i.putExtra("name",name);
                i.putExtra("email",email);
                i.putExtra("mobile",mobile);
                byte[] decodedString = Base64.decode(encoded_string_image, Base64.DEFAULT);
                i.putExtra("image_bytes",decodedString);
            }
            startActivity(i);
        }
        if (item.getItemId() == R.id.logout) {
            sessionManager.logoutUser();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        getUserDetail(AppUrl.GET_USER_PROFILE_URL);

    }
    public  void getUserDetail(String url) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                             name=jsonObject.getString("first_name")+"  "+jsonObject.getString("last_name");
                             email=jsonObject.getString("email");
                            System.out.println(name+""+email);
                            JSONArray jsonArray = jsonObject.getJSONArray("profile");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                 JSONObject object = jsonArray.getJSONObject(i);
                                 mobile=object.getString("mobile");
                                 JSONObject picture=object.getJSONObject("picture");
                                 encoded_string_image=picture.getString("img");
                                 shared=object.getInt("shared");
                                 all=object.getInt("all");
                                 today=object.getInt("today");

                               // System.out.println(encoded_string_image);
                            }
                            textname.setText(jsonObject.getString("first_name"));
                            last_visited.setText("Last Visited : "+jsonObject.getString("last_login").substring(0,10));
                            byte[] decodedString = Base64.decode(encoded_string_image, Base64.DEFAULT);
                            profile_image.setImageBitmap(getImage(decodedString));
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
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
