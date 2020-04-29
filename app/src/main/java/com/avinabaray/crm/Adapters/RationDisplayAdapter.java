package com.avinabaray.crm.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.avinabaray.crm.MainActivity;
import com.avinabaray.crm.Models.RationRequestModel;
import com.avinabaray.crm.Models.UserModel;
import com.avinabaray.crm.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

public class RationDisplayAdapter extends RecyclerView.Adapter<RationDisplayAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "RtnDispAdpt";
    private Activity mActivity;
    private ArrayList<RationRequestModel> rationRequestModels;
    private ArrayList<RationRequestModel> rationRequestModelsFull;
    AlertDialog.Builder alertDialogBuilder;

    public RationDisplayAdapter(Activity mActivity, ArrayList<RationRequestModel> rationRequestModels) {
        this.mActivity = mActivity;
        this.rationRequestModels = rationRequestModels;
        rationRequestModelsFull = new ArrayList<>(rationRequestModels);
        alertDialogBuilder = new AlertDialog.Builder(mActivity);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ration_display_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
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

        holder.notesLinLay.setVisibility(View.GONE);
        if (rationRequestModels.get(position).getApprovedBy() != null) {
            holder.notesLinLay.setVisibility(View.VISIBLE);
            holder.approvedTextView.setVisibility(View.VISIBLE);
            holder.approvedTextView.setText("Approved by " +
                    rationRequestModels.get(position).getApprovedBy() +
                    " on " +
                    getFormattedDateTime(rationRequestModels.get(position).getApproveTime())
            );
        } else {
            holder.approvedTextView.setVisibility(View.GONE);
        }

        if (rationRequestModels.get(position).getDeliveredBy() != null) {
            holder.notesLinLay.setVisibility(View.VISIBLE);
            holder.deliveredTextView.setVisibility(View.VISIBLE);
            holder.deliveredTextView.setText("Issued by " +
                    rationRequestModels.get(position).getDeliveredBy() +
                    " on " +
                    getFormattedDateTime(rationRequestModels.get(position).getDeliverTime())
            );
        } else {
            holder.deliveredTextView.setVisibility(View.GONE);
        }

        if (rationRequestModels.get(position).getRejectedBy() != null) {
            holder.notesLinLay.setVisibility(View.VISIBLE);
            holder.rejectedTextView.setVisibility(View.VISIBLE);
            holder.rejectedTextView.setText("Rejected by " +
                    rationRequestModels.get(position).getRejectedBy() +
                    " on " +
                    getFormattedDateTime(rationRequestModels.get(position).getRejectTime()));
        } else {
            holder.rejectedTextView.setVisibility(View.GONE);
        }

        if (rationRequestModels.get(position).getRequestStatus().equals(RationRequestModel.APPROVED)) {
            holder.approveBtn.setEnabled(false);
        } else if (rationRequestModels.get(position).getRequestStatus().equals(RationRequestModel.DELIVERED)) {
            holder.approveBtn.setEnabled(false);
            holder.deliveredBtn.setEnabled(false);
            holder.rejectBtn.setEnabled(false);
        } else if (rationRequestModels.get(position).getRequestStatus().equals(RationRequestModel.REJECTED)) {
            holder.approveBtn.setEnabled(false);
            holder.deliveredBtn.setEnabled(false);
            holder.rejectBtn.setEnabled(false);
        }

        holder.requestDateTextView.setText(getFormattedDate(rationRequestModels.get(position).getRequestTime()));
        holder.requestTimeTextView.setText(getFormattedTime(rationRequestModels.get(position).getRequestTime()));

        final RationRequestModel toAddRationModel = rationRequestModels.get(position);
        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogBuilder.setTitle("Reject Request")
                        .setMessage("Are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("           NO           ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).setNeutralButton("           YES           ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toAddRationModel.setRequestStatus(RationRequestModel.REJECTED);
                        toAddRationModel.setResponseTime(Timestamp.now());
                        toAddRationModel.setRejectTime(Timestamp.now());
                        toAddRationModel.setRejectedBy(MainActivity.CURRENT_USER_MODEL.getName());
                        updateRationModel(toAddRationModel);
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        holder.approveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogBuilder.setTitle("Approve Request")
                        .setMessage("Are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("           NO           ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                            }
                        }).setNeutralButton("           YES           ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toAddRationModel.setRequestStatus(RationRequestModel.APPROVED);
                        toAddRationModel.setResponseTime(Timestamp.now());
                        toAddRationModel.setApproveTime(Timestamp.now());
                        toAddRationModel.setApprovedBy(MainActivity.CURRENT_USER_MODEL.getName());
                        updateRationModel(toAddRationModel);
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });

        holder.deliveredBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogBuilder.setTitle("Issue Request")
                        .setMessage("Are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("           NO           ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                            }
                        }).setNeutralButton("           YES           ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        toAddRationModel.setRequestStatus(RationRequestModel.DELIVERED);
                        toAddRationModel.setResponseTime(Timestamp.now());
                        toAddRationModel.setDeliverTime(Timestamp.now());
                        toAddRationModel.setDeliveredBy(MainActivity.CURRENT_USER_MODEL.getName());
                        updateRationModel(toAddRationModel);
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        Log.wtf("USER_ID", currUserId);
        FirebaseFirestore.getInstance().collection("users")
                .document(currUserId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null) {
                            UserModel currUserModel = new UserModel();
                            currUserModel = documentSnapshot.toObject(UserModel.class);

                            assert currUserModel != null;
                            holder.phone.setText(currUserModel.getPhone().substring(3, currUserModel.getPhone().length()));
                            final UserModel finalCurrUserModel = currUserModel;
                            holder.phone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                    callIntent.setData(Uri.parse("tel:" + finalCurrUserModel.getPhone()));
                                    mActivity.startActivity(callIntent);
                                }
                            });
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

    /**
     * Formats the Firebase Timestamp object in a readable format
     *
     * @param timestamp Firebase Timestamp Object
     * @return String in DD-MM-YYYY, HH:MM(AM/PM) format
     */
    static String getFormattedDateTime(Timestamp timestamp) {
        String dateTimeString = "";
        String timeSuffix;
        if (timestamp.toDate().getDate() < 10) {
            dateTimeString += "0" + timestamp.toDate().getDate() + "-";
        } else {
            dateTimeString += timestamp.toDate().getDate() + "-";
        }
        if (timestamp.toDate().getMonth() < 10) {
            dateTimeString += "0" + (timestamp.toDate().getMonth() + 1) + "-";
        } else {
            dateTimeString += (timestamp.toDate().getMonth() + 1) + "-";
        }
        dateTimeString += String.valueOf(timestamp.toDate().getYear() + 1900) + ", ";
        if (timestamp.toDate().getHours() > 12) {
            dateTimeString += timestamp.toDate().getHours() - 12 + ":";
            timeSuffix = "PM";
        } else {
            dateTimeString += timestamp.toDate().getHours() + ":";
            timeSuffix = "AM";
        }
        if (timestamp.toDate().getMinutes() < 10) {
            dateTimeString += "0" + timestamp.toDate().getMinutes();
        } else {
            dateTimeString += timestamp.toDate().getMinutes();
        }
        dateTimeString += timeSuffix;

        return dateTimeString;
    }

    private String getFormattedDate(Timestamp timestamp) {
        String dateString = "";
        if (timestamp.toDate().getDate() < 10) {
            dateString += "0" + timestamp.toDate().getDate() + "-";
        } else {
            dateString += timestamp.toDate().getDate() + "-";
        }
        if (timestamp.toDate().getMonth() < 10) {
            dateString += "0" + (timestamp.toDate().getMonth() + 1) + "-";
        } else {
            dateString += (timestamp.toDate().getMonth() + 1) + "-";
        }
        dateString += String.valueOf(timestamp.toDate().getYear() + 1900);

        return dateString;
    }

    private String getFormattedTime(Timestamp timestamp) {
        String timeString = "";
        String timeSuffix;

        if (timestamp.toDate().getHours() > 12) {
            timeString += timestamp.toDate().getHours() - 12 + ":";
            timeSuffix = "PM";
        } else {
            timeString += timestamp.toDate().getHours() + ":";
            timeSuffix = "AM";
        }
        if (timestamp.toDate().getMinutes() < 10) {
            timeString += "0" + timestamp.toDate().getMinutes();
        } else {
            timeString += timestamp.toDate().getMinutes();
        }
        timeString += timeSuffix;

        return timeString;
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

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<RationRequestModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(rationRequestModelsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (RationRequestModel item : rationRequestModelsFull) {
                    if (String.valueOf(item.getPinCode()).toLowerCase().startsWith(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            rationRequestModels.clear();
            rationRequestModels.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, phone, address, occupation, pinCode, monthlyIncome, adults, children, earningMembers;
        TextView approvedTextView, deliveredTextView, rejectedTextView, requestDateTextView, requestTimeTextView;
        RecyclerView rationItemsRecy;
        MaterialButton rejectBtn, approveBtn, deliveredBtn;
        LinearLayout notesLinLay;

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
            approvedTextView = itemView.findViewById(R.id.approvedTextView);
            deliveredTextView = itemView.findViewById(R.id.deliveredTextView);
            rejectedTextView = itemView.findViewById(R.id.rejectedTextView);
            notesLinLay = itemView.findViewById(R.id.notesLinLay);
            requestDateTextView = itemView.findViewById(R.id.requestDateTextView);
            requestTimeTextView = itemView.findViewById(R.id.requestTimeTextView);


        }
    }

}
