package com.avinabaray.crm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class SuperAdminDashActivity extends BaseActivity {

    private Activity mActivity;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_item, menu);
        MenuItem downloadItems = menu.findItem(R.id.download);
        downloadItems.setVisible(true);

        downloadItems.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(mActivity, DownloadDbActivity.class);
                startActivity(i);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

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
