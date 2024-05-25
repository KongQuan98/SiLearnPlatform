 package com.example.imagepro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

 public class LearningClass extends AppCompatActivity {

     BottomNavigationView bottomNavigationView;
     RecyclerView recyclerView;
     List<DataModel> mList;
     ItemAdapter adapter;
     ConstraintLayout layoutheader, recycleViewConstraint;
     TextView edit, add, back, alphaValue, numValue, greetValue;
     ImageView backArrow, refresh;
     SearchView learningSearch;
     ProgressBar progressBarAlpha, progressBarNum, progressBarGreet;
     float f = 0;
     boolean admin = false;
     double alphaCount, numCount, greetCount;
     String checkedAlph = "";

     FirebaseFirestore fStore = FirebaseFirestore.getInstance();
     FirebaseAuth fAuth = FirebaseAuth.getInstance();

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_learning_class);

         if (fAuth.getCurrentUser() == null){
             Intent intent = new Intent(LearningClass.this, LoginActivity.class);
             startActivity(intent);
         }

         Intent intent = getIntent();
//         String alphtodb = "";
//         final String[] alphfromdb = {""};

         try {
             if (intent.getStringExtra("checkSign") != null){
                 JSONObject jsonObject = new JSONObject(intent.getStringExtra("checkSign"));
                 checkedAlph = jsonObject.getString("alphabet");
                 String userID = fAuth.getCurrentUser().getUid();
                 DocumentReference alphaDoc = fStore.collection("users").document(userID);
                 String finalCheckedAlph = checkedAlph;
                 alphaDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                     @Override
                     public void onSuccess(DocumentSnapshot documentSnapshot) {
                         if (documentSnapshot.exists()) {
                             alphaDoc.update("wordsChecked", FieldValue.arrayUnion(finalCheckedAlph));
                         }
//                         else{
//                             Map<String, Object> random = new HashMap<>();
//                             random.put("checked words", Arrays.asList(finalCheckedAlph));
//                             alphaDoc.set(random).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                 @Override
//                                 public void onSuccess(Void aVoid) {
//                                     Log.d(TAG, "Alphabet is added to " + userID);
//                                 }
//                             });
//                         }
                     }
                 });
