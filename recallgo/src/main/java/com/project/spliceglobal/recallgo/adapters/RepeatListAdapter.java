package com.project.spliceglobal.recallgo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.spliceglobal.recallgo.R;
import com.project.spliceglobal.recallgo.model.AllCategory;

import java.util.ArrayList;


public class RepeatListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> repeatList;
    LayoutInflater layoutInflater;
    public RepeatListAdapter(Context context, ArrayList<String> repeatList) {
        this.context = context;
        this.repeatList = repeatList;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return repeatList.size();
    }
    @Override
    public Object getItem(int position) {
        return repeatList.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        TextView txtName;
        ImageView imageViewOptions;
        private ViewHolder(View view)
        {
            txtName = (TextView) view.findViewById(R.id.category_name);
            imageViewOptions = (ImageView) view.findViewById(R.id.ok);
        }
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.indi_view_repeat, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtName.setText(repeatList.get(position));

      /*  convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i <=categoryList.size(); i++) {
                      if (i==position){
                          viewHolder.imageViewOptions.setVisibility(View.VISIBLE);
                      }
                      else {
                          viewHolder.imageViewOptions.setVisibility(View.GONE);
                      }

                }
            }

        });*/
       // notifyDataSetChanged();
        return convertView;
    }

}


