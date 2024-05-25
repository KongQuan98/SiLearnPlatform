package com.example.imagepro;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.checkerframework.common.subtyping.qual.Bottom;
import org.w3c.dom.Text;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class CommunityClass extends AppCompatActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    BottomNavigationView bottomNavigationView;
    ImageView backbutton;
    TextView back, add_post, submit_post, cancel_post, nopost;
    Button filterAll, filterMyPost, filterOrg;
    ConstraintLayout constraintLayout;
    ConstraintLayout recycleViewConstraint;
    RecyclerView recyclerView;
    List<CommunityModel> cList;
    CommunityPostAdapter adapter;
    float f = 0;

    RoundedImageView postImage;
    EditText description;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    Uri uploadedImageUri;
    String imageLink;
    ProgressDialog progressDialog;

    final String[] postOrg = new String[1];
    final String[] profileImages = new String[1];

    private String field = "";
    private String item = "";
    ArrayList<String> orgList = new ArrayList<>();


    public Uri getUploadedImageUri() {
        return uploadedImageUri;
    }

    public void setUploadedImageUri(Uri uploadedImageUri) {
        this.uploadedImageUri = uploadedImageUri;
    }

    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    StorageReference fStorage = FirebaseStorage.getInstance().getReference("communityPost");

    Random random = new Random();
    int randomNumber;
    int fixed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_class);

        randomNumber = random.nextInt(999999 - 111111) + 65;
        fixed = randomNumber;

        nopost = findViewById(R.id.nopost);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        if (fAuth.getCurrentUser() == null) {
            Intent intent = new Intent(CommunityClass.this, LoginActivity.class);
            startActivity(intent);
        }

        //Get data from firebase
        recyclerView = findViewById(R.id.recycleView_Community);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cList = new ArrayList<CommunityModel>();
        adapter = new CommunityPostAdapter(CommunityClass.this, cList);
        recyclerView.setAdapter(adapter);

        //filter function
        filterAll = findViewById(R.id.filterAll);
        filterMyPost = findViewById(R.id.filterMyPost);
        filterOrg = findViewById(R.id.filerOrg);

        EventChangeListener(field, item);
        adapter.notifyDataSetChanged();

        filterAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterAll.setBackgroundResource(R.drawable.button_bg);
                filterAll.setTextColor(Color.WHITE);
                filterMyPost.setBackgroundResource(R.drawable.edit_text);
                filterMyPost.setTextColor(Color.BLACK);
                filterOrg.setBackgroundResource(R.drawable.edit_text);
                filterOrg.setTextColor(Color.BLACK);
                cList.clear();
                field = "";
                item = "";
                EventChangeListener(field, item);
                Toast.makeText(v.getContext(), "Now Showing All Post", Toast.LENGTH_SHORT).show();
            }
        });

        //check for my post exist
        fStore.collection("community").whereEqualTo("userID", fAuth.getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    if (document.getString("userID") != null && !document.getString("userID").isEmpty()) {
                        String uID = document.getString("userID");
                        if (uID.equals(fAuth.getCurrentUser().getUid())){
                            filterMyPost.setVisibility(View.VISIBLE);
                        }else {
                            filterMyPost.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        });

        filterMyPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterMyPost.setBackgroundResource(R.drawable.button_bg);
                filterMyPost.setTextColor(Color.WHITE);
                filterAll.setBackgroundResource(R.drawable.edit_text);
                filterAll.setTextColor(Color.BLACK);
                filterOrg.setBackgroundResource(R.drawable.edit_text);
                filterOrg.setTextColor(Color.BLACK);
                cList.clear();
                field = "userID";
                item = fAuth.getCurrentUser().getUid();
                EventChangeListener(field, item);
                Toast.makeText(v.getContext(), "Now Showing Posts by You", Toast.LENGTH_SHORT).show();
            }
        });

        filterOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterOrg.setBackgroundResource(R.drawable.button_bg);
                filterOrg.setTextColor(Color.WHITE);
                filterMyPost.setBackgroundResource(R.drawable.edit_text);
                filterMyPost.setTextColor(Color.BLACK);
                filterAll.setBackgroundResource(R.drawable.edit_text);
                filterAll.setTextColor(Color.BLACK);

                //pop up filter window
                Dialog dialog = new Dialog(CommunityClass.this);
                View popupView = LayoutInflater.from(CommunityClass.this).inflate(
                        R.layout.org_filter_box, findViewById(R.id.popup_container));
                RelativeLayout relativeLayout = popupView.findViewById(R.id.popup_container);
                RadioGroup radioGroup = (RadioGroup) popupView.findViewById(R.id.radioGroup);
                Button select = popupView.findViewById(R.id.deletePost);
                final String[] selectedRadioText = {""};

                // get organization list from firestore
                fStore.collection("community").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        orgList = new ArrayList<String>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            if (document.getString("userOrg") != null && !document.getString("userOrg").isEmpty()) {
                                String getString = document.getString("userOrg");
                                orgList.add(getString);
                            }
                        }

                        //load list into popup window
                        ArrayList<String> checkedList = new ArrayList<>();
                        for (int i = 1; i < orgList.size() + 1; i++) {
                            if (!checkedList.toString().contains(orgList.get(i-1))) {
                                RadioButton radioButton = new RadioButton(v.getContext());
                                radioButton.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                String text = orgList.get(i - 1);
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
                                    Toast.makeText(v.getContext(), "Please select one of the organization", Toast.LENGTH_SHORT).show();

                                } else {
                                    int checkedid = radioGroup.getCheckedRadioButtonId();
                                    item = orgList.get(checkedid-1);
                                    field = "userOrg";
                                    cList.clear();
                                    EventChangeListener(field, item);
                                    Toast.makeText(v.getContext(), "Now Showing Posts by " + item, Toast.LENGTH_SHORT).show();
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


        //Refresh function
        swipeRefreshLayout = findViewById(R.id.SwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cList = new ArrayList<CommunityModel>();
                adapter = new CommunityPostAdapter(CommunityClass.this, cList);
                recyclerView.setAdapter(adapter);
                cList.clear();
                EventChangeListener(field, item);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(), "Successfully Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

        //Add Post
        add_post = findViewById(R.id.add_post);

        DocumentReference profileData = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        profileData.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String org = documentSnapshot.getString("organization");
                    String positionType = documentSnapshot.getString("userType");
                    String imageUrl = documentSnapshot.getString("profileImages");

                    if (positionType.equals("Self-Learner")) {
                        add_post.setVisibility(View.INVISIBLE);
                    } else {
                        add_post.setVisibility(View.VISIBLE);
                    }

                    postOrg[0] = org;
                    profileImages[0] = imageUrl;
                } else {
                    Toast.makeText(CommunityClass.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CommunityClass.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }
        });

        add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        CommunityClass.this, R.style.BottomSheetDialogTheme
                );
                View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.layout_bottom_sheet, (LinearLayout) findViewById(R.id.bottomSheetContainer));
                submit_post = bottomSheetView.findViewById(R.id.postNow);
                cancel_post = bottomSheetView.findViewById(R.id.cancel_button);
                postImage = bottomSheetView.findViewById(R.id.post_image);
                description = bottomSheetView.findViewById(R.id.description);
                //Upload image from gallery function
                postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(CommunityClass.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                        } else {
                            selectImage();
                        }
                    }
                });

                submit_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getUploadedImageUri() != null && !description.getText().toString().equals("")) {
                            uploadFiletoStorage(getUploadedImageUri(), "add");
                            progressDialog.dismiss();
                            bottomSheetDialog.dismiss();
                        } else {
                            Toast.makeText(v.getContext(), "Please upload an image and fill in the text description", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                cancel_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        description.setText("");
                        Picasso.with(getApplicationContext()).load((Uri) null).fit().centerCrop().into(postImage);
                        bottomSheetDialog.dismiss();
                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
            }
        });

        //Update post
        Intent intent = getIntent();
        String documentid = "";
        if (intent.getStringExtra("edit") != null) {
            documentid = intent.getStringExtra("edit");

            DocumentReference communityData = fStore.collection("community").document(documentid);

            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                    CommunityClass.this, R.style.BottomSheetDialogTheme
            );
            View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                    R.layout.layout_bottom_sheet, (LinearLayout) findViewById(R.id.bottomSheetContainer));

            TextView title = bottomSheetView.findViewById(R.id.addPostText);
            title.setText("Edit Post");

            submit_post = bottomSheetView.findViewById(R.id.postNow);
            cancel_post = bottomSheetView.findViewById(R.id.cancel_button);
            postImage = bottomSheetView.findViewById(R.id.post_image);
            description = bottomSheetView.findViewById(R.id.description);

            communityData.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        String postImg = documentSnapshot.getString("postImages");
                        String descrip = documentSnapshot.getString("description");
                        Picasso.with(CommunityClass.this).load(postImg).fit().centerCrop().into(postImage);
                        description.setText(descrip);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CommunityClass.this, "Failed to load post", Toast.LENGTH_SHORT).show();
                }
            });

            //Upload image from gallery function
            postImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(CommunityClass.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                    } else {
                        selectImage();
                    }
                }
            });

            String finalDocumentid = documentid;
            submit_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (postImage.getDrawable() != null && !description.getText().toString().equals("")) {
                        uploadFiletoStorage(getUploadedImageUri(), finalDocumentid);
                        progressDialog.dismiss();
                        bottomSheetDialog.dismiss();
                    } else {
                        Toast.makeText(v.getContext(), "Please upload an image and fill in the text description", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            cancel_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    description.setText("");
                    Picasso.with(getApplicationContext()).load((Uri) null).fit().centerCrop().into(postImage);
                    bottomSheetDialog.dismiss();
                }
            });

            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        }


        //Navigation bottom
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.menuCommunity);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHome:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.menuCamera:
                        startActivity(new Intent(CommunityClass.this, CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.menuLearn: {
                        startActivity(new Intent(getApplicationContext(), LearningClass.class));
                        overridePendingTransition(0, 0);
                        return true;
                    }
                    case R.id.menuCommunity: {
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

        recycleViewConstraint = findViewById(R.id.recycleViewConstraint);
        recycleViewConstraint.setTranslationX(-300);
        recycleViewConstraint.setAlpha(f);
        recycleViewConstraint.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(400).start();

        //back navigation
        backbutton = findViewById(R.id.backArrow_learn);
        back = findViewById(R.id.back);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void EventChangeListener(String field, String item) {
        if (field.equals("")) {
            fStore.collection("community").orderBy("postCount", Query.Direction.ASCENDING)
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
                                        cList.add(dc.getDocument().toObject(CommunityModel.class));
                                    }
                                    adapter.notifyDataSetChanged();
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                }
                            } else {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                nopost.setVisibility(View.VISIBLE);
                            }

                        }
                    });
        } else {
            fStore.collection("community").whereEqualTo(field, item).orderBy("postCount", Query.Direction.ASCENDING)
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
                                            cList.add(dc.getDocument().toObject(CommunityModel.class));
                                        }
                                        adapter.notifyDataSetChanged();
                                        if (progressDialog.isShowing()) {
                                            progressDialog.dismiss();
                                        }
                                    }
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
                                                String descrip = description.getText().toString();
                                                String userID = fAuth.getCurrentUser().getUid();
