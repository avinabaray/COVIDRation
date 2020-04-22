package com.avinabaray.crm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.avinabaray.crm.Adapters.UsersAdapter;
import com.avinabaray.crm.Models.UserModel;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_item, menu);
        MenuItem searchItem = menu.findItem(R.id.searchBar);
        searchItem.setVisible(true);

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
        return super.onCreateOptionsMenu(menu);    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_users);

        usersRecy = findViewById(R.id.usersRecy);

        userRoles.add("User");
        userRoles.add("Admin");
        userRoles.add("Super Admin");

        FirebaseFirestore.getInstance().collection("users")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (queryDocumentSnapshots != null) {
                            userModels.clear();
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                userModels.add(documentSnapshot.toObject(UserModel.class));
                            }

                            usersAdapter = new UsersAdapter(mActivity, userModels, userRoles);
                            usersRecy.setAdapter(usersAdapter);
                            usersRecy.setLayoutManager(new LinearLayoutManager(mActivity));

                        }
                    }
                });
    }
}
