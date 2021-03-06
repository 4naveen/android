package com.project.spliceglobal.recallgo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.project.spliceglobal.recallgo.adapters.RepeatListAdapter;
import com.project.spliceglobal.recallgo.adapters.StoreListAdapter;
import com.project.spliceglobal.recallgo.utils.AppConstants;
import com.project.spliceglobal.recallgo.utils.AppUrl;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

public class AddReminderItemActivity extends AppCompatActivity {
    LinearLayout notes_layout, category, brand, prefered_store, repeat;
    EditText description,quantity;
    ImageView date_time;
    Switch aSwitch;
    ArrayList<String> repeatList;
    TextView catgeory_text,brand_text,prefered_store_text,repeat_text,date_time_text,none,one,two,three,four;
    int priority;
    String item_name,notes,brand_id,store_id;
    String formated_date="null",called_from,called_from_adapter,reminder_date_for_update="null";
    SharedPreferences preferences;
    int PRIVATE_MODE = 0;
    public static final String PREF_NAME1 = "UncategorisedId";
    int category_id,repeat_text_id=1,item_id;
    LinearLayout layout;
    private double longitude=0.00;
    private double latitude=0.00;
    private double radiusInMeters=0.00;
    private String entry;
    private DateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        preferences = getApplicationContext().getSharedPreferences(PREF_NAME1, PRIVATE_MODE);
        category_id=preferences.getInt("id",0);
        layout=(LinearLayout)findViewById(R.id.layout);
        catgeory_text=(TextView)findViewById(R.id.category_text);
        quantity=(EditText)findViewById(R.id.quantity);
        brand_text=(TextView)findViewById(R.id.brand_text);
        prefered_store_text=(TextView)findViewById(R.id.prefered_store_text);
        repeat_text=(TextView)findViewById(R.id.repeat_text);
        date_time_text=(TextView)findViewById(R.id.date_time_text);
        description = (EditText) findViewById(R.id.description);
        none=(TextView)findViewById(R.id.none);
        none.setBackgroundColor(Color.BLACK);
        one=(TextView)findViewById(R.id.one);
        two=(TextView)findViewById(R.id.two);
        three=(TextView)findViewById(R.id.three);
        four=(TextView)findViewById(R.id.four);
        item_id=getIntent().getIntExtra("Item_id",0);
       // System.out.println("item_id"+item_id);
        called_from=getIntent().getStringExtra("called_from");
        System.out.println("called_from"+called_from);
        called_from_adapter=getIntent().getStringExtra("called_from_adapter");
       // System.out.println("called_from_adapter"+called_from_adapter);
        notes_layout = (LinearLayout) findViewById(R.id.notes);
        date_time = (ImageView) findViewById(R.id.date_time);
        category = (LinearLayout) findViewById(R.id.category);
        brand = (LinearLayout) findViewById(R.id.brand);
        prefered_store = (LinearLayout) findViewById(R.id.prefered_store);
        repeat = (LinearLayout) findViewById(R.id.repeat);
       // dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",Locale.ENGLISH);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getDefault());
        //formated_date = dateFormat.format(new Date());
        repeatList = new ArrayList<>();
        repeatList.add("One Time");
        repeatList.add("Daily");
        repeatList.add("Weekly");
        repeatList.add("Every two Week");
        repeatList.add("Monthly");
        repeatList.add("Yearly");
        aSwitch = (Switch) findViewById(R.id.simpleSwitch);
        item_name=getIntent().getStringExtra("name");
        brand_id=getIntent().getStringExtra("brand_id");
        store_id=getIntent().getStringExtra("store_id");
        System.out.println("brandId"+brand_id+"store_id"+store_id);
        if (called_from.equalsIgnoreCase("add_category")||called_from.equalsIgnoreCase("update_category"))
        {
          catgeory_text.setText(getIntent().getStringExtra("category_name"));
        }
        if (called_from.equalsIgnoreCase("update_today")||called_from.equalsIgnoreCase("update_category")){
            String repeat="";
            catgeory_text.setText(getIntent().getStringExtra("list_name"));
            category_id=getIntent().getIntExtra("list_id",0);
            if (getIntent().getStringExtra("brand_name").equalsIgnoreCase("null"))
            {
                brand_text.setText(" ");
            }
            else {
                brand_text.setText(getIntent().getStringExtra("brand_name"));
            }
            if (getIntent().getStringExtra("date_time").equalsIgnoreCase("null"))
            {
                date_time_text.setText(" ");
            }
            else {
                date_time_text.setText(getIntent().getStringExtra("date_time"));
            }

            if (!(getIntent().getStringExtra("lat").equalsIgnoreCase("null")&&getIntent().getStringExtra("long").equalsIgnoreCase("null")))
            {
                latitude=Double.parseDouble(getIntent().getStringExtra("lat"));
                longitude=Double.parseDouble(getIntent().getStringExtra("long"));
                aSwitch.setChecked(true);
            }
            prefered_store_text.setText(getIntent().getStringExtra("store_name"));
            Log.e("update date",getIntent().getStringExtra("date_time"));
            reminder_date_for_update=getIntent().getStringExtra("reminder_date_for_update");
            repeat_text_id=Integer.parseInt(getIntent().getStringExtra("repeat_type"));
            description.setText(getIntent().getStringExtra("description"));
            quantity.setText(getIntent().getStringExtra("qty"));
            if (getIntent().getStringExtra("priority").equalsIgnoreCase("1"))
            {
                one.setText("1");
                priority=1;
                none.setTextColor(Color.BLACK);
                System.out.println("priority in add"+getIntent().getStringExtra("priority"));
                one.setBackgroundColor(Color.BLACK);
                none.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                two.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                three.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                four.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            }
            if (getIntent().getStringExtra("priority").equalsIgnoreCase("2"))
            {
                two.setText("2");
                priority=2;
                System.out.println("priority in add"+getIntent().getStringExtra("priority"));
                none.setTextColor(Color.BLACK);
                two.setBackgroundColor(Color.BLACK);
                none.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                one.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                three.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                four.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            }
            if (getIntent().getStringExtra("priority").equalsIgnoreCase("3"))
            {
                three.setText("3");
                priority=3;
                System.out.println("priority"+getIntent().getStringExtra("priority"));
                none.setTextColor(Color.BLACK);
                three.setBackgroundColor(Color.BLACK);
                none.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                two.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                one.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                four.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            }
            if (getIntent().getStringExtra("priority").equalsIgnoreCase("4"))
            {
                four.setText("4");
                priority=4;
                System.out.println("priority"+getIntent().getStringExtra("priority"));
                none.setTextColor(Color.BLACK);
                four.setBackgroundColor(Color.BLACK);
                none.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                two.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                three.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                one.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            }

            if (getIntent().getStringExtra("repeat_type").equalsIgnoreCase("1"))
            {
                repeat="One Time";
            }
            if (getIntent().getStringExtra("repeat_type").equalsIgnoreCase("2"))
            {
                repeat="Daily";
            }  if (getIntent().getStringExtra("repeat_type").equalsIgnoreCase("3"))
            {
                repeat="Weekly";
            }  if (getIntent().getStringExtra("repeat_type").equalsIgnoreCase("4"))
            {
                repeat="Every two Week";
            }
            if (getIntent().getStringExtra("repeat_type").equalsIgnoreCase("5"))
            {
                repeat="Monthly";
            }
            if (getIntent().getStringExtra("repeat_type").equalsIgnoreCase("6"))
            {
                repeat="Yearly";
            }
            repeat_text.setText(repeat);

        }
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getIntent().getStringExtra("name"));
        }
 /*       notes_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog1 = new MaterialDialog.Builder(AddReminderItemActivity.this)
                        .title("Add Note")
                        .customView(R.layout.add_notes_dialog, true)
                        .positiveText("ADD")
                        .autoDismiss(false)
                        .positiveColorRes(R.color.colorPrimary)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                notes=description.getText().toString();
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
                    description = (EditText) dialog1.getCustomView().findViewById(R.id.description);
                    description.setText("  ");
                    description.setSelection(1);
                    description.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                                case MotionEvent.ACTION_UP:
                                    v.getParent().requestDisallowInterceptTouchEvent(false);
                                    break;
                            }
                            return false;
                        }
                    });
                }
            }
        });*/
        date_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SingleDateAndTimePickerDialog.Builder(AddReminderItemActivity.this)
                        //.bottomSheet()
                        .curved()
                        .minutesStep(1)
                        .mainColor(Color.rgb(63, 81, 181))
                        .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                            @Override
                            public void onDisplayed(SingleDateAndTimePicker picker) {
                                //retrieve the SingleDateAndTimePicker
                            }
                        })
                        .title("Pick Your Date")
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {
                                date_time_text.setText(date.toLocaleString());
                                formated_date = dateFormat.format(date);
                                reminder_date_for_update=formated_date;
                                Log.e("formated date",formated_date);
                            }
                        }).display();
            }
        });
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (called_from_adapter.equalsIgnoreCase("shared")){
                    return;
                }
                else {
                    Intent intent=new Intent(AddReminderItemActivity.this, CategoryListActivity.class);
                    intent.putExtra("called_from","without_move");
                    startActivityForResult(intent,0);
                }
            }
        });
        brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddReminderItemActivity.this, BrandListActivity.class);
                startActivityForResult(intent,1);
            }
        });
        prefered_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddReminderItemActivity.this, StoreListActivity.class);
                startActivityForResult(intent,2);
            }
        });
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog dialog1 = new MaterialDialog.Builder(AddReminderItemActivity.this)
                        .title("Select ")
                        .customView(R.layout.indi_view_repeat_dialog, true)
                        .backgroundColor(getResources().getColor(R.color.list_bg_color))
                        .positiveText("ok")
                        .positiveColorRes(R.color.colorPrimary)
                        .negativeColorRes(R.color.colorPrimary)
                        .negativeText("cancel")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();

                            }
                        })
                        .show();
                View view = dialog1.getCustomView();
                if (view != null) {
                    ListView listView = (ListView) dialog1.getCustomView().findViewById(R.id.lv);
                  /*  ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<>(AddReminderItemActivity.this, android.R.layout.simple_list_item_1, repeatList);
                    locationArrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    listView.setAdapter(locationArrayAdapter);*/
                    RepeatListAdapter repeatListAdapter=new RepeatListAdapter(AddReminderItemActivity.this,repeatList);
                    listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    listView.setAdapter(repeatListAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Toast.makeText(getApplicationContext(), "you selected" + parent.getItemAtPosition(position), Toast.LENGTH_LONG).show();
                             repeat_text.setText(String.valueOf(parent.getItemAtPosition(position)));
                            if (String.valueOf(parent.getItemAtPosition(position)).equalsIgnoreCase("One Time"))
                            {
                                repeat_text_id=1;
                            }
                            else if (String.valueOf(parent.getItemAtPosition(position)).equalsIgnoreCase("Daily"))
                            {
                                repeat_text_id=2;
                            }
                            else if (String.valueOf(parent.getItemAtPosition(position)).equalsIgnoreCase("Weekly"))
                            {
                                repeat_text_id=3;
                            }
                            else if (String.valueOf(parent.getItemAtPosition(position)).equalsIgnoreCase("Every two Week"))
                            {
                                repeat_text_id=4;
                            }
                           else if (String.valueOf(parent.getItemAtPosition(position)).equalsIgnoreCase("Monthly"))
                            {
                                repeat_text_id=5;
                            }
                            else
                            {
                                repeat_text_id=6;
                            }
                          //  dialog1.dismiss();
                        }
                    });
                }
            }
        });
        none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priority=1;
                none.setBackgroundColor(Color.BLACK);
               /* none.setBackgroundColor(Color.WHITE);
                two.setBackgroundColor(Color.WHITE);
                three.setBackgroundColor(Color.WHITE);
                four.setBackgroundColor(Color.WHITE);*/
                none.setTextColor(Color.WHITE);
                one.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                two.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                three.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                four.setBackground(getResources().getDrawable(R.drawable.cell_shape));


            }
        });
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priority=1;
                none.setTextColor(Color.BLACK);
                one.setBackgroundColor(Color.BLACK);
               /* none.setBackgroundColor(Color.WHITE);
                two.setBackgroundColor(Color.WHITE);
                three.setBackgroundColor(Color.WHITE);
                four.setBackgroundColor(Color.WHITE);*/
                none.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                two.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                three.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                four.setBackground(getResources().getDrawable(R.drawable.cell_shape));


            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priority=2;
                none.setTextColor(Color.BLACK);
                two.setBackgroundColor(Color.BLACK);
             /*   none.setBackgroundColor(Color.WHITE);
                one.setBackgroundColor(Color.WHITE);
                three.setBackgroundColor(Color.WHITE);
                four.setBackgroundColor(Color.WHITE);*/
                none.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                one.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                three.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                four.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priority=3;
                none.setTextColor(Color.BLACK);
                three.setBackgroundColor(Color.BLACK);
           /*     none.setBackgroundColor(Color.WHITE);
                two.setBackgroundColor(Color.WHITE);
                one.setBackgroundColor(Color.WHITE);
                four.setBackgroundColor(Color.WHITE);*/
                none.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                two.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                one.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                four.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                priority=4;
                none.setTextColor(Color.BLACK);
                four.setBackgroundColor(Color.BLACK);
               /* none.setBackgroundColor(Color.WHITE);
                two.setBackgroundColor(Color.WHITE);
                three.setBackgroundColor(Color.WHITE);
                one.setBackgroundColor(Color.WHITE);*/
                none.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                two.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                three.setBackground(getResources().getDrawable(R.drawable.cell_shape));
                one.setBackground(getResources().getDrawable(R.drawable.cell_shape));
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent=new Intent(AddReminderItemActivity.this, LocationActivity.class);
                    startActivityForResult(intent,3);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_ok_all, menu);
        if (called_from.equalsIgnoreCase("add_today")||called_from.equalsIgnoreCase("add_category")){
            menu.findItem(R.id.add).setVisible(true);
            menu.findItem(R.id.update).setVisible(false);

        }
        if (called_from.equalsIgnoreCase("update_today")||called_from.equalsIgnoreCase("update_category")){
            menu.findItem(R.id.update).setVisible(true);
            menu.findItem(R.id.add).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                           break;
            }
            case R.id.add:{
                System.out.println("add");

/*
                System.out.println(catgeory_text.getText()+""+brand_text.getText()+""+prefered_store_text.getText()+""+quantity.getText().toString()+""+date_time_text.getText().toString()+""+
                repeat_text.getText().toString()+""+priority+""+description.getText().toString());*/
               if (called_from.equalsIgnoreCase("add_category"))
               {
                   int category_id=getIntent().getIntExtra("category_id",0);
                   new AddItem().execute(item_name,String.valueOf(category_id),String.valueOf(store_id),String.valueOf(brand_id),quantity.getText().toString(),formated_date,
                           String.valueOf(priority),description.getText().toString(),String.valueOf(repeat_text_id),String.valueOf(latitude),String.valueOf(longitude),String.valueOf(radiusInMeters),entry);
               }
               else {
                   new AddItem().execute(item_name,String.valueOf(category_id),String.valueOf(store_id),String.valueOf(brand_id),quantity.getText().toString(),formated_date,
                           String.valueOf(priority),description.getText().toString(),String.valueOf(repeat_text_id),String.valueOf(latitude),String.valueOf(longitude),String.valueOf(radiusInMeters),entry);
               }
                          break;
            }
            case R.id.update:{
                System.out.println("update");
/*
                System.out.println(catgeory_text.getText()+""+brand_text.getText()+""+prefered_store_text.getText()+""+quantity.getText().toString()+""+date_time_text.getText().toString()+""+
                repeat_text.getText().toString()+""+priority+""+description.getText().toString());*/
                if (called_from.equalsIgnoreCase("update_category"))
                {// int category_id=getIntent().getIntExtra("category_id",0);
                    new UpdateItem().execute(String.valueOf(item_id),item_name,String.valueOf(category_id),store_id,brand_id,quantity.getText().toString(),reminder_date_for_update,
                            String.valueOf(priority),description.getText().toString(),String.valueOf(repeat_text_id),String.valueOf(latitude),String.valueOf(longitude),String.valueOf(radiusInMeters));
                }
                else {
                    new UpdateItem().execute(String.valueOf(item_id),item_name,String.valueOf(category_id),store_id,brand_id,quantity.getText().toString(),reminder_date_for_update,
                            String.valueOf(priority),description.getText().toString(),String.valueOf(repeat_text_id),String.valueOf(latitude),String.valueOf(longitude),String.valueOf(radiusInMeters));
                }


                                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 0:
            {
                if (resultCode==Activity.RESULT_OK)
                {
                    String category_name=data.getStringExtra("category");
                    if (called_from.equalsIgnoreCase("update_today")||called_from.equalsIgnoreCase("update_category")){
                        category_id=data.getIntExtra("id",category_id);
                    }
                    else {
                        category_id=data.getIntExtra("id",preferences.getInt("id",0));
                    }
                    catgeory_text.setText(category_name);
                }
                break;
            }
            case 1:
            {
                if (resultCode==Activity.RESULT_OK)
                {
                    String brand_name=data.getStringExtra("brand");
                    brand_id=String.valueOf(data.getIntExtra("id",0));
                    brand_text.setText(brand_name);
                }
                break;
            }
            case 2:
            {
                if (resultCode==Activity.RESULT_OK)
                {
                    String store_name=data.getStringExtra("store");
                    store_id=String.valueOf(data.getIntExtra("id",0));
                    prefered_store_text.setText(store_name);
                }
                break;
            }
            case 3:
            {
                if (resultCode==Activity.RESULT_OK)
                {
                    latitude=data.getDoubleExtra("latitude",0.00);
                    longitude=data.getDoubleExtra("longitude",0.00);
                    radiusInMeters=data.getDoubleExtra("radius",0.00);
                    entry=data.getStringExtra("entry");
                }
                break;
            }
        }
    }

    private class AddItem extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddReminderItemActivity.this);
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
                jsonObject.put("name",params[0]);
                jsonObject.put("qty", params[4]);
                if (params[5].equalsIgnoreCase("null")){
                    jsonObject.put("date",JSONObject.NULL);
                }
                else {
                    jsonObject.put("date", params[5]);
                }
                jsonObject.put("list",params[1]);
                jsonObject.put("entry",params[12]);
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
                jsonObject.put("priority",params[6]);
                if (params[7].isEmpty()){
                    jsonObject.put("description",JSONObject.NULL);
                }
                else {
                    jsonObject.put("description", params[7]);
                }
                jsonObject.put("type", params[8]);
                if (params[9].equalsIgnoreCase("0.0"))
                {jsonObject.put("lat",JSONObject.NULL);}
                else {jsonObject.put("lat", params[9]);}
                if (params[10].equalsIgnoreCase("0.0"))
                { jsonObject.put("long",JSONObject.NULL);}
                else {jsonObject.put("long", params[10]);}
                if (params[11].equalsIgnoreCase("0.0"))
                {jsonObject.put("radius", JSONObject.NULL);}
                else {jsonObject.put("radius", params[11]);}

                System.out.println(jsonObject.toString());
                url = new URL(AppUrl.ITEM_LIST_URL);
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
                if (responseCode == HttpsURLConnection.HTTP_CREATED) {
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
                sendMessageToActivity(AddReminderItemActivity.this,"add");
                final Snackbar snackbar = Snackbar.make(layout, "Added Item Succesfully!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
                Intent myIntent = new Intent();
                setResult(Activity.RESULT_OK, myIntent);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 3000);
            } else {
                final Snackbar snackbar = Snackbar.make(layout, "Item not Added! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
    }


    private class UpdateItem extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        HttpURLConnection conn;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(AddReminderItemActivity.this);
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
                jsonObject.put("name",params[1]);
                jsonObject.put("qty", params[5]);
                //jsonObject.put("date", params[6]);
                jsonObject.put("list",params[2]);
                if (params[3].equalsIgnoreCase("null"))
                {
                    jsonObject.put("store",JSONObject.NULL);
                }
                else {
                    jsonObject.put("store", params[3]);
                }
                if (params[4].equalsIgnoreCase("null"))
                {
                    jsonObject.put("brand",JSONObject.NULL);
                }else {
                    jsonObject.put("brand", params[4]);
                }
               // Log.e("date in update","value"+params[6]);
                if (params[6]==null){
                    jsonObject.put("date",JSONObject.NULL);
                }
                else {
                    jsonObject.put("date", params[6]);
                }

                jsonObject.put("priority",params[7]);
                if (params[8].isEmpty()){
                    jsonObject.put("description", jsonObject.NULL);
                }
                else {
                    jsonObject.put("description", params[8]);

                }
                jsonObject.put("type", params[9]);
                if (params[10].equalsIgnoreCase("0.0"))
                {jsonObject.put("lat",JSONObject.NULL);}
                else {jsonObject.put("lat", params[10]);}
                if (params[11].equalsIgnoreCase("0.0"))
                { jsonObject.put("long",JSONObject.NULL);}
                else {jsonObject.put("long", params[11]);}
                if (params[12].equalsIgnoreCase("0.0"))
                {jsonObject.put("radius", JSONObject.NULL);}
                else {jsonObject.put("radius", params[12]);}
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
                final Snackbar snackbar = Snackbar.make(layout, "Item Updated Succesfully!", Snackbar.LENGTH_LONG);
                sendMessageToActivity(AddReminderItemActivity.this,"add");
                //sendMessageToFragment(AddReminderItemActivity.this,"add");
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
                Intent myIntent = new Intent();
                setResult(Activity.RESULT_OK, myIntent);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 3000);
            } else {
                final Snackbar snackbar = Snackbar.make(layout, "Item not updated! Try Again", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.YELLOW);
                snackbar.show();
            }
        }
    }
    private  void sendMessageToActivity(Context context, String msg) {
        Intent intent = new Intent("BeaconId");
        // You can also include some extra data.
        intent.putExtra("id", msg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    private  void sendMessageToFragment(Context context, String msg) {
        Intent intent = new Intent("BeaconId");
        // You can also include some extra data.
        intent.putExtra("id", msg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
