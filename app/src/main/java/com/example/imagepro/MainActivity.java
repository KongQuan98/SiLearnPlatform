package com.example.imagepro;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.opencv.android.OpenCVLoader;

import java.util.List;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    static {
        if(OpenCVLoader.initDebug()){
            Log.d("MainActivity: ","Opencv is loaded");
        }
        else {
            Log.d("MainActivity: ","Opencv failed to load");
        }
    }

//    public static String EXTRA_MESSAGE = "hi";
//    private ViewPager2 viewPager2;
//    private Handler sliderHandler = new Handler();

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    ConstraintLayout constraintLayout, constraintTips;
    ScrollView scrollView;
    BottomNavigationView bottomNavigationView;
    ImageView love;
    TextView tips, textUsername, welcome, sl, ts;
    LinearLayout recognition, learner, community, profile, admin, library, quiz;
    float f = 0;
    String displayText = "";
    String userType = "";

    public void setText(String displayText){
        this.displayText = displayText;
    }

    public String getText(){
        return displayText;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.top_navigation, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        constraintTips = findViewById(R.id.constraintTips);
        admin = findViewById(R.id.layoutAdmin);

        if (fAuth.getCurrentUser() == null){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        // Username on top
        textUsername = findViewById(R.id.textUsername);

        DocumentReference profileData = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        profileData.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("fName");
                    userType = documentSnapshot.getString("userType");

                    //Admin function
                    if (userType.equals("System Administrator")){
                        constraintTips.setVisibility(View.INVISIBLE);
                        admin.setVisibility(View.VISIBLE);
                        admin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                                startActivity(intent);
                            }
                        });
                    }

                    textUsername.setText(name + "!");
                }else{
                    Toast.makeText(MainActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }
        });

        //Navigation bottom
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menuHome:
                        return true;
                    case R.id.menuCamera:
                        startActivity(new Intent(MainActivity.this,CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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
        scrollView = findViewById(R.id.scrollView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        love = findViewById(R.id.love);
        tips = findViewById(R.id.tipsoftheday);
        profile = findViewById(R.id.layoutProfile);
        recognition = findViewById(R.id.layoutRecognition);
        learner = findViewById(R.id.layoutLearning);
        community = findViewById(R.id.layoutCommunity);
        library = findViewById(R.id.layoutLibrary);
        quiz = findViewById(R.id.layoutQuiz);
        welcome = findViewById(R.id.Welcome);
        sl = findViewById(R.id.sl);
//        ts = findViewById(R.id.ts);

        welcome.setTranslationX(-300);
        welcome.setAlpha(f);
        welcome.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(400).start();

        sl.setTranslationX(-300);
        sl.setAlpha(f);
        sl.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(500).start();

//        ts.setTranslationX(-300);
//        ts.setAlpha(f);
//        ts.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(600).start();

        constraintLayout.setTranslationY(-300);
        constraintLayout.setAlpha(f);
        constraintLayout.animate().translationY(0).alpha(1).setDuration(500).setStartDelay(400).start();

        scrollView.setTranslationX(-300);
        scrollView.setAlpha(f);
        scrollView.animate().translationX(0).alpha(1).setDuration(500).setStartDelay(400).start();

        //Favorite animation
        int fullLove = getResources().getIdentifier("@drawable/ic_baseline_favorite_24", null, getPackageName());
        int emptyLove = getResources().getIdentifier("@drawable/ic_baseline_favorite_border_24", null, getPackageName());
        int moretips = getResources().getIdentifier("@string/moretipes", null, getPackageName());
        int dummy1 = getResources().getIdentifier("@string/dummy", null, getPackageName());
        int dummy2 = getResources().getIdentifier("@string/dummy2", null, getPackageName());
        int dummy3 = getResources().getIdentifier("@string/dummy3", null, getPackageName());
        int dummy4 = getResources().getIdentifier("@string/dummy4", null, getPackageName());
        int dummy5 = getResources().getIdentifier("@string/dummy5", null, getPackageName());

        CharSequence dummyString1 = getResources().getText(dummy1);
        CharSequence dummyString2 = getResources().getText(dummy2);
        CharSequence dummyString3 = getResources().getText(dummy3);
        CharSequence dummyString4 = getResources().getText(dummy4);
        CharSequence dummyString5 = getResources().getText(dummy5);

        Drawable favourited = getResources().getDrawable(fullLove);
        Drawable unfavourited = getResources().getDrawable(emptyLove);

        //favourite action
        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(love.getDrawable() == favourited){
                    love.setImageDrawable(unfavourited);
                    if(getText().isEmpty() || getText() == null){
                        setText(tips.getText().toString());
                    }else{
                        tips.setText(getText());
                    }
                    constraintTips.setBackgroundResource(R.drawable.trending);
                    tips.setTextAlignment(View.TEXT_ALIGNMENT_INHERIT);
                    tips.setTranslationY(300);
                    tips.setAlpha(f);
                    tips.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
                }else{
                    if(getText().isEmpty() || getText() == null){
                        setText(tips.getText().toString());
                    }
                    love.setImageDrawable(favourited);
                    constraintTips.setBackgroundResource(R.drawable.trending_dark);
                    tips.setText(R.string.moretipes);
                    tips.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    tips.setTranslationY(300);
                    tips.setAlpha(f);
                    tips.animate().translationY(0).alpha(1).setDuration(300).setStartDelay(100).start();
                }
            }
        });

        tips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tips.getText().toString().equals(getResources().getText(moretips).toString())){
                    if (getText().equals(dummyString1.toString())){
                        setText(dummyString2.toString());
                    }else if (getText().equals(dummyString2.toString())){
                        setText(dummyString3.toString());
                    }else if (getText().equals(dummyString3.toString())){
                        setText(dummyString4.toString());
                    }else if (getText().equals(dummyString4.toString())){
                        setText(dummyString5.toString());
                    }else if (getText().equals(dummyString5.toString())){
                        setText(dummyString1.toString());
                    }
                    constraintTips.setBackgroundResource(R.drawable.trending);
                    tips.setText(getText());
                    tips.setTextAlignment(View.TEXT_ALIGNMENT_INHERIT);
                    tips.setTranslationY(300);
                    tips.setAlpha(f);
                    tips.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(100).start();
                    love.setImageDrawable(unfavourited);
                }
            }
        });

        //Navigation
        recognition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,CameraActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        learner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LearningClass.class);
                startActivity(intent);
            }
        });

        community.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommunityClass.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LibraryActivity.class);
                startActivity(intent);
            }
        });

        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                startActivity(intent);
            }
        });


    }
}