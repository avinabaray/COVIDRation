package com.avinabaray.crm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SuperAdminDashActivity extends BaseActivity {

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
        Intent i = new Intent(mActivity, EditUsersActivity.class);
        startActivity(i);
    }

    public void viewRationRequests(View view) {
        Intent i = new Intent(mActivity, AdminActivity.class);
        startActivity(i);
    }

    public void test(View view) {
        Toast.makeText(mActivity, MainActivity.CURRENT_USER_MODEL.getName(), Toast.LENGTH_SHORT).show();
    }
}
