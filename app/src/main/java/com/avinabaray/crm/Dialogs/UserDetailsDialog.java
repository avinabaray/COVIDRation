package com.avinabaray.crm.Dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.avinabaray.crm.Adapters.RationDisplayAdapter;
import com.avinabaray.crm.Models.UserModel;
import com.avinabaray.crm.R;

import static com.avinabaray.crm.Adapters.RationDisplayAdapter.getFormattedDateTime;

public class UserDetailsDialog extends Dialog {

    private Activity mActivity;
    private UserModel currUserModel;
    private TextView name, phone, address, occupation, pinCode, monthlyIncome, userRole, userCreationTime;
    private TextView adults, children, earningMembers;
    private ImageView imageViewCloseButton;
    private LinearLayout occupationLinLay, incomeLinLay, addressLinLay, familyMembersLinLay;

    public UserDetailsDialog(@NonNull Activity mActivity, UserModel currUserModel) {
        super(mActivity);

        this.mActivity = mActivity;
        this.currUserModel = currUserModel;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details_dialog);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        occupation = findViewById(R.id.occupation);
        pinCode = findViewById(R.id.pinCode);
        monthlyIncome = findViewById(R.id.monthlyIncome);
        imageViewCloseButton = findViewById(R.id.imageViewCloseButton);
        userRole = findViewById(R.id.userRole);
        userCreationTime = findViewById(R.id.userCreationTime);
        occupationLinLay = findViewById(R.id.occupationLinLay);
        incomeLinLay = findViewById(R.id.incomeLinLay);
        addressLinLay = findViewById(R.id.addressLinLay);
        familyMembersLinLay = findViewById(R.id.familyMembersLinLay);
        adults = findViewById(R.id.adults);
        children = findViewById(R.id.children);
        earningMembers = findViewById(R.id.earningMembers);

        imageViewCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        switch (currUserModel.getUserRole()) {
            case "user":
                userRole.setText("User");
                break;
            case "admin":
                userRole.setText("Volunteer");
                break;
            case "superAdmin":
                userRole.setText("Super Admin");
                break;
            case "adminPending":
                userRole.setText("To be Volunteer");
                break;
        }
        name.setText(currUserModel.getName());
        phone.setText(currUserModel.getPhone().substring(3, currUserModel.getPhone().length()));
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + currUserModel.getPhone()));
                mActivity.startActivity(callIntent);
            }
        });
        address.setText(currUserModel.getAddress());
        pinCode.setText(String.valueOf(currUserModel.getPinCode()));
        userCreationTime.setText(getFormattedDateTime(currUserModel.getTimestamp()));

        if (currUserModel.getUserRole().equals("user")) {
            occupation.setText(currUserModel.getOccupation());
            monthlyIncome.setText("₹ " + String.valueOf(currUserModel.getMonthlyIncome()) + "/month");
            adults.setText(String.valueOf(currUserModel.getAdultMembers()));
            children.setText(String.valueOf(currUserModel.getChildMembers()));
            earningMembers.setText(String.valueOf(currUserModel.getEarningMembers()));
        } else {
            occupationLinLay.setVisibility(View.GONE);
            incomeLinLay.setVisibility(View.GONE);
            familyMembersLinLay.setVisibility(View.GONE);
        }

    }

}
