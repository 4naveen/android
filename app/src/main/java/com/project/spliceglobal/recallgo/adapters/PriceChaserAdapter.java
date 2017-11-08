package com.project.spliceglobal.recallgo.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.project.spliceglobal.recallgo.model.PriceChaser;
import com.project.spliceglobal.recallgo.R;
import com.project.spliceglobal.recallgo.SetTargetActivity;
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

public class PriceChaserAdapter extends RecyclerSwipeAdapter<PriceChaserAdapter.ViewHolder> {
    private Context context;
    private ArrayList<PriceChaser> priceChaserArrayList;
    private int priceChaserIdPosition;

    public PriceChaserAdapter(Context context, ArrayList<PriceChaser> priceChaserArrayList) {
        this.context = context;
        this.priceChaserArrayList = priceChaserArrayList;
    }

    @Override
    public PriceChaserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_row_price_chaser, parent, false);
        return new ViewHolder(v);    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        PriceChaser priceChaser= priceChaserArrayList.get(position);
        final int priceChaserId = priceChaser.getId();
        viewHolder.product_name.setText(priceChaser.getProduct_name());
        viewHolder.product_url.setText(priceChaser.getProduct_url());
        viewHolder.date.setText(priceChaser.getProduct_date());
        viewHolder.target_price.setText(priceChaser.getTarget_price());
        viewHolder.original_price.setText(priceChaser.getOriginal_rice());
       // viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper2));
        viewHolder.layout_setPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,SetTargetActivity.class);
                intent.putExtra("id",String.valueOf(priceChaserId));
                context.startActivity(intent);
                mItemManger.closeAllItems();

            }
        });
        viewHolder.layout_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priceChaserIdPosition = viewHolder.getLayoutPosition();
                new DeletePriceChaser().execute(String.valueOf(priceChaserId));
                mItemManger.closeAllItems();
            }
        });
        mItemManger.bindView(viewHolder.itemView, position);

    }
    public void setPriceChaserData(ArrayList<PriceChaser> priceChaserList) {
        Log.v("my_tag", "setMovieData called with size: " + priceChaserList.size());
        priceChaserArrayList = priceChaserList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return priceChaserArrayList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView product_name,date, product_url,target_price,original_price;
        SwipeLayout swipeLayout;
        LinearLayout layout_remove, layout_move,layout_setPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            product_name = (TextView) itemView.findViewById(R.id.product_name);
            product_url = (TextView) itemView.findViewById(R.id.product_url);
            date = (TextView) itemView.findViewById(R.id.date);
            target_price = (TextView) itemView.findViewById(R.id.target_price);
            original_price= (TextView) itemView.findViewById(R.id.original_price);
            layout_remove=(LinearLayout)itemView.findViewById(R.id.layout_remove);
            layout_move=(LinearLayout)itemView.findViewById(R.id.layout_move);
            layout_setPrice=(LinearLayout)itemView.findViewById(R.id.layout_setPrice);

        }
    }

    private class DeletePriceChaser extends AsyncTask<String, Void, String> {
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
                url = new URL(AppUrl.PRICE_CHASER_URL+params[0]+"/");
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

                Log.i("callPosition--", String.valueOf(priceChaserIdPosition));
                priceChaserArrayList.remove(priceChaserIdPosition);
                notifyItemRemoved(priceChaserIdPosition);
                notifyItemRangeChanged(priceChaserIdPosition, priceChaserArrayList.size());

                Log.i("Res--", result);
            } else {


            }
        }
    }

}
