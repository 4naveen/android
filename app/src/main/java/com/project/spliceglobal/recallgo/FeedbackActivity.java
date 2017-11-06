package com.project.spliceglobal.recallgo;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;

public class FeedbackActivity extends AppCompatActivity {
    Button feedback, refer;
    LinearLayout feedback_type;
    ArrayList<String> feedback_typeList;
    TextView feedback_type_text;
    SimpleRatingBar simpleRatingBar;
    EditText description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Feedback");
        }*/

        simpleRatingBar = (SimpleRatingBar)findViewById(R.id.rating);
        description = (EditText)findViewById(R.id.description);
        description.setText(" ");
        description.setSelection(1);
        feedback_type = (LinearLayout) findViewById(R.id.funtionality_feedback);
        feedback_type_text=(TextView)findViewById(R.id.feedback_type);
        feedback_typeList=new ArrayList<>();
        feedback_typeList.add("Feedback");
        feedback_typeList.add("Functionality Feedback");
        feedback_typeList.add("I want to share my idea");
        feedback_typeList.add("Some feature not working");
        feedback_typeList.add("General Feedback");

        feedback = (Button) findViewById(R.id.arriving);
        refer = (Button) findViewById(R.id.leaving);
        refer.setBackgroundColor(Color.rgb(0,0, 0));
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback.setBackgroundColor(Color.rgb(0, 0, 0));
                refer.setBackgroundColor(Color.WHITE);
                feedback.setTextColor(Color.WHITE);
                refer.setTextColor(Color.BLACK);

            }
        });
        refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refer.setBackgroundColor(Color.rgb(0, 0,0));
                feedback.setBackgroundColor(Color.WHITE);
                refer.setTextColor(Color.WHITE);
                feedback.setTextColor(Color.BLACK);
                Intent intentShare=new Intent(Intent.ACTION_SEND);
                intentShare.setType("text/plain");
                intentShare.putExtra(Intent.EXTRA_TEXT,"Hey I am using Recallgo to remind me for everything .you can dowmload it from play store");
                startActivity(Intent.createChooser(intentShare, "Select an action"));

            }
        });
        feedback_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final MaterialDialog dialog1 = new MaterialDialog.Builder(FeedbackActivity.this)
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
                    ArrayAdapter<String> locationArrayAdapter = new ArrayAdapter<>(FeedbackActivity.this, android.R.layout.simple_list_item_1, feedback_typeList);
                    locationArrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    listView.setAdapter(locationArrayAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //Toast.makeText(getApplicationContext(), "you selected" + parent.getItemAtPosition(position), Toast.LENGTH_LONG).show();
                            feedback_type_text.setText(String.valueOf(parent.getItemAtPosition(position)));
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
