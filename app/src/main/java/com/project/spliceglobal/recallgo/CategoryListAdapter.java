package com.project.spliceglobal.recallgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;



public class CategoryListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> categoryList;
    LayoutInflater layoutInflater;
    public CategoryListAdapter(Context context, ArrayList<String> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return categoryList.size();
    }
    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
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
            convertView = layoutInflater.inflate(R.layout.indi_view_category, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtName.setText(categoryList.get(position));
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


