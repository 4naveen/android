package com.project.spliceglobal.recallgo.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.project.spliceglobal.recallgo.CategoryItemListActivity;
import com.project.spliceglobal.recallgo.CategoryListActivity;
import com.project.spliceglobal.recallgo.model.AllCategory;
import com.project.spliceglobal.recallgo.R;
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

public class AllCategoryAdapter extends RecyclerSwipeAdapter<AllCategoryAdapter.ViewHolder> {
    private Context context;
    private ArrayList<AllCategory> allCategoryArrayList;
    private int categoryIdPosition;
    private  EditText email;
    private TextInputLayout input_email;
    public AllCategoryAdapter(ArrayList<AllCategory> allCategoryArrayList) {
        this.allCategoryArrayList = allCategoryArrayList;
    }
    @Override
    public AllCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_row_all, parent, false);
        context=parent.getContext();
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        AllCategory category= allCategoryArrayList.get(position);
        viewHolder.category_name.setText(category.getCategory_name());
        viewHolder.completed.setText(category.getCompleted());
        viewHolder.uncompleted.setText(category.getUncompletd());
        String name = category.getCategory_name();
        final String category_id=String.valueOf(category.getId());
        String ch = String.valueOf(name.charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        //int color = generator.getRandomColor();
        TextDrawable.builder().beginConfig().fontSize(20).width(10).height(10).endConfig();
        //Color.rgb(97,107,192);
        TextDrawable drawable = TextDrawable.builder().buildRound(ch.toUpperCase(), Color.rgb(51,51,51));
        viewHolder.letter.setImageDrawable(drawable);
      //  viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));
        viewHolder.layout_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryIdPosition = viewHolder.getLayoutPosition();
                new DeleteCategoryTask().execute(String.valueOf(category_id));
                mItemManger.closeAllItems();
            }
        });
        viewHolder.layout_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog1 = new MaterialDialog.Builder(context)
                        .title("Share")
                        .titleGravity(GravityEnum.CENTER)
                        .customView(R.layout.share_category_dialog, true)
                        .positiveText("share")
                        .autoDismiss(false)
                        .positiveColorRes(R.color.colorPrimary)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                categoryIdPosition = viewHolder.getLayoutPosition();
                                new ShareCategoryTask().execute(String.valueOf(category_id),email.getText().toString().trim());
                                mItemManger.closeAllItems();
                                dialog.dismiss();
                            }
                        })
                        .negativeColorRes(R.color.colorPrimary)
                        .negativeText("CANCEL")
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                View view = dialog1.getCustomView();
                if (view != null) {
                    email = (EditText) dialog1.getCustomView().findViewById(R.id.email);
                    input_email = (TextInputLayout) dialog1.getCustomView().findViewById(R.id.input_layout_email);
                    email.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            input_email.setError("");

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });

                }


            }
        });
        mItemManger.bindView(viewHolder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return allCategoryArrayList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView category_name,completed,uncompleted;
        ImageView letter;
        SwipeLayout swipeLayout;
        LinearLayout top_view,layout_delete,layout_share;

        public ViewHolder(View itemView) {
            super(itemView);
            category_name = (TextView) itemView.findViewById(R.id.name);
            completed = (TextView) itemView.findViewById(R.id.completed);
            uncompleted = (TextView) itemView.findViewById(R.id.incomplete);
            letter = (ImageView) itemView.findViewById(R.id.letter);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            top_view=(LinearLayout)itemView.findViewById(R.id.top_view);
            layout_delete=(LinearLayout)itemView.findViewById(R.id.layout_delete);
            layout_share=(LinearLayout)itemView.findViewById(R.id.layout_share);
            top_view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int category_id = allCategoryArrayList.get(getAdapterPosition()).getId();
            Intent i = new Intent(context, CategoryItemListActivity.class);
            i.putExtra("category_id", category_id);
            i.putExtra("name",allCategoryArrayList.get(getAdapterPosition()).getCategory_name());
            i.putExtra("called_from_adapter","all");
            i.putExtra("called_from","without_move");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
    private class DeleteCategoryTask extends AsyncTask<String, Void, String> {
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
                url = new URL(AppUrl.ALL_CATEGORY_URL+params[0]+"/");
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

                Log.i("callPosition--", String.valueOf(categoryIdPosition));
                allCategoryArrayList.remove(categoryIdPosition);
                notifyItemRemoved(categoryIdPosition);
                notifyItemRangeChanged(categoryIdPosition, allCategoryArrayList.size());

                Log.i("Res--", result);
                Toast.makeText(context," Deleted Successfully!",Toast.LENGTH_LONG).show();
            } else {


            }
        }
    }
    private class ShareCategoryTask extends AsyncTask<String, Void, String> {
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
                jsonObject.put("email",params[1]);
                System.out.println(jsonObject.toString());
                url = new URL(AppUrl.ALL_CATEGORY_URL+params[0]+"/share/");
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
                if (responseCode == HttpsURLConnection.HTTP_OK) {
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
/*
                Log.i("callPosition--", String.valueOf(categoryIdPosition));
                allCategoryArrayList.remove(categoryIdPosition);
                notifyItemRemoved(categoryIdPosition);
                notifyItemRangeChanged(categoryIdPosition, allCategoryArrayList.size());

                Log.i("Res--", result);*/
                Toast.makeText(context," Shared Successfully!",Toast.LENGTH_LONG).show();
            } else {


            }
        }
    }

}
