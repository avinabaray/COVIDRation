package com.avinabaray.crm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SuperAdminDashActivity extends AppCompatActivity {

    private Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_super_admin_dash);
    }

    public void editItemList(View view) {
        Intent i = new Intent(mActivity, EditItemListActivity.class);
        startActivity(i);
    }

    public void editUsers(View view) {

    }
}
