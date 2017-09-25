package com.project.spliceglobal.recallgo;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

public class BrandListActivity extends AppCompatActivity {
    ListView listView;
    CategoryListAdapter listAdapter;
    ArrayList<String> categoryArrayList;
    EditText name;
    String brand_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_list);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        listView = (ListView)findViewById(R.id.category);
        categoryArrayList=new ArrayList<>();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Select Brand");
        }
        categoryArrayList.add("Titan");
        categoryArrayList.add("HP");
        categoryArrayList.add("Vistara");
        categoryArrayList.add("Amul India");
        categoryArrayList.add("Pizza Hut");
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listAdapter = new CategoryListAdapter(this, categoryArrayList);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                brand_text=""+parent.getItemAtPosition(position);
            }
        });
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog1 = new MaterialDialog.Builder(BrandListActivity.this)
                        .title("Add your own brand")
                        .customView(R.layout.add_dialog, true)
                        .positiveText("ADD")
                        .autoDismiss(false)
                        .positiveColorRes(R.color.colorPrimary)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
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
                    name = (EditText) dialog1.getCustomView().findViewById(R.id.name);
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                 break;
            }
            case R.id.ok: {
                Intent myIntent = new Intent();
                myIntent.putExtra("brand",brand_text );
                setResult(Activity.RESULT_OK, myIntent);
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_ok, menu);
        return super.onCreateOptionsMenu(menu);

    }
}
