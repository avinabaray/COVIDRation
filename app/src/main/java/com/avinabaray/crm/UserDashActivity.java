package com.avinabaray.crm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.avinabaray.crm.Dialogs.AddRationDialog;
import com.avinabaray.crm.Models.RationRequestModel;
import com.avinabaray.crm.Utils.CommonMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class UserDashActivity extends BaseActivity {

    private static final String TAG = "UserDashAct";
    private Button addRationRequestBtn;
    private RecyclerView rationRecycler;
    private ConstraintLayout rootLayout;
    private TextView contactUs;
    Activity mActivity = this;

    CommonMethods commonMethods = new CommonMethods();
    ArrayList<String> itemNames = new ArrayList<>();
    ArrayList<String> itemUnits = new ArrayList<>();
    private LottieAnimationView lottieStayHome;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
        finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        addRationRequestBtn = findViewById(R.id.addRationRequestBtn);
        rationRecycler = findViewById(R.id.rationRecycler);
        rootLayout = findViewById(R.id.rootLayout);
        lottieStayHome = findViewById(R.id.lottieStayHome);
        contactUs = findViewById(R.id.contactUs);

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonMethods.makeSnack(rootLayout, getString(R.string.conn_error));
            }
        });

        FirebaseFirestore.getInstance().collection("fields")
                .document("contactUs")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable final DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null) {
                            contactUs.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String phoneNo = documentSnapshot.getString("phone");
                                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                    callIntent.setData(Uri.parse("tel:" + phoneNo));
                                    mActivity.startActivity(callIntent);
                                }
                            });
                        }
                    }
                });

        lottieStayHome.enableMergePathsForKitKatAndAbove(true);
//        lottieStayHome.playAnimation();
//        lottieStayHome.setRepeatCount(3);

        addRationRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonMethods.makeSnack(rootLayout, "Please wait...");
            }
        });

        FirebaseFirestore.getInstance()
                .collection("rationRequest")
                .whereEqualTo("userId", MainActivity.CURRENT_USER_MODEL.getId())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            addRationRequestBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    Toast.makeText(mActivity, "DONE", Toast.LENGTH_SHORT).show();
                                    commonMethods.loadingDialogStart(mActivity, getString(R.string.fetch_item_list));
                                    FirebaseFirestore.getInstance()
                                            .collection("fields")
                                            .document("itemNames")
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    commonMethods.loadingDialogStop();
                                                    itemNames = (ArrayList<String>) documentSnapshot.get("itemNames");
                                                    itemUnits = (ArrayList<String>) documentSnapshot.get("itemUnits");

                                                    AddRationDialog addRationDialog = new AddRationDialog(mActivity, itemNames, itemUnits, rootLayout);
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
                            });
//                            Toast.makeText(mActivity, String.valueOf(queryDocumentSnapshots.size()), Toast.LENGTH_SHORT).show();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if (Objects.equals(documentSnapshot.get("requestStatus"), RationRequestModel.PENDING) ||
                                        Objects.equals(documentSnapshot.get("requestStatus"), RationRequestModel.APPROVED)) {
                                    addRationRequestBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
//                                            Toast.makeText(mActivity, "DONE", Toast.LENGTH_SHORT).show();
                                            commonMethods.createAlert(new AlertDialog.Builder(mActivity),
                                                    "We are processing your Pending Request");
                                        }
                                    });
                                }
                            }
                        }
                    }
                });


    }
}
