package com.project.spliceglobal.recallgo.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.project.spliceglobal.recallgo.R;
import com.project.spliceglobal.recallgo.model.Place;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;


public class ListStoreAdapter extends RecyclerView.Adapter<ListStoreAdapter.SimpleViewHolder> {
    private SimpleViewHolder svHolder;
    public ArrayList<Place> places;
    private Context mContext;

    public ListStoreAdapter(Context context, ArrayList<Place> places) {
        this.mContext = context;
        this.places = places;
    }

    @Override
    public ListStoreAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_shop, parent, false);
        return new SimpleViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {
        Place place = places.get(position);
        svHolder = holder;
        holder.name.setText(place.getName());
        holder.distance.setText(place.getDistance()+"km");
        holder.address.setText(place.getAddress());
        // holder.poster.setImageResource(movie.getImage_id());
        Picasso.with(mContext)
                .load(place.getIcon_url())
                .placeholder(R.drawable.user)
                .error(R.drawable.user)
                .into(holder.poster);
        //ViewCompat.setTransitionName(holder.poster, String.valueOf(position) + "_image");
    /*    holder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClicked(holder, position);
            }
        });*/
    }


    @Override
    public int getItemCount() {
        return places.size();
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name, address, distance;
        ImageView poster,map;
        SimpleViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.image);
            map = (ImageView) itemView.findViewById(R.id.map);
            name = (TextView) itemView.findViewById(R.id.name);
            address = (TextView) itemView.findViewById(R.id.address);
            distance = (TextView) itemView.findViewById(R.id.distance);

            map.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            double lat=places.get(getAdapterPosition()).getLatitude();
            double lng = places.get(getAdapterPosition()).getLongitude();
            String uri = String.format(Locale.ENGLISH, "geo:%f,%f",lat, lng);
            Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lng);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            mContext.startActivity(mapIntent);

        }
    }
}