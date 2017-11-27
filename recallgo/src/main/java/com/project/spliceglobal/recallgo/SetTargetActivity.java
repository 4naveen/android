package com.project.spliceglobal.recallgo;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.spliceglobal.recallgo.model.AllCategory;
import com.project.spliceglobal.recallgo.utils.AppUrl;
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

public class SetTargetActivity extends AppCompatActivity {
    TextView store_list,product_url,original_price,current_price,target_price,number,origanal_date,current_date,target_date;
    ImageButton increase,decrease;
    ArrayList<String> storeList;
    String priceChaserId;
    String month[]={"","Jan","Feb","March","April","May","June","July","August","Sept","Oct","Nov","Dec"};
    ProgressBar progressBar;
    Button track;
    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_target);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Price Chaser");
        }
        layout=(LinearLayout)findViewById(R.id.layout);
        store_list=(TextView)findViewById(R.id.store_list);
        product_url=(TextView)findViewById(R.id.product_url);
        original_price=(TextView)findViewById(R.id.original_price);
        current_price=(TextView)findViewById(R.id.current_price);
        target_price=(TextView)findViewById(R.id.target_price);
        origanal_date=(TextView)findViewById(R.id.original_date);
        current_date=(TextView)findViewById(R.id.current_date);
        target_date=(TextView)findViewById(R.id.target_date);
        track=(Button)findViewById(R.id.track);
        storeList=new ArrayList<>();
        storeList.add("Walmart");
        storeList.add("Walgreens");
        storeList.add("marsh");
        storeList.add("Relays");
        storeList.add("Weis");
        priceChaserId=getIntent().getStringExtra("id");
        store_list.setPaintFlags(store_list.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        increase=(ImageButton)findViewById(R.id.increase);
        decrease=(ImageButton)findViewById(R.id.decrease);
        number=(TextView)findViewById(R.id.number);

        //new GetPriceChaserDetails().execute(priceChaserId);
        GetPriceChaserDetails(priceChaserId);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        store_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog dialog1 = new MaterialDialog.Builder(SetTargetActivity.this)
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

                    ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<>(SetTargetActivity.this, android.R.layout.simple_list_item_1, storeList);
                    locationArrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    listView.setAdapter(locationArrayAdapter);
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
        });

        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateTargetPrice().execute(number.getText().toString());
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


    public  void GetPriceChaserDetails(String priceChaserId) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET,AppUrl.PRICE_CHASER_URL+priceChaserId+"/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            System.out.println("response"+response);
                            original_price.setText(jsonObject.getString("original_price"));
                            current_price.setText(jsonObject.getString("current_price"));
                            target_price.setText(jsonObject.getString("target_price"));
                            String []dates1=jsonObject.getString("date_created").split("T");
                            String []dates2=jsonObject.getString("date_updated").split("T");
                            String dt_arr1[]=dates1[0].split("-");
                            String dt_arr2[]=dates2[0].split("-");
                            int mon1=Integer.parseInt(dt_arr1[1]);
                            int mon2=Integer.parseInt(dt_arr2[1]);
                            String conv_date1=dt_arr1[2]+" "+month[mon1]+","+dt_arr1[0];
                            String conv_date2=dt_arr2[2]+" "+month[mon2]+","+dt_arr2[0];
                            origanal_date.setText(conv_date1);
                            DateFormat dateFormat=new SimpleDateFormat("d MMM,yyyy", Locale.ENGLISH);
                            current_date.setText(dateFormat.format(new Date()));
                            target_date.setText(conv_date2);
                            number.setText(String.valueOf(Math.round(Double.parseDouble(jsonObject.getString("current_price")))));
                            product_url.setText(jsonObject.getString("url"));
                            progressBar.setVisibility(View.GONE);
                            increase.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int count=Integer.parseInt(number.getText().toString())+1;
                                    System.out.println("number"+count);

                                    number.setText(String.valueOf(count));
                                }
                            });
                            decrease.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int count=Integer.parseInt(number.getText().toString())-1;
                                    System.out.println("number"+count);
                                    number.setText(String.valueOf(count));                                }
                            });
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
        MyVolleySingleton.getInstance(SetTargetActivity.this).getRequestQueue().add(stringRequest);
    }

    private class UpdateTargetPrice extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(SetTargetActivity.this);
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
                jsonObject.put("target_price",params[0]);
                jsonObject.put("site",1);
                System.out.println(jsonObject.toString());
                url = new URL(AppUrl.PRICE_CHASER_URL+priceChaserId+"/");
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
                System.out.println("responsecode--"+responseCode);
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
                final Snackbar snackbar = Snackbar.make(layout, "Added item Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            } else {
                final Snackbar snackbar = Snackbar.make(layout, "Item not added! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
    }
}
