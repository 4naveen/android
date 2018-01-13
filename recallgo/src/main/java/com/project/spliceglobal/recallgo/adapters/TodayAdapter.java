package com.project.spliceglobal.recallgo.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

public class TodayAdapter extends RecyclerSwipeAdapter<TodayAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Item> itemArrayList;
    private String called_from;
    private int category_id;
    private String cat_name;
    private String called_from_adapter;
    private int itemIdPosition;
    private String type;
    ArrayList<String> repeatList;

    public TodayAdapter(Context context, ArrayList<Item> itemArrayList,String called_from,int category_id,String cat_name,String called_from_adapter) {
        this.itemArrayList = itemArrayList;
        this.called_from=called_from;
        this.category_id=category_id;
        this.cat_name=cat_name;
        this.called_from_adapter=called_from_adapter;
        this.context=context;
    }
    public TodayAdapter(Context context, ArrayList<Item> itemArrayList,String called_from,String called_from_adapter ) {
        this.itemArrayList = itemArrayList;
        this.called_from=called_from;
        this.called_from_adapter=called_from_adapter;
        this.context=context;

    }
    public void setItemData(ArrayList<Item> itemList) {
        Log.v("my_tag", "setMovieData called with size: " + itemList.size());
        itemArrayList = itemList;
        notifyDataSetChanged();
    }
    @Override
    public TodayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_row_today, parent, false);
        //context=parent.getContext();
        return new ViewHolder(v);
        }
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
/*        if (viewHolder instanceof UserViewHolder) {
        } else if (viewHolder instanceof ProgressViewHolder) {
            ProgressViewHolder loadingViewHolder = (ProgressViewHolder) viewHolder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }*/
        repeatList = new ArrayList<>();
        repeatList.add("One Time");
        repeatList.add("Daily");
        repeatList.add("Weekly");
        repeatList.add("Every two Week");
        repeatList.add("Monthly");
        repeatList.add("Yearly");
         Item item = itemArrayList.get(position);
        final int item_id = itemArrayList.get(position).getId();
        category_id=itemArrayList.get(position).getList_id();
        type=item.getRepeat_type();
        final String brand=item.getBrand_id();
        final String store=item.getStore_id();
        viewHolder.category_name.setText(item.getItem_name());
        if (item.getBrand_name().equalsIgnoreCase("null")){
            viewHolder.sharedby.setVisibility(View.GONE);
        }else {
            viewHolder.sharedby.setVisibility(View.VISIBLE);
            viewHolder.sharedby.setText(item.getBrand_name());
        }
        if (item.getDate_time().equalsIgnoreCase("null")){
           // viewHolder.date.setText("");
            viewHolder.date.setVisibility(View.GONE);

        }else {
            viewHolder.date.setVisibility(View.VISIBLE);
            viewHolder.date.setText(item.getDate_time());
        }
        String name = item.getItem_name();
        String ch = String.valueOf(name.charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getRandomColor();
        TextDrawable.builder().beginConfig().fontSize(20).width(10).height(10).endConfig();
        //Color.rgb(97,107,192);
        TextDrawable drawable = TextDrawable.builder().buildRound(ch.toUpperCase(),color);
        viewHolder.letter.setImageDrawable(drawable);
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));
        viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper2));
        viewHolder.layout_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemIdPosition = viewHolder.getLayoutPosition();
                new DeleteItemTask().execute(String.valueOf(item_id));
                mItemManger.closeAllItems();
            }
        });
        viewHolder.layout_move.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemIdPosition = viewHolder.getLayoutPosition();
                Intent intent=new Intent(context, CategoryListActivity.class);
                intent.putExtra("called_from","move");
                intent.putExtra("id",item_id);
                System.out.println("id in adapter--"+item_id);
                intent.putExtra("type",type);
                intent.putExtra("brand",brand);
                intent.putExtra("store",store);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                mItemManger.closeAllItems();
            }
        });
        viewHolder.layout_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemIdPosition = viewHolder.getLayoutPosition();
                final MaterialDialog dialog1 = new MaterialDialog.Builder(context)
                        .title("Select ")
                        .customView(R.layout.indi_view_repeat_dialog, true)
                        .positiveText("ok")
                        .backgroundColor(context.getResources().getColor(R.color.list_bg_color))
                        .positiveColorRes(R.color.colorPrimary)
                        .negativeColorRes(R.color.colorPrimary)
                        .negativeText("cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                new LaterItemTask().execute(String.valueOf(item_id),String.valueOf(category_id),store,brand,type);
                                dialog.dismiss();

                            }
                        })
                        .show();
                View view = dialog1.getCustomView();
                if (view != null) {
                    ListView listView = (ListView) dialog1.getCustomView().findViewById(R.id.lv);
                  /*  ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, repeatList);
                    locationArrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    listView.setAdapter(locationArrayAdapter);*/
                    RepeatListAdapter repeatListAdapter=new RepeatListAdapter(context,repeatList);
                    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    listView.setAdapter(repeatListAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Toast.makeText(getApplicationContext(), "you selected" + parent.getItemAtPosition(position), Toast.LENGTH_LONG).show();
                           // repeat_text.setText(String.valueOf(parent.getItemAtPosition(position)));
                            if (String.valueOf(parent.getItemAtPosition(position)).equalsIgnoreCase("One Time")) {
                                type =String.valueOf(1);
                            } else if (String.valueOf(parent.getItemAtPosition(position)).equalsIgnoreCase("Daily")) {
                                type =String.valueOf(2);
                            } else if (String.valueOf(parent.getItemAtPosition(position)).equalsIgnoreCase("Weekly")) {
                                type = String.valueOf(3);
                            } else if (String.valueOf(parent.getItemAtPosition(position)).equalsIgnoreCase("Every two Week")) {
                                type = String.valueOf(4);
                            } else if (String.valueOf(parent.getItemAtPosition(position)).equalsIgnoreCase("Monthly")) {
                                type =String.valueOf(5);
                            } else {
                                type =String.valueOf(1);
                            }
                           // dialog1.dismiss();
                        }
                    });
                }

                mItemManger.closeAllItems();
            }
        });

        viewHolder.layout_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemIdPosition = viewHolder.getLayoutPosition();

                new CompleteItemTask().execute(String.valueOf(item_id),String.valueOf(category_id),store,brand,type,"2");

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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView category_name,sharedby, date;
        ImageView letter;
        SwipeLayout swipeLayout;
        LinearLayout top_view,layout_delete,layout_move,layout_later,layout_complete;

        public ViewHolder(View itemView) {
            super(itemView);
            category_name = (TextView) itemView.findViewById(R.id.name);
            sharedby = (TextView) itemView.findViewById(R.id.sharedby);
            date = (TextView) itemView.findViewById(R.id.date);
            letter = (ImageView) itemView.findViewById(R.id.letter);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            top_view=(LinearLayout)itemView.findViewById(R.id.top_view);
            layout_delete=(LinearLayout)itemView.findViewById(R.id.layout_delete);
            layout_move=(LinearLayout)itemView.findViewById(R.id.layout_move);
            layout_later=(LinearLayout)itemView.findViewById(R.id.layout_later);
            layout_complete=(LinearLayout)itemView.findViewById(R.id.layout_Complete);
            top_view.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int item_id = itemArrayList.get(getAdapterPosition()).getId();
            Intent i = new Intent(context, AddReminderItemActivity.class);
            i.putExtra("Item_id", item_id);
            i.putExtra("name",itemArrayList.get(getAdapterPosition()).getItem_name());
            i.putExtra("called_from",called_from);
            i.putExtra("list_name",itemArrayList.get(getAdapterPosition()).getList_name());
            i.putExtra("store_name",itemArrayList.get(getAdapterPosition()).getStore_name());
            i.putExtra("brand_name",itemArrayList.get(getAdapterPosition()).getBrand_name());
            i.putExtra("repeat_type",itemArrayList.get(getAdapterPosition()).getRepeat_type());
            i.putExtra("date_time",itemArrayList.get(getAdapterPosition()).getDate_time());
            i.putExtra("reminder_date_for_update",itemArrayList.get(getAdapterPosition()).getReminder_date_for_update());
            i.putExtra("brand_id",itemArrayList.get(getAdapterPosition()).getBrand_id());
            i.putExtra("store_id",itemArrayList.get(getAdapterPosition()).getStore_id());
            i.putExtra("list_id",itemArrayList.get(getAdapterPosition()).getList_id());
            i.putExtra("called_from_adapter",called_from_adapter);
            i.putExtra("description",itemArrayList.get(getAdapterPosition()).getDescription());
            i.putExtra("priority",itemArrayList.get(getAdapterPosition()).getPriority());
            i.putExtra("lat",itemArrayList.get(getAdapterPosition()).getLati());
            i.putExtra("long",itemArrayList.get(getAdapterPosition()).getLongi());
            i.putExtra("qty",itemArrayList.get(getAdapterPosition()).getQty());
            System.out.println("priority in adapter"+itemArrayList.get(getAdapterPosition()).getPriority());
            if (called_from.equalsIgnoreCase("update_category"))
            {
                i.putExtra("category_id",category_id);
                i.putExtra("category_name",cat_name);
            }
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
    private class DeleteItemTask extends AsyncTask<String, Void, String> {
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
                System.out.println(jsonObject.toString());
                url = new URL(AppUrl.ITEM_LIST_URL+params[0]+"/");
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("DELETE");
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

                Log.i("callPosition--", String.valueOf(itemIdPosition));
                itemArrayList.remove(itemIdPosition);
                notifyItemRemoved(itemIdPosition);
                notifyItemRangeChanged(itemIdPosition, itemArrayList.size());

                Log.i("Res--", result);
            } else {
            }
        }
    }
    private class LaterItemTask extends AsyncTask<String, Void, String> {
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
              /*  final Snackbar snackbar = Snackbar.make(context, "Item Moved Succesfully!", Snackbar.LENGTH_LONG);
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
                }, 3000);*/
                Toast.makeText(context,"updated successfully",Toast.LENGTH_LONG).show();
            } else {
           /*     final Snackbar snackbar = Snackbar.make(layout, "Item not moved! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();*/
                Toast.makeText(context," Not updated ! Try Again",Toast.LENGTH_LONG).show();

            }
        }
    }
    private class CompleteItemTask extends AsyncTask<String, Void, String> {
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
              /*  final Snackbar snackbar = Snackbar.make(context, "Item Moved Succesfully!", Snackbar.LENGTH_LONG);
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
                }, 3000);*/

                if (called_from.equalsIgnoreCase("update_category"))
                {
                    itemArrayList.remove(itemIdPosition);
                    notifyItemRemoved(itemIdPosition);
                    notifyItemRangeChanged(itemIdPosition, itemArrayList.size());
                }

                Toast.makeText(context,"updated successfully",Toast.LENGTH_LONG).show();
            } else {
           /*     final Snackbar snackbar = Snackbar.make(layout, "Item not moved! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();*/
                Toast.makeText(context," Not updated ! Try Again",Toast.LENGTH_LONG).show();

            }
        }
    }
}
