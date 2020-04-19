package com.avinabaray.crm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.avinabaray.crm.Adapters.EditItemListAdapter;
import com.avinabaray.crm.Utils.CommonMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EditItemListActivity extends AppCompatActivity {

    private static final String TAG = "EditItmAct";
    private RecyclerView itemListRecycler;
    Activity mActivity;
    DocumentReference mDocRef = FirebaseFirestore.getInstance().collection("fields").document("itemNames");
    ArrayList<String> itemNames = new ArrayList<>();
    ArrayList<String> itemUnits = new ArrayList<>();
//    ArrayList<String> newItemNames = new ArrayList<>();
//    ArrayList<String> newItemUnits = new ArrayList<>();
    private Button updateItemListBtn;
    private CommonMethods commonMethods = new CommonMethods();
    private FloatingActionButton addNewItemFab;
    private EditItemListAdapter editItemListAdapter;
    private View rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item_list);
        mActivity = this;

        itemListRecycler = findViewById(R.id.itemListRecycler);
        updateItemListBtn = findViewById(R.id.updateItemListBtn);
        addNewItemFab = findViewById(R.id.addNewItemFab);
        rootLayout = findViewById(R.id.rootLayout);

        commonMethods.loadingDialogStart(mActivity, getString(R.string.fetch_item_list));
        mDocRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        commonMethods.loadingDialogStop();
                        itemNames = (ArrayList<String>) documentSnapshot.get("itemNames");
                        itemUnits = (ArrayList<String>) documentSnapshot.get("itemUnits");

                        editItemListAdapter = new EditItemListAdapter(mActivity,
                                itemNames,
                                itemUnits);

                        editItemListAdapter.addOnDataChangeListener(new EditItemListAdapter.OnDataChangeListener() {
                            @Override
                            public void setItemName(int position, String itemName) {
                                itemNames.set(position, itemName);
                                Log.wtf("NAME_setname", itemNames.toString());
                            }

                            @Override
                            public void setItemUnit(int position, String itemUnit) {
                                itemUnits.set(position, itemUnit);
                                Log.wtf("NAME_setunit", itemNames.toString());
                            }

                            @Override
                            public void deleteItem(int position) {
                                itemNames.remove(position);
                                itemUnits.remove(position);
                                Log.wtf("NAME_del", itemNames.toString());
                                editItemListAdapter.notifyDataSetChanged();
                            }
                        });

                        itemListRecycler.setAdapter(editItemListAdapter);
                        itemListRecycler.setLayoutManager(new LinearLayoutManager(mActivity));

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                commonMethods.loadingDialogStop();
                Toast.makeText(mActivity, R.string.oops_error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: ", e);
            }
        });

        addNewItemFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemNames.add("");
                itemUnits.add("");
                Log.wtf("NAME_FAB", itemNames.toString());

                editItemListAdapter.notifyDataSetChanged();
            }
        });

        updateItemListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("NAME", itemNames.toString());

                for (int i=0; i<itemNames.size(); i++) {
                    if (itemNames.get(i).isEmpty() ^ itemUnits.get(i).isEmpty()) {
                        commonMethods.createAlert(new AlertDialog.Builder(mActivity), "Fill up all the fields correctly");
                        return;
                    }
                }
//                itemNames.removeAll(Collections.singleton(""));
//                itemUnits.removeAll(Collections.singleton(""));
//                editItemListAdapter.notifyDataSetChanged();

                Map<String, ArrayList<String>> data = new HashMap<>();
                data.put("itemNames", itemNames);
                data.put("itemUnits", itemUnits);

                commonMethods.loadingDialogStart(mActivity);
                mDocRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        commonMethods.loadingDialogStop();
//                        Log.wtf("NAME", itemNames.toString());
                        finish();
                        Toast.makeText(mActivity, "Items Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
}
