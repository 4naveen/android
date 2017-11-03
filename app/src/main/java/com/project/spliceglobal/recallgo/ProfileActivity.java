package com.project.spliceglobal.recallgo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.spliceglobal.recallgo.utils.AppUrl;
import com.project.spliceglobal.recallgo.utils.MyVolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    EditText name, email, mobile;
    ToggleButton toggleButton;
    CircleImageView profile_image;
    Bitmap bitmap;
    String encoded_image;
    byte[] images_bytes;
    LinearLayout layout;
    ImageView change_pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My Profile");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        layout=(LinearLayout)findViewById(R.id.layout);
        toggleButton = (ToggleButton) findViewById(R.id.togglebutton);
        change_pwd = (ImageView) findViewById(R.id.change_button_link);
        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        mobile = (EditText) findViewById(R.id.mobile);
        images_bytes = getIntent().getByteArrayExtra("image_bytes");
        name.setText(getIntent().getStringExtra("name"));
        email.setText(getIntent().getStringExtra("email"));
        mobile.setText(getIntent().getStringExtra("mobile"));
        profile_image.setImageBitmap(getImage(images_bytes));
        change_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this,ChangePasswordActivity.class));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onToggleClicked(View view) {
        if (((ToggleButton) view).isChecked()) {
            // handle toggle on

            name.setClickable(true);
            name.setFocusable(true);
            name.setFocusableInTouchMode(true);
            name.setBackgroundResource(R.drawable.edittext_bottom_back);
            name.requestFocus();
            name.setSelection(name.getText().length());

            email.setClickable(true);
            email.setFocusable(true);
            email.setFocusableInTouchMode(true);
            email.setBackgroundResource(R.drawable.edittext_bottom_back);
            email.requestFocus();
            email.setSelection(email.getText().length());

            mobile.setClickable(true);
            mobile.setFocusable(true);
            mobile.setFocusableInTouchMode(true);
            mobile.setBackgroundResource(R.drawable.edittext_bottom_back);
            mobile.requestFocus();
            mobile.setSelection(mobile.getText().length());

            profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.setType("image/*");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(i.createChooser(i, "select pic"), 101);
                }
            });
        } else {
            // handle toggle off
            name.setClickable(false);
            name.setFocusable(false);
            name.setFocusableInTouchMode(false);
            name.setBackground(getResources().getDrawable(R.color.textColor));
            email.setClickable(false);
            email.setFocusable(false);
            email.setFocusableInTouchMode(false);
            email.setBackground(getResources().getDrawable(R.color.textColor));
            mobile.setClickable(false);
            mobile.setFocusable(false);
            mobile.setFocusableInTouchMode(false);
            mobile.setBackground(getResources().getDrawable(R.color.textColor));
            name.setText(name.getText());
            email.setText(email.getText());
            mobile.setText(mobile.getText());

           // EditUserProfile(AppUrl.GET_USER_PROFILE_URL);
            new EditUserProfile().execute(AppUrl.GET_USER_PROFILE_URL,name.getText().toString(),email.getText().toString(),mobile.getText().toString());
            profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                long lengthbmp = imageInByte.length;
                Log.i("image length", String.valueOf(lengthbmp));
                if (lengthbmp > 512000) {
                    Toast.makeText(getApplicationContext(), "image size should be less than 500kb", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    profile_image.setImageBitmap(bitmap);
                }
                encoded_image = getStringImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
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
    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
      //  Log.i("encodedImageString", encodedImage);
        return encodedImage;
    }
    private void EditUserProfile(String edit_url) {
        final ProgressDialog progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Loading, please wait");
        progressDialog.setTitle("Connecting server");
        progressDialog.show();
        progressDialog.setCancelable(false);
        System.out.println("login_url" + edit_url);
        StringRequest stringRequest = new StringRequest(Request.Method.PATCH, edit_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonobject = null;
                        try {
                            jsonobject = new JSONObject(response);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (String.valueOf(error) != null) {
                    new MaterialDialog.Builder(ProfileActivity.this)
                            .content("Invalid Credentials!Please try again")
                            .positiveText("Ok")
                            .positiveColorRes(R.color.colorPrimary)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                }
                            })
                            .show();
                    progressDialog.dismiss();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                String full_name=name.getText().toString();
                String []names=full_name.split(" ");
                String first_name=names[0];
                String user_name=names[0];
                String last_name="";
                for (int i = 0; i <names.length-1 ; i++) {
                    last_name=last_name+names[i+1];
                }
                params.put("first_name",first_name);
                params.put("last_name",last_name);
                params.put("username",user_name);
                params.put("email", email.getText().toString());
               // params.put("mobile", mobile.getText().toString());
                params.put("img", encoded_image);
                return params;
            }

           @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String>header=new HashMap<>();
                header.put("Content-Type", "application/json");
                header.put("Authorization","Token fe63a7b37e04515a4cba77d2960526a84d1a56da");

               return header;
            }

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyVolleySingleton.getInstance(getApplicationContext()).getRequestQueue().add(stringRequest);

    }
    private class EditUserProfile extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ProfileActivity.this);
            dialog.setMessage("Loading, please wait...");
            dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            String response = "", jsonresponse = "";
            BufferedReader bufferedReader = null;
            JSONObject json = null;
            JSONObject jsonObject = null;
            String name=params[1];
            String []names=name.split(" ");
            String first_name=names[0];
            String user_name=names[0];
            String last_name="";
            for (int i = 0; i <names.length-1 ; i++) {
                last_name=last_name+names[i+1];
            }
           // String accessToken="fe63a7b37e04515a4cba77d2960526a84d1a56da";
            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("username",user_name);
                jsonObject.put("email", params[2]);
                jsonObject.put("first_name",first_name);
                jsonObject.put("last_name", last_name);
                jsonObject.put("mobile", params[3]);
                JSONObject sub_object=new JSONObject();
                sub_object.put("mime","image/jpeg");
                sub_object.put("img",encoded_image);
                jsonObject.put("picture",sub_object);
               // jsonObject.put("picture", encoded_image);
                System.out.println(jsonObject.toString());
                url = new URL(AppUrl.GET_USER_PROFILE_URL);
                System.out.println(url.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("PATCH");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Authorization", "Token "+ AppUrl.TOKEN);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    //Log.d("Output",br.toString());
                    while ((line = br.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    json = new JSONObject(response);
                    //Get Values from JSONobject
                    // System.out.println("success");

                    jsonresponse ="success";
                } else {
                    InputStreamReader inputStreamReader = new InputStreamReader(conn.getErrorStream());
                    bufferedReader = new BufferedReader(inputStreamReader);
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    // Log.i("response", response);
                    // json = new JSONObject(response);
                     jsonresponse ="error";
                    //System.out.println("error=" + json.get("error"));
                    //succes = json.getString("success");
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
            return jsonresponse;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("success")) {
                final Snackbar snackbar = Snackbar.make(layout, "Updated Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 3000);
            } else {
                final Snackbar snackbar = Snackbar.make(layout, "Item not Updated! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
            dialog.dismiss();

        }
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
