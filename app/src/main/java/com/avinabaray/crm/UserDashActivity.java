package com.avinabaray.crm;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

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
    Activity mActivity = this;

    CommonMethods commonMethods = new CommonMethods();
    ArrayList<String> itemNames = new ArrayList<>();
    ArrayList<String> itemUnits = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        addRationRequestBtn = findViewById(R.id.addRationRequestBtn);
        rationRecycler = findViewById(R.id.rationRecycler);
        rootLayout = findViewById(R.id.rootLayout);

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
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (Objects.equals(documentSnapshot.get("requestStatus"), RationRequestModel.PENDING)) {
                                addRationRequestBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        commonMethods.createAlert(new AlertDialog.Builder(mActivity),
                                                "You already have a Pending Request.");
                                    }
                                });
                            } else {
                                addRationRequestBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
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
                            }
                        }
                    }
                });


    }
}
