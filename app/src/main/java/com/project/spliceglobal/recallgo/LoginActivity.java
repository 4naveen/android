package com.project.spliceglobal.recallgo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.spliceglobal.recallgo.utils.AppUrl;
import com.project.spliceglobal.recallgo.utils.MyVolleySingleton;
import com.project.spliceglobal.recallgo.utils.NetworkStatus;
import com.project.spliceglobal.recallgo.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity {
    ImageView facebook, google, twitter, linkedin;
    Button signin;
    SessionManager sessionManager;
    EditText email_phone, password;
    TextInputLayout input_email_phone,input_pwd;
    TextView passwodLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Sign In");
        }
        sessionManager = new SessionManager(getApplicationContext());
        passwodLink = (TextView) findViewById(R.id.forgetPasswordLink);
        facebook = (ImageView) findViewById(R.id.facebook);
        google = (ImageView) findViewById(R.id.google);
        twitter = (ImageView) findViewById(R.id.twitter);
        linkedin = (ImageView) findViewById(R.id.linkedin);
        signin = (Button) findViewById(R.id.login);
        email_phone = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        input_email_phone = (TextInputLayout) findViewById(R.id.input_layout_email);
        input_pwd = (TextInputLayout) findViewById(R.id.input_layou_password);
        email_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_email_phone.setError("");
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_pwd.setError("");
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        if (!NetworkStatus.isConnected(LoginActivity.this)) {
            Toast.makeText(getApplicationContext(), "Please check your internet connectivity!", Toast.LENGTH_LONG).show();
        }
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email_phone.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    email_phone.requestFocus();
                    if (email_phone.getText().toString().isEmpty()) {
                        email_phone.requestFocus();
                        input_email_phone.setError("Please enter your email or mobile");
                        return;
                    }
                    if (password.getText().toString().isEmpty()) {
                        password.requestFocus();
                        input_pwd.setError("Please enter your password");
                        return;
                    }
                }
                if (NetworkStatus.isConnected(LoginActivity.this)) {
                   loginUser(AppUrl.LOGIN_URL);
                }
               /* if (email_phone.getText().toString().equals("test@gmail.com") && password.getText().toString().equals("password")) {

                    sessionManager.createLoginSession(email_phone.getText().toString(), password.getText().toString());
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();

                } else {
                    // username / password doesn't match
                    Toast.makeText(getApplicationContext(), "Login failed...,Username/Password is incorrect", Toast.LENGTH_LONG).show();
                }*/
            }
        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.facebook.com/login/"));
                startActivity(intent);
            }
        });
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://accounts.google.com/ServiceLogin"));
                startActivity(intent);
            }
        });
        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://twitter.com/login?lang=en"));
                startActivity(intent);
            }
        });
        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://in.linkedin.com/"));
                startActivity(intent);
            }
        });

        passwodLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email_phone.getText().toString().isEmpty()) {
                    email_phone.requestFocus();
                    input_email_phone.setError("Please enter your email or mobile");
                    return;
                }
                 new ForgetPassword().execute(email_phone.getText().toString().trim());

            }
        });
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

    private void loginUser(String login_url) {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Please Wait..");
        //progressDialog.setTitle("Connecting server");
        progressDialog.show();
        progressDialog.setCancelable(false);
        //System.out.println("login_url" + login_url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, login_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonobject = null;
                        try {
                            jsonobject = new JSONObject(response);
                            String key=jsonobject.getString("key");
                            System.out.println("key "+key);
                            if (!key.isEmpty()){
                               // AppUrl.TOKEN="fe63a7b37e04515a4cba77d2960526a84d1a56da";
                                AppUrl.TOKEN=key;
                                sessionManager.createLoginSession(email_phone.getText().toString(), password.getText().toString(),key);
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        progressDialog.dismiss();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (String.valueOf(error) != null) {
                    new MaterialDialog.Builder(LoginActivity.this)
                            .content("Email id or mobile and password are wrong. Please try again!")
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
                params.put("email", email_phone.getText().toString());
                params.put("password", password.getText().toString());
                return params;
            }

       /*    @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String>header=new HashMap<>();
                header.put("Content-Type", "application/json");

               return header;
            }*/

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        MyVolleySingleton.getInstance(getApplicationContext()).getRequestQueue().add(stringRequest);

    }
    private class ForgetPassword extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage("Please Wait..");
            //dialog.setTitle("Connecting server");
            dialog.show();
            dialog.setCancelable(false);
        }
        @Override
        protected String doInBackground(String... params) {
            String response = "", jsonresponse = "";
            BufferedReader bufferedReader = null;
            JSONObject json = null;
            JSONObject jsonObject = null;
            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("email", params[0]);
                //jsonObject.put("picture", encoded_image);
                System.out.println(jsonObject.toString());
                url = new URL(AppUrl.FORGET_PASSWORD_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
               // conn.setRequestProperty("Authorization", "Token "+AppUrl.TOKEN);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();
                System.out.println("responsecode"+responseCode);
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    //Log.d("Output",br.toString());
                    while ((line = br.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    //json = new JSONObject(response);
                    //Get Values from JSONobject
                    // System.out.println("success=" + json.get("success"));
                    // jsonresponse = json.getString("success");
                    jsonresponse = "success";
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
                    jsonresponse = "failure";

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
                MaterialDialog dialog1 = new MaterialDialog.Builder(LoginActivity.this)
                        .title("Password Reset")
                        .titleGravity(GravityEnum.CENTER)
                        .customView(R.layout.forget_pwd_info, true)
                        .positiveText("ok")
                        .autoDismiss(false)
                        .positiveColorRes(R.color.colorPrimary)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                                dialog.dismiss();
                            }
                        })
                        .show();


            } else {


            }
            dialog.dismiss();

        }
    }

}
