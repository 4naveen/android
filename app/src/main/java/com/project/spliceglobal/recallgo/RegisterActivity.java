package com.project.spliceglobal.recallgo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView facebook,google,twitter,linkedin;
    CircleImageView profile_image;
    RadioGroup radioGroup;
    RadioButton radioButton;
    String sur_name,encoded_image,status;
    int sur_name_id;
    Bitmap bitmap;
    EditText name,email,mobile,password,confirm_password;
    TextInputLayout input_name,input_email,input_mobile,input_password,input_confirm_password;
    Button register;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Sign Up");
        }
        register=(Button)findViewById(R.id.save);
        layout=(LinearLayout)findViewById(R.id.layout);

        input_name=(TextInputLayout)findViewById(R.id.input_layout_name);
        input_email=(TextInputLayout)findViewById(R.id.input_layout_email);
        input_mobile=(TextInputLayout)findViewById(R.id.input_layout_mobile);
        input_password=(TextInputLayout)findViewById(R.id.input_layout_password);
        input_confirm_password=(TextInputLayout)findViewById(R.id.input_layout_confirm_pasword);
        radioGroup = (RadioGroup)findViewById(R.id.sur_name);
        name=(EditText)findViewById(R.id.name);
        email=(EditText)findViewById(R.id.email);
        mobile=(EditText)findViewById(R.id.mobile);
        password=(EditText)findViewById(R.id.password);
        confirm_password=(EditText)findViewById(R.id.confirm_password);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        facebook = (ImageView) findViewById(R.id.facebook);
        google = (ImageView) findViewById(R.id.google);
        twitter = (ImageView) findViewById(R.id.twitter);
        linkedin = (ImageView) findViewById(R.id.linkedin);

      //  int selectedId=radioGroup.getCheckedRadioButtonId();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.sur_name1:
                        radioButton = (RadioButton) findViewById(checkedId);
                        sur_name=radioButton.getText().toString();
                        sur_name_id=0;
                        System.out.println("salutation"+sur_name);
                        break;
                    case R.id.sur_name2:
                        radioButton = (RadioButton) findViewById(checkedId);
                        sur_name=radioButton.getText().toString();
                        sur_name_id=1;
                        System.out.println("salutation"+sur_name);
                        break;
                }
            }
        });
     /*   if (radioGroup.isSelected()) {
            sur_name = ((RadioButton)findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
        }*/
       // radioGroup.clearCheck();
        facebook.setOnClickListener(this);
        google.setOnClickListener(this);
        twitter.setOnClickListener(this);
        linkedin.setOnClickListener(this);
        profile_image.setOnClickListener(this);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_name.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_mobile.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_email.setError("");

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
                input_password.setError("password must be alpha numeric!");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        confirm_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input_confirm_password.setError("");

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (name.getText().toString().trim().isEmpty()) {
                    input_name.setError("Please Enter Your Name");
                    return;
                }
                if (email.getText().toString().trim().isEmpty()) {
                    input_email.setError("Please Enter Email");
                    return;
                }
                if (mobile.getText().toString().trim().isEmpty()) {
                    input_mobile.setError("Please Enter Mobile Number");
                    return;
                }
                if (password.getText().toString().trim().isEmpty()) {
                    input_password.setError("Please Enter Password");
                    return;
                }
                if (password.getText().toString().compareTo(confirm_password.getText().toString()) != 0) {
                    input_confirm_password.setError("Password not Matched");
                    return;
                }

               // RegisterUser(AppUrl.REGISTRATION_URL);
                new RegisterUser().execute(AppUrl.REGISTRATION_URL,name.getText().toString(),email.getText().toString(),mobile.getText().toString(),
                        password.getText().toString(),confirm_password.getText().toString());
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
              /*  if (lengthbmp > 512000) {
                    Toast.makeText(getApplicationContext(), "image size should be less than 500kb", Toast.LENGTH_LONG).show();
                    return;
                } else {
                }*/
                Bitmap scaledBitmap = scaleDown(bitmap, 100, true);
                profile_image.setImageBitmap(scaledBitmap);
                encoded_image = getStringImage(scaledBitmap);
               // encoded_image = getStringImage(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private class RegisterUser extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(RegisterActivity.this);
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
            String name=params[1];
            String []names=name.split(" ");
            String first_name=names[0];
            String user_name=names[0];
            String last_name="";
            for (int i = 0; i <names.length-1 ; i++) {
                last_name=last_name+names[i+1];
            }
            URL url = null;
            try {
                jsonObject = new JSONObject();
                jsonObject.put("username",user_name);
                jsonObject.put("email", params[2]);
                jsonObject.put("password1", params[4]);
                jsonObject.put("password2", params[5]);
                jsonObject.put("first_name",first_name);
                if (last_name.isEmpty()) {
                    jsonObject.put("last_name","BLANK");
                }else{
                    jsonObject.put("last_name", last_name);
                }
                jsonObject.put("mobile", params[3]);
                jsonObject.put("salutation",sur_name_id);
                  JSONObject sub_object=new JSONObject();
                sub_object.put("mime","image/jpeg");

                sub_object.put("img",encoded_image);
                jsonObject.put("picture",sub_object);
                //jsonObject.put("picture", encoded_image);
                System.out.println(jsonObject.toString());
                url = new URL(AppUrl.REGISTRATION_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();
                System.out.println("responsecode"+responseCode);
                if (responseCode == HttpsURLConnection.HTTP_CREATED) {
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
                    jsonresponse = response;
                    status="success";
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
                    jsonresponse = response;
                    status="fail";
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
            if (status.equals("success")) {
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    AppUrl.TOKEN=jsonObject.getString("key");
                    startActivity(new Intent(RegisterActivity.this,HomeActivity.class));
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    JSONObject jsonObject=new JSONObject(result);
                    input_name.setError(jsonObject.getString("username"));
                    input_email.setError(jsonObject.getString("email"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
           /*     final Snackbar snackbar = Snackbar.make(layout, "Registration not successful! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();*/
            }
            dialog.dismiss();

        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
       // Log.i("encodedImageString", encodedImage);
        return encodedImage;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.facebook:
            {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.facebook.com/login/"));
                startActivity(intent);
                break;
            }
            case R.id.google:
            {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://accounts.google.com/ServiceLogin"));
                startActivity(intent);
                break;
            }
            case R.id.twitter:
            {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://twitter.com/login?lang=en"));
                startActivity(intent);
                break;
            }
            case R.id.linkedin:
            {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://in.linkedin.com/"));
                startActivity(intent);
                break;
            }
            case R.id.profile_image:
            {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(i.createChooser(i, "select pic"), 101);
                break;
            }
        }
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());

        System.out.println("image ratio"+String.valueOf(ratio));
        int width = Math.round((float) ratio * realImage.getWidth());

        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }
}
