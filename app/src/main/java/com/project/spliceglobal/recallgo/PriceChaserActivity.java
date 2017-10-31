package com.project.spliceglobal.recallgo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.project.spliceglobal.recallgo.adapters.PriceChaserAdapter;
import com.project.spliceglobal.recallgo.model.PriceChaser;

import java.util.ArrayList;

public class PriceChaserActivity extends AppCompatActivity {
    RecyclerView rv;
    ArrayList<PriceChaser> priceChaserArrayList;
    PriceChaserAdapter priceChaserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_chaser);
        Intent intent=getIntent();

        /*if (intent.getType().indexOf("image/") != -1) {
            Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
            System.out.println("image url"+imageUri.getPath());
            Toast.makeText(getApplicationContext(),imageUri.getPath(),Toast.LENGTH_LONG).show();
        }*/
        if (getIntent().getBooleanExtra("flag",true)){
            if (intent.getType().equals("text/plain")) {
                System.out.println("data string  received"+intent.getStringExtra(Intent.EXTRA_TEXT));
                Toast.makeText(getApplicationContext(),intent.getStringExtra(Intent.EXTRA_TEXT),Toast.LENGTH_LONG).show();

            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Price Chaser");
        }
        priceChaserArrayList = new ArrayList<>();
        rv = (RecyclerView) findViewById(R.id.rv);
        for (int i = 0; i < 4; i++) {
            PriceChaser priceChaser = new PriceChaser();
            priceChaser.setItem_name("product1");
            priceChaser.setProduct_url("www.amazon.in");
            priceChaser.setDate("13 May,2017");
            priceChaser.setTarget_price("$654");
            priceChaser.setOriginal_rice("$625");
            priceChaserArrayList.add(priceChaser);
        }
        priceChaserAdapter = new PriceChaserAdapter(this, priceChaserArrayList);
        rv.setAdapter(priceChaserAdapter);
        rv.setItemAnimator(new DefaultItemAnimator());
        // rv.addItemDecoration(new DividerItemDecoration(getActivity(),GridLayoutManager.HORIZONTAL));
        RecyclerView.LayoutManager lmanager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //RecyclerView.LayoutManager lmanager=new GridLayoutManager(getActivity(),3);
        rv.setLayoutManager(lmanager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
