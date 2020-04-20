package com.avinabaray.crm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class BaseActivity extends AppCompatActivity {

    public SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemHelp:
//                Toast.makeText(this, "Help and Support will be added soon", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, AboutUsActivity.class);
                startActivity(i);
                break;

            case R.id.itemLogout:
                MainActivity.CURRENT_USER_ID = null;
                MainActivity.CURRENT_USER_ROLE = null;
                FirebaseAuth.getInstance().signOut();
                MainActivity.currentUser = null;
                editor.clear();
                editor.commit();

                Intent intentToMainActivity = new Intent(this, MainActivity.class);
                startActivity(intentToMainActivity);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initializing SharedPreferences
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
    }
}
// end of file
