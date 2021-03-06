package com.project.spliceglobal.recallgo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.spliceglobal.recallgo.adapters.CompleteItemAdapter;
import com.project.spliceglobal.recallgo.adapters.TodayAdapter;
import com.project.spliceglobal.recallgo.model.Item;
import com.project.spliceglobal.recallgo.utils.AppUrl;
import com.project.spliceglobal.recallgo.utils.EndlessRecyclerViewScrollListener;
import com.project.spliceglobal.recallgo.utils.MyVolleySingleton;

import org.apache.commons.lang3.StringUtils;
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
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class CategoryItemListActivity extends AppCompatActivity {
    int category_id;
    String category_name, next_url, called_from_adapter;
    int count;
    ArrayList<Item> itemArrayList,completeItemArrayList;
    RecyclerView rv,rv1;
    TodayAdapter todayAdapter;
    CompleteItemAdapter completeItemAdapter;
    TextView blank_message;
    LinearLayoutManager layoutManager;
   // CustomLinearLayoutManager customLayoutManager;
    String month[]={"","January","February","March","April","May","June","July","August","September","October","November","December"};
    EditText name;
    TextInputLayout input_name;
    EditText add;
    ImageView info_add;
    Menu menu1;
    LinearLayout layout;
    MaterialSearchView searchView;
    ProgressDialog dialog;
    ToggleButton toggleButton;
    ProgressBar progressBar;
    private EndlessRecyclerViewScrollListener scrollListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_item_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        blank_message = (TextView) findViewById(R.id.blank_data);
        layout = (LinearLayout) findViewById(R.id.layout);
        toggleButton = (ToggleButton)findViewById(R.id.togglebutton);
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        add = (EditText) findViewById(R.id.add);
        info_add = (ImageView) findViewById(R.id.info_add);
        itemArrayList = new ArrayList<>();
        completeItemArrayList =new ArrayList<>();
        category_id = getIntent().getIntExtra("category_id", 0);
        category_name = getIntent().getStringExtra("name");
        called_from_adapter = getIntent().getStringExtra("called_from_adapter");
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(category_name);
        }
        rv = (RecyclerView) findViewById(R.id.rv);
        rv1 = (RecyclerView)findViewById(R.id.rv1);
        getItems(AppUrl.CATEGOTY_ITEM_LIST_URL+category_id);
        layoutManager = new LinearLayoutManager(CategoryItemListActivity.this,LinearLayoutManager.VERTICAL, false);
        layoutManager.scrollToPosition(0);

       /* customLayoutManager = new CustomLinearLayoutManager(CategoryItemListActivity.this,LinearLayoutManager.VERTICAL,false);
        customLayoutManager.scrollToPosition(0);*/

       // rv.setLayoutManager(customLayoutManager);
        rv.setLayoutManager(layoutManager);
        rv.setNestedScrollingEnabled(false);
        todayAdapter = new TodayAdapter(CategoryItemListActivity.this,itemArrayList, "update_category", category_id, category_name, called_from_adapter);
        rv.setAdapter(todayAdapter);
        rv.setItemAnimator(new DefaultItemAnimator());
        add.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                menu1.findItem(R.id.add).setVisible(true);
                if (menu1!=null){
                    info_add.setVisibility(View.VISIBLE);

                }
                if (count == 0) {
                    if (menu1!=null){
                        menu1.findItem(R.id.add).setVisible(false);

                    }
                    info_add.setVisibility(View.GONE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (menu1!=null){
                    menu1.findItem(R.id.add).setVisible(false);
                }
            }
            @Override
            public void afterTextChanged(final Editable s) {
                System.out.println(s.toString());
                info_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CategoryItemListActivity.this, AddReminderItemActivity.class);
                        intent.putExtra("name", s.toString());
                        intent.putExtra("called_from", "add_category");
                        intent.putExtra("category_id", category_id);
                        intent.putExtra("category_name", category_name);
                        intent.putExtra("called_from_adapter", called_from_adapter);
                        startActivityForResult(intent, 0);
                    }
                });
            }
        });
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((ToggleButton) view).isChecked()) {
                    // handle toggle on
                    rv1.setVisibility(View.VISIBLE);
                   // customLayoutManager =  new CustomLinearLayoutManager(CategoryItemListActivity.this,LinearLayoutManager.VERTICAL,false);
                    layoutManager = new LinearLayoutManager(CategoryItemListActivity.this,LinearLayoutManager.VERTICAL, false);
                    rv1.setLayoutManager(layoutManager);
                    completeItemAdapter = new CompleteItemAdapter(CategoryItemListActivity.this,completeItemArrayList,category_id);
                    rv1.setAdapter(completeItemAdapter);
                    rv1.setItemAnimator(new DefaultItemAnimator());
                } else {
                    // handle toggle off
                    rv1.setVisibility(View.INVISIBLE);
                }
            }
        });
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!next_url.equalsIgnoreCase("null")) {
                    Log.v("my_tagggg", "next_url inside onLoadMore is: " + next_url);
                    getItems(next_url);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                itemArrayList.clear();
                info_add.setVisibility(View.INVISIBLE);
                add.setText("");
                add.setHint("What you want me to Remind you ?");
                add.setCursorVisible(false);
                getItems(AppUrl.CATEGOTY_ITEM_LIST_URL+category_id);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        menu1 = menu;
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menu.findItem(R.id.action_search).setVisible(true);
        searchView.setMenuItem(menuItem);
        // searchView.showSearch();
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Item> subitemArrayList = new ArrayList<>();
                for (int i = 0; i < itemArrayList.size(); i++) {
                 /*   if (itemArrayList.get(i).getItem_name().contains(newText)) {
                        subitemArrayList.add(itemArrayList.get(i));
                    }*/
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                    if (StringUtils.containsIgnoreCase(itemArrayList.get(i).getItem_name(),newText)) {
                        subitemArrayList.add(itemArrayList.get(i));
                    }
                }
                rv.setAdapter(new TodayAdapter(CategoryItemListActivity.this,subitemArrayList,"update_category", category_id, category_name, called_from_adapter));
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    public void getItems(String url) {
        //System.out.println("request"+url+category_id);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try {
                            System.out.println("response "+response);
                            JSONObject jsonObject = new JSONObject(response);
                            count = jsonObject.getInt("count");
                            next_url = jsonObject.getString("next");
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Item item = new Item();
                                // String[] dates=object.getString("date").split("T");
                                item.setItem_name(object.getString("name"));
                                if (!object.getString("date").equalsIgnoreCase("null")){
                                    String []dates=object.getString("date").split("T");
                                    String dt_arr[]=dates[0].split("-");
                                    int mon=Integer.parseInt(dt_arr[1]);
                                    String conv_date=dt_arr[2]+" "+month[mon]+", "+dt_arr[0];
                                    item.setDate_time(conv_date);
                                    item.setReminder_date_for_update(object.getString("date"));
                                }
                               else {
                                    item.setDate_time("null");
                                }
                                if (object.getString("qty").equalsIgnoreCase("null")){
                                    item.setQty(" ");
                                }
                                else {
                                    item.setQty(object.getString("qty"));
                                }
                                item.setId(object.getInt("id"));
                                item.setList_name(object.getString("list_name"));
                                item.setList_id(object.getInt("list"));
                                int status=object.getInt("status");
                                item.setPriority(object.getString("priority"));
                                if (object.getString("brand").equalsIgnoreCase("null")){
                                    item.setBrand_name("null");
                                    item.setBrand_id("null");

                                }
                                else {
                                    item.setBrand_name(object.getString("brand_name"));
                                    item.setBrand_id(String.valueOf(object.getInt("brand")));

                                }
                                if (object.getString("lat").equalsIgnoreCase("null")){
                                    item.setLati("null");
                                }
                                else {
                                    item.setLati(object.getString("lat"));

                                }
                                if (object.getString("long").equalsIgnoreCase("null")){
                                    item.setLongi("null");
                                }
                                else {
                                    item.setLongi(object.getString("long"));

                                }
                                if (object.getString("description").equalsIgnoreCase("null")){
                                    item.setDescription("");

                                }
                                else {
                                    item.setDescription(object.getString("description"));

                                }
                                if (object.getString("store").equalsIgnoreCase("null")) {
                                    item.setStore_name("");
                                    item.setStore_id("null");

                                } else {
                                    item.setStore_name(object.getString("store_name"));
                                    item.setStore_id(String.valueOf(object.getInt("store")));

                                }
                                item.setRepeat_type(String.valueOf(object.getInt("type")));
                               // itemArrayList.add(item);
                                if (status==1){
                                    itemArrayList.add(item);
                                    System.out.println("uncomplete items"+itemArrayList.size());
                                }
                                else {
                                    completeItemArrayList.add(item);
                                    System.out.println("complete items"+completeItemArrayList.size());
                                }
                            }
                            updateUI(itemArrayList);

                            if (itemArrayList.isEmpty()) {
                               // blank_message.setVisibility(View.VISIBLE);

                            }

                         /*   if (dialog.isShowing()) {
                                dialog.dismiss();
                            }*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log.i("response--", String.valueOf(error));
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

                // header.put("Authorization","Token fe63a7b37e04515a4cba77d2960526a84d1a56da");
                header.put("Authorization", "Token " + AppUrl.TOKEN);

                // header.put("Content-Type", "application/x-www-form-urlencoded");

                return header;
            }
        };
        MyVolleySingleton.getInstance(this).getRequestQueue().add(stringRequest);
    }

    private void updateUI(ArrayList<Item> data) {
        todayAdapter.setItemData(data);
        System.out.println("updating data");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.add: {
                item.setVisible(false);
                new AddItem().execute(add.getText().toString(), String.valueOf(category_id), "1");
                break;
            }


        }
        return super.onOptionsItemSelected(item);
    }

    private class AddItem extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        HttpURLConnection conn;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(CategoryItemListActivity.this);
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
                jsonObject.put("name", params[0]);
                jsonObject.put("list", params[1]);
                jsonObject.put("type", params[2]);

                System.out.println(jsonObject.toString());
                url = new URL(AppUrl.ITEM_LIST_URL);
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
                itemArrayList.clear();
                getItems(AppUrl.CATEGOTY_ITEM_LIST_URL+category_id);
                add.setText("");
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

    @Override
    protected void onResume() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
        super.onResume();
    }
}
