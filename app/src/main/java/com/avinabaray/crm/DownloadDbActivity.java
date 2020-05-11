package com.avinabaray.crm;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.avinabaray.crm.Models.RationRequestModel;
import com.avinabaray.crm.Models.UserModel;
import com.avinabaray.crm.Utils.CommonMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.avinabaray.crm.Adapters.RationDisplayAdapter.getFormattedDate;
import static com.avinabaray.crm.Adapters.RationDisplayAdapter.getFormattedDateTime;

public class DownloadDbActivity extends BaseActivity {

    private static final int USER_DETAILS_CODE = 100;
    private static final int RATION_REQUESTS_CODE = 101;
    CommonMethods commonMethods = new CommonMethods();
    private Activity mActivity;
    private ViewGroup rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_db);
        rootLayout = findViewById(R.id.rootLayout);
        mActivity = this;
    }

    public void downloadUserDetails(View view) {

        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                    mActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    USER_DETAILS_CODE);
        } else {
            generateUserDetails();
        }
    }

    private void generateUserDetails() {
        commonMethods.loadingDialogStart(mActivity, "Generating CSV...");
        FirebaseFirestore.getInstance()
                .collection("users")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        commonMethods.loadingDialogStop();
                        StringBuilder csvOutput = new StringBuilder();
                        csvOutput.append("ID,")
                                .append("User Role,")
                                .append("Name,")
                                .append("Address,")
                                .append("PIN Code,")
                                .append("Phone No,")
                                .append("Occupation,")
                                .append("Monthly Income,")
                                .append("Adult Members,")
                                .append("Child Members,")
                                .append("Earning Members,")
                                .append("Registration Date,")
                                .append("Registration Time,")
                                .append("Active Status");
                        csvOutput.append("\n");
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            UserModel userModel = documentSnapshot.toObject(UserModel.class);
                            if (userModel != null) {
                                csvOutput.append("\"").append(userModel.getId())
                                        .append("\",\"").append(userModel.getUserRole())
                                        .append("\",\"").append(userModel.getName())
                                        .append("\",\"").append(userModel.getAddress())
                                        .append("\",\"").append(String.valueOf(userModel.getPinCode()))
                                        .append("\",\"").append(userModel.getPhone().substring(3))
                                        .append("\",\"").append(userModel.getOccupation())
                                        .append("\",\"").append(String.valueOf(userModel.getMonthlyIncome()))
                                        .append("\",\"").append(String.valueOf(userModel.getAdultMembers()))
                                        .append("\",\"").append(String.valueOf(userModel.getChildMembers()))
                                        .append("\",\"").append(String.valueOf(userModel.getEarningMembers()))
                                        .append("\",\"").append(getFormattedDate(userModel.getTimestamp()))
                                        .append("\",\"").append(getFormattedTime(userModel.getTimestamp(), false))
                                        .append("\",\"").append(String.valueOf(userModel.getActiveStatus()));
                                csvOutput.append("\"\n");
                            }
                        }
                        csvOutput.append("\n");
                        writeToFile(csvOutput.toString(),
                                "User_Details_" +
                                        getFormattedDate(Timestamp.now()) +
                                        "_" +
                                        getFormattedTime(Timestamp.now(), true) +
                                        ".csv"
                                );
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                commonMethods.loadingDialogStop();

            }
        });
    }

    public void downloadRationRequests(View view) {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(
                    mActivity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RATION_REQUESTS_CODE);
        } else {
            generateRationRequests();
        }
    }

    private void generateRationRequests() {
        commonMethods.loadingDialogStart(mActivity, "Generating CSV...");
        FirebaseFirestore.getInstance()
                .collection("rationRequest")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        commonMethods.loadingDialogStop();
                        Map<String, String> items = new HashMap<>();
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            RationRequestModel rationRequestModel = documentSnapshot.toObject(RationRequestModel.class);
                            ArrayList<String> itemNames = rationRequestModel.getItemNames();
                            ArrayList<String> itemUnits = rationRequestModel.getItemUnits();
                            for (int i=0; i<itemNames.size(); i++) {
                                items.put(itemNames.get(i), itemUnits.get(i));
                            }
                        }

