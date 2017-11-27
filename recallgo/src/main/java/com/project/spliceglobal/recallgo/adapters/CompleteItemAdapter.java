package com.project.spliceglobal.recallgo.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.project.spliceglobal.recallgo.AddReminderItemActivity;
import com.project.spliceglobal.recallgo.CategoryListActivity;
import com.project.spliceglobal.recallgo.R;
import com.project.spliceglobal.recallgo.model.Item;
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

/**
 * Created by Personal on 9/18/2017.
 */

public class CompleteItemAdapter extends RecyclerSwipeAdapter<CompleteItemAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Item> itemArrayList;
    private int itemIdPosition;
    private String type;
    private int category_id;

    public CompleteItemAdapter(Context context, ArrayList<Item> itemArrayList,int category_id) {
        this.itemArrayList = itemArrayList;
        this.context=context;
        this.category_id=category_id;
    }

    @Override
    public CompleteItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_row_complete_item, parent, false);
        //context=parent.getContext();
        return new ViewHolder(v);
        }
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {

         Item item = itemArrayList.get(position);
        final int item_id = itemArrayList.get(position).getId();
        type=item.getRepeat_type();
        final String brand=item.getBrand_id();
        final String store=item.getStore_id();

        viewHolder.category_name.setText(item.getItem_name());
        viewHolder.category_name.setPaintFlags(viewHolder.category_name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        viewHolder.sharedby.setText(item.getQty());
        viewHolder.date.setText(item.getDate_time());
        String name = item.getItem_name();
        String ch = String.valueOf(name.charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        //int color = generator.getRandomColor();
        TextDrawable.builder().beginConfig().fontSize(20).width(10).height(10).endConfig();
        //Color.rgb(97,107,192);
        TextDrawable drawable = TextDrawable.builder().buildRound(ch.toUpperCase(), Color.rgb(51,51,51));
        viewHolder.letter.setImageDrawable(drawable);
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper2));

        viewHolder.layout_uncomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemIdPosition = viewHolder.getLayoutPosition();

                new UnCompleteItemTask().execute(String.valueOf(item_id),String.valueOf(category_id),store,brand,type,"1");

                mItemManger.closeAllItems();
            }
        });

        mItemManger.bindView(viewHolder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView category_name,sharedby, date;
        ImageView letter;
        SwipeLayout swipeLayout;
        LinearLayout top_view,layout_uncomplete;

        public ViewHolder(View itemView) {
            super(itemView);
            category_name = (TextView) itemView.findViewById(R.id.name);
            sharedby = (TextView) itemView.findViewById(R.id.sharedby);
            date = (TextView) itemView.findViewById(R.id.date);
            letter = (ImageView) itemView.findViewById(R.id.letter);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            top_view=(LinearLayout)itemView.findViewById(R.id.top_view);
            layout_uncomplete=(LinearLayout)itemView.findViewById(R.id.layout_uncomplete);

        }


    }


    private class UnCompleteItemTask extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
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
                jsonObject.put("status",params[5]);
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
                Toast.makeText(context,"updated successfully",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context," Not updated ! Try Again",Toast.LENGTH_LONG).show();

            }
        }
    }

}
