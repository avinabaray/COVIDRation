package com.avinabaray.crm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.avinabaray.crm.Models.UserModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    public static final String COUNTRY_CODE = "+91";
    private static final String TAG = "MainAct";
    private Activity mActivity = this;

    private CollectionReference mDocRef = FirebaseFirestore.getInstance().collection("users");

    public static String CURRENT_PHONE;
    public static String CURRENT_USER_ID;
    public static String CURRENT_USER_ROLE;
    public static String CURRENT_USER_NAME;
    public static UserModel CURRENT_USER_MODEL = new UserModel();
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private FirebaseAuth mAuth;
    public static FirebaseUser currentUser;

    @Override
    protected void onStart() {
        super.onStart();
        CURRENT_USER_ID = pref.getString("CURRENT_USER_ID", "");
        CURRENT_PHONE = pref.getString("CURRENT_PHONE", "");

        currentUser = mAuth.getCurrentUser();

        updateUI(currentUser);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();

        mAuth = FirebaseAuth.getInstance();


//        Intent i = new Intent(mActivity, LoginActivity.class);
//        startActivity(i);

    }

    private void updateUI(FirebaseUser currentUser) {
//        Toast.makeText(this, "UpdateUI", Toast.LENGTH_SHORT).show();
        if (currentUser != null) {
            CURRENT_PHONE = currentUser.getPhoneNumber();

            mDocRef.whereEqualTo("phone", CURRENT_PHONE)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.isEmpty()) {
                                // New user. Get all details from user here
                                Intent i = new Intent(mActivity, RegisterUserActivity.class);
                                startActivity(i);
                            } else {
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    MainActivity.CURRENT_USER_MODEL = documentSnapshot.toObject(UserModel.class);
                                    CURRENT_USER_ID = documentSnapshot.getId();
                                    editor.putString("CURRENT_USER_ID", CURRENT_USER_ID);
                                    editor.apply();

//                                    Toast.makeText(mActivity, "OnSuccess", Toast.LENGTH_SHORT).show();
//                                    details1Uploaded = documentSnapshot.getBoolean("details1Uploaded");
//                                    photo2Uploaded = documentSnapshot.getBoolean("details2Uploaded");
                                    Intent intent;

                                    switch (MainActivity.CURRENT_USER_MODEL.getUserRole()) {
                                        case "user":
                                            intent = new Intent(mActivity, UserDashActivity.class);
                                            break;
                                        case "admin":
                                            intent = new Intent(mActivity, SuperAdminDashActivity.class);
                                            break;
                                        case "superAdmin":
                                            intent = new Intent(mActivity, SuperAdminDashActivity.class);
                                            break;
                                        default:
                                            intent = new Intent(mActivity, UserDashActivity.class);
                                    }
//                                    if (details1Uploaded && photo2Uploaded) {
//                                        // Intent to Dashboard
//                                        intent = new Intent(mActivity, DashboardActivity.class);
//                                    } else if (details1Uploaded) {
//                                        // Intent to photo upload page
//                                        intent = new Intent(mActivity, RegisterStage2Activity.class);
//                                    } else {
//                                        // Intent to details fill page
//                                        intent = new Intent(mActivity, RegisterUserActivity.class);
//                                    }
                                    startActivity(intent);

                                }

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mActivity, "Something went wrong...", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onFailure: ", e);
                }
            });

        } else {
            boolean isFirstRun = pref.getBoolean("isFirstRun", true);
            Intent intent;
//            Toast.makeText(this, "else", Toast.LENGTH_SHORT).show();
            if (isFirstRun) {
                // Intent to Intro Carousel
                intent = new Intent(mActivity, LoginActivity.class);
            } else {
                intent = new Intent(mActivity, LoginActivity.class);
            }
            startActivity(intent);

        }

    }

}
