package com.avinabaray.crm.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avinabaray.crm.R;

import java.util.ArrayList;

public class ItemDisplayAdapter extends RecyclerView.Adapter<ItemDisplayAdapter.ViewHolder> {

    Activity mActivity;
    ArrayList<String> itemNames = new ArrayList<>();
    ArrayList<String> itemUnits = new ArrayList<>();
    ArrayList<Long> itemQtys = new ArrayList<>();

    public ItemDisplayAdapter(Activity mActivity,
                              ArrayList<String> itemNames,
                              ArrayList<String> itemUnits,
                              ArrayList<Long> itemQtys) {
        this.mActivity = mActivity;
        this.itemNames = itemNames;
        this.itemUnits = itemUnits;
        this.itemQtys = itemQtys;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemName.setText(itemNames.get(position));
        holder.itemQty.setText(String.valueOf(itemQtys.get(position)));
        holder.itemUnit.setText(itemUnits.get(position));

    }

    @Override
    public int getItemCount() {
        return itemNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName, itemQty, itemUnit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.itemName);
            itemQty = itemView.findViewById(R.id.itemQty);
            itemUnit = itemView.findViewById(R.id.itemUnit);
        }
    }
}
