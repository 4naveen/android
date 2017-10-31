package com.project.spliceglobal.recallgo.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.project.spliceglobal.recallgo.model.PriceChaser;
import com.project.spliceglobal.recallgo.R;
import com.project.spliceglobal.recallgo.SetTargetActivity;

import java.util.ArrayList;

/**
 * Created by Personal on 9/18/2017.
 */

public class PriceChaserAdapter extends RecyclerSwipeAdapter<PriceChaserAdapter.ViewHolder> {
    private Context context;
    private ArrayList<PriceChaser> priceChaserArrayList;

    public PriceChaserAdapter(Context context, ArrayList<PriceChaser> priceChaserArrayList) {
        this.context = context;
        this.priceChaserArrayList = priceChaserArrayList;
    }

    @Override
    public PriceChaserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_row_price_chaser, parent, false);
        return new ViewHolder(v);    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        PriceChaser priceChaser= priceChaserArrayList.get(position);
        viewHolder.product_name.setText(priceChaser.getItem_name());
        viewHolder.product_url.setText(priceChaser.getProduct_url());
        viewHolder.date.setText(priceChaser.getDate());
        viewHolder.target_price.setText(priceChaser.getTarget_price());
        viewHolder.original_price.setText(priceChaser.getOriginal_rice());
       // viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper2));
        viewHolder.layout_setPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,SetTargetActivity.class));
                mItemManger.closeAllItems();

            }
        });


        mItemManger.bindView(viewHolder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return priceChaserArrayList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView product_name,date, product_url,target_price,original_price;
        SwipeLayout swipeLayout;
        LinearLayout layout_remove, layout_move,layout_setPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            product_name = (TextView) itemView.findViewById(R.id.product_name);
            product_url = (TextView) itemView.findViewById(R.id.product_url);
            date = (TextView) itemView.findViewById(R.id.date);
            target_price = (TextView) itemView.findViewById(R.id.target_price);
            original_price= (TextView) itemView.findViewById(R.id.original_price);
            layout_remove=(LinearLayout)itemView.findViewById(R.id.layout_remove);
            layout_move=(LinearLayout)itemView.findViewById(R.id.layout_move);
            layout_setPrice=(LinearLayout)itemView.findViewById(R.id.layout_setPrice);

        }
    }
}
