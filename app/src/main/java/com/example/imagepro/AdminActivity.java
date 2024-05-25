package com.example.imagepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AdminActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageView backbutton;
    TextView back, requestNum;
    ConstraintLayout constraintLayout;
    float f = 0;
    Button viewuser,  adminRequest, viewFeedback;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        if (fAuth.getCurrentUser() == null){
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        // admin function
        fStore.collection("users").document(fAuth.getCurrentUser().getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String userType = documentSnapshot.getString("userType");

                    //Admin function
                    if (userType != null && userType.equals("System Administrator")){
                        viewuser = findViewById(R.id.viewUser);
                        adminRequest = findViewById(R.id.adminRequest);
                        viewFeedback = findViewById(R.id.viewFeedback);
                        requestNum = findViewById(R.id.requestNum);

                        fStore.collection("adminRequest").whereEqualTo("request", "requested").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                int num = queryDocumentSnapshots.getDocuments().size();
                                requestNum.setText(num);
                                requestNum.setVisibility(View.VISIBLE);
                            }
                        });

                        viewuser.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(AdminActivity.this, AdminViewUser.class);
                                startActivity(intent);
                            }
                        });
                        adminRequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(AdminActivity.this, AdminViewRequest.class);
                                startActivity(intent);
                            }
                        });
                        viewFeedback.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }

                }
            }
        });

        //Navigation bottom
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.menuCamera);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
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

        //navigation
        backbutton = findViewById(R.id.backArrow);
        back = findViewById(R.id.back);
        backbutton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
        back.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        });
    }
}