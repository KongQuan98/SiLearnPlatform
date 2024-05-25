package com.example.imagepro;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminViewUser extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    ImageView backbutton;
    TextView back;
    ConstraintLayout constraintLayout;
    float f = 0;

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerView;
    List<UserModel> cList;
    ProgressDialog progressDialog;
    AdminViewUserAdapter adapter;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    TextView nopost, save, cancel_button;
    RoundedImageView postImage;
    EditText fullname, organization, email;
    Spinner spinner;
    String positionText = "Self-Learner";

    SearchView userSearch;
    Button filterAll, filterType;
    ArrayList<String> typeList = new ArrayList<>();
    private String field = "";
    private String item = "";

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    StorageReference fStorage = FirebaseStorage.getInstance().getReference("profileImages");

    String imageLink;
    Uri uploadedImageUri;

    public Uri getUploadedImageUri() {
        return uploadedImageUri;
    }

    public void setUploadedImageUri(Uri uploadedImageUri) {
        this.uploadedImageUri = uploadedImageUri;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user);

        if (fAuth.getCurrentUser() == null) {
            Intent intent = new Intent(AdminViewUser.this, LoginActivity.class);
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

        cList = new ArrayList<UserModel>();
        adapter = new AdminViewUserAdapter(AdminViewUser.this, cList);
        recyclerView.setAdapter(adapter);

        EventChangeListener(field, item);
        adapter.notifyDataSetChanged();

        //Swipe for refresh function
        swipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cList = new ArrayList<UserModel>();
                adapter = new AdminViewUserAdapter(AdminViewUser.this, cList);
                recyclerView.setAdapter(adapter);
                cList.clear();
                EventChangeListener(field, item);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(), "Successfully Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        //filter
        filterAll = findViewById(R.id.filterAll);
        filterType = findViewById(R.id.filterType);

        filterAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterAll.setBackgroundResource(R.drawable.button_bg);
                filterAll.setTextColor(Color.WHITE);
                filterType.setBackgroundResource(R.drawable.edit_text);
                filterType.setTextColor(Color.BLACK);
                cList.clear();
                field = "";
                item = "";
                EventChangeListener(field, item);
                adapter.notifyDataSetChanged();
                Toast.makeText(v.getContext(), "Now Showing All Post", Toast.LENGTH_SHORT).show();
            }
        });

        filterType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //pop up filter window
                Dialog dialog = new Dialog(AdminViewUser.this);
                View popupView = LayoutInflater.from(AdminViewUser.this).inflate(
                        R.layout.org_filter_box, findViewById(R.id.popup_container));
                RelativeLayout relativeLayout = popupView.findViewById(R.id.popup_container);
                RadioGroup radioGroup = (RadioGroup) popupView.findViewById(R.id.radioGroup);
                Button select = popupView.findViewById(R.id.deletePost);
                final String[] selectedRadioText = {""};

                // get organization list from firestore
                fStore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        typeList = new ArrayList<String>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            if (document.getString("userType") != null && !document.getString("userType").isEmpty()) {
                                String getString = document.getString("userType");
                                typeList.add(getString);
                            }
                        }

                        //load list into popup window
                        ArrayList<String> checkedList = new ArrayList<>();
                        for (int i = 1; i < typeList.size() + 1; i++) {
                            if (!checkedList.toString().contains(typeList.get(i-1))) {
                                RadioButton radioButton = new RadioButton(v.getContext());
                                radioButton.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                String text = typeList.get(i - 1);
                                radioButton.setText(text);
                                radioButton.setId(i);
                                radioGroup.addView(radioButton);
                                checkedList.add(text);
                            }
                        }

                        select.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (radioGroup.getCheckedRadioButtonId() == -1) {
                                    Toast.makeText(v.getContext(), "Please select one of the user type", Toast.LENGTH_SHORT).show();

                                } else {
                                    filterType.setBackgroundResource(R.drawable.button_bg);
                                    filterType.setTextColor(Color.WHITE);
                                    filterAll.setBackgroundResource(R.drawable.edit_text);
                                    filterAll.setTextColor(Color.BLACK);
                                    int checkedid = radioGroup.getCheckedRadioButtonId();
                                    item = typeList.get(checkedid-1);
                                    field = "userType";
                                    cList.clear();
                                    EventChangeListener(field, item);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(v.getContext(), "Now Showing List of " + item, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });

                dialog.setContentView(popupView);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        //edit user function
        Intent intent = getIntent();
        String userID = "";
        if (intent.getStringExtra("edit") != null) {
            userID = intent.getStringExtra("edit");

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                    AdminViewUser.this, R.style.BottomSheetDialogTheme
            );

            View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.layout_bottom_user, (LinearLayout) findViewById(R.id.bottomSheetContainer));

            save = bottomSheetView.findViewById(R.id.save);
            cancel_button = bottomSheetView.findViewById(R.id.cancel_button);
            postImage = bottomSheetView.findViewById(R.id.post_image);
            fullname = bottomSheetView.findViewById(R.id.fullName);
            organization = bottomSheetView.findViewById(R.id.Organization);
            spinner = bottomSheetView.findViewById(R.id.position_spinner_admin);
            email = bottomSheetView.findViewById(R.id.email);

            ArrayAdapter<CharSequence> adapterSpin = ArrayAdapter.createFromResource(getApplicationContext(), R.array.positions, android.R.layout.simple_spinner_item);
            adapterSpin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapterSpin);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    positionText = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            DocumentReference profileData = fStore.collection("users").document(userID);
            profileData.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String mail = documentSnapshot.getString("email");
                        String name = documentSnapshot.getString("fName");
                        String org = documentSnapshot.getString("organization");
                        String postImg = documentSnapshot.getString("profileImages");
                        String posi = documentSnapshot.getString("userType");

                        email.setText(mail);
                        email.setTag(email.getKeyListener());
                        email.setKeyListener(null);
                        email.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                email.setError("Email cannot be changed");
                            }
                        });
                        fullname.setText(name);
                        organization.setText(org);
                        if (postImg != null && !postImg.equals("")){
                            Picasso.with(AdminViewUser.this).load(postImg).fit().centerCrop().into(postImage);
                        }
                        if (!posi.equals("System Administrator")){
                            spinner.setVisibility(View.VISIBLE);
                            int spinnerPosition = adapterSpin.getPosition(posi);
                            spinner.setSelection(spinnerPosition);
                        }else {
                            spinner.setVisibility(View.INVISIBLE);
                        }

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AdminViewUser.this, "Failed to load post", Toast.LENGTH_SHORT).show();
                }
            });

            //Upload image from gallery function
            postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AdminViewUser.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                    } else {
                        selectImage();
                    }
                }
            });

            String finalUserID = userID;
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadFiletoStorage(getUploadedImageUri(), finalUserID);
                    progressDialog.dismiss();
                    bottomSheetDialog.dismiss();

                }
            });

            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    email.setText("");
                    fullname.setText("");
                    organization.setText("");
                    Picasso.with(getApplicationContext()).load((Uri) null).fit().centerCrop().into(postImage);
                    bottomSheetDialog.dismiss();
                }
            });

            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        }

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
        if (field.equals("")) {
            fStore.collection("users")
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (!value.getDocuments().isEmpty()) {
                                if (error != null) {
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    Log.e("FireStore Error", error.getMessage());
                                    return;
                                }
                                for (DocumentChange dc : value.getDocumentChanges()) {
                                    if (dc.getType() == DocumentChange.Type.ADDED) {
                                        cList.add(dc.getDocument().toObject(UserModel.class));
                                    }
                                    adapter.notifyDataSetChanged();
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }

                                //Search View
                                userSearch = findViewById(R.id.learningSearch);
                                userSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

                            } else {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                nopost.setVisibility(View.VISIBLE);
                            }

                        }
                    });
        } else {
            fStore.collection("users").whereEqualTo(field, item)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                Log.e("FireStore Error", error.getMessage());
                                return;
                            } else {
                                if (value.getDocuments() != null && !value.getDocuments().isEmpty()) {

                                    for (DocumentChange dc : value.getDocumentChanges()) {
                                        if (dc.getType() == DocumentChange.Type.ADDED) {
                                            cList.add(dc.getDocument().toObject(UserModel.class));
                                        }
                                        adapter.notifyDataSetChanged();
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                    }

                                    //Search View
                                    userSearch = findViewById(R.id.learningSearch);
                                    userSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                setUploadedImageUri(data.getData());
                if (getUploadedImageUri() != null) {
                    try {
//                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
//                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                        uploadedImage = bitmap;
                        Picasso.with(getApplicationContext()).load(getUploadedImageUri()).fit().centerCrop().into(postImage);


//                        File selectedImageFile = new File(getPathFromUri(selectedImageUri));
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadFiletoStorage(Uri getImageUri, String mode) {
        if (getImageUri != null) {
            StorageReference fileReference = fStorage.child(System.currentTimeMillis() + "." + getFileExtension(getImageUri));
            fileReference.putFile(getImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            imageLink = uri.toString();
                                            if (mode.equals("add")) {
//                                                String descrip = description.getText().toString();
//                                                String userID = fAuth.getCurrentUser().getUid();
//                                                Date currentTime = Calendar.getInstance().getTime();
//                                                String time = currentTime.toString();
//                                                List<String> commentsLikes = new ArrayList<>();
//
//                                                DocumentReference communityData = fStore.collection("community").document(fixed + "");
//                                                Map<String, Object> random = new HashMap<>();
//                                                random.put("postImages", imageLink);
//                                                random.put("description", descrip);
//                                                random.put("userID", userID);
//                                                random.put("userImage", profileImages[0]);
//                                                random.put("userOrg", postOrg[0]);
//                                                random.put("postTime", time);
//                                                random.put("documentID", fixed + "");
//                                                random.put("comments", commentsLikes);
//                                                random.put("commentID", commentsLikes);
//                                                random.put("likes", commentsLikes);
//
//                                                communityData.set(random).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                    @Override
//                                                    public void onSuccess(Void aVoid) {
//                                                        Toast.makeText(CommunityClass.this, "Adding post...", Toast.LENGTH_SHORT);
//                                                    }
//                                                }).addOnFailureListener(new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//                                                        Toast.makeText(CommunityClass.this, "Post not uploaded", Toast.LENGTH_SHORT);
//                                                    }
//                                                });
                                            } else {
                                                String name = fullname.getText().toString();
                                                String organ = organization.getText().toString();
                                                String usertype = positionText;

                                                DocumentReference profileData = fStore.collection("users").document(mode);
                                                Map<String, Object> random = new HashMap<>();
                                                random.put("profileImages", imageLink);
                                                random.put("fName", name);
                                                random.put("organization", organ);
                                                random.put("userType", usertype);

                                                profileData.update(random).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(AdminViewUser.this, "User Updated", Toast.LENGTH_SHORT);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(AdminViewUser.this, "User not updated", Toast.LENGTH_SHORT);
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AdminViewUser.this, "Error Uploading Image", Toast.LENGTH_SHORT);
                        }
                    });
        } else {
            String name = fullname.getText().toString();
            String organ = organization.getText().toString();
            String usertype = positionText;

            DocumentReference profileData = fStore.collection("users").document(mode);
            Map<String, Object> random = new HashMap<>();
            random.put("fName", name);
            random.put("organization", organ);
            random.put("userType", usertype);

            profileData.update(random).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AdminViewUser.this, "Post Updated", Toast.LENGTH_SHORT);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AdminViewUser.this, "Post not updated", Toast.LENGTH_SHORT);
                }
            });
        }
    }

    //search void
    private void filter(String newText) {
        List<UserModel> filteredList = new ArrayList<>();
        for (UserModel item : cList){
            if (item.getfName().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }
}