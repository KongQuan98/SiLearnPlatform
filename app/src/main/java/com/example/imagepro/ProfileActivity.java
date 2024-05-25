package com.example.imagepro;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObservable;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ProfileActivity extends AppCompatActivity {

    RelativeLayout header;
    LinearLayout cardview, textoption;
    ImageView backbutton, profileImage, logOutImage;
    TextView profileName, position, commentnum, learnNum, logOutText, backName;
    TextInputEditText fullName, email, organ;
    Button save;
    BottomNavigationView bottomNavigationView;
    float f = 0;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    StorageReference fStorage = FirebaseStorage.getInstance().getReference("profileImages");
    private StorageTask storageTask;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    Uri imageUri;
    String imageLink;

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (fAuth.getCurrentUser() == null) {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        // profile logic
        profileImage = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        position = findViewById(R.id.position);
        commentnum = findViewById(R.id.commentnum);
        learnNum = findViewById(R.id.learnNum);
        fullName = findViewById(R.id.fullName);
        email = findViewById(R.id.email);
        organ = findViewById(R.id.organ);
        save = findViewById(R.id.save_changes);

        //populate from dbase to activity profile

        DocumentReference profileData = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        profileData.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("fName");
                    String org = documentSnapshot.getString("organization");
                    String positionType = documentSnapshot.getString("userType");
                    String mail = documentSnapshot.getString("email");
                    String imageUrl = documentSnapshot.getString("profileImages");
                    List<String> wordsLearnt = (List<String>) documentSnapshot.get("wordsChecked");


                    double word = wordsLearnt.size();
                    double percentage = (word / 41 * 100);

                    profileName.setText(name);
                    position.setText(positionType);
                    commentnum.setText(Math.round(word) + "");
                    learnNum.setText(Math.round(percentage) + "%");
                    fullName.setText(name);
                    email.setText(mail);
                    organ.setText(org);
                    if (!imageUrl.equals("") && imageUrl != null) {
                        Picasso.with(getApplicationContext()).load(imageUrl).fit().centerCrop().into(profileImage);
                        profileImage.setBackground(null);
                    }

                } else {
                    Toast.makeText(ProfileActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }
        });

        //edit profile

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ProfileActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                } else {
                    selectImage();
                }
            }
        });

        email.setTag(email.getKeyListener());
        email.setKeyListener(null);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email.setError("Email cannot be changed here");
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = fullName.getText().toString();
                String newOrg = organ.getText().toString();

                if (storageTask != null && storageTask.isInProgress()){
                    Toast.makeText(ProfileActivity.this, "Image upload in progress", Toast.LENGTH_SHORT).show();

                } else{
                    uploadFiletoStorage(getImageUri());
                }
                Map<String, Object> random = new HashMap<>();
                random.put("fName", newName);
                random.put("organization", newOrg);
                profileData.update(random).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Your information has been updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });

        header = findViewById(R.id.headerbackground);
        cardview = findViewById(R.id.cardbackground);
        textoption = findViewById(R.id.textbackground);
        backbutton = findViewById(R.id.backArrow);
        backName = findViewById(R.id.backName);
        logOutImage = findViewById(R.id.logOutImage);
        logOutText = findViewById(R.id.logOutText);

        //naviagtion
        //back to main menu
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        backName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        //logout
        logOutText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ProfileActivity.this);
                View popupView = LayoutInflater.from(ProfileActivity.this).inflate(
                        R.layout.confirm_logout, findViewById(R.id.popup_container));
                Button editPost = popupView.findViewById(R.id.editPost);
                Button deletePost = popupView.findViewById(R.id.deletePost);

                editPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                });

                deletePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(popupView);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        logOutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ProfileActivity.this);
                View popupView = LayoutInflater.from(ProfileActivity.this).inflate(
                        R.layout.confirm_logout, findViewById(R.id.popup_container));
                Button editPost = popupView.findViewById(R.id.editPost);
                Button deletePost = popupView.findViewById(R.id.deletePost);

                editPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                });

                deletePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setContentView(popupView);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });


        //Navigation bottom
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.menuProfile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHome:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.menuCamera:
                        startActivity(new Intent(ProfileActivity.this, CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        overridePendingTransition(0, 0);
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
                        return true;
                    }
                }
                return false;
            }
        });

        //transition
        header.setTranslationY(-300);
        header.setAlpha(f);
        header.animate().translationY(0).alpha(1).setDuration(500).setStartDelay(400).start();

        cardview.setTranslationX(-300);
        cardview.setAlpha(f);
        cardview.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(400).start();

        textoption.setTranslationY(300);
        textoption.setAlpha(f);
        textoption.animate().translationY(0).alpha(1).setDuration(500).setStartDelay(400).start();


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
                setImageUri(data.getData());
                if (getImageUri() != null) {
                    try {
//                        InputStream inputStream = getContentResolver().openInputStream(getImageUri);
//                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                        uploadedImage = bitmap;
//                        postImage.setImageBitmap(bitmap);
                        Picasso.with(getApplicationContext()).load(getImageUri()).fit().centerCrop().into(profileImage);
                        profileImage.setBackground(null);
//                        File selectedImageFile = new File(getPathFromUri(getImageUri));
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

//    private String getPathFromUri(Uri contentUri) {
//        String filePath;
//        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
//        if (cursor == null) {
//            filePath = contentUri.getPath();
//        } else {
//            cursor.moveToFirst();
//            int index = cursor.getColumnIndex("_data");
//            filePath = cursor.getString(index);
//            cursor.close();
//        }
//        return filePath;
//    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void uploadFiletoStorage(Uri getImageUri) {
        if (getImageUri != null) {
            StorageReference fileReference = fStorage.child(fAuth.getCurrentUser().getUid() + "." + getFileExtension(getImageUri));
            storageTask = fileReference.putFile(getImageUri)
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
                                            DocumentReference profileData = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
                                            Map<String, Object> random = new HashMap<>();
                                            random.put("profileImages", imageLink);
                                            profileData.update(random).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(ProfileActivity.this, "Image not uploaded", Toast.LENGTH_SHORT);
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Error Uploading Image", Toast.LENGTH_SHORT);
                        }
                    });
        }
    }
}