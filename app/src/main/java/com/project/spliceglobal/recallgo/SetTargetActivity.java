package com.project.spliceglobal.recallgo;

import android.app.ProgressDialog;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.project.spliceglobal.recallgo.utils.AppUrl;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class SetTargetActivity extends AppCompatActivity {
    TextView store_list,product_url,original_price,current_price,target_price;
    EditText number;
    ImageButton increase,decrese;
    ArrayList<String> storeList;
    String priceChaserId;
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
        store_list=(TextView)findViewById(R.id.store_list);
        product_url=(TextView)findViewById(R.id.product_url);
        original_price=(TextView)findViewById(R.id.original_price);
        current_price=(TextView)findViewById(R.id.current_price);
        target_price=(TextView)findViewById(R.id.target_price);

        storeList=new ArrayList<>();
        storeList.add("Walmart");
        storeList.add("Walgreens");
        storeList.add("marsh");
        storeList.add("Relays");
        storeList.add("Weis");
        priceChaserId=getIntent().getStringExtra("id");
        store_list.setPaintFlags(store_list.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        increase=(ImageButton)findViewById(R.id.increase);
        decrese=(ImageButton)findViewById(R.id.decrese);
        number=(EditText) findViewById(R.id.number);
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  number.setText(Integer.parseInt(number.getText().toString())+1);
            }
        });
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

    private class GetPriceChaserDetails extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(SetTargetActivity.this);
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
                jsonObject.put("id",params[0]);
                System.out.println(jsonObject.toString());
                url = new URL(AppUrl.PRICE_CHASER_URL+params[0]+"/");
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
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
                if (responseCode == HttpsURLConnection.HTTP_NO_CONTENT) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    //Log.d("Output",br.toString());
                    while ((line = br.readLine()) != null) {
                        response += line;
                        Log.d("output lines", line);
                    }
                    // json = new JSONObject(response);
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
                Log.i("Res--", result);
            } else {
            }
        }
    }

}
