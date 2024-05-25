package com.example.imagepro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerView;
    List<LibraryModel> mList;
    LibraryAdapter adapter;
    ConstraintLayout layoutheader, recycleViewConstraint;
    SearchView learningSearch;
    ImageView backButton;
    TextView backText;
    float f = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        //Recycle View
        recyclerView = findViewById(R.id.recycleView_Learn);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mList = new ArrayList<>();

        // Add List to Recycle View
        mList.add(new LibraryModel("A"));
        mList.add(new LibraryModel("B"));
        mList.add(new LibraryModel("C"));
        mList.add(new LibraryModel("D"));
        mList.add(new LibraryModel("E"));
        mList.add(new LibraryModel("F"));
        mList.add(new LibraryModel("G"));
        mList.add(new LibraryModel("H"));
        mList.add(new LibraryModel("I"));
        mList.add(new LibraryModel("J"));
        mList.add(new LibraryModel("K"));
        mList.add(new LibraryModel("L"));
        mList.add(new LibraryModel("M"));
        mList.add(new LibraryModel("N"));
        mList.add(new LibraryModel("O"));
        mList.add(new LibraryModel("P"));
        mList.add(new LibraryModel("Q"));
        mList.add(new LibraryModel("R"));
        mList.add(new LibraryModel("S"));
        mList.add(new LibraryModel("T"));
        mList.add(new LibraryModel("U"));
        mList.add(new LibraryModel("V"));
        mList.add(new LibraryModel("W"));
        mList.add(new LibraryModel("X"));
        mList.add(new LibraryModel("Y"));
        mList.add(new LibraryModel("Z"));
        mList.add(new LibraryModel("1"));
        mList.add(new LibraryModel("2"));
        mList.add(new LibraryModel("3"));
        mList.add(new LibraryModel("4"));
        mList.add(new LibraryModel("5"));
        mList.add(new LibraryModel("6"));
        mList.add(new LibraryModel("7"));
        mList.add(new LibraryModel("8"));
        mList.add(new LibraryModel("9"));
        mList.add(new LibraryModel("10"));
        mList.add(new LibraryModel("Hello"));
        mList.add(new LibraryModel("Good Bye"));
        mList.add(new LibraryModel("Thank You"));
        mList.add(new LibraryModel("Please"));
        mList.add(new LibraryModel("Sorry"));

        adapter = new LibraryAdapter(mList);
        recyclerView.setAdapter(adapter);

        //Search View
        learningSearch = findViewById(R.id.learningSearch);
        learningSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        //Navigation bottom
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHome:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.menuCamera:
                        startActivity(new Intent(getApplicationContext(), CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.menuLearn: {
                        startActivity(new Intent(getApplicationContext(), LearningClass.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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

        //back to main menu
        backButton = findViewById(R.id.backArrow_learn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        backText = findViewById(R.id.back);
        backText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });


        //animation
        layoutheader = findViewById(R.id.layoutHeader);
        recycleViewConstraint = findViewById(R.id.recycleViewConstraint);

        layoutheader.setTranslationY(-300);
        layoutheader.setAlpha(f);
        layoutheader.animate().translationY(0).alpha(1).setDuration(500).setStartDelay(400).start();

        recycleViewConstraint.setTranslationX(-300);
        recycleViewConstraint.setAlpha(f);
        recycleViewConstraint.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(400).start();
    }

    //search void
     private void filter(String newText) {
         List<LibraryModel> filteredList = new ArrayList<>();
         for (LibraryModel item : mList){
             if (item.getItemText().toLowerCase().contains(newText.toLowerCase())){
                 filteredList.add(item);
             }
         }
         adapter.filterList(filteredList);
     }
}