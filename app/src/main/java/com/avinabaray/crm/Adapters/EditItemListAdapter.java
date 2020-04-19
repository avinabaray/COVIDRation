package com.avinabaray.crm.Adapters;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.avinabaray.crm.R;

import java.util.ArrayList;

public class EditItemListAdapter extends RecyclerView.Adapter<EditItemListAdapter.ViewHolder> {

    Activity mActivity;
    ArrayList<String> itemNames = new ArrayList<>();
    ArrayList<String> itemUnits = new ArrayList<>();
    private OnDataChangeListener onDataChangeListener;

    public EditItemListAdapter(Activity mActivity,
                               ArrayList<String> itemNames,
                               ArrayList<String> itemUnits) {
        this.mActivity = mActivity;
        this.itemNames = itemNames;
        this.itemUnits = itemUnits;
    }

    public void addOnDataChangeListener(OnDataChangeListener onDataChangeListener) {
        this.onDataChangeListener = onDataChangeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.edit_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.itemName.setText(itemNames.get(position));
        holder.itemUnit.setText(itemUnits.get(position));

        holder.itemName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty())
                    onDataChangeListener.setItemName(position, String.valueOf(s));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        holder.itemUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                onDataChangeListener.setItemUnit(position, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        holder.imageViewCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDataChangeListener.deleteItem(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemNames.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        EditText itemName, itemUnit;
        ImageView imageViewCloseButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemName = itemView.findViewById(R.id.itemName);
            itemUnit = itemView.findViewById(R.id.itemUnit);
            imageViewCloseButton = itemView.findViewById(R.id.imageViewCloseButton);
        }
    }

    public interface OnDataChangeListener {
        void setItemName(int position, String itemName);

        void setItemUnit(int position, String itemUnit);

        void deleteItem(int position);
    }
}
