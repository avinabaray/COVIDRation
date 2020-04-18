package com.avinabaray.crm.Adapters;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avinabaray.crm.R;

import java.util.ArrayList;

public class AddRationItemsAdapter extends RecyclerView.Adapter<AddRationItemsAdapter.ViewHolder>{

    Activity mActivity;
    ArrayList<String> itemNames = new ArrayList<>();
    ArrayList<String> itemUnits = new ArrayList<>();

    private ItemQtyAdapterCallback itemQtyAdapterCallbackListener;

    public void addOnItemQtyChangeListener(ItemQtyAdapterCallback callback) {
        itemQtyAdapterCallbackListener = callback;
    }

    public AddRationItemsAdapter(Activity mActivity,
                                 ArrayList<String> itemNames,
                                 ArrayList<String> itemUnits) {
        this.mActivity = mActivity;
        this.itemNames = itemNames;
        this.itemUnits = itemUnits;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_ration_adapter_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.itemName.setText(itemNames.get(position));
        holder.itemUnit.setText(itemUnits.get(position));

        holder.itemQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Long currItemQty;
                try {
                    currItemQty = Long.valueOf(String.valueOf(s));
                } catch (NumberFormatException e) {
                    currItemQty = 0L;
                }
                itemQtyAdapterCallbackListener.setItemQuantity(position, currItemQty);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return itemNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName, itemUnit;
        EditText itemQty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.itemName);
            itemUnit = itemView.findViewById(R.id.itemUnit);
            itemQty = itemView.findViewById(R.id.itemQty);


        }
    }

    public interface ItemQtyAdapterCallback {
        void setItemQuantity(int position, Long currItemQty);
    }

}
