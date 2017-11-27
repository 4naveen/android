package com.project.spliceglobal.recallgo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.spliceglobal.recallgo.adapters.PriceChaserAdapter;
import com.project.spliceglobal.recallgo.model.Item;
import com.project.spliceglobal.recallgo.model.PriceChaser;
import com.project.spliceglobal.recallgo.utils.AppUrl;
import com.project.spliceglobal.recallgo.utils.EndlessRecyclerViewScrollListener;
import com.project.spliceglobal.recallgo.utils.MyVolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class PriceChaserActivity extends AppCompatActivity {
    RecyclerView rv;
    ArrayList<PriceChaser> priceChaserArrayList;
    PriceChaserAdapter priceChaserAdapter;
    String next_url;
    LinearLayoutManager layoutManager;
    ProgressBar progressBar;
    private EndlessRecyclerViewScrollListener scrollListener;
    String month[]={"","January","February","March","April","May","June","July","August","September","October","November","December"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_chaser);
        Intent intent=getIntent();

        /*if (intent.getType().indexOf("image/") != -1) {
            Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            System.out.println("image url"+imageUri.getPath());
            Toast.makeText(getApplicationContext(),imageUri.getPath(),Toast.LENGTH_LONG).show();
        }*/

        if (getIntent().getBooleanExtra("flag",true)){
            if (intent.getType().equals("text/plain")) {
               // System.out.println("data string  received"+intent.getStringExtra(Intent.EXTRA_TEXT));
                String shared_data=intent.getStringExtra(Intent.EXTRA_TEXT);
                int index=shared_data.indexOf("http");
                Log.i("shared_url",shared_data.substring(index));
                Log.i("shared_product_name",shared_data.substring(0,index));
                new ShareUrl().execute(shared_data.substring(index));
                // Toast.makeText(getApplicationContext(),intent.getStringExtra(Intent.EXTRA_TEXT),Toast.LENGTH_LONG).show();
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Price Chaser");
        }
        priceChaserArrayList = new ArrayList<>();
        rv = (RecyclerView) findViewById(R.id.rv);
        getPriceChasers(AppUrl.PRICE_CHASER_URL);
        layoutManager = new LinearLayoutManager(PriceChaserActivity.this,LinearLayoutManager.VERTICAL, false);
            /* Optionally customize the position you want to default scroll to */
        layoutManager.scrollToPosition(0);
            /* Attach layout manager to the RecyclerView */
        rv.setLayoutManager(layoutManager);
        priceChaserAdapter = new PriceChaserAdapter(PriceChaserActivity.this, new ArrayList<PriceChaser>());
        rv.setAdapter(priceChaserAdapter);
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!next_url.equalsIgnoreCase("null")) {
                    Log.v("my_tagggg", "next_url inside onLoadMore is: " + next_url);
                    getPriceChasers(next_url);
                    next_url = null;
                }
            }
        };
        // Adds the scroll listener to RecyclerView
        rv.addOnScrollListener(scrollListener);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
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
    public void getPriceChasers(String url) {
        Log.v("my_taggggg", "url is: " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            System.out.println("response"+response);
                            JSONObject jsonObject = new JSONObject(response);
                            next_url = jsonObject.getString("next");
                            Log.v("my_tag", "next_url is: " + next_url);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            Log.v("my_taggg", "response is: " + response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                PriceChaser priceChaser = new PriceChaser();
                                priceChaser.setId(object.getInt("id"));
                                priceChaser.setProduct_url(object.getString("url"));
                                priceChaser.setProduct_name("Mac Book Pro");
                                String []dates=object.getString("date_created").split("T");
                                String dt_arr[]=dates[0].split("-");
                                int mon=Integer.parseInt(dt_arr[1]);
                                String conv_date=dt_arr[2]+" "+month[mon]+","+dt_arr[0];
                                priceChaser.setProduct_date(conv_date);
                                priceChaser.setTarget_price(object.getString("target_price"));
                                priceChaser.setOriginal_rice(object.getString("original_price"));
                                priceChaserArrayList.add(priceChaser);

                            }
                            updateUI(priceChaserArrayList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("Content-Type", "application/json; charset=utf-8");
                header.put("Authorization", "Token "+AppUrl.TOKEN);
                return header;
            }
        };
        MyVolleySingleton.getInstance(PriceChaserActivity.this).getRequestQueue().add(stringRequest);
    }
        private void updateUI(ArrayList<PriceChaser> data) {
        priceChaserAdapter.setPriceChaserData(data);
    }

    private class ShareUrl extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(PriceChaserActivity.this);
            dialog.setMessage("Please Wait..");
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
                jsonObject.put("url",params[0]);
                jsonObject.put("site",1);
                System.out.println(jsonObject.toString());
                url = new URL(AppUrl.PRICE_CHASER_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
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
                System.out.println("responsecode--"+responseCode);
                if (responseCode == HttpsURLConnection.HTTP_CREATED) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    //Log.d("Output",br.toString());
                    while ((line = br.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    json = new JSONObject(response);
                    //Get Values from JSONobject
                    // System.out.println("success=" + json.get("success"));

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
                    // jsonresponse = json.getString("error");
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
            dialog.dismiss();
            if (result.equals("success")) {

                getPriceChasers(AppUrl.PRICE_CHASER_URL);

          /*      final Snackbar snackbar = Snackbar.make(layout, "Added item Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();*/
            } else {
              /*  final Snackbar snackbar = Snackbar.make(layout, "Item not added! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();*/
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

     //   getPriceChasers(AppUrl.PRICE_CHASER_URL);

    }
}
