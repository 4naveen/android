package com.project.spliceglobal.recallgo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.project.spliceglobal.recallgo.R;

/**
 * Created by Personal on 12/20/2017.
 */

public class HomeGridAdapter extends BaseAdapter {
    private Context mContext;
    private final String[] text=new String[]{"Reminder","Price Chaser","Tutorial","Feedback"};
    private final int[] Imageid=new int[]{R.drawable.reminder,R.drawable.price_chaser,R.drawable.tutorial,R.drawable.feedback};
    private final int[] colorid=new int[]{R.color.dashboad_bg1,R.color.dashboad_bg2,R.color.dashboad_bg3,R.color.dashboad_bg4};


    public HomeGridAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return text.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_single, null);
            TextView textView = (TextView) grid.findViewById(R.id.text);
            ImageView imageView = (ImageView)grid.findViewById(R.id.image);
            LinearLayout linearLayout=(LinearLayout)grid.findViewById(R.id.layout);
           // linearLayout.setBackgroundColor(colorid[position]);
            linearLayout.setBackground(mContext.getResources().getDrawable(colorid[position]));
            textView.setText(text[position]);
            imageView.setImageResource(Imageid[position]);
        } else {
            grid = (View) convertView;
        }

        return grid;    }
}
