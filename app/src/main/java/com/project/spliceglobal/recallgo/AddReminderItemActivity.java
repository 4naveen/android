package com.project.spliceglobal.recallgo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;
import java.util.Date;

public class AddReminderItemActivity extends AppCompatActivity {
    LinearLayout notes_layout, category, brand, prefered_store, repeat;
    EditText description;
    ImageView date_time;
    Switch aSwitch;
    ArrayList<String> repeatList;
    TextView catgeory_text,brand_text,prefered_store_text,repeat_text,date_time_text,none,one,two,three,four;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        catgeory_text=(TextView)findViewById(R.id.category_text);
        brand_text=(TextView)findViewById(R.id.brand_text);
        prefered_store_text=(TextView)findViewById(R.id.prefered_store_text);
        repeat_text=(TextView)findViewById(R.id.repeat_text);
        date_time_text=(TextView)findViewById(R.id.date_time_text);
        none=(TextView)findViewById(R.id.none);
        one=(TextView)findViewById(R.id.one);
        two=(TextView)findViewById(R.id.two);
        three=(TextView)findViewById(R.id.three);
        four=(TextView)findViewById(R.id.four);

        notes_layout = (LinearLayout) findViewById(R.id.notes);
        date_time = (ImageView) findViewById(R.id.date_time);
        category = (LinearLayout) findViewById(R.id.category);
        brand = (LinearLayout) findViewById(R.id.brand);
        prefered_store = (LinearLayout) findViewById(R.id.prefered_store);
        repeat = (LinearLayout) findViewById(R.id.repeat);
        repeatList = new ArrayList<>();
        repeatList.add("One Time");
        repeatList.add("Daily");
        repeatList.add("Weekly");
        repeatList.add("Every two week");
        repeatList.add("Monthly");
        repeatList.add("yearly");
        aSwitch = (Switch) findViewById(R.id.simpleSwitch);

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getIntent().getStringExtra("name"));
        }

        notes_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog1 = new MaterialDialog.Builder(AddReminderItemActivity.this)
                        .title("Add note")
                        .customView(R.layout.add_notes_dialog, true)
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
                    description = (EditText) dialog1.getCustomView().findViewById(R.id.description);
                    description.setText(" ");
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
        });
        date_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SingleDateAndTimePickerDialog.Builder(AddReminderItemActivity.this)
                        //.bottomSheet()
                        //.curved()
                        //.minutesStep(15)
                        .mainColor(Color.rgb(63, 81, 181))
                        .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                            @Override
                            public void onDisplayed(SingleDateAndTimePicker picker) {
                                //retrieve the SingleDateAndTimePicker
                            }
                        })

                        .title("Pick your date")
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {

                             date_time_text.setText(date.toLocaleString());
                            }
                        }).display();
            }
        });
        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddReminderItemActivity.this, CategoryListActivity.class);
                startActivityForResult(intent,0);

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
                        .positiveText("")
                        .positiveColorRes(R.color.colorPrimary)
                        .negativeColorRes(R.color.colorPrimary)
                        .negativeText("")
                        .show();
                View view = dialog1.getCustomView();
                if (view != null) {
                    ListView listView = (ListView) dialog1.getCustomView().findViewById(R.id.lv);

                    ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<>(AddReminderItemActivity.this, android.R.layout.simple_list_item_1, repeatList);
                    locationArrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    listView.setAdapter(locationArrayAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Toast.makeText(getApplicationContext(), "you selected" + parent.getItemAtPosition(position), Toast.LENGTH_LONG).show();
                             repeat_text.setText(String.valueOf(parent.getItemAtPosition(position)));
                            dialog1.dismiss();
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
        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             one.setBackgroundColor(Color.BLACK);
                none.setBackgroundColor(Color.WHITE);
                two.setBackgroundColor(Color.WHITE);
                three.setBackgroundColor(Color.WHITE);
                four.setBackgroundColor(Color.WHITE);

            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                two.setBackgroundColor(Color.BLACK);
                none.setBackgroundColor(Color.WHITE);
                one.setBackgroundColor(Color.WHITE);
                three.setBackgroundColor(Color.WHITE);
                four.setBackgroundColor(Color.WHITE);
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                three.setBackgroundColor(Color.BLACK);
                none.setBackgroundColor(Color.WHITE);
                two.setBackgroundColor(Color.WHITE);
                one.setBackgroundColor(Color.WHITE);
                four.setBackgroundColor(Color.WHITE);
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                four.setBackgroundColor(Color.BLACK);
                none.setBackgroundColor(Color.WHITE);
                two.setBackgroundColor(Color.WHITE);
                three.setBackgroundColor(Color.WHITE);
                one.setBackgroundColor(Color.WHITE);
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startActivity(new Intent(AddReminderItemActivity.this, LocationActivity.class));
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
        return super.onCreateOptionsMenu(menu);

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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
            {
                if (resultCode==Activity.RESULT_OK)
                {
                    String category_name=data.getStringExtra("category");
                    catgeory_text.setText(category_name);
                }
                break;
            }

            case 1:
            {
                if (resultCode==Activity.RESULT_OK)
                {
                    String brand_name=data.getStringExtra("brand");
                    brand_text.setText(brand_name);
                }
                break;
            }
            case 2:
            {
                if (resultCode==Activity.RESULT_OK)
                {
                    String store_name=data.getStringExtra("store");
                    prefered_store_text.setText(store_name);
                }
                break;
            }
        }
    }
}
