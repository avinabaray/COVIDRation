package com.avinabaray.crm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.avinabaray.crm.Utils.CommonMethods;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class AdminEntryActivity extends BaseActivity {

    private static final String TAG = "AdminEntyAct";
    private ConstraintLayout rootLayout;
    private TextView contactUs, pendingAdminMsg;
    Activity mActivity = this;
    private Button viewRationRequestsBtn;
    private LottieAnimationView lottieStayHome;

    CommonMethods commonMethods = new CommonMethods();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_entry);

        rootLayout = findViewById(R.id.rootLayout);
        lottieStayHome = findViewById(R.id.lottieStayHome);
        contactUs = findViewById(R.id.contactUs);
        pendingAdminMsg = findViewById(R.id.pendingAdminMsg);
        viewRationRequestsBtn = findViewById(R.id.viewRationRequestsBtn);

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonMethods.makeSnack(rootLayout, getString(R.string.conn_error));
            }
        });

        viewRationRequestsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity, AdminActivity.class);
                startActivity(i);
            }
        });

        FirebaseFirestore.getInstance().collection("fields")
                .document("contactUs")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable final DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null) {
                            pendingAdminMsg.setText(documentSnapshot.getString("pendingAdminMsg"));
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
    }
}
