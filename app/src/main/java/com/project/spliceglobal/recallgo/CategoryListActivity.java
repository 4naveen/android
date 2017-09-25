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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class CategoryListActivity extends AppCompatActivity {
    ListView listView;
    CategoryListAdapter listAdapter;
    ArrayList<String> categoryArrayList;
    String category_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_list);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        listView = (ListView) findViewById(R.id.category);
        categoryArrayList = new ArrayList<>();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Select Category");
        }
        categoryArrayList.add("UnCategorized");
        categoryArrayList.add("Grocery");
        categoryArrayList.add("Shopping");
        categoryArrayList.add("Electronics");
        categoryArrayList.add("Meetings");
        categoryArrayList.add("Birthday");
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listAdapter = new CategoryListAdapter(this, categoryArrayList);
       listView.setAdapter(listAdapter);
       /* listView.setAdapter(new ArrayAdapter<String>(this, R.layout.indi_view_category,
                R.id.category_name,categoryArrayList) {
            @NonNull
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ImageView icon = (ImageView) v.findViewById(R.id.ok);
                if (listView.isItemChecked(position)) {
                    icon.setImageResource(R.mipmap.ok);
                } else {
                   // icon.setImageResource(R.mipmap.ic_add_white_24dp);
                }
                return v;
            }
        });*/
/*        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                    for (int ctr=0;ctr<=categoryArrayList.size();ctr++){
                        if(i==ctr){
                           // listView.getChildAt(ctr).setBackgroundColor(Color.CYAN);
                            ImageView imageView=(ImageView) view.findViewById(R.id.ok);
                             imageView.setVisibility(View.VISIBLE);
                        }else{
                           // listView.getChildAt(ctr).setBackgroundColor(Color.WHITE);
                            ImageView imageView=(ImageView) view.findViewById(R.id.ok);
                            imageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_done_white_24dp));;
                            imageView.setVisibility(View.GONE);
                        }
                    }
                    listAdapter.notifyDataSetChanged();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                Log.v("Selected item", String.valueOf(listView.getItemAtPosition(i)));
            }
        });*/

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              category_text=""+parent.getItemAtPosition(position);
               /* try{
                    for (int ctr=0;ctr<=categoryArrayList.size();ctr++){
                        if(position==ctr){
                            // listView.getChildAt(ctr).setBackgroundColor(Color.CYAN);
                            ImageView imageView=(ImageView) view.findViewById(R.id.ok);
                            imageView.setVisibility(View.VISIBLE);
                        }else{
                            // listView.getChildAt(ctr).setBackgroundColor(Color.WHITE);
                            ImageView imageView=(ImageView) view.findViewById(R.id.ok);
                           // imageView.setImageDrawable(getResources().getDrawable(R.mipmap.ic_done_white_24dp));;
                           // imageView.setVisibility(View.GONE);
                        }
                    }
                    listAdapter.notifyDataSetChanged();
                }
                catch (Exception e){
                    e.printStackTrace();
                }*/
            }
        });

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CategoryListActivity.this, AddCategoryActivity.class));
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
                myIntent.putExtra("category",category_text );
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