//
//                 alphtodb = checkedAlph + alphfromdb[0] ;


             }
         } catch (JSONException e) {
             e.printStackTrace();
         }

         //Recycle View
         recyclerView = findViewById(R.id.recycleView_Learn);
         recyclerView.setHasFixedSize(true);
         recyclerView.setLayoutManager(new LinearLayoutManager(this));

         mList = new ArrayList<>();

         String path = null, path2 = null, path3 = null;

         List<String> nestedList = new ArrayList<>();
         nestedList.add("A");
         nestedList.add("B");
         nestedList.add("C");
         nestedList.add("D");
         nestedList.add("E");
         nestedList.add("F");
         nestedList.add("G");
         nestedList.add("H");
         nestedList.add("I");
         nestedList.add("J");
         nestedList.add("K");
         nestedList.add("L");
         nestedList.add("M");
         nestedList.add("N");
         nestedList.add("O");
         nestedList.add("P");
         nestedList.add("Q");
         nestedList.add("R");
         nestedList.add("S");
         nestedList.add("T");
         nestedList.add("U");
         nestedList.add("V");
         nestedList.add("W");
         nestedList.add("X");
         nestedList.add("Y");
         nestedList.add("Z");
         List<String> videoNestedList = new ArrayList<>();
         videoNestedList.add("bsYLoba2CV0");
         videoNestedList.add("3A4pdSEmuTE");
         videoNestedList.add("dJqoBH9NDOI");
         videoNestedList.add("unaicv88lLI");
         videoNestedList.add("DuqblObUlxk");
         videoNestedList.add("SadgQ0mvY50");
         videoNestedList.add("UM8feCdEh2k");
         videoNestedList.add("gz6Ux2SWVTc");
         videoNestedList.add("Hyfqjm3fFNk");
         videoNestedList.add("uvqNhKdoUw0");
         videoNestedList.add("9KP2yPZTZ5o");
         videoNestedList.add("PUVdWVNCZTk");
         videoNestedList.add("rrTRRfUzlB0");
         videoNestedList.add("fF5_b0dhTS4");
         videoNestedList.add("lvl-UxT0oOM");
         videoNestedList.add("9dlrwu_9qk8");
         videoNestedList.add("asa1mwMzuN8");
         videoNestedList.add("S9Wsq66A9xI");
         videoNestedList.add("sABdqaGImmw");
         videoNestedList.add("Z4RUlAm5EWA");
         videoNestedList.add("TMk2OJAmOmE");
         videoNestedList.add("0mjIJ_Maf34");
         videoNestedList.add("qd5icmqdALI");
         videoNestedList.add("vdL-hKuSwTs");
         videoNestedList.add("t689H0-w-Ww");
         videoNestedList.add("0BlkX6MWByw");

         List<String> nestedList2 = new ArrayList<>();
         nestedList2.add("1");
         nestedList2.add("2");
         nestedList2.add("3");
         nestedList2.add("4");
         nestedList2.add("5");
         nestedList2.add("6");
         nestedList2.add("7");
         nestedList2.add("8");
         nestedList2.add("9");
         nestedList2.add("10");

         List<String> videoNestedList2 = new ArrayList<>();
         videoNestedList2.add("8K4yWpiVJPk");
         videoNestedList2.add("fQppTpwisZQ");
         videoNestedList2.add("J58DGrShwuY");
         videoNestedList2.add("txpKOXcmwn0");
         videoNestedList2.add("U8SpX0zlFpE");
         videoNestedList2.add("RsAsBv6I5T8");
         videoNestedList2.add("nnnTYk1UlS4");
         videoNestedList2.add("u40PBl5xRTQ");
         videoNestedList2.add("fB1UgWDghow");
         videoNestedList2.add("XodvKGVG5_s");

         List<String> nestedList3 = new ArrayList<>();
         nestedList3.add("Hello");
         nestedList3.add("Good Bye");
         nestedList3.add("Thank You");
         nestedList3.add("Please");
         nestedList3.add("Sorry");

         List<String> videoNestedList3 = new ArrayList<>();
         videoNestedList3.add("xSYGBpalVTA");
         videoNestedList3.add("Hbwtcoz_CBA");
         videoNestedList3.add("O5Tv5KT1VLs");
         videoNestedList3.add("VBr_hnELRqM");
         videoNestedList3.add("0W_Jbl7mxuU");

         //Progress Bar
         progressBarAlpha = findViewById(R.id.progressBar_alpha);
         progressBarNum = findViewById(R.id.progressBar_num);
         progressBarGreet = findViewById(R.id.progressBar_greet);
         alphaValue = findViewById(R.id.alpha_value);
         numValue = findViewById(R.id.num_value);
         greetValue = findViewById(R.id.greet_value);

         DocumentReference profileData = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
         profileData.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
             @Override
             public void onSuccess(DocumentSnapshot documentSnapshot) {
                 if (documentSnapshot.exists()) {
                     List<String> wordsLearnt = (List<String>) documentSnapshot.get("wordsChecked");

                     for (int i = 0; i < wordsLearnt.size(); i++){
                         for (int j = 0; j < nestedList.size(); j++){
                             if (wordsLearnt.get(i).equals(nestedList.get(j))){
                                 alphaCount = alphaCount + 1;
                                 break;
                             }
                         }
                         for (int k = 0; k < nestedList2.size(); k++){
                             if (wordsLearnt.get(i).equals(nestedList2.get(k))){
                                 numCount = numCount + 1;
                                 break;
                             }
                         }
                         for (int l = 0; l < nestedList3.size(); l++){
                             if (wordsLearnt.get(i).equals(nestedList3.get(l))){
                                 greetCount = greetCount + 1;
                                 break;
                             }
                         }
                     }

                     double percentageAlpha = (alphaCount / 26 * 100);
                     double percentageNum = (numCount / 10 * 100);
                     double percentageGreet = (greetCount / 5 * 100);

                     System.out.println(percentageAlpha);

                     progressBarAlpha.setProgress((int)percentageAlpha);
                     progressBarNum.setProgress((int)percentageNum);
                     progressBarGreet.setProgress((int)percentageGreet);

                     alphaValue.setText((int)percentageAlpha + "%");
                     numValue.setText((int)percentageNum + "%");
                     greetValue.setText((int)percentageGreet + "%");

                     // Add List to Recycle View
                     mList.add(new DataModel(nestedList, "Alphabet", videoNestedList, checkedAlph, alphaCount));
                     mList.add(new DataModel(nestedList2, "Numbers", videoNestedList2, checkedAlph, numCount));
                     mList.add(new DataModel(nestedList3, "Greetings", videoNestedList3, checkedAlph, greetCount));

                     adapter = new ItemAdapter(mList);
                     recyclerView.setAdapter(adapter);


                 } else {
                     Toast.makeText(LearningClass.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                 }
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(LearningClass.this, "Error!", Toast.LENGTH_SHORT).show();
                 Log.d(TAG, e.toString());
             }
         });



         //Search View
