package com.avinabaray.crm.Dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avinabaray.crm.Adapters.AddRationItemsAdapter;
import com.avinabaray.crm.MainActivity;
import com.avinabaray.crm.Models.RationRequestModel;
import com.avinabaray.crm.Models.UserModel;
import com.avinabaray.crm.R;
import com.avinabaray.crm.Utils.CommonMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AddRationDialog extends Dialog {

    Activity mActivity;
    ArrayList<String> itemNames = new ArrayList<>();
    ArrayList<String> itemUnits = new ArrayList<>();
    ArrayList<Double> itemQtys = new ArrayList<Double>();

    private ImageView imageViewCloseButton;
    private RecyclerView itemsToAddRecycler;
    private Button requestItemsBtn;
    private AlertDialog.Builder alertBuilder;
    private ViewGroup rootLayout;
    private UserModel currUserModel;
    CommonMethods commonMethods = new CommonMethods();
    private TextView forUserName;

    /**
     * @param mActivity
     * @param itemNames
     * @param itemUnits
     * @param rootLayout
     * @param currUserModel pass null if the user is requesting his own ration
     */
    public AddRationDialog(@NonNull Activity mActivity,
                           ArrayList<String> itemNames,
                           ArrayList<String> itemUnits,
                           ViewGroup rootLayout,
                           UserModel currUserModel) {
        super(mActivity);
        this.mActivity = mActivity;
        this.itemNames = itemNames;
        this.itemUnits = itemUnits;
        this.rootLayout = rootLayout;
        this.currUserModel = currUserModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_ration_dialog);

        alertBuilder = new AlertDialog.Builder(mActivity);

        imageViewCloseButton = findViewById(R.id.imageViewCloseButton);
        itemsToAddRecycler = findViewById(R.id.itemsToAddRecycler);
        requestItemsBtn = findViewById(R.id.requestItemsBtn);
        forUserName = findViewById(R.id.forUserName);

        if (currUserModel != null) {
            forUserName.setVisibility(View.VISIBLE);
            forUserName.setText("For " + currUserModel.getName());
        }

        for (int i=0; i<itemNames.size(); i++) {
            itemQtys.add(0.0);
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
            public void setItemQuantity(int position, Double currItemQty) {
                itemQtys.set(position, currItemQty);
            }
        });

        itemsToAddRecycler.setAdapter(addRationItemsAdapter);
        itemsToAddRecycler.setLayoutManager(new LinearLayoutManager(mActivity));

        requestItemsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currUserModel != null) {
                    Log.wtf("USER_NAME", currUserModel.getName());
                } else {
                    Log.wtf("USER_NAME", MainActivity.CURRENT_USER_MODEL.getName());
                }

                boolean allZero = true;
                for (Double l : itemQtys) {
                    if (l > 0) {
                        allZero = false;
                        break;
                    }
                }
                if (allZero) {
                    commonMethods.createAlert(alertBuilder, "All items can't be Zero");
                    return;
                }

                DocumentReference mDocRef = FirebaseFirestore.getInstance()
                        .collection("rationRequest")
                        .document();

                RationRequestModel newRationRequestModel = new RationRequestModel();

                newRationRequestModel.setId(mDocRef.getId());
                if (currUserModel != null) {
                    newRationRequestModel.setUserName(currUserModel.getName());
                    newRationRequestModel.setUserId(currUserModel.getId());
                    newRationRequestModel.setUserRole(currUserModel.getUserRole());
                    newRationRequestModel.setPinCode(currUserModel.getPinCode());
                } else {
                    newRationRequestModel.setUserName(MainActivity.CURRENT_USER_MODEL.getName());
                    newRationRequestModel.setUserId(MainActivity.CURRENT_USER_MODEL.getId());
                    newRationRequestModel.setUserRole(MainActivity.CURRENT_USER_MODEL.getUserRole());
                    newRationRequestModel.setPinCode(MainActivity.CURRENT_USER_MODEL.getPinCode());
                }
                newRationRequestModel.setItemNames(itemNames);
                newRationRequestModel.setItemUnits(itemUnits);
                newRationRequestModel.setItemQtys(itemQtys);
                newRationRequestModel.setRequestTime(Timestamp.now());
                commonMethods.loadingDialogStart(mActivity);
                mDocRef.set(newRationRequestModel)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                commonMethods.loadingDialogStop();
                                if (task.isSuccessful()) {
                                    commonMethods.makeSnack(rootLayout, "Request added Successfully");
                                    dismiss();
                                } else {
                                    commonMethods.makeSnack(rootLayout, mActivity.getString(R.string.oops_error));
                                    dismiss();
                                }
                            }
                        });



            }
        });


    }
}
