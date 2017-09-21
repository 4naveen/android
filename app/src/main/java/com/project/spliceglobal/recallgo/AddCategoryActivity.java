package com.project.spliceglobal.recallgo;

import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

public class AddCategoryActivity extends AppCompatActivity {
    Button color_picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Add your own Category");
        }
        color_picker = (Button) findViewById(R.id.color_picker);
        color_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ColorPicker cp = new ColorPicker(AddCategoryActivity.this, 0, 255, 255);
                cp.show();

    /* Set a new Listener called when user click "select" */
                cp.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(@ColorInt int color) {
                        // Do whatever you want
                        // Examples
                        cp.dismiss();
                        Log.d("Alpha", Integer.toString(Color.alpha(color)));
                        Log.d("Red", Integer.toString(Color.red(color)));
                        Log.d("Green", Integer.toString(Color.green(color)));
                        Log.d("Blue", Integer.toString(Color.blue(color)));
                        Log.d("Pure Hex", Integer.toHexString(color));
                        Log.d("#Hex no alpha", String.format("#%06X", (0xFFFFFF & color)));
                        Log.d("#Hex with alpha", String.format("#%08X", (0xFFFFFFFF & color)));
                    }
                });
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
