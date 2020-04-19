package com.avinabaray.crm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.avinabaray.crm.Models.UserModel;
import com.avinabaray.crm.Utils.CommonMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class RegisterUserActivity extends BaseActivity {

    private static final String TAG = "RegisterAct";
    EditText editTextName, editTextAddress, editTextPINCode, editTextIncome;
    EditText editTextAdultMembers, editTextChildMembers, editTextEarningMembers;
    Spinner spinnerOccupation;

    private DocumentReference mDocRef = FirebaseFirestore.getInstance().collection("users").document();

    ArrayList<String> occupations = new ArrayList<String>();
    ArrayList<Long> AdultMembers = new ArrayList<Long>();
    private Activity mActivity = this;

    UserModel newUserModel = new UserModel();
    CommonMethods commonMethods = new CommonMethods();
    private AlertDialog.Builder alertBuilder;
    private ScrollView rootLayout;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        rootLayout = findViewById(R.id.rootLayout);
        editTextName = findViewById(R.id.editTextName);
        editTextAddress = findViewById(R.id.editTextAddress);
        editTextPINCode = findViewById(R.id.editTextPINCode);
        editTextIncome = findViewById(R.id.editTextIncome);
        spinnerOccupation = findViewById(R.id.spinnerOccupation);
        editTextAdultMembers = findViewById(R.id.editTextAdultMembers);
        editTextChildMembers = findViewById(R.id.editTextChildMembers);
        editTextEarningMembers = findViewById(R.id.editTextEarningMembers);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        alertBuilder = new AlertDialog.Builder(mActivity);

        initOccupationSpinner();

        editTextAddress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rootLayout.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


    }

    private void initOccupationSpinner() {

        occupations.add("Choose Occupation");
        occupations.add("Service (Permanent)");
        occupations.add("Service (Temporary)");
        occupations.add("Business");
        occupations.add("Others");

        ArrayAdapter<String> occupationAdapter = new ArrayAdapter<String>(
                mActivity,
                android.R.layout.simple_spinner_dropdown_item,
                occupations
        );

        spinnerOccupation.setAdapter(occupationAdapter);
        spinnerOccupation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    newUserModel.setOccupation(occupations.get(position));
                } else {
                    newUserModel.setOccupation(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

    public void register(View view) {

        newUserModel.setName(editTextName.getText().toString());
        newUserModel.setAddress(editTextAddress.getText().toString());
        try {
            newUserModel.setPinCode(Long.valueOf(editTextPINCode.getText().toString()));
        } catch (Exception e) {
            newUserModel.setPinCode(-1L);
        }
        try {
            newUserModel.setMonthlyIncome(Long.valueOf(editTextIncome.getText().toString()));
        } catch (Exception e) {
            newUserModel.setMonthlyIncome(-1L);
        }
        try {
            newUserModel.setAdultMembers(Long.valueOf(editTextAdultMembers.getText().toString()));
        } catch (NumberFormatException e) {
            newUserModel.setAdultMembers(-1L);
        }
        try {
            newUserModel.setChildMembers(Long.valueOf(editTextChildMembers.getText().toString()));
        } catch (NumberFormatException e) {
            newUserModel.setChildMembers(-1L);
        }
        try {
            newUserModel.setEarningMembers(Long.valueOf(editTextEarningMembers.getText().toString()));
        } catch (NumberFormatException e) {
            newUserModel.setEarningMembers(-1L);
        }

        if (newUserModel.getName().isEmpty()) {
            commonMethods.createAlert(alertBuilder, "Enter a valid Name");
            return;
        } else if (newUserModel.getAddress().isEmpty()) {
            commonMethods.createAlert(alertBuilder, "Enter a valid Address");
            return;
        } else if (newUserModel.getPinCode() < 99999) {
            commonMethods.createAlert(alertBuilder, "Enter a valid PIN Code");
            return;
        } else if (newUserModel.getMonthlyIncome() < 0) {
            commonMethods.createAlert(alertBuilder, "Enter a valid Income");
            return;
        } else if (newUserModel.getOccupation() == null) {
            commonMethods.createAlert(alertBuilder, "Select an Occupation");
            return;
        } else if (newUserModel.getAdultMembers() < 0) {
            commonMethods.createAlert(alertBuilder, "Enter a valid No. of Adults");
            return;
        } else if (newUserModel.getChildMembers() < 0) {
            commonMethods.createAlert(alertBuilder, "Enter a valid No. of Children");
            return;
        } else if (newUserModel.getEarningMembers() >= newUserModel.getAdultMembers() + newUserModel.getChildMembers()) {
            commonMethods.createAlert(alertBuilder, "Earning members can't be more than total Family Members");
            return;
        }

        newUserModel.setPhone(pref.getString("CURRENT_PHONE", ""));
        newUserModel.setId(mDocRef.getId());
        newUserModel.setTimestamp(Timestamp.now());

        mDocRef.set(newUserModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mActivity, "Successfully Registered", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(mActivity, MainActivity.class);
                        startActivity(i);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: ", e);
                commonMethods.makeSnack(rootLayout, "Oops... Please Try Again");
            }
        });

    }
}