//         learningSearch = findViewById(R.id.learningSearch);
//         learningSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//             @Override
//             public boolean onQueryTextSubmit(String query) {
//                 return false;
//             }
//
//             @Override
//             public boolean onQueryTextChange(String newText) {
//                 filter(newText);
//                 return false;
//             }
//         });

         //Navigation bottom
         bottomNavigationView = findViewById(R.id.bottomNavigationView);

         bottomNavigationView.setSelectedItemId(R.id.menuLearn);

         bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 switch (item.getItemId()){
                     case R.id.menuHome:
                         startActivity(new Intent(getApplicationContext(), MainActivity.class));
                         overridePendingTransition(0, 0);
                         return true;
                     case R.id.menuCamera:
                         startActivity(new Intent(LearningClass.this,CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                         overridePendingTransition(0, 0);
                         return true;
                     case R.id.menuLearn: {
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
         layoutheader = findViewById(R.id.layoutHeader);
         recycleViewConstraint = findViewById(R.id.recycleViewConstraint);

         layoutheader.setTranslationY(-300);
         layoutheader.setAlpha(f);
         layoutheader.animate().translationY(0).alpha(1).setDuration(500).setStartDelay(400).start();

         recycleViewConstraint.setTranslationX(-300);
         recycleViewConstraint.setAlpha(f);
         recycleViewConstraint.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(400).start();



         // admin function
         refresh = findViewById(R.id.refresh);
         add = findViewById(R.id.add_learn);
         backArrow = findViewById(R.id.backArrow_learn);

         backArrow.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                 startActivity(intent);
             }
         });

         refresh.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(getApplicationContext(), LearningClass.class);
                 startActivity(intent);
             }
         });

//         if(admin == true){
//             edit.setVisibility(View.VISIBLE);
//         }
//
//         final ItemTouchHelper[] itemTouchHelper = {new ItemTouchHelper(simpleCallback)};
//
//         edit.setOnClickListener(new View.OnClickListener() {
//                 @Override
//                 public void onClick(View v) {
//                     if (edit.getText().equals("Edit")){
//                         itemTouchHelper[0].attachToRecyclerView(recyclerView);
//                         edit.setText("Done");
//                         backArrow.setVisibility(View.INVISIBLE);
//                         add.setVisibility(View.VISIBLE);
//                     } else if (edit.getText().equals("Done")){
//                         edit.setText("Edit");
//                         add.setVisibility(View.INVISIBLE);
//                         backArrow.setVisibility(View.VISIBLE);
//                         itemTouchHelper[0] = new ItemTouchHelper(stopCallback);
//                         itemTouchHelper[0].attachToRecyclerView(recyclerView);
//                     }
//                 }
//             });

     }


     //search void
//     private void filter(String newText) {
//         List<DataModel> filteredList = new ArrayList<>();
//         for (DataModel item : mList){
//             if (item.getItemText().toLowerCase().contains(newText.toLowerCase())){
//                 filteredList.add(item);
//             }
//         }
//         adapter.filterList(filteredList);
//     }

//     String deletedItem = null;
//     List<String> deletedNest = new ArrayList<>();
//     List<String> deletedNestVideo = new ArrayList<>();
//     String deletedCheckedAlph = null;
//
//     ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN |
//             ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//         @Override
//         public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//
//             int fromPosition = viewHolder.getAdapterPosition();
//             int toPosition = target.getAdapterPosition();
//
//             Collections.swap(mList, fromPosition, toPosition);
//
//             recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
//             return false;
//         }
//
//         @Override
//         public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//             int position = viewHolder.getAdapterPosition();
//             switch (direction){
//                 case ItemTouchHelper.LEFT:
//                     DataModel getPosition = mList.get(position);
//                     deletedItem = getPosition.getItemText();
//                     deletedNest = getPosition.getNestedList();
//                     deletedNestVideo = getPosition.getNestedVideoList();
//                     deletedCheckedAlph = getPosition.getCheckAlph();
//
//                     mList.remove(position);
//                     adapter.notifyItemRemoved(position);
//                     Snackbar.make(recyclerView, deletedItem.toString(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
//                         @Override
//                         public void onClick(View v) {
//                             mList.add(position, new DataModel(deletedNest, deletedItem, deletedNestVideo, deletedCheckedAlph));
//                             adapter.notifyItemInserted(position);
//                         }
//                     }).show();
//                     break;
//             }
//         }
//     };
//
//     ItemTouchHelper.SimpleCallback stopCallback = new ItemTouchHelper.SimpleCallback(0,0) {
//         @Override
//         public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//             return false;
//         }
//
//         @Override
//         public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//         }
//     };
 }