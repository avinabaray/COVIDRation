package com.avinabaray.crm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.avinabaray.crm.Adapters.RationDisplayAdapter;
import com.avinabaray.crm.Models.RationRequestModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AdminActivity extends BaseActivity {

    Spinner categorySpinner;
    private ArrayList<Long> spinnerPos = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<String>();
    private Activity mActivity = this;
    private RecyclerView requestsRecy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        categorySpinner = findViewById(R.id.categorySpinner);
        requestsRecy = findViewById(R.id.requestsRecy);

        categories.add("Pending");
        categories.add("Approved");
        categories.add("Delivered");
        categories.add("Rejected");

        spinnerPos.add(0L);
        spinnerPos.add(1L);
        spinnerPos.add(2L);
        spinnerPos.add(3L);

//        refreshRecyclerView(spinnerPos.get(0));

        ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(
                mActivity,
                android.R.layout.simple_spinner_dropdown_item,
                categories
        );

        categorySpinner.setAdapter(categoriesAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshRecyclerView(spinnerPos.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }

    private void refreshRecyclerView(final Long requestStatus) {
        final ArrayList<RationRequestModel> currRationModel = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("rationRequest")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            currRationModel.clear();
                            RationRequestModel tempRationModel = documentSnapshot.toObject(RationRequestModel.class);
                            if (tempRationModel.getRequestStatus().equals(requestStatus)) {
                                currRationModel.add(documentSnapshot.toObject(RationRequestModel.class));
                            }
                        }

                        Log.wtf("ADPT_SIZE", String.valueOf(currRationModel.size()));
                        RationDisplayAdapter rationDisplayAdapter = new RationDisplayAdapter(mActivity, currRationModel);
                        requestsRecy.setAdapter(rationDisplayAdapter);
                        requestsRecy.setLayoutManager(new LinearLayoutManager(mActivity));

                    }
                });
    }
}
