package com.project.spliceglobal.recallgo.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.project.spliceglobal.recallgo.CategoryItemListActivity;
import com.project.spliceglobal.recallgo.CategoryListActivity;
import com.project.spliceglobal.recallgo.model.AllCategory;
import com.project.spliceglobal.recallgo.R;

import java.util.ArrayList;

/**
 * Created by Personal on 9/18/2017.
 */

public class AllCategoryAdapter extends RecyclerSwipeAdapter<AllCategoryAdapter.ViewHolder> {
    private Context context;
    private ArrayList<AllCategory> allCategoryArrayList;

    public AllCategoryAdapter(ArrayList<AllCategory> allCategoryArrayList) {
        this.allCategoryArrayList = allCategoryArrayList;
    }

    @Override
    public AllCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_row_all, parent, false);
        context=parent.getContext();
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        AllCategory category= allCategoryArrayList.get(position);
        viewHolder.category_name.setText(category.getCategory_name());
        viewHolder.completed.setText(category.getCompleted());
        viewHolder.uncompleted.setText(category.getUncompletd());
        String name = category.getCategory_name();
        String ch = String.valueOf(name.charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        //int color = generator.getRandomColor();
        TextDrawable.builder().beginConfig().fontSize(20).width(10).height(10).endConfig();
        //Color.rgb(97,107,192);
        TextDrawable drawable = TextDrawable.builder().buildRound(ch.toUpperCase(), Color.rgb(51,51,51));
        viewHolder.letter.setImageDrawable(drawable);
      //  viewHolder.swipeLayout.addDrag(SwipeLayout.DragEdge.Left, viewHolder.swipeLayout.findViewById(R.id.bottom_wrapper1));

        mItemManger.bindView(viewHolder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return allCategoryArrayList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView category_name,completed,uncompleted;
        ImageView letter;
        SwipeLayout swipeLayout;
        LinearLayout top_view;

        public ViewHolder(View itemView) {
            super(itemView);
            category_name = (TextView) itemView.findViewById(R.id.name);
            completed = (TextView) itemView.findViewById(R.id.completed);
            uncompleted = (TextView) itemView.findViewById(R.id.incomplete);
            letter = (ImageView) itemView.findViewById(R.id.letter);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            top_view=(LinearLayout)itemView.findViewById(R.id.top_view);
            top_view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int category_id = allCategoryArrayList.get(getAdapterPosition()).getId();
            Intent i = new Intent(context, CategoryItemListActivity.class);
            i.putExtra("category_id", category_id);
            i.putExtra("name",allCategoryArrayList.get(getAdapterPosition()).getCategory_name());
            i.putExtra("called_from_adapter","all");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
