package com.project.spliceglobal.recallgo.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.pwittchen.infinitescroll.library.InfiniteScrollListener;
import com.project.spliceglobal.recallgo.R;
import com.project.spliceglobal.recallgo.adapters.AllCategoryAdapter;
import com.project.spliceglobal.recallgo.model.AllCategory;
import com.project.spliceglobal.recallgo.utils.AppUrl;
import com.project.spliceglobal.recallgo.utils.MyVolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.support.v7.recyclerview.R.attr.layoutManager;


/**
 * A simple {@link Fragment} subclass.
 */
public class AllFragment extends Fragment {
    ArrayList<AllCategory> allCategoryArrayList;
    RecyclerView rv;
    AllCategoryAdapter categoryAdapter;
    String next_url;
    int count;
    private static final int maxItemsPerRequest = 10;
    private LinearLayoutManager layoutManager;
    public AllFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_all, container, false);
        allCategoryArrayList=new ArrayList<>();
        rv = (RecyclerView)view.findViewById(R.id.rv);
        getCategory(AppUrl.ALL_CATEGORY_URL);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
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
    public  void getCategory(String url) {
        System.out.println("getItemcalled");
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            System.out.println("response"+response);
                            JSONObject jsonObject=new JSONObject(response);
                            count=jsonObject.getInt("count");
                            next_url=jsonObject.getString("next");
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                AllCategory category = new AllCategory();
                                category.setCategory_name(object.getString("name"));
                                category.setCompleted(object.getString("completed"));
                                category.setUncompletd(object.getString("notcompleted"));
                                category.setId(object.getInt("id"));
                                allCategoryArrayList.add(category);
                            }
                            categoryAdapter = new AllCategoryAdapter(allCategoryArrayList);
                            rv.setAdapter(categoryAdapter);
                            rv.setItemAnimator(new DefaultItemAnimator());
                            layoutManager = new LinearLayoutManager(getActivity());
                            rv.setLayoutManager(layoutManager);
                            rv.addOnScrollListener(createInfiniteScrollListener());

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
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);
    }
    public  void getMoreCategory(String url) {
        System.out.println("getMoreItem called");
        System.out.println(next_url);
        if (next_url.equalsIgnoreCase("null")){
            System.out.println("true in next");
            Toast.makeText(getContext(),"No More Data Available",Toast.LENGTH_LONG).show();
            return;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            count=jsonObject.getInt("count");
                            next_url=jsonObject.getString("next");
                            System.out.println(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                AllCategory category = new AllCategory();
                                category.setCategory_name(object.getString("name"));
                                category.setCompleted(object.getString("completed"));
                                category.setUncompletd(object.getString("notcompleted"));
                                category.setId(object.getInt("id"));
                                allCategoryArrayList.add(category);
                            }
                            categoryAdapter = new AllCategoryAdapter(allCategoryArrayList);
                            rv.setAdapter(categoryAdapter);
                            rv.setItemAnimator(new DefaultItemAnimator());
                            layoutManager = new LinearLayoutManager(getActivity());
                            rv.setLayoutManager(layoutManager);

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
        MyVolleySingleton.getInstance(getActivity()).getRequestQueue().add(stringRequest);
    }

    @NonNull
    private InfiniteScrollListener createInfiniteScrollListener() {
        return new InfiniteScrollListener(10,layoutManager) {
            @Override public void onScrolledToEnd(final int firstVisibleItemPosition) {
                if (next_url.equalsIgnoreCase("null")){
                    Toast.makeText(getContext(),"No More Data Available",Toast.LENGTH_LONG).show();

                }
                else {
                    getMoreCategory(next_url);
                    refreshView(rv, new AllCategoryAdapter(allCategoryArrayList), firstVisibleItemPosition);

                }
            }
        };
    }

}
