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
import com.project.spliceglobal.recallgo.model.Site;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewStoreAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Site> siteArrayList;
    LayoutInflater layoutInflater;
    public ViewStoreAdapter(Context context, ArrayList<Site> siteArrayList) {
        this.context = context;
        this.siteArrayList = siteArrayList;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return siteArrayList.size();
    }
    @Override
    public Object getItem(int position) {
        return siteArrayList.get(position);
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
            txtName = (TextView) view.findViewById(R.id.store_name);
            imageViewOptions = (ImageView) view.findViewById(R.id.icon);
        }
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.indi_view_supported_store, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.txtName.setText(siteArrayList.get(position).getSite_name());
        Picasso.with(context)
                .load(siteArrayList.get(position).getImage_url())
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(viewHolder.imageViewOptions);
        return convertView;
    }
}


