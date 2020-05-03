package com.avinabaray.crm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.avinabaray.crm.Adapters.UsersAdapter;
import com.avinabaray.crm.Models.RationRequestModel;
import com.avinabaray.crm.Models.UserModel;
import com.avinabaray.crm.Utils.CommonMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class EditUsersActivity extends BaseActivity {

    private RecyclerView usersRecy;
    private ArrayList<UserModel> userModels = new ArrayList<>();
    private ArrayList<String> userRoles = new ArrayList<>();
    private Activity mActivity = this;
    private UsersAdapter usersAdapter;
    private ViewGroup rootLayout;
    private CommonMethods commonMethods = new CommonMethods();
    private LinearLayout filtersBottomSheet;
    private FloatingActionButton addUserBySuperAdmin;
    private BottomSheetBehavior<LinearLayout> filterBehavious;
    EditText editTextPhone, editTextPINCode;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_item, menu);
        MenuItem searchItem = menu.findItem(R.id.searchBar);
        MenuItem filterItems = menu.findItem(R.id.filter);
        searchItem.setVisible(true);
        filterItems.setVisible(true);

        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
        searchView.setQueryHint("User Name");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                usersAdapter.getFilter().filter(newText);
                return false;
            }
        });

        filterItems.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
//                Toast.makeText(mActivity, "Filter Tapped", Toast.LENGTH_SHORT).show();
//                filterBehavious.setState(BottomSheetBehavior.STATE_EXPANDED);
                commonMethods.makeSnack(rootLayout, "Filters coming soon...");
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_users);

        usersRecy = findViewById(R.id.usersRecy);
        rootLayout = findViewById(R.id.rootLayout);
        addUserBySuperAdmin = findViewById(R.id.addUserBySuperAdmin);
        filtersBottomSheet = findViewById(R.id.filtersBottomSheet);
        filterBehavious = BottomSheetBehavior.from(filtersBottomSheet);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPINCode = findViewById(R.id.editTextPINCode);

        filtersBottomSheet.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true; // returned true so that the touch events pass on to the Views below
            }
        });
        filterBehavious.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        addUserBySuperAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity, RegisterUserActivity.class);
                startActivity(i);
            }
        });

        userRoles.add("User");
        userRoles.add("Volunteer");
        userRoles.add("Super Admin");

        FirebaseFirestore.getInstance().collection("users")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(mActivity, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            userModels.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                userModels.add(documentSnapshot.toObject(UserModel.class));
                            }

                            final int[] noOfDeliveries = new int[userModels.size()];
                            usersAdapter = new UsersAdapter(mActivity, userModels, userRoles, noOfDeliveries, rootLayout);
                            usersRecy.setAdapter(usersAdapter);
                            usersRecy.setLayoutManager(new LinearLayoutManager(mActivity));

                            FirebaseFirestore.getInstance()
                                    .collection("rationRequest")
//                                    .whereEqualTo("requestStatus", RationRequestModel.DELIVERED)
                                    .addSnapshotListener(mActivity, new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                            for (int i=0; i<noOfDeliveries.length; i++) {
                                                noOfDeliveries[i] = 0;
                                            }
                                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                String userIdInRationRequest = documentSnapshot.getString("userId");
                                                Long requestStatus = documentSnapshot.getLong("requestStatus");
                                                for (int i=0; i<userModels.size(); i++) {
                                                    if (userModels.get(i).getId().equals(userIdInRationRequest) && requestStatus.equals(RationRequestModel.DELIVERED)) {
                                                        noOfDeliveries[i]++;
                                                    }
                                                }
                                            }
                                            usersAdapter.notifyDataSetChanged();
                                        }
                                    });

                        }
                    }
                });


        editTextPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usersAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}
