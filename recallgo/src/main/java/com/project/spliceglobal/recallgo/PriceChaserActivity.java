package com.project.spliceglobal.recallgo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.spliceglobal.recallgo.adapters.PriceChaserAdapter;
import com.project.spliceglobal.recallgo.adapters.ViewStoreAdapter;
import com.project.spliceglobal.recallgo.model.PriceChaser;
import com.project.spliceglobal.recallgo.model.Site;
import com.project.spliceglobal.recallgo.utils.AppUrl;
import com.project.spliceglobal.recallgo.utils.EndlessRecyclerViewScrollListener;
import com.project.spliceglobal.recallgo.utils.MyVolleySingleton;
import com.project.spliceglobal.recallgo.utils.ObjectSerializer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class PriceChaserActivity extends AppCompatActivity {
    RecyclerView rv;
    ArrayList<PriceChaser> priceChaserArrayList,priceChaserArrayList1;
    PriceChaserAdapter priceChaserAdapter;
    String next_url;
    LinearLayoutManager layoutManager;
    ProgressBar progressBar;
    private EndlessRecyclerViewScrollListener scrollListener;
    String month[] = {"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    ProgressDialog dialog;
    SharedPreferences sharedPreferences;
    public static final String PREF_NAME = "SitePref";
    public static final String KEY_SITE = "site_name";
    ArrayList<Site> siteArrayList;
    int PRIVATE_MODE = 0;
    Set<String>siteList;
    ViewStoreAdapter listAdapter;
    ArrayList<Site> storeList;
    EditText product_url;
    TextInputLayout input_layout;
    String []site_names;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_chaser);
        Intent intent = getIntent();
        siteArrayList=new ArrayList<>();
        sharedPreferences = getApplicationContext().getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        siteList=sharedPreferences.getStringSet(KEY_SITE,null);
        storeList=new ArrayList<>();
        site_names=siteList.toArray(new String[siteList.size()]);
    /*    Iterator<String> itr=siteList.iterator();
        while(itr.hasNext()){
            System.out.println("site name---"+itr.next());
        }*/
     /*   for (String name:site_names) {
            Log.e("site_name",name);
        }*/
        if (getIntent().getBooleanExtra("flag", true)) {
            if (intent.getType().equals("text/plain")) {
                // System.out.println("data string  received"+intent.getStringExtra(Intent.EXTRA_TEXT));
                // priceChaserArrayList.clear();
                String shared_data = intent.getStringExtra(Intent.EXTRA_TEXT);
                int index = shared_data.indexOf("http");
               Log.i("shared_url", shared_data.substring(index));
                Log.i("shared_product_name", shared_data.substring(0, index));
                for (int i = 0; i <site_names.length ; i++) {
                    if (shared_data.substring(index).contains(site_names[i])){
                        new ShareUrl().execute(shared_data.substring(index));
                       // Log.e("")
                         break;
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"we do not support product of selected store",Toast.LENGTH_LONG).show();
                    }
                }
                // Toast.makeText(getApplicationContext(),intent.getStringExtra(Intent.EXTRA_TEXT),Toast.LENGTH_LONG).show();
            }

        }
        else {
            //Log.e("called from","Reminder acrivity");
            siteArrayList=getIntent().getParcelableArrayListExtra("site_list");
            //Log.e("sitelist size",String.valueOf(siteArrayList.size()));

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Price Chaser");
        }
        priceChaserArrayList = new ArrayList<>();
        priceChaserArrayList1=new ArrayList<>();
        rv = (RecyclerView) findViewById(R.id.rv);
        getPriceChasers(AppUrl.PRICE_CHASER_URL);
        layoutManager = new LinearLayoutManager(PriceChaserActivity.this, LinearLayoutManager.VERTICAL, false);
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
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            /*    getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );*/

                MaterialDialog dialog1 = new MaterialDialog.Builder(PriceChaserActivity.this)
                        .customView(R.layout.price_chaser_product, true)
                        .positiveText("ADD")
                        .autoDismiss(false)
                        .positiveColorRes(R.color.colorPrimary)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (product_url.getText().toString().isEmpty()) {
                                    //input_layout.setError("please enter product url");
                                    Toast.makeText(getApplicationContext(),"please enter product url",Toast.LENGTH_LONG).show();
                                    return;
                                }
                                String url = product_url.getText().toString();
                                dialog.dismiss();

                              //  new AddCategoryTask().execute(token, c_name);
                                for (int i = 0; i <site_names.length ; i++) {
                                    if (url.contains(site_names[i])){
                                        new ShareUrl().execute(url);
                                        // Log.e("")
                                        break;
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(),"we do not support product of selected store",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        })
                        .negativeColorRes(R.color.colorPrimary)
                        .negativeText("CANCEL")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();

                            }
                        })
                        .show();
                View view = dialog1.getCustomView();
                if (view != null) {
                    product_url = (EditText) dialog1.getCustomView().findViewById(R.id.product_url);
                    input_layout = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout);
                    product_url.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            input_layout.setError("");

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                }

            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_store, menu);
        // this.menu=menu;
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }

        }
        if (item.getItemId() == R.id.store) {
            final MaterialDialog dialog1 = new MaterialDialog.Builder(PriceChaserActivity.this)
                    .title("Supported Store")
                    .customView(R.layout.indi_view_repeat_dialog, true)
                    .positiveText("")
                    .positiveColorRes(R.color.colorPrimary)
                    .negativeColorRes(R.color.colorPrimary)
                    .negativeText("")
                    .show();
            View view = dialog1.getCustomView();
            if (view != null) {
                ListView listView = (ListView) dialog1.getCustomView().findViewById(R.id.lv);
                listAdapter = new ViewStoreAdapter(PriceChaserActivity.this, siteArrayList);
                listView.setAdapter(listAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //Toast.makeText(getApplicationContext(), "you selected" + parent.getItemAtPosition(position), Toast.LENGTH_LONG).show();
                        // repeat_text.setText(String.valueOf(parent.getItemAtPosition(position)));
                        dialog1.dismiss();
                    }
                });
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void getPriceChasers(String url) {
       // Log.v("my_taggggg", "url is: " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            System.out.println("response" + response);
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
                                priceChaser.setProduct_name(object.getString("name"));
                                String[] dates = object.getString("date_created").split("T");
                                String dt_arr[] = dates[0].split("-");
                                int mon = Integer.parseInt(dt_arr[1]);
                                String conv_date = dt_arr[2] + " " + month[mon] + "," + dt_arr[0];
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
                header.put("Authorization", "Token " + AppUrl.TOKEN);
                return header;
            }
        };
        MyVolleySingleton.getInstance(PriceChaserActivity.this).getRequestQueue().add(stringRequest);
    }

    private void updateUI(ArrayList<PriceChaser> data) {
        priceChaserAdapter.setPriceChaserData(data);
    }

    private class ShareUrl extends AsyncTask<String, Void, String> {

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
                jsonObject.put("url", params[0]);
                jsonObject.put("site", 1);
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
                conn.setRequestProperty("Authorization", "Token " + AppUrl.TOKEN);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();
                System.out.println("responsecode--" + responseCode);
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
                updateUI(priceChaserArrayList);

                 refreshPriceChasersList(AppUrl.PRICE_CHASER_URL);

          /*      final Snackbar snackbar = Snackbar.make(layout, "Added item Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();*/
                Toast.makeText(getApplicationContext(), "shared succesfully", Toast.LENGTH_LONG).show();
            } else {
              /*  final Snackbar snackbar = Snackbar.make(layout, "Item not added! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();*/
                Toast.makeText(getApplicationContext(), "Plaese Try Again!", Toast.LENGTH_LONG).show();

            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        //   getPriceChasers(AppUrl.PRICE_CHASER_URL);

    }
    public void refreshPriceChasersList(String url) {
        Log.v("my_taggggg", "url is: " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            System.out.println("response" + response);
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
                                priceChaser.setProduct_name(object.getString("name"));
                                String[] dates = object.getString("date_created").split("T");
                                String dt_arr[] = dates[0].split("-");
                                int mon = Integer.parseInt(dt_arr[1]);
                                String conv_date = dt_arr[2] + " " + month[mon] + "," + dt_arr[0];
                                priceChaser.setProduct_date(conv_date);
                                priceChaser.setTarget_price(object.getString("target_price"));
                                priceChaser.setOriginal_rice(object.getString("original_price"));
                                priceChaserArrayList1.add(priceChaser);

                            }
                            updateUI(priceChaserArrayList1);

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
                header.put("Authorization", "Token " + AppUrl.TOKEN);
                return header;
            }
        };
        MyVolleySingleton.getInstance(PriceChaserActivity.this).getRequestQueue().add(stringRequest);
    }




}
