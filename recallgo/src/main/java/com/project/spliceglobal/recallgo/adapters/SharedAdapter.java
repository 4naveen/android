package com.project.spliceglobal.recallgo.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.project.spliceglobal.recallgo.CategoryItemListActivity;
import com.project.spliceglobal.recallgo.R;
import com.project.spliceglobal.recallgo.model.Shared;

import java.util.ArrayList;

/**
 * Created by Personal on 9/15/2017.
 */

public class SharedAdapter extends RecyclerView.Adapter<SharedAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Shared> sharedArrayList;
    public SharedAdapter(Context context, ArrayList<Shared> sharedArrayList) {
        this.context = context;
        this.sharedArrayList = sharedArrayList;
    }
    @Override
    public SharedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_row_shared, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(SharedAdapter.ViewHolder holder, int position) {
        Shared shared= sharedArrayList.get(position);
        holder.category_name.setText(shared.getCategory_name().substring(0,1).toUpperCase()+shared.getCategory_name().substring(1));
        holder.sharedby.setText("Shared by : "+shared.getSharedby());
        holder.completed.setText(shared.getCompleted());
        holder.uncompleted.setText(shared.getUncompletd());
        holder.date.setText(shared.getDate());
        String name = shared.getCategory_name();
        String ch = String.valueOf(name.charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getRandomColor();
        Typeface tf2 = Typeface.createFromAsset(context.getAssets(), "fonts/roboto-bold.ttf");

        TextDrawable.builder().beginConfig().useFont(tf2).fontSize(20).width(10).height(10).endConfig();
        //Color.rgb(97,107,192);
        TextDrawable drawable = TextDrawable.builder().buildRound(ch.toUpperCase(),color);
        holder.letter.setImageDrawable(drawable);
    }
    @Override
    public int getItemCount() {
        return sharedArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView category_name,sharedby, date,completed,uncompleted;
        ImageView letter;
        LinearLayout top_view;
        public ViewHolder(View itemView) {
            super(itemView);
            category_name = (TextView) itemView.findViewById(R.id.name);
            sharedby = (TextView) itemView.findViewById(R.id.sharedby);
            completed = (TextView) itemView.findViewById(R.id.completed);
            uncompleted = (TextView) itemView.findViewById(R.id.incomplete);
            date = (TextView) itemView.findViewById(R.id.date);
            letter = (ImageView) itemView.findViewById(R.id.letter);
            top_view=(LinearLayout)itemView.findViewById(R.id.top_view);
            top_view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int category_id = sharedArrayList.get(getAdapterPosition()).getId();
            Intent i = new Intent(context, CategoryItemListActivity.class);
            i.putExtra("category_id", category_id);
            i.putExtra("name",sharedArrayList.get(getAdapterPosition()).getCategory_name());
            i.putExtra("called_from_adapter","shared");
            i.putExtra("called_from","without_move");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }
}
