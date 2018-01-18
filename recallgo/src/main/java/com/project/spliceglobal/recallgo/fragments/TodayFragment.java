package com.project.spliceglobal.recallgo.fragments;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.project.spliceglobal.recallgo.AddReminderItemActivity;
import com.project.spliceglobal.recallgo.R;
import com.project.spliceglobal.recallgo.adapters.TodayAdapter;
import com.project.spliceglobal.recallgo.model.Item;
import com.project.spliceglobal.recallgo.utils.AppConstants;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayFragment extends Fragment {
    MaterialSearchView searchView;
    String called_from,next_url;
    int count;
    ArrayList<Item> itemArrayList;
    RecyclerView rv;
    TodayAdapter todayAdapter;
    TextView blank_message;
    EditText add;
    ImageView info_add;
    Menu menu1;
    FrameLayout layout;
    private LinearLayoutManager layoutManager;
    private ProgressBar progressBar;
    private EndlessRecyclerViewScrollListener scrollListener;
    private DateFormat dateFormat;
    SharedPreferences preferences;
    int PRIVATE_MODE = 0;
    public static final String PREF_NAME1 = "UncategorisedId";
    public TodayFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_today, container, false);
        setHasOptionsMenu(true);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.ENGLISH);
    /*    LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                mMessageReceiver, new IntentFilter("BeaconId"));*/
        /*getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);*/
        preferences = getActivity().getSharedPreferences(PREF_NAME1, PRIVATE_MODE);
        called_from=getArguments().getString("called_from","");
        System.out.println("called_from"+called_from);
        searchView = (MaterialSearchView) getActivity().findViewById(R.id.search_view);
        blank_message=(TextView)view.findViewById(R.id.blank_data);
        layout=(FrameLayout)view.findViewById(R.id.frame_layout);
        add=(EditText)view.findViewById(R.id.add);
        info_add=(ImageView)view.findViewById(R.id.info_add);
        if (called_from.equalsIgnoreCase("search")){
           searchView.setVisibility(View.VISIBLE);
            /*imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);*/
        }
        if (called_from.equalsIgnoreCase("today_layout")){
            searchView.clearFocus();
            searchView.closeSearch();
            searchView.setVisibility(View.GONE);
          /*  imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);*/
        }
        if (called_from.equalsIgnoreCase("main_body")){
            searchView.clearFocus();
            searchView.closeSearch();
            searchView.setVisibility(View.GONE);
            /*imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);*/
        }
        if (called_from.equalsIgnoreCase("reminder")){
            searchView.clearFocus();
            searchView.closeSearch();
            searchView.setVisibility(View.GONE);
            /*imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);*/
        }
        itemArrayList=new ArrayList<>();
        rv = (RecyclerView)view.findViewById(R.id.rv);
        //getItems(AppUrl.ITEM_LIST_URL);
        getItems(AppUrl.TODAY_REMINDERS_URL+dateFormat.format(new Date()));
        add.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               // add.setCursorVisible(false);
                if (menu1!=null){
                    menu1.findItem(R.id.add).setVisible(true);
                }
                info_add.setVisibility(View.VISIBLE);
                if (count==0){
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
                        Intent intent=new Intent(getActivity(),AddReminderItemActivity.class);
                        intent.putExtra("name",s.toString());
                        intent.putExtra("called_from","add_today");
                        intent.putExtra("called_from_adapter","today");
                        startActivityForResult(intent,0);
                    }
                });
            }
        });
        layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        todayAdapter = new TodayAdapter(getActivity(),itemArrayList,"update_today","today");
        rv.setAdapter(todayAdapter);
        rv.setItemAnimator(new DefaultItemAnimator());
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
        rv.addOnScrollListener(scrollListener);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (searchView.isSearchOpen()) {
                    }
                    searchView.closeSearch();

                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                           getActivity().finish();

                        return true;
                    }
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        menu1=menu;
        MenuItem menuItem = menu.findItem(R.id.action_search);
        searchView.setMenuItem(menuItem);
        if (called_from.equalsIgnoreCase("search")) {
            searchView.showSearch();
        }
        called_from="";
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Item> subitemArrayList = new ArrayList<>();
                for (int i = 0; i < itemArrayList.size(); i++) {
                  /*  if (itemArrayList.get(i).getItem_name().contains(newText)) {
                        subitemArrayList.add(itemArrayList.get(i));
                    }*/
                    //System.out.println("lead item --"+leadsArrayList.get(i).getName()+" "+leadsArrayList.get(i).getNumber());
                    if (StringUtils.containsIgnoreCase(itemArrayList.get(i).getItem_name(),newText)) {
                        subitemArrayList.add(itemArrayList.get(i));
                    }
                }
                rv.setAdapter(new TodayAdapter(getActivity(),subitemArrayList,"update_today","today"));
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.add)
        {
            item.setVisible(false);
            if (!String.valueOf(preferences.getInt("id",0)).isEmpty()){
                new AddItem().execute(add.getText().toString(),String.valueOf(preferences.getInt("id",0)),"1");
            }
            else {
                Toast.makeText(getActivity(),"Something went wrong!Please Relogin or check your internet Connection",Toast.LENGTH_LONG).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }
        /*itemArrayList.clear();
        getItems(AppUrl.ITEM_LIST_URL);*/

        super.onResume();
    }

    public  void getItems(String url) {
        //System.out.println("getItemcalled");

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            count=jsonObject.getInt("count");
                            next_url=jsonObject.getString("next");
                            DateFormat timeFormat=new SimpleDateFormat("hh:mm a",Locale.ENGLISH);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            Date date=new Date();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Item item = new Item();
                                try {
                                    date=dateFormat.parse(object.getString("date"));
                                    //Log.e("parsed date",date.toString());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                               // String[] dates=object.getString("date").split("T");
                                String formate_date=timeFormat.format(date);
                               // Log.e("formated date",formate_date);
                                //System.out.println("item date"+object.getString("date"));
                                item.setItem_name(object.getString("name"));
                                item.setDate_time("Today "+formate_date);
                                item.setReminder_date_for_update(object.getString("date"));
                                item.setGoogle_category(object.getString("google_category"));
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
                                //item.setDescription(object.getString("description"));
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
                               // Log.e("priority in toay",object.getString("priority"));
                                if (object.getString("store").equalsIgnoreCase("null") ) {
                                    item.setStore_name("");
                                    item.setStore_id("null");
                                }else {
                                    item.setStore_name(object.getString("store_name"));
                                    item.setStore_id(String.valueOf(object.getInt("store")));
                                }
                                item.setRepeat_type(String.valueOf(object.getInt("type")));
                                itemArrayList.add(item);
                            }
                            updateUI(itemArrayList);
                            if (itemArrayList.isEmpty())
                            {
                                blank_message.setVisibility(View.GONE);
                            }
                            else {
                                blank_message.setVisibility(View.GONE);

                            }
                           progressBar.setVisibility(View.GONE);
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
        };
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);
    }
    private class AddItem extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
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
                jsonObject.put("name",params[0]);
                jsonObject.put("list",params[1]);
                jsonObject.put("type", params[2]);
               // DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",Locale.ENGLISH);
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.ENGLISH);
                dateFormat.setTimeZone(TimeZone.getDefault());
                String formated_date = dateFormat.format(new Date());
                //System.out.println("today formated date "+formated_date);
                jsonObject.put("date",formated_date);
               // System.out.println(jsonObject.toString());
                Log.e("json request",jsonObject.toString());
                url = new URL(AppUrl.ITEM_LIST_URL);
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
                itemArrayList.clear();
                getItems(AppUrl.TODAY_REMINDERS_URL+dateFormat.format(new Date()));
                sendMessageToActivity(getActivity(),"add");
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
    private void updateUI(ArrayList<Item> data) {
        todayAdapter.setItemData(data);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("fragment","on Activity result in fragment");
        itemArrayList.clear();
        info_add.setVisibility(View.INVISIBLE);
        add.setText("");
        add.setHint("What you want me to Remind you ?");
        //add.setCursorVisible(false);
        getItems(AppUrl.TODAY_REMINDERS_URL+dateFormat.format(new Date()));
    }
    private  void sendMessageToActivity(Context context, String msg) {
        Intent intent = new Intent("BeaconId");
        // You can also include some extra data.
        intent.putExtra("id", msg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
 /*   private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("id");
            // System.out.println("message from serevice"+message);
            if (!message.equalsIgnoreCase(""))
            {        itemArrayList.clear();
                     getItems(AppUrl.TODAY_REMINDERS_URL+dateFormat.format(new Date()));
            }

            // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };*/
}
