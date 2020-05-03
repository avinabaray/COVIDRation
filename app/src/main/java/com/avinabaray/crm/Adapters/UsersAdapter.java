package com.avinabaray.crm.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.avinabaray.crm.Dialogs.AddRationDialog;
import com.avinabaray.crm.Dialogs.UserDetailsDialog;
import com.avinabaray.crm.Models.RationRequestModel;
import com.avinabaray.crm.Models.UserModel;
import com.avinabaray.crm.R;
import com.avinabaray.crm.Utils.CommonMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.avinabaray.crm.Adapters.RationDisplayAdapter.getFormattedDateTime;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "UserAdpt";
    private Activity mActivity;
    private ArrayList<UserModel> userModels = new ArrayList<>();
    private ArrayList<UserModel> userModelsFull;
    private int[] noOfDeliveries;
    private int[] noOfDeliveriesFull;
    private ViewGroup rootLayout;
    private ArrayList<String> userRoles;
    private ArrayAdapter<String> roleSpinnerAdapter;

    public UsersAdapter(Activity mActivity,
                        ArrayList<UserModel> userModels,
                        ArrayList<String> userRoles,
                        int[] noOfDeliveries,
                        ViewGroup rootLayout) {
        this.mActivity = mActivity;
        this.userModels = userModels;
        this.userRoles = userRoles;
        this.noOfDeliveries = noOfDeliveries;
        this.rootLayout = rootLayout;
        userModelsFull = new ArrayList<>(userModels);
        noOfDeliveriesFull = noOfDeliveries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_item, parent, false);
        ViewHolder holder = new ViewHolder(view);


        roleSpinnerAdapter = new ArrayAdapter<String>(
                mActivity,
                android.R.layout.simple_spinner_dropdown_item,
                userRoles
        );

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final CommonMethods commonMethods = new CommonMethods();

        holder.name.setText(userModels.get(position).getName());
        holder.phone.setText(userModels.get(position).getPhone().substring(3, userModels.get(position).getPhone().length()));
        final int finalPos = position;

        holder.infoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDetailsDialog userDetailsDialog = new UserDetailsDialog(mActivity, userModels.get(position));
                userDetailsDialog.setCanceledOnTouchOutside(false);
                userDetailsDialog.show();
            }
        });

        if (!userModels.get(position).getUserRole().equals("user")) {
            holder.lastIssuedLinLay.setVisibility(View.GONE);
        } else {
            holder.lastIssuedLinLay.setVisibility(View.VISIBLE);
        }
        holder.lastDelivered.setText("No Requests yet");

        holder.lastDelivered.setTextColor(mActivity.getResources().getColor(R.color.black));
        if (userModels.get(position).getUserRole().equals("user")) {
            final int[] currNoOfDeliveries = {0};
            FirebaseFirestore.getInstance()
                    .collection("rationRequest")
                    .whereEqualTo("userId", userModels.get(position).getId())
                    .addSnapshotListener(mActivity, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if (documentSnapshot.get("deliverTime") != null) {
                                    currNoOfDeliveries[0]++;
                                }
                            }
                            holder.noOfIssues.setVisibility(View.VISIBLE);
                            if (currNoOfDeliveries[0] > 0) {
                                holder.noOfIssues.setTextColor(mActivity.getResources().getColor(R.color.delivered_green));
                                holder.noOfIssues.setText("Delivered " + currNoOfDeliveries[0] + " time(s)");
                            } else {
                                holder.noOfIssues.setTextColor(mActivity.getResources().getColor(R.color.reject_red));
                                holder.noOfIssues.setText("No deliveries yet ");
                            }                    }
                    });
        } else {
            holder.noOfIssues.setVisibility(View.GONE);
        }

        FirebaseFirestore.getInstance()
                .collection("rationRequest")
                .whereEqualTo("userId", userModels.get(position).getId())
                .orderBy("deliverTime", Query.Direction.DESCENDING).limit(1L)
                .addSnapshotListener(mActivity, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                        Toast.makeText(mActivity, " " + position, Toast.LENGTH_SHORT).show();
                        if (queryDocumentSnapshots != null) {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if (documentSnapshot.toObject(RationRequestModel.class).getDeliverTime() != null) {
                                    holder.lastDelivered.setText(getFormattedDateTime(documentSnapshot.toObject(RationRequestModel.class).getDeliverTime()));
                                    holder.lastDelivered.setTextColor(mActivity.getResources().getColor(R.color.delivered_green));
                                } else {
                                    holder.lastDelivered.setText("Not yet Issued");
                                    holder.lastDelivered.setTextColor(mActivity.getResources().getColor(R.color.reject_red));
                                }
                            }
                        } else {
                            holder.lastDelivered.setText("No Requests yet");
                            holder.lastDelivered.setTextColor(mActivity.getResources().getColor(R.color.black));
                        }
                    }
                });

        holder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mActivity, "" + position + " " + noOfDeliveries[position], Toast.LENGTH_SHORT).show();
