package com.project.spliceglobal.recallgo.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.project.spliceglobal.recallgo.HomeActivity;
import com.project.spliceglobal.recallgo.R;
import com.project.spliceglobal.recallgo.ReminderActivity;
import com.project.spliceglobal.recallgo.model.Item;
import com.project.spliceglobal.recallgo.utils.AppConstants;
import com.project.spliceglobal.recallgo.utils.AppUrl;
import com.project.spliceglobal.recallgo.utils.MyVolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by Personal on 11/10/2017.
 */

public class DateItemReminderService extends Service {
    int counter1,counter;
    String next_url;
    ArrayList<Item>itemArrayList;

    public DateItemReminderService() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        itemArrayList=new ArrayList<>();


    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        getItems("http://ec2-35-154-135-19.ap-south-1.compute.amazonaws.com:8001/api/reminders/"+"?date="+dateFormat.format(new Date()));

        return START_STICKY;
    }
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(com.project.spliceglobal.recallgo.R.mipmap.ic_launcher)
                .setContentTitle("Today Reminder")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.title_img);
            notificationBuilder.setColor(getResources().getColor(R.color.bg_screen3));
        } else {
            notificationBuilder.setSmallIcon(R.drawable.notification_icon);
        }
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
                          //  System.out.println("resonse"+response);
                            JSONObject jsonObject=new JSONObject(response);
                            int final_count=jsonObject.getInt("count");
                            next_url=jsonObject.getString("next");
                           // System.out.println("next url"+next_url);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            if (jsonArray.length()!=0){

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    Item item=new Item();
                                    item.setItem_name(object.getString("name"));
                                    item.setDate_time(object.getString("date"));
                                    itemArrayList.add(item);
                                }
                            }
                            if (!next_url.equalsIgnoreCase("null")){
                                getItems(next_url);
                            }
                            else {
                                DateFormat dateFormat1=new SimpleDateFormat("HH:mm",Locale.ENGLISH);
                               // System.out.println("today araay size"+itemArrayList.size());
                                for (int i=0;i<itemArrayList.size();i++){
                                    String [] item_dates=itemArrayList.get(i).getDate_time().split("T");
                                    Date date=new Date();
                                    date.setTime(System.currentTimeMillis()-((5*60*60*1000)+(30*60*1000)));
                                    String current_date=dateFormat1.format(date);
                                    String original_time=item_dates[1].substring(0,5);
                                   // System.out.println("current time"+current_date);
                                   // System.out.println("original time"+original_time);
                                    if (original_time.equalsIgnoreCase(current_date)){
                                        System.out.println("send notification");
                                        sendNotification(itemArrayList.get(i).getItem_name());
                                    }
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
                header.put("Authorization","Token aa5c12b3ebac6d122304d9b6c0713ae39863d938");
                // header.put("Content-Type", "application/x-www-form-urlencoded");
                return header;
            }
        } ;
        MyVolleySingleton.getInstance(this).getRequestQueue().add(stringRequest);
    }

}
