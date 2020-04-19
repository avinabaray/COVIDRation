package com.avinabaray.crm.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avinabaray.crm.Models.RationRequestModel;
import com.avinabaray.crm.Models.UserModel;
import com.avinabaray.crm.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class RationDisplayAdapter extends RecyclerView.Adapter<RationDisplayAdapter.ViewHolder> {

    private static final String TAG = "RtnDispAdpt";
    private Activity mActivity;
    private ArrayList<RationRequestModel> rationRequestModels;

    public RationDisplayAdapter(Activity mActivity, ArrayList<RationRequestModel> rationRequestModels) {
        this.mActivity = mActivity;
        this.rationRequestModels = rationRequestModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ration_display_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        String currUserId = rationRequestModels.get(position).getUserId();

        holder.name.setText(rationRequestModels.get(position).getUserName());
        holder.pinCode.setText(String.valueOf(rationRequestModels.get(position).getPinCode()));

//        Toast.makeText(mActivity, rationRequestModels.get(position).getRequestTime().toString(), Toast.LENGTH_SHORT).show();

        ItemDisplayAdapter itemDisplayAdapter = new ItemDisplayAdapter(mActivity,
                rationRequestModels.get(position).getItemNames(),
                rationRequestModels.get(position).getItemUnits(),
                rationRequestModels.get(position).getItemQtys());

        holder.rationItemsRecy.setAdapter(itemDisplayAdapter);
        holder.rationItemsRecy.setLayoutManager(new LinearLayoutManager(mActivity));

        holder.approveBtn.setEnabled(true);
        holder.deliveredBtn.setEnabled(true);
        holder.rejectBtn.setEnabled(true);

        if (rationRequestModels.get(position).getRequestStatus().equals(RationRequestModel.APPROVED)) {
            holder.approveBtn.setEnabled(false);
        } else if (rationRequestModels.get(position).getRequestStatus().equals(RationRequestModel.DELIVERED)) {
            holder.deliveredBtn.setEnabled(false);
        } else if (rationRequestModels.get(position).getRequestStatus().equals(RationRequestModel.REJECTED)) {
            holder.rejectBtn.setEnabled(false);
        }

        final RationRequestModel toAddRationModel = rationRequestModels.get(position);
        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAddRationModel.setRequestStatus(RationRequestModel.REJECTED);
                toAddRationModel.setResponseTime(Timestamp.now());
                updateRationModel(toAddRationModel);
            }
        });

        holder.approveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAddRationModel.setRequestStatus(RationRequestModel.APPROVED);
                toAddRationModel.setResponseTime(Timestamp.now());
                updateRationModel(toAddRationModel);
            }
        });

        holder.deliveredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toAddRationModel.setRequestStatus(RationRequestModel.DELIVERED);
                toAddRationModel.setResponseTime(Timestamp.now());
                updateRationModel(toAddRationModel);
            }
        });

        FirebaseFirestore.getInstance().collection("users")
                .document(currUserId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null) {
                            UserModel currUserModel = new UserModel();
                            currUserModel = documentSnapshot.toObject(UserModel.class);

                            assert currUserModel != null;
                            holder.phone.setText(currUserModel.getPhone());
                            holder.address.setText(currUserModel.getAddress());
                            holder.occupation.setText(currUserModel.getOccupation());
                            holder.monthlyIncome.setText("₹ " + String.valueOf(currUserModel.getMonthlyIncome()) + "/month");
                            holder.adults.setText(String.valueOf(currUserModel.getAdultMembers()));
                            holder.children.setText(String.valueOf(currUserModel.getChildMembers()));
                            holder.earningMembers.setText(String.valueOf(currUserModel.getEarningMembers()));
                        }
                        Log.e(TAG, "onEvent: ", e);
                    }
                });


    }

//    private void setEnableButton(MaterialButton approveBtn, boolean setEnabled) {
//        if (setEnabled) {
            // set dynamic color here
//        }
//    }

    private void updateRationModel(RationRequestModel toAddRationModel) {
        FirebaseFirestore.getInstance()
                .collection("rationRequest")
                .document(toAddRationModel.getId())
                .set(toAddRationModel);
    }

    @Override
    public int getItemCount() {
        return rationRequestModels.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, phone, address, occupation, pinCode, monthlyIncome, adults, children, earningMembers;
        RecyclerView rationItemsRecy;
        MaterialButton rejectBtn, approveBtn, deliveredBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            address = itemView.findViewById(R.id.address);
            occupation = itemView.findViewById(R.id.occupation);
            pinCode = itemView.findViewById(R.id.pinCode);
            monthlyIncome = itemView.findViewById(R.id.monthlyIncome);
            adults = itemView.findViewById(R.id.adults);
            children = itemView.findViewById(R.id.children);
            earningMembers = itemView.findViewById(R.id.earningMembers);
            rationItemsRecy = itemView.findViewById(R.id.rationItemsRecy);
            rejectBtn = itemView.findViewById(R.id.rejectBtn);
            approveBtn = itemView.findViewById(R.id.approveBtn);
            deliveredBtn = itemView.findViewById(R.id.deliveredBtn);


        }
    }

}
