package com.project.spliceglobal.recallgo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.spliceglobal.recallgo.adapters.AllCategoryAdapter;
import com.project.spliceglobal.recallgo.adapters.CategoryListAdapter;
import com.project.spliceglobal.recallgo.model.AllCategory;
import com.project.spliceglobal.recallgo.model.Category;
import com.project.spliceglobal.recallgo.utils.AppConstants;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class CategoryListActivity extends AppCompatActivity {
    ListView listView;
    CategoryListAdapter listAdapter;
    ArrayList<AllCategory> allCategoryArrayList;
    String category_text;
    int category_id;
    String next_url;
    int count,no_of_item_loaded;
    ProgressDialog dialog;
    EditText add;
    ImageView info_add;
    String called_from,id,list_id,brand_id,store_id,type;
    LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        listView = (ListView) findViewById(R.id.category);
        allCategoryArrayList = new ArrayList<>();
        add=(EditText)findViewById(R.id.add);
        info_add=(ImageView)findViewById(R.id.info_add);
        called_from=getIntent().getStringExtra("called_from");
        if (called_from.equalsIgnoreCase("move"))
        {
            id= String.valueOf(getIntent().getIntExtra("id",0));
            System.out.println();
            brand_id=getIntent().getStringExtra("brand");
            store_id=getIntent().getStringExtra("store");
            type=getIntent().getStringExtra("type");
        }
        layout=(LinearLayout)findViewById(R.id.layout);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Select Category");
        }
        getCategory(AppUrl.ALL_CATEGORY_URL);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AllCategory category=allCategoryArrayList.get(position);
              //category_text=""+parent.getItemAtPosition(position);
                category_text=category.getCategory_name();
                category_id=category.getId();

            }
        });


        add.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(final Editable s) {
                info_add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(CategoryListActivity.this,AddCategoryActivity.class);
                        intent.putExtra("name",s.toString());
                        startActivity(intent);
                    }
                });
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.ok: {
                if (called_from.equalsIgnoreCase("move"))
                {
                    new MoveItemTask().execute(id,String.valueOf(category_id),store_id,brand_id,type);
                }
                else {
                    Intent myIntent = new Intent();
                    myIntent.putExtra("category",category_text);
                    myIntent.putExtra("id",category_id);
                    AppConstants.categoryId=String.valueOf(category_id);
                    System.out.println("id--"+category_id);
                    setResult(Activity.RESULT_OK, myIntent);
                    finish();
                }

                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_ok, menu);
        return super.onCreateOptionsMenu(menu);

    }
    public  void getCategory(String url) {
        dialog = new ProgressDialog(CategoryListActivity.this);
        dialog.setMessage("Please Wait..");
        //dialog.setTitle("Connecting server");
        dialog.show();
        dialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            count=jsonObject.getInt("count");
                            next_url=jsonObject.getString("next");
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            no_of_item_loaded=jsonArray.length();
                            System.out.println("no of item loaded"+no_of_item_loaded);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                AllCategory category = new AllCategory();
                                category.setCategory_name(object.getString("name"));
                                category.setCompleted(object.getString("completed"));
                                category.setUncompletd(object.getString("notcompleted"));
                                category.setId(object.getInt("id"));
                                allCategoryArrayList.add(category);
                            }
                            for (int i = count-no_of_item_loaded; i >0; i-=no_of_item_loaded) {
                                System.out.println("i"+i);
                                getMoreCategory(next_url);
                            }
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
        MyVolleySingleton.getInstance(CategoryListActivity.this).getRequestQueue().add(stringRequest);
    }
    public  void getMoreCategory(String url)
    {
   /*     if (next_url.equalsIgnoreCase("null")){
            System.out.println("true in next");
            Toast.makeText(getContext(),"No More Data Available",Toast.LENGTH_LONG).show();
            return;
        }*/
        System.out.println("getmore called");
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            //count=jsonObject.getInt("count");
                            next_url=jsonObject.getString("next");
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            no_of_item_loaded+=jsonArray.length();
                            System.out.println("json array length"+jsonArray.length());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                AllCategory category = new AllCategory();
                                category.setCategory_name(object.getString("name"));
                                category.setCompleted(object.getString("completed"));
                                category.setUncompletd(object.getString("notcompleted"));
                                category.setId(object.getInt("id"));
                                allCategoryArrayList.add(category);
                            }
                            dialog.dismiss();
                            System.out.println("no of item loaded"+no_of_item_loaded);
                            System.out.println("array size"+allCategoryArrayList.size());
                            System.out.println("count"+count);
                            if (allCategoryArrayList.size()==count){
                                listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                                listAdapter = new CategoryListAdapter(CategoryListActivity.this, allCategoryArrayList);
                                listView.setAdapter(listAdapter);
                            }
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
        MyVolleySingleton.getInstance(CategoryListActivity.this).getRequestQueue().add(stringRequest);
    }

    private class MoveItemTask extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(CategoryListActivity.this);
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
                jsonObject.put("list",params[1]);
                if (params[2].equalsIgnoreCase("null"))
                {
                    jsonObject.put("store",JSONObject.NULL);
                }
                else {
                    jsonObject.put("store", params[2]);
                }
                if (params[3].equalsIgnoreCase("null"))
                {
                    jsonObject.put("brand",JSONObject.NULL);
                }else {
                    jsonObject.put("brand", params[3]);
                }
                jsonObject.put("type", params[4]);
                System.out.println(jsonObject.toString());
                url = new URL(AppUrl.ITEM_LIST_URL+params[0]+"/");
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
                final Snackbar snackbar = Snackbar.make(layout, "Item Moved Succesfully!", Snackbar.LENGTH_LONG);
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
                final Snackbar snackbar = Snackbar.make(layout, "Item not moved! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
    }

}
