package com.project.spliceglobal.recallgo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
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

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity {
    ImageView facebook, google, twitter, linkedin;
    Button signin;
    SessionManager sessionManager;
    EditText email_phone, password;
    TextInputLayout input_email_phone,input_pwd;
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
            Toast.makeText(getApplicationContext(), "Please check your Internet Connectivity!", Toast.LENGTH_LONG).show();
        }
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email_phone.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                    email_phone.requestFocus();
                    if (email_phone.getText().toString().isEmpty()) {
                        email_phone.requestFocus();
                        input_email_phone.setError("please enter your email");
                        return;
                    }
                    if (password.getText().toString().isEmpty()) {
                        password.requestFocus();
                        input_pwd.setError("please enter your password");
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
        progressDialog.setMessage("Loading, please wait");
        progressDialog.setTitle("Connecting server");
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

}