//                Intent callIntent = new Intent(Intent.ACTION_DIAL);
//                callIntent.setData(Uri.parse("tel:" + userModels.get(finalPos).getPhone()));
//                mActivity.startActivity(callIntent);
            }
        });
        holder.userRoleSpinner.setAdapter(roleSpinnerAdapter);
        final boolean[] spinnerFirstCall = {true};

        final UserModel toAddUserModel = userModels.get(position);

        switch (userModels.get(position).getUserRole()) {
            case "user":
                holder.userRoleSpinner.setSelection(0);
                break;
            case "admin":
                holder.userRoleSpinner.setSelection(1);
                break;
            case "superAdmin":
                holder.userRoleSpinner.setSelection(2);
                break;
        }

        if (userModels.get(position).getUserRole().equals("adminPending")) {
            holder.userRoleSpinner.setEnabled(false);
            holder.approveAdminRequest.setVisibility(View.VISIBLE);
            holder.approveAdminRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    toAddUserModel.setUserRole("admin");
                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(toAddUserModel.getId())
                            .set(toAddUserModel)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.wtf("DATABASE", "called");
                                    commonMethods.makeSnack(rootLayout, "New Volunteer added");
                                }
                            });

                }
            });
        } else {
            holder.userRoleSpinner.setEnabled(true);
            holder.approveAdminRequest.setVisibility(View.GONE);
        }

        holder.userRoleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!spinnerFirstCall[0]) {
                    switch (position) {
                        case 0:
                            toAddUserModel.setUserRole("user");
                            break;
                        case 1:
                            toAddUserModel.setUserRole("admin");
                            break;
                        case 2:
                            toAddUserModel.setUserRole("superAdmin");
                            break;
                    }
                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(toAddUserModel.getId())
                            .set(toAddUserModel)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.wtf("DATABASE", "called");
                                }
                            });
                }
                spinnerFirstCall[0] = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (userModels.get(position).getUserRole().equals("user")) {
            holder.addRationBySupAdmin.setVisibility(View.VISIBLE);
        } else {
            holder.addRationBySupAdmin.setVisibility(View.GONE);
        }

        holder.addRationBySupAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String currUserId = userModels.get(finalPos).getId();
                final String currUserName = userModels.get(finalPos).getName();

                commonMethods.loadingDialogStart(mActivity);
                FirebaseFirestore.getInstance()
                        .collection("rationRequest")
                        .whereEqualTo("userId", currUserId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                commonMethods.loadingDialogStop();
//                                    Toast.makeText(mActivity, currUserId + queryDocumentSnapshots.size(), Toast.LENGTH_SHORT).show();
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    if (Objects.equals(documentSnapshot.get("requestStatus"), RationRequestModel.PENDING) ||
                                            Objects.equals(documentSnapshot.get("requestStatus"), RationRequestModel.APPROVED)) {

                                        commonMethods.createAlert(new AlertDialog.Builder(mActivity),
                                                "This user already has a Pending or Approved Request");
                                        return;

                                    }
                                }

                                commonMethods.loadingDialogStart(mActivity, mActivity.getString(R.string.fetch_item_list));
                                FirebaseFirestore.getInstance()
                                        .collection("fields")
                                        .document("itemNames")
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                commonMethods.loadingDialogStop();
                                                ArrayList<String> itemNames = (ArrayList<String>) documentSnapshot.get("itemNames");
                                                ArrayList<String> itemUnits = (ArrayList<String>) documentSnapshot.get("itemUnits");

                                                AddRationDialog addRationDialog = new AddRationDialog(mActivity, itemNames, itemUnits, rootLayout, userModels.get(finalPos));
                                                addRationDialog.setCanceledOnTouchOutside(false);
                                                addRationDialog.show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        commonMethods.loadingDialogStop();
                                        Toast.makeText(mActivity, R.string.oops_error, Toast.LENGTH_SHORT).show();
                                        Log.e(TAG, "onFailure: ", e);
                                    }
                                });
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        commonMethods.loadingDialogStop();
                        Toast.makeText(mActivity, R.string.oops_error, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onFailure: ", e);
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    @Override
    public Filter getFilter() {
        Log.wtf("FIRST_VALUE", "" + noOfDeliveriesFull[0]);
        noOfDeliveriesFull[3] = 5;
        return userNameFilter;
    }

    public Filter getPhoneFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                ArrayList<UserModel> filteredUserList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredUserList.addAll(userModelsFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (UserModel userModel : userModelsFull) {
                        if (String.valueOf(userModel.getPhone()).toLowerCase().contains(filterPattern))
                        filteredUserList.add(userModel);
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredUserList;
                return results ;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                userModels.clear();
                userModels.addAll((ArrayList) results.values);
                notifyDataSetChanged();
            }
        };
    }


    private Filter userNameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<UserModel> filteredUserList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredUserList.addAll(userModelsFull);
                noOfDeliveries = noOfDeliveriesFull;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                int filtered_i = 0;
                for (int i = 0; i < userModelsFull.size(); i++) {
                    UserModel item = userModelsFull.get(i);
                    if (String.valueOf(item.getName()).toLowerCase().contains(filterPattern)) {
                        filteredUserList.add(item);
                        noOfDeliveries[filtered_i] = noOfDeliveriesFull[i];
                        Log.d("INDICES", "i:" + i + " filtered_i:" + filtered_i);
                        Log.d("DATA", "i:" + noOfDeliveriesFull[i] + " filtered_i:" + noOfDeliveriesFull[filtered_i]);
                        filtered_i++;
                    }
                }
            }

            FilterResults results = new FilterResults();
            Map<String, Object> filteredData = new HashMap<String, Object>();
            filteredData.put("filteredUserList", filteredUserList);
            filteredData.put("filteredDeliveries", noOfDeliveries);

            results.values = filteredData;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            Map<String, Object> filteredData = new HashMap<String, Object>();
            filteredData = (Map<String, Object>) results.values;

            userModels.clear();
            userModels.addAll((ArrayList)filteredData.get("filteredUserList"));
//            userModels.addAll((ArrayList) results.values);
            noOfDeliveries = (int[]) filteredData.get("filteredDeliveries");
            notifyDataSetChanged();
        }
    };


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, phone, lastDelivered, noOfIssues;
        Spinner userRoleSpinner;
        Button addRationBySupAdmin, approveAdminRequest;
        LinearLayout lastIssuedLinLay;
        ImageView infoImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);
            userRoleSpinner = itemView.findViewById(R.id.userRoleSpinner);
            addRationBySupAdmin = itemView.findViewById(R.id.addRationBySupAdmin);
            approveAdminRequest = itemView.findViewById(R.id.approveAdminRequest);
            lastDelivered = itemView.findViewById(R.id.lastDelivered);
            lastIssuedLinLay = itemView.findViewById(R.id.lastIssuedLinLay);
            infoImageView = itemView.findViewById(R.id.infoImageView);
            noOfIssues = itemView.findViewById(R.id.noOfIssues);

        }
    }

}