//                                                Date currentTime = Calendar.getInstance().getTime();
//                                                String time = currentTime.toString();
                                                List<String> commentsLikes = new ArrayList<>();
                                                fStore.collection("community").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                        int count = queryDocumentSnapshots.getDocuments().size();
                                                        DocumentReference communityData = fStore.collection("community").document(fixed + "");
                                                        Map<String, Object> random = new HashMap<>();
                                                        random.put("postImages", imageLink);
                                                        random.put("description", descrip);
                                                        random.put("userID", userID);
                                                        random.put("userImage", profileImages[0]);
                                                        random.put("userOrg", postOrg[0]);
                                                        random.put("documentID", fixed + "");
                                                        random.put("comments", commentsLikes);
                                                        random.put("commentID", commentsLikes);
                                                        random.put("likes", commentsLikes);
                                                        random.put("postCount", count + 1);
                                                        Date currentTime = Calendar.getInstance().getTime();
//                                                String time = currentTime.toString();
                                                        communityData.set(random).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(CommunityClass.this, "Adding post...", Toast.LENGTH_SHORT);
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(CommunityClass.this, "Post not uploaded", Toast.LENGTH_SHORT);
                                                            }
                                                        });
                                                    }
                                                });

                                            } else {
                                                String descrip = description.getText().toString();
//

                                                DocumentReference communityData = fStore.collection("community").document(mode);
                                                Map<String, Object> random = new HashMap<>();
                                                random.put("postImages", imageLink);
                                                random.put("description", descrip);

                                                communityData.update(random).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(CommunityClass.this, "Post Updated", Toast.LENGTH_SHORT);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(CommunityClass.this, "Post not updated", Toast.LENGTH_SHORT);
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
                            Toast.makeText(CommunityClass.this, "Error Uploading Image", Toast.LENGTH_SHORT);
                        }
                    });
        } else if (!description.getText().toString().isEmpty()) {
            String descrip = description.getText().toString();
//            Date currentTime = Calendar.getInstance().getTime();

            DocumentReference communityData = fStore.collection("community").document(mode);
            Map<String, Object> random = new HashMap<>();
            random.put("description", descrip);

            communityData.update(random).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(CommunityClass.this, "Post Updated", Toast.LENGTH_SHORT);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(CommunityClass.this, "Post not updated", Toast.LENGTH_SHORT);
                }
            });
        }
    }
}