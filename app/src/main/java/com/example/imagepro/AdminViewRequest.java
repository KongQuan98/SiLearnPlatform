package com.example.imagepro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdminViewRequest extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageView backbutton;
    TextView back;
    ConstraintLayout constraintLayout;
    float f = 0;

    TextView nopost;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    List<RequestModel> cList;
    ProgressDialog progressDialog;
    AdminViewRequestAdapter adapter;

    Button filterPending, filterAccepted, filterRejected;
    ArrayList<String> typeList = new ArrayList<>();
    private String field = "request";
    private String item = "requested";

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_request);

        if (fAuth.getCurrentUser() == null) {
            Intent intent = new Intent(AdminViewRequest.this, LoginActivity.class);
            startActivity(intent);
        }

        nopost = findViewById(R.id.nopost);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        //Get data from firebase
        recyclerView = findViewById(R.id.recycleView_Community);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cList = new ArrayList<RequestModel>();
        adapter = new AdminViewRequestAdapter(AdminViewRequest.this, cList);
        recyclerView.setAdapter(adapter);

        EventChangeListener("request", "requested");
        adapter.notifyDataSetChanged();

        //Swipe for refresh function
        swipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            cList = new ArrayList<>();
            adapter = new AdminViewRequestAdapter(AdminViewRequest.this, cList);
            recyclerView.setAdapter(adapter);
            cList.clear();
            EventChangeListener(field, item);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getApplicationContext(), "Successfully Refreshed", Toast.LENGTH_SHORT).show();
        });

        //filter
        filterPending = findViewById(R.id.pending);
        filterAccepted = findViewById(R.id.accepted);
        filterRejected = findViewById(R.id.rejected);

        filterPending.setOnClickListener(v -> {
            filterPending.setBackgroundResource(R.drawable.button_bg);
            filterPending.setTextColor(Color.WHITE);
            filterAccepted.setBackgroundResource(R.drawable.edit_text);
            filterAccepted.setTextColor(Color.BLACK);
            filterRejected.setBackgroundResource(R.drawable.edit_text);
            filterRejected.setTextColor(Color.BLACK);

            cList.clear();
            field = "request";
            item = "requested";
            EventChangeListener(field, item);
            adapter.notifyDataSetChanged();
            Toast.makeText(v.getContext(), "Now Showing Pending Request", Toast.LENGTH_SHORT).show();
        });

        filterAccepted.setOnClickListener(v -> {
            filterPending.setBackgroundResource(R.drawable.edit_text);
            filterPending.setTextColor(Color.BLACK);
            filterAccepted.setBackgroundResource(R.drawable.button_bg);
            filterAccepted.setTextColor(Color.WHITE);
            filterRejected.setBackgroundResource(R.drawable.edit_text);
            filterRejected.setTextColor(Color.BLACK);

            cList.clear();
            field = "request";
            item = "accepted";
            EventChangeListener(field, item);
            adapter.notifyDataSetChanged();
            Toast.makeText(v.getContext(), "Now Showing Accepted Request", Toast.LENGTH_SHORT).show();
        });

        filterRejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                filterPending.setBackgroundResource(R.drawable.edit_text);
                filterPending.setTextColor(Color.BLACK);
                filterAccepted.setBackgroundResource(R.drawable.edit_text);
                filterAccepted.setTextColor(Color.BLACK);
                filterRejected.setBackgroundResource(R.drawable.button_bg);
                filterRejected.setTextColor(Color.WHITE);

                cList.clear();
                field = "request";
                item = "rejected";
                EventChangeListener(field, item);
                adapter.notifyDataSetChanged();
                Toast.makeText(v.getContext(), "Now Showing Rejected Request", Toast.LENGTH_SHORT).show();
            }
        });

        //Navigation bottom
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menuCamera);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHome:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.menuCamera:
                        return true;
                    case R.id.menuLearn: {
                        startActivity(new Intent(getApplicationContext(), LearningClass.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }
                    case R.id.menuCommunity: {
                        startActivity(new Intent(getApplicationContext(), CommunityClass.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }
                    case R.id.menuProfile: {
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }
                }
                return false;
            }
        });

        //animation
        constraintLayout = findViewById(R.id.layoutHeader);
        constraintLayout.setTranslationY(-300);
        constraintLayout.setAlpha(f);
        constraintLayout.animate().translationY(0).alpha(1).setDuration(500).setStartDelay(400).start();

        //naviagtion
        backbutton = findViewById(R.id.backArrow);
        back = findViewById(R.id.back);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                startActivity(intent);
            }
        });
    }

    private void EventChangeListener(String field, String item) {
        fStore.collection("adminRequest").whereEqualTo(field, item)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            nopost.setVisibility(View.VISIBLE);
                            Log.e("FireStore Error", error.getMessage());
                            return;
                        } else {
                            if (value.getDocuments() != null && !value.getDocuments().isEmpty()) {

                                for (DocumentChange dc : value.getDocumentChanges()) {
                                    if (dc.getType() == DocumentChange.Type.ADDED) {
                                        cList.add(dc.getDocument().toObject(RequestModel.class));
                                    }
                                    adapter.notifyDataSetChanged();
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }
                                nopost.setVisibility(View.INVISIBLE);
                            } else {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                nopost.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }
}