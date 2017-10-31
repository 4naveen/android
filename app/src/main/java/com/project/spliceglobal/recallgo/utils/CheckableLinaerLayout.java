package com.project.spliceglobal.recallgo.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.project.spliceglobal.recallgo.R;

/**
 * Created by Personal on 9/25/2017.
 */

public class CheckableLinaerLayout extends LinearLayout implements Checkable{

    private boolean checked = false;

    private ImageView icon;
    public CheckableLinaerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckableLinaerLayout(Context context) {
        super(context);
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        icon = (ImageView)findViewById(R.id.ok); // optimisation - you don't need to search for image view every time you want to reference it
    }
    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        if (icon.getVisibility() == View.VISIBLE) {
            icon.setImageResource((checked) ? R.mipmap.ok : R.mipmap.ic_done_white_24dp);
        }
    }

    @Override
    public boolean isChecked() {
        return checked;
    }
    @Override
    public void toggle() {
        setChecked(!checked);
    }
}
