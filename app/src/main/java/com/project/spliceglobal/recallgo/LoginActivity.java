package com.project.spliceglobal.recallgo;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity {
    CircleImageView facebook,google,twitter,linkedin;
    Button signin;
    SessionManager sessionManager;
    EditText email_phone,password;
    TextInputLayout input_email_phone, input_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("SignIn");
        }
        sessionManager=new SessionManager(getApplicationContext());
        facebook = (CircleImageView) findViewById(R.id.facebook);
        google = (CircleImageView) findViewById(R.id.google);
        twitter = (CircleImageView) findViewById(R.id.twitter);
        linkedin = (CircleImageView) findViewById(R.id.linkedin);
        signin = (Button) findViewById(R.id.login);
        email_phone=(EditText)findViewById(R.id.email);
        password=(EditText)findViewById(R.id.password);
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
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email_phone.getText().toString().isEmpty()  || password.getText().toString().isEmpty()) {
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
                if(email_phone.getText().toString().equals("test@gmail.com") && password.getText().toString().equals("password")){

                    // Creating user login session
                    // For testing i am stroing name, email as follow
                    // Use user real data
                    sessionManager.createLoginSession(email_phone.getText().toString(), password.getText().toString());

                    startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                    finish();

                }else{
                    // username / password doesn't match

                    Toast.makeText(getApplicationContext(),"Login failed...,Username/Password is incorrect",Toast.LENGTH_LONG).show();
                }
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
}
