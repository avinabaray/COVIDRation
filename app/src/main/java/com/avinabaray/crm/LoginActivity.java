package com.avinabaray.crm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.avinabaray.crm.Utils.CommonMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginAct";
    EditText editTextPhone, editTextOtp;
    Button loginButton, getOtpButton;
    ImageView imageViewLock;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CollectionReference mDocRef = FirebaseFirestore.getInstance().collection("users");

    private Activity mActivity = this;

    CommonMethods commonMethods = new CommonMethods();
    private ScrollView rootLayout;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String otpEntered;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rootLayout = findViewById(R.id.rootLayout);
        imageViewLock = findViewById(R.id.imageViewLock);
        editTextOtp = findViewById(R.id.editTextOtp);
        editTextPhone = findViewById(R.id.editTextPhone);
        loginButton = findViewById(R.id.loginButton);
        getOtpButton = findViewById(R.id.getOtpButton);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();


        initSendOtpBtn();
        initLoginBtn();


    }

    private void initLoginBtn() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpEntered = editTextOtp.getText().toString();
                if (otpEntered.length() > 0) {
                    /*
                    Creating the credential to send it to Firebase for verification
                    through signInWithPhoneAuthCredential () method
                    */
                    commonMethods.loadingDialogStart(mActivity);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otpEntered);
                    signInWithPhoneAuthCredential(credential);
                } else {
                    commonMethods.makeSnack(rootLayout, "Invalid OTP");
                }
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            commonMethods.loadingDialogStop();
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // ...
//                            commonMethods.makeSnack(rootLayout, "Login Successful");
                            updateUI(user);
                        } else {
                            // Sign in failed, display a message and update the UI
                            commonMethods.loadingDialogStop();
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                commonMethods.makeSnack(rootLayout, "Invalid OTP");
                            } else {
                                commonMethods.makeSnack(rootLayout, "Authentication Failed");
                                updateUI(null);
                            }
                        }
                    }
                });
    }

    private void initSendOtpBtn() {
        getOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.CURRENT_PHONE = MainActivity.COUNTRY_CODE + editTextPhone.getText().toString();

                mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        commonMethods.loadingDialogStop();
                        commonMethods.makeSnack(rootLayout, "Device Auto-Verified");
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        commonMethods.loadingDialogStop();
                        commonMethods.makeSnack(rootLayout, "Verification Failed");
                        Log.e(TAG, "onVerificationFailed: ", e);
                        updateUI(null);
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        commonMethods.loadingDialogStop();
                        commonMethods.makeSnack(rootLayout, "OTP Sent");
                        mVerificationId = verificationId;
                        mResendToken = forceResendingToken;
                        editTextOtp.setEnabled(true);
                        imageViewLock.setAlpha(1.0f);
                    }

                    @Override
                    public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                        super.onCodeAutoRetrievalTimeOut(s);
                        commonMethods.loadingDialogStop();
                    }
                };

                /*
                 *  Phone number here includes "+91"
                 */
                if (MainActivity.CURRENT_PHONE.length() == 13) {
                    commonMethods.loadingDialogStart(mActivity, "Sending OTP");
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            MainActivity.CURRENT_PHONE,
                            60,
                            TimeUnit.SECONDS,
                            mActivity,
                            mCallback);
                    getOtpButton.setVisibility(View.GONE);
                    loginButton.setVisibility(View.VISIBLE);
                } else {
                    commonMethods.makeSnack(rootLayout, "Invalid Phone Number");
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {

            mDocRef.whereEqualTo("phone", MainActivity.CURRENT_PHONE)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            editor.putString("CURRENT_PHONE", MainActivity.CURRENT_PHONE);
                            editor.putBoolean("isFirstRun", false);
                            editor.apply();
                            if (queryDocumentSnapshots.isEmpty()) {
                                // New user. Get all details from user here
                                Intent i = new Intent(mActivity, RegisterUserActivity.class);
                                startActivity(i);
                            } else {
                                // Exiting user
                                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    MainActivity.CURRENT_USER_ID = documentSnapshot.getId();
                                    editor.putString("CURRENT_USER_ID", MainActivity.CURRENT_USER_ID);
                                    editor.apply();

//                                    MainActivity.details1Uploaded = documentSnapshot.getBoolean("details1Uploaded");
//                                    MainActivity.photo2Uploaded = documentSnapshot.getBoolean("details2Uploaded");

                                    Intent intent = new Intent(mActivity, UserDashboardActivity.class);
//                                    if (MainActivity.details1Uploaded && MainActivity.photo2Uploaded) {
//                                        // Intent to Dashboard
//                                        intent = new Intent(mActivity, DashboardActivity.class);
//                                    } else if (MainActivity.details1Uploaded) {
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


//            Intent intent = new Intent(mActivity, DashboardActivity.class);
//            startActivity(intent);
        } else {
            // Authentication failed. Refreshing all fields
            commonMethods.makeSnack(rootLayout, "Login failed... Please Try again");
            editTextOtp.setText("");
            editTextOtp.setEnabled(false);
            imageViewLock.setAlpha(0.5f);
//            otpLayout.setAlpha(0.0f);
            loginButton.setVisibility(View.GONE);
            getOtpButton.setVisibility(View.VISIBLE);
        }
    }

}
