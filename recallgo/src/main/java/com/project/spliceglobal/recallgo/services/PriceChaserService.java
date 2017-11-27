package com.project.spliceglobal.recallgo.services;

import android.*;
import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.project.spliceglobal.recallgo.*;
import com.project.spliceglobal.recallgo.R;
import com.project.spliceglobal.recallgo.model.Item;
import com.project.spliceglobal.recallgo.utils.AppUrl;
import com.project.spliceglobal.recallgo.utils.MyVolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PriceChaserService extends Service  {
    String next_url;
    int count,no_of_item_loaded;
    ArrayList<Item>itemArrayList1,gpstodayItemArrayList;
    public PriceChaserService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        itemArrayList1=new ArrayList<>();
        gpstodayItemArrayList=new ArrayList<>();
        System.out.println("oncreate in service");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getItems(AppUrl.ITEM_LIST_URL);
        return START_STICKY;
    }

    public  void getItems(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("resonse"+response);
                            JSONObject jsonObject=new JSONObject(response);
                            count=jsonObject.getInt("count");
                            next_url=jsonObject.getString("next");
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            no_of_item_loaded=jsonArray.length();
                            System.out.println("result array size--"+jsonArray.length());
                            if (jsonArray.length()!=0){
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Item item = new Item();
                                    if (!(object.getString("lat").equalsIgnoreCase("null")&&object.getString("long").equalsIgnoreCase("null")))
                                    {
                                        item.setLati(object.getString("lat"));
                                        item.setLongi(object.getString("long"));
                                        // System.out.println("date"+dates[0].substring(0,10));
                                        itemArrayList1.add(item);
                                    }
                                }
                            /*    double no_of_next_url=(int)count/10;
                                System.out.println(no_of_next_url);
                                for (int i = 0; i <Math.round(no_of_next_url)-10 ; i++) {
                                    getMoreItem(next_url);
                                }*/
                            do {
                                count-=10;
                                getMoreItem(next_url);

                            }while (count>0);
                                System.out.println("total item size"+itemArrayList1.size());
                                if (next_url.equalsIgnoreCase("null")){
                                  /*  for (int i = count-no_of_item_loaded; i >0; i-=no_of_item_loaded) {
                                        System.out.println("i"+i);
                                        getMoreItem(next_url);
                                    }*/

                                    System.out.println("total item size"+itemArrayList1.size());
                                }
                                else {
                     /*               if (itemArrayList.size()==count) {
                                        if (next_url.equalsIgnoreCase("null")){
                                            System.out.println("total item size"+itemArrayList.size());
                                            System.out.println("total item size"+itemArrayList1.size());
                                            for (int i = 0; i < itemArrayList.size(); i++) {
                                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                                System.out.println(dateFormat.format(new Date()));
                                                if (itemArrayList.get(i).getDate_time().equalsIgnoreCase(dateFormat.format(new Date()))){
                                                    todayItemArrayList.add(itemArrayList.get(i));
                                                }
                                            }
                                            System.out.println("today array size"+todayItemArrayList.size());
                                        }

                                        // System.out.println("today total item size"+todayItemArrayList.size());
                                        sendNotification("today you have "+String.valueOf(todayItemArrayList.size())+"reminder item ");
                                    }*/
                                }
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
        MyVolleySingleton.getInstance(this).getRequestQueue().add(stringRequest);
    }
    public  void getMoreItem(String url)
    {
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
                            for (int i = 0; i < jsonArray.length(); i++) {
                                System.out.println("next"+next_url);
                                JSONObject object = jsonArray.getJSONObject(i);
                                Item item = new Item();

                                if (!(object.getString("lat").equalsIgnoreCase("null")&&object.getString("long").equalsIgnoreCase("null")))
                                {
                                    item.setLati(object.getString("lat"));
                                    item.setLongi(object.getString("long"));
                                    itemArrayList1.add(item);
                                }
                            }
                            if (next_url.equalsIgnoreCase("null")){

                                System.out.println("total item size"+itemArrayList1.size());
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
        MyVolleySingleton.getInstance(this).getRequestQueue().add(stringRequest);
    }



}
