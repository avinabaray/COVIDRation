package com.avinabaray.crm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.avinabaray.crm.Utils.CommonMethods;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class AboutUsActivity extends AppCompatActivity {

    private TextView version, contactDeveloper, appIntroText;
    private String devUrl;
    private CommonMethods commonMethods = new CommonMethods();
    private View rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        rootLayout = findViewById(R.id.rootLayout);
        version = findViewById(R.id.version);
        contactDeveloper = findViewById(R.id.contactUs);
        appIntroText = findViewById(R.id.appIntroText);

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        version.setText("Version : " + versionName);

        FirebaseFirestore.getInstance().collection("fields")
                .document("aboutUs")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot != null) {
                            if (documentSnapshot.getString("introText") != null) {
                                appIntroText.setText(documentSnapshot.getString("introText"));
                            }
                            devUrl = documentSnapshot.getString("contactDev");
                            contactDeveloper.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = null;
                                    try {
                                        browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(devUrl));
                                        startActivity(browserIntent);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                        commonMethods.makeSnack(rootLayout, getString(R.string.conn_error));
                                    }
                                }
                            });

                        }
                    }
                });

    }
}
