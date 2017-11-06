package com.project.spliceglobal.recallgo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

public class NotificationService extends Service {
    public String url="http://laitit.com/hce/api/notificationmsg.php?beaconsmsg=";
    String beconsmsg;
    int count,no_of_item_loaded;
    String next_url;
    ArrayList<Item> itemArrayList,todayItemArrayList;
    public NotificationService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        itemArrayList=new ArrayList<>();
        todayItemArrayList=new ArrayList<>();
        System.out.println("oncreate in service");

    }
/*
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

     *//*   if (!beaconManager.||!beaconManager.isBluetoothEnabled()) {
            Toast.makeText(this, "Device does not have Bluetooth Low Energy or it is not enabled", Toast.LENGTH_LONG).show();
            this.stopSelf();
        }*//*
        connectToService();
    }*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        getItems(AppUrl.ITEM_LIST_URL);

        return START_STICKY;
    }


    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
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
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Item item = new Item();
                                if (!object.getString("date").equalsIgnoreCase("null"))
                                {
                                    String[] dates=object.getString("date").split("T");
                                    item.setItem_name(object.getString("name"));
                                    item.setDate_time(dates[1].substring(0,5));
                                    System.out.println("date"+dates[1].substring(0,5));
                                    itemArrayList.add(item);

                                }
                            }
                            while (next_url.equalsIgnoreCase("null"))
                            {
                                getMoreItem(next_url);
                            }
                            System.out.println("total item size"+itemArrayList.size());
                            for (int i = 0; i < itemArrayList.size(); i++) {
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                System.out.println(dateFormat.format(new Date()));
                                if (itemArrayList.get(i).getDate_time().equalsIgnoreCase(dateFormat.format(new Date()))){
                                    todayItemArrayList.add(itemArrayList.get(i));
                                }
                            }
                            System.out.println("today total item size"+todayItemArrayList.size());
                            sendNotification("today you have "+String.valueOf(todayItemArrayList.size())+"reminder item ");
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
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                Item item = new Item();
                                String[] dates=object.getString("date").split("T");
                                item.setItem_name(object.getString("name"));
                                item.setDate_time("Today "+dates[1].substring(0,5));
                                itemArrayList.add(item);                            }
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
