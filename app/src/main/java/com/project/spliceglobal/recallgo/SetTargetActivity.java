package com.project.spliceglobal.recallgo;

import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

public class SetTargetActivity extends AppCompatActivity {
    TextView store_list;
    EditText number;
    ImageButton increase,decrese;
    ArrayList<String> storeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_target);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Price Chaser");
        }
        store_list=(TextView)findViewById(R.id.store_list);
        storeList=new ArrayList<>();
        storeList.add("Walmart");
        storeList.add("Walgreens");
        storeList.add("marsh");
        storeList.add("Relays");
        storeList.add("Weis");
        store_list.setPaintFlags(store_list.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        increase=(ImageButton)findViewById(R.id.increase);
        decrese=(ImageButton)findViewById(R.id.decrese);
        number=(EditText) findViewById(R.id.number);
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  number.setText(Integer.parseInt(number.getText().toString())+1);

            }
        });
        store_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog dialog1 = new MaterialDialog.Builder(SetTargetActivity.this)
                        .title("Supported store")
                        .customView(R.layout.indi_view_repeat_dialog, true)
                        .positiveText("")
                        .positiveColorRes(R.color.colorPrimary)
                        .negativeColorRes(R.color.colorPrimary)
                        .negativeText("")
                        .show();
                View view = dialog1.getCustomView();
                if (view != null) {
                    ListView listView = (ListView) dialog1.getCustomView().findViewById(R.id.lv);

                    ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<>(SetTargetActivity.this, android.R.layout.simple_list_item_1, storeList);
                    locationArrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    listView.setAdapter(locationArrayAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Toast.makeText(getApplicationContext(), "you selected" + parent.getItemAtPosition(position), Toast.LENGTH_LONG).show();
                           // repeat_text.setText(String.valueOf(parent.getItemAtPosition(position)));
                            dialog1.dismiss();
                        }
                    });
                }
            }
        });
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
