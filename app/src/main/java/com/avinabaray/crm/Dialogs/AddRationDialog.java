package com.avinabaray.crm.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avinabaray.crm.Adapters.AddRationItemsAdapter;
import com.avinabaray.crm.R;
import com.avinabaray.crm.Utils.CommonMethods;

import java.util.ArrayList;

public class AddRationDialog extends Dialog {

    Activity mActivity;
    ArrayList<String> itemNames = new ArrayList<>();
    ArrayList<String> itemUnits = new ArrayList<>();
    ArrayList<Long> itemQtys = new ArrayList<>();

    private ImageView imageViewCloseButton;
    private RecyclerView itemsToAddRecycler;
    private Button requestItemsBtn;
    private AlertDialog.Builder alertBuilder;
    CommonMethods commonMethods = new CommonMethods();

    public AddRationDialog(@NonNull Activity mActivity,
                           ArrayList<String> itemNames,
                           ArrayList<String> itemUnits) {
        super(mActivity);
        this.mActivity = mActivity;
        this.itemNames = itemNames;
        this.itemUnits = itemUnits;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ration_dialog);

        alertBuilder = new AlertDialog.Builder(mActivity);

        imageViewCloseButton = findViewById(R.id.imageViewCloseButton);
        itemsToAddRecycler = findViewById(R.id.itemsToAddRecycler);
        requestItemsBtn = findViewById(R.id.requestItemsBtn);

        for (int i=0; i<itemNames.size(); i++) {
            itemQtys.add(0L);
        }

        imageViewCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // Init Recycler View

        AddRationItemsAdapter addRationItemsAdapter = new AddRationItemsAdapter(mActivity, itemNames, itemUnits);

        addRationItemsAdapter.addOnItemQtyChangeListener(new AddRationItemsAdapter.ItemQtyAdapterCallback() {
            @Override
            public void setItemQuantity(int position, Long currItemQty) {
                itemQtys.set(position, currItemQty);
            }
        });

        itemsToAddRecycler.setAdapter(addRationItemsAdapter);
        itemsToAddRecycler.setLayoutManager(new LinearLayoutManager(mActivity));

        requestItemsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean allZero = true;
                for (Long l : itemQtys) {
                    if (l > 0) {
                        allZero = false;
                        break;
                    }
                }
                if (allZero) {
                    commonMethods.createAlert(alertBuilder, "All items can't be Zero");
                    return;
                }



            }
        });


    }
}