//                        Log.wtf("Yes", "On" + items.entrySet().size());
//                        for (Map.Entry<String, String> a : items.entrySet()) {
//                            System.out.println(a.getKey() + ": " + a.getValue());
//                        }

                        StringBuilder csvOutput = new StringBuilder();
                        csvOutput.append("ID,")
                                .append("User Role,")
                                .append("Name,")
                                .append("User ID,")
                                .append("PIN Code,");
                        for (Map.Entry<String, String> a : items.entrySet()) {
                            csvOutput.append(a.getKey() + "(" + a.getValue() + ")" + ",");
                        }
                        csvOutput.append("Request Status,")
                                .append("Request Time,")
                                .append("Last Response Time,")
                                .append("Approve Time,")
                                .append("Approved by,")
                                .append("Delivered Time,")
                                .append("Delivered by,")
                                .append("Rejected Time,")
                                .append("Rejected by,");
                        csvOutput.append("\n");
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            RationRequestModel rationRequestModel = documentSnapshot.toObject(RationRequestModel.class);
                            if (rationRequestModel != null) {
                                csvOutput.append("\"").append(rationRequestModel.getId())
                                        .append("\",\"").append(rationRequestModel.getUserRole())
                                        .append("\",\"").append(rationRequestModel.getUserName())
                                        .append("\",\"").append(rationRequestModel.getUserId())
                                        .append("\",\"").append(rationRequestModel.getPinCode());
                                for (Map.Entry<String, String> a : items.entrySet()) {
                                    if (rationRequestModel.getItemNames().contains(a.getKey())) {
                                        csvOutput.append("\",\"").append(rationRequestModel.getItemQtys().get(rationRequestModel.getItemNames().indexOf(a.getKey())));
                                    } else {
                                        csvOutput.append("\",\"").append("0");
                                    }
                                }
                                switch (rationRequestModel.getRequestStatus().intValue()) {
                                    case 0:
                                        csvOutput.append("\",\"").append("Pending");
                                        break;
                                    case 1:
                                        csvOutput.append("\",\"").append("Approved");
                                        break;
                                    case 2:
                                        csvOutput.append("\",\"").append("Delivered");
                                        break;
                                    case 3:
                                        csvOutput.append("\",\"").append("Rejected");
                                        break;
                                    default:
                                        csvOutput.append("\",\"").append("Unknown");
                                }
                                csvOutput.append("\",\"").append(getFormattedDateTime(rationRequestModel.getRequestTime()))
                                        .append("\",\"").append(getFormattedDateTime(rationRequestModel.getResponseTime()))
                                        .append("\",\"").append(getFormattedDateTime(rationRequestModel.getApproveTime()))
                                        .append("\",\"").append(rationRequestModel.getApprovedBy())
                                        .append("\",\"").append(getFormattedDateTime(rationRequestModel.getDeliverTime()))
                                        .append("\",\"").append(rationRequestModel.getDeliveredBy())
                                        .append("\",\"").append(getFormattedDateTime(rationRequestModel.getRejectTime()))
                                        .append("\",\"").append(rationRequestModel.getRejectedBy());
                                csvOutput.append("\"\n");
                            }
                        }
                        csvOutput.append("\n");
                        writeToFile(csvOutput.toString(),
                                "Ration_Requests_" +
                                        getFormattedDate(Timestamp.now()) +
                                        "_" +
                                        getFormattedTime(Timestamp.now(), true) +
                                        ".csv"
                        );
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                commonMethods.loadingDialogStop();
            }
        });
    }

    public void writeToFile(String data, String fileName) {
        // Get the directory for the user's public pictures directory.
        final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

//        // Make sure the path directory exists.
//        if(!path.exists())
//        {
//            // Make it, if it doesn't exit
//            path.mkdirs();
//        }

        final File file = new File(path, fileName);

        // Save your stream, don't forget to flush() it before closing it.
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            Toast.makeText(mActivity, "Something wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Todo The below code is used to create a download request for the file so that it shows up in the downloads
        try {
            DownloadManager downloadManager = (DownloadManager) mActivity.getSystemService(DOWNLOAD_SERVICE);
            downloadManager.addCompletedDownload(
                    file.getName(),
                    file.getName(),
                    true,
                    "text/csv",
                    file.getAbsolutePath(),
                    file.length(),
                    true
            );
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mActivity, "Something wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == USER_DETAILS_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generateUserDetails();
            } else {
                commonMethods.makeSnack(rootLayout, "Please Allow to Download");
            }
        }
        if (requestCode == RATION_REQUESTS_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                generateRationRequests();
            } else {
                commonMethods.makeSnack(rootLayout, "Please Allow to Download");
            }
        }

    }

    private String getFormattedTime(Timestamp timestamp, boolean isFileName) {
        String timeString = "";
        String timeSuffix;

        if (timestamp.toDate().getHours() > 12) {
            timeString += timestamp.toDate().getHours() - 12 + (isFileName ? "-" : ":");
            timeSuffix = "PM";
        } else {
            timeString += timestamp.toDate().getHours() + (isFileName ? "-" : ":");
            timeSuffix = "AM";
        }
        if (timestamp.toDate().getMinutes() < 10) {
            timeString += "0" + timestamp.toDate().getMinutes();
        } else {
            timeString += timestamp.toDate().getMinutes();
        }
        timeString += timeSuffix;

        return timeString;
    }

}
